MAVEN_IMAGE=maven:3.6-jdk-12
DOCKER_REPOSITORY=jnorwood


.PHONY: all
all: consumers webapp

consumers: hashbash-engine.jar
	docker build --tag $(DOCKER_REPOSITORY)/hashbash-consumers:current --file docker/Dockerfile-consumers .
	touch consumers

webapp: hashbash-webapp.jar
	docker build --tag $(DOCKER_REPOSITORY)/hashbash-webapp:current --file docker/Dockerfile-webapp .
	touch webapp

hashbash-engine.jar hashbash-webapp.jar: version-poms
	docker run --rm -it -v ${HOME}/.m2:/root/.m2 -v ${PWD}:/opt/build -w /opt/build $(MAVEN_IMAGE) mvn clean verify
	mv engine/target/hashbash-engine.jar webapp/target/hashbash-webapp.jar .

version-poms: version.txt
	docker run --rm -it -v ${HOME}/.m2:/root/.m2 -v ${PWD}:/opt/build -w /opt/build $(MAVEN_IMAGE) mvn versions:set --define newVersion=$(shell cat version.txt)
	touch version-poms

version.txt:
	echo release-$(shell docker run --rm --entrypoint date $(MAVEN_IMAGE) --utc "+%Y%m%d-%H%M") > version.txt

.PHONY: push
push: all
	docker tag $(DOCKER_REPOSITORY)/hashbash-consumers:current $(DOCKER_REPOSITORY)/hashbash-consumers:$(shell cat version.txt)
	docker tag $(DOCKER_REPOSITORY)/hashbash-webapp:current $(DOCKER_REPOSITORY)/hashbash-webapp:$(shell cat version.txt)
	docker push $(DOCKER_REPOSITORY)/hashbash-consumers:$(shell cat version.txt)
	docker push $(DOCKER_REPOSITORY)/hashbash-webapp:$(shell cat version.txt)

.PHONY: run-deps
run-deps: volume
	HASHBASH_HOST_IP_ADDRESS=$(shell ./get-wan-ip) docker-compose -f docker/docker-compose-hashbash-deps.yaml up

.PHONY: run
run: all volume
	docker-compose -f docker/docker-compose-hashbash.yaml up

volume:
	docker volume create --name=hashbash-data
	touch volume

.PHONY: clean
clean:
	rm -f version.txt
	git checkout pom.xml */pom.xml
	rm -f pom.xml.versionsBackup */pom.xml.versionsBackup
	rm -f *.jar
