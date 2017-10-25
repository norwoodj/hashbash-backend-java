Docker Images and Compose configuration
=======================================
This directory contains all of the docker-related files in this project. This includes
the Dockerfiles that are used to build the docker images that run this project both
locally and in deployed environments, as well as the docker-compose files used to
run console scripts and local servers.

You'll note that in this project there's a twist where there are both rpi and x86 docker
images being built. This is because I deploy this project on a raspberry pi cluster at
home and different images are needed for each architecture.
