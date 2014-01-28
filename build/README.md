HALE Continuous Delivery
================================

This directory contains the driver for the HALE Continuous Delivery process.
The driver has been designed with the following objectives in mind:

* It should be possible to start the whole delivery process with only one
  command
* Everything should be reproducible, even installing the build environment
  (see limitations below)
* Tasks should be atomic and as small as possible so build tasks can be grouped
  into pipeline steps (see below)

Requirements
------------

The only thing you need to install beforehand is a JDK 7. Please make
sure you have set the JAVA_HOME environment variable correctly. If you did
not set JAVA_HOME correctly, the script will warn you about that.

Setting up the build environment
--------------------------------

Currently no additional setup is needed. Simply make sure the
requirements mentioned above are met. Eventually, acceptance tests will
be added and this section will describe how to set-up databases and
stuff like this.

The main build script
---------------------

The main build script is called `build.bat` (Windows) or `build.sh` (Linux).
It is a thin wrapper around `gradlew` which itself is a wrapper for Gradle.
In the examples in this file we will use `build.sh`, but you can replace
this by `build.bat` under Windows of course.

Run the following command if you need help:

    ./build.sh help

Cleaning the workspace
----------------------

Just call the following command to remove all build artifacts:

    ./build.sh clean

Deployment Pipeline
-------------------

The deployment pipeline is separated into several stages which will be
described in this section.

### Commit Stage

The commit stage includes building all sources, creating binary artifacts (in
this case an update site containing all exported bundles) and running unit
tests. Currently it consists of the following steps:

* Build projects in workspace
* Export binary bundles to an update site
* Run unit tests using the binary bundles

You can run the commit stage with the following command:

    ./build.sh

### Production stage

The production stage consists of compiling sources and building RCP
products. You can build a specific product with the following command:

    ./build.sh <type> <name>

`<type>` must be `client` or `server`. The `name` is one of the product
names declared in `templates/products/client.yaml` or `templates/products/server.yaml`
depending on which type you chose. For example:

    ./build.sh client HALE

This command will build the normal HALE client application.

Alternatively you can use the following command to build from a specific
product definition file:

    ./gradlew buildProduct -PproductFile=<path-to-product-file>

The product file must be based on plugins. The path to this file must be
relative. For example:

    ./gradlew buildProduct -PproductFile=../ui/plugins/eu.esdihumboldt.hale.ui.application/HALE.product

Build properties
----------------

See the build help for options to provide to the different stages:

    ./build.sh help

