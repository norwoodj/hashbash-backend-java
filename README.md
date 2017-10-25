Hash Bash
============
From the Hashbash [homepage](https://hashbash.johnmalcolmnorwood.com):

```
This is a web-based rainbow table generator and searcher. It is deployed on a raspberry pi cluster
running kubernetes. Visit my github to view the salt-stack configuration, build tools, and custom
docker images I've built to deploy this and other projects on this platform.

You can see this particular project at https://github.com/norwoodj/hashbash
You might also visit my other project https://stupidchess.johnmalcolmnorwood.com

A rainbow table is a data structure that supports the space and time efficient reversal of
cryptographic hash functions. For details on how this works visit this article
https://en.wikipedia.org/wiki/Rainbow_table

This implementation uses java, spring-batch, and mysql to generate and store the rainbow table.
You can then use the API or web interface to search existing rainbow tables. This is certainly not the most
efficient implementation of rainbow tables possible or available. This was simply a fun way to implement the
algorithm in a user-friendly way.
```

### This Codebase
This code was written as a way to mess around with spring batch, which I have also used in a professional
setting at a job, and use it to generate a cool data structure that I wrote a C++ implementation
for in college. This is a slower, if much more enterprise-friendly version of that using spring boot,
and a bunch of it's starters for batch, web, jpa, rabbitmq, etc. The other key thing that it dumbs down is
using a mysql database and a unique index on endpoint hashes to support efficient uniquifying and searching.

This codebase builds two applications. The first is a java web server that handles requests for information
about rainbow tables, as well as handles requests to search or generate a rainbow table by posting a message
to one of several task queues. The second application is a set of rabbitmq consumers that consume from the 
task queues and perform the compute-intensive search, generate, and delete tasks.

The main point of this is that I'm a professional software developer, and my hope is that you, dear recruiter,
see this as an indication of my skillset.

This project leverages docker and docker-compose to build and run the application locally requiring
installation of minimal requirements, and with great developer ease. The same docker images that are built
to do this can beg deployed to a remote docker image repository as well and there is helm configuration to
deploy these images to a kubernetes cluster. This is how I deploy this project to
https://hashbash.johnmalcolmnorwood.com

All of this makes heavy use of other projects that I have written:

* [jscripts](https://github.com/norwoodj/jscripts) - utility bash scripts, build tools, release automation
* [rpi-salt](https://github.com/norwoodj/rpi-salt) - salt-stack configuration for setting up my raspberry pis
  as a kubernetes cluster

### Building and Developing Locally
In order to build, run and develop this project locally you'll need a number of things installed:

* docker - 17.06 or newer
* docker-compose - 1.16.1 or newer
* bash - 4.4 or newer
* git
* jq

After that you must install jscripts locally on your machine, and then in this repo:
```
cd
git clone https://github.com/norwoodj/jscripts.git .jscripts
cd /path/to/hashbash
./_jscripts-ctl.sh install
```

Once you have the requirements and scripts installed, you can build all of the docker images necessary
to run locally with:
```
./jscripts/build-images.sh all
```

This should build the three images for running locally in parallel.

You can then run with:
```
./jscripts/run-local.sh start hashbash
```

This starts the hashbhash application including the consumers, the web server, nginx, and a webpack_builder
image that watches your frontend assets and rebuilds them in the container when they change locally on disk.

If you want to use an IDE like IntelliJ to run the java applications, both HashbashEngineApplication and 
HashbashWebApplication, which I would recommend, you can also run only the dependencies in a docker container,
by running
```
./jscripts/run-local.sh start hashbash-deps
```

The config should be set up to work in an IDE if you configure with `spring.profiles.active=LCL` and this
app is running.
