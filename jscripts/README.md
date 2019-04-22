jscripts
========
This project contains a number of bash scripts for automating various aspects of building,
running, deploying, and releasing applications. These scripts take an opinionated approach
to project management and tech choice, but are also hopefully highly customizable.

### Requirements
This project and the tech decisions it has made require several things to be installed on
your machine:

* docker - 17.06 or newer
* docker-compose - 1.16.1 or newer
* bash - 4.4 or newer
* git
* jq

### Installation
The main benefit of packaging common project scripts here in this manner comes from how
jscripts are configured. To install jscripts into a project you will need this repository
cloned on your machine somewhere, the default being `~/.jscripts`
```
cd
git clone https://github.com/norwoodj/jscripts.git .jscripts
```

Then to use in a project you'd copy the `_jscripts-ctl` script from this project into yours
and run:
```
./_jscripts-ctl install
```

_Note_: if you clone this project at a location other than the default, you will have to configure
that location when running this script:

```
./_jscripts-ctl --jscripts /path/to/jscripts install
```

This will symlink the scripts and utilities files from the `jscripts` directory in this project
into a `jscripts` subdirectory in your project, and copy over the settings files from
`jscripts/settings`. This means that any changes in your project's copies of the utilities
files or scripts themselves reflects in the installation of jscripts on your computer
and affects the (presumably) multiple other projects also using the shared installation.

This is however, not true for the settings files copied into your project. This reflects
the intent of jscripts configuration. The utilities files and scripts should never be altered
in a project using them, and any project specific configuration is made as changes to the
settings files copied into your project. If you find that you cannot do something you want
to do using these scripts, configuring only settings files, and you want to make something about
the scripts configurable in a new way, submit an issue to this project, the intent is that you
should only ever have to change the settings files.

### The Scripts
Currently the following scripts are available for use

* `build-images.sh` - This script is used to build the docker images necessary to run and deploy
  the application
* `deploy-images.sh` - This script is used to deploy the docker images for this application to a
  remote docker image repository
* `prepare-release.sh` - This script assumes you're using the git model described below and prepares
  a release branch cut off of the stable branch
* `finish-release.sh` - This script finishes a release cut by `prepare-release/sh`. It merges the
  release branch into master, updates all the necessary verion files and starts a new development
  version.
* `run-console-script.sh` - This enables you to run a console script in a docker image. Uses docker-compose
  so you can configure dependent containers to start up as well as the container running the script
* `run-local.sh` - Runs the application locally. Uses docker-compose to enable use of other containers and
  volumes necessary to make the application run in a dev-friendly way locally.

### Git Model
I'm not sure what the git model this project uses is called, it probably has a name and came from somewhere,
but I just know it was used at a place I worked and is the most flexible, I believe, for enabling rapid
iteration and smooth releases.

In this model there are three important branches:

* `master` - Always holds the current production release state, whenever a release is deployed and completed
  the last thing that happens is the released version is merged to master.
* `stable` - Always hold a fast-forwarded version of master with all of the commits that have been verified in test
  since the last production release. The intent of this branch is that at any time, stable can be released
  to production and merged to master with little fear of things failing, as everything on stable is conceivably,
  well, stable.
* `test` - This branch is where you merge code that has been cleared to go to test to be verified. The
  integrity of this branch is not very important, and so git history is not imporatant to keep clean
  on this branch like it is on the other two. After a release completes in fact, you need test to be updated
  to the new development version started on stable, and so commonly you would just reset test to stable at
  this point.

This project is managed by the jscripts git scripts, take a look at the git history. Using this model
gets you nice linear history within releases, with a single merge commit every time a release happens.
Additionally and more importantly, this enables you to release at any time, as stable can be freely
cut from to start a release, and any features that get verified while the release is in flight can continue
to merge to stable, as those commits will get rebased onto the front of the release merge on the new
stable when the release completes.
