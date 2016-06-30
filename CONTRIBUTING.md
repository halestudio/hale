# How to contribute

Third-party contributions are essential to grow and expand the functionality of HALE.
We want to keep it as easy as possible to contribute changes that get your use cases working.
There are a few guidelines that we need contributors to follow so that we can have a chance of keeping on top of things.

## Getting Started

* Make sure you have a [GitHub account](https://github.com/signup/free)
* Fork and clone the repository on GitHub
* [Set up your local development environment](https://github.com/halestudio/hale/wiki/Set-up-your-development-environment)

## Making Changes

* Create a feature branch from where you want to base your work.
  * This is usually the **master** branch.
  * To quickly create a topic branch based on master; `git checkout -b
    feature/my_contribution master`. Please avoid working directly on the
    **master** branch.
* Make commits of logical units.
* Make sure you use meaningful and properly formatted commit messages.
* Make sure you have added the necessary tests for your changes.
* Run _all_ the tests to assure nothing else was accidentally broken.

## Conventions

### Project preferences and code formatting

All HALE projects use the same set of project preferences in Eclipse.
These preferences include settings for compiler warnings and code formatting.
Code formatting is applied automatically each time a file is saved in Eclipse.

We use a Groovy script `apply-preferences.groovy` to set the preferences for new projects or update the preferences of existing projects.
When creating a new project in the default HALE plugin folders, run the script to set the project preferences.

Part of the preferences are also templates for the code comments. If you want to use another default author name than your login name, just add

```
-Duser.name=Your Name
```

to the eclipse.ini-File of your Eclipse installation - just make sure you add it after the `-vmargs`-Line.


### Project and package names

Projects in the default HALE plugin folders (`common`, `ui`, `util`, etc.) have a common prefix that should also be used for new projects.

The name of a project/bundle must conform to a package name, which is the root package of the bundle.
There may be no two projects that have classes in the same package (with OSGi bundle fragments being the only exception).


### Tests

Tests are implemented using *JUnit*. Tests for classes in a bundle are not added to the bundle itself, but to a separate bundle for fragment that adds the suffix `.test` to the name of the original bundle.

Names of test classes by convention end with `Test`, or with `IT` if they are integration tests.

Test bundles/fragments need to be added to the `Tests.product`. Make sure the product validates.

To run all tests the simplest way is to run the *Tests* product from within Eclipse. To run single test classes or methods, you can right click them in Eclipse and select *Run As* → *JUnit Plug-in Test*.


## Submitting Changes

* Sign the [Contributor License Agreement](https://wetransform.box.com/v/hale-cla).
* Push your changes to a feature branch in your fork of the repository.
* Submit a pull request to the repository in the **halestudio** organization.
* The core team looks at Pull Requests on a regular basis and provides feedback.
* If your pull request passes code reviews and automated tests it will eventually get merged.

# Additional Resources

* [Setting up your development environment](https://github.com/halestudio/hale/wiki/Set-up-your-development-environment)
* [Contributor License Agreement](https://wetransform.box.com/v/hale-cla)
* [General GitHub documentation](https://help.github.com/)
* [GitHub pull request documentation](https://help.github.com/send-pull-requests/)
