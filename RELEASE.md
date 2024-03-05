# General rules

- hale»studio releases should always be done on the _release_ branch
- Release specific changes (e.g. excluded features) should not be merged into the _master_ branch (only merge from _master_ into _release_, not vice versa)

# Before proceeding with the release steps

Create a new branch from master and apply the following changes:

1. check if there is a need to change the [splash year in the copyright notice](https://github.com/halestudio/hale/blob/master/ui/plugins/eu.esdihumboldt.hale.ui.application/plugin.xml#L103)
2. update the [CHANGELOG.md](https://github.com/halestudio/hale/blob/master/CHANGELOG.md#change-log)
3. update the [what's new page](https://github.com/halestudio/hale/tree/master/doc/plugins/eu.esdihumboldt.hale.doc.user/html/new) by creating a new `<release-tag>.xhtml` page.
4. update the [Infocenter container](https://github.com/halestudio/hale/blob/fb07374fdd4e5078ccbc2074fd8a13bd48982e58/doc/plugins/eu.esdihumboldt.hale.doc.user/toc.xml#L17), so that the [what's new page in the documentation]http://help.halestudio.org/latest/index.jsp?topic=%2Feu.esdihumboldt.hale.doc.user%2Fhtml%2Fnew%2F5_1_0.xhtml) is updated to show the latest release information.

and then create a PR to master containing the changes.

Then proceed with the steps for a hale»studio release.

# Release Process Steps:

0. Refresh your forked repository

Steps for Merging into the `release` branch:

1. Create new branch: Begin by creating a new branch, such as `release_new_version`, branching off from `release`.
2. Merge changes: Merge changes from `origin/master` into the release branch and commit the updates.
3. Update version numbers: Execute the `./updateversionnumbers.groovy` script to set the release version for application bundles and configure the necessary build settings. Use the command: `./updateversionnumbers.groovy --release <RELEASE-VERSION>`.
4. Commit changes: Commit the version number updates and all the commits resulting from the merge.
5. Tag the Release: Create a tag to signify the new release version.
6. Push Changes: Push the `release_new_version` branch and tags to the remote repository.
7. Create Pull Request: Start a pull request (PR) from the `release_new_version` branch to merge the changes into the `release` branch.

Steps for merging into the `master` branch:

1. Create new branch: Start by creating a new branch (e.g., `master_update_version`) from the `master` branch.
2. Set new snapshot version for bundles and features: Use the `./updateversionnumbers.groovy` script to set a new snapshot version for bundles and features with the command: `./updateversionnumbers.groovy -o <RELEASE-VERSION> -n <NEW-VERSION>`.
3. Set new snapshot version for application: Set a new snapshot version for the application using the command: `./updateversionnumbers.groovy --snapshot <NEW-VERSION>`.
4. Push changes: Push the `master_update_version` branch to the remote repository.
5. Create pull request: Initiate a pull request (PR) from the `master_update_version` branch to merge the changes into the `master` branch.

# Example

Steps that will be merged into the release branch:

```
git merge origin/master
./updateversionnumbers.groovy --release 5.1.0
git add -A
git commit -m "build: update application versions for 5.1.0 release"
git tag 5.1.0
git push origin release
git push --tags
```

Steps that will be merged into the master branch:

```
./updateversionnumbers.groovy -o 5.2.0 -n 5.3.0
./updateversionnumbers.groovy --snapshot 5.3.0
```

# After the release

1. Create Windows installer:
   - Prerequisites: install Wix Toolset v.3.11 [available here](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm)
   - While on the release branch, execute `build.bat product -o windows -a x86_64 HALE` from the root directory.
2. Drafting a New Release on GitHub:
   - Gather the files generated in Jenkins from the latest successful build of the release branch.
   - Utilize these files to draft the release on the [hale»studio GitHub repository](https://github.com/halestudio/hale/releases), including the .msi file generated in step 1.
3. Update Infocenter (more details will come from Kapil)
4. Update the download page:
   - Make necessary updates to the [download page](https://github.com/wetransform/www.wetransform.to/blob/deploy/app/downloads/index.html)
   - Ensure that the latest builds are accessible for download from https://www.wetransform.to/downloads/
