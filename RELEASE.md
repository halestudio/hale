General rules
=============

* HALE releases should always be done on the *release* branch
* Release specific changes (e.g. excluded features) should not be merged into the *master* branch (only merge from *master* into *release*, not vice versa)

Before proceeding with the release steps
========================================

Create a new branch from the master:

1. Create a PR to update the [CHANGELOG.md](https://github.com/kapil-agnihotri/hale/blob/master/CHANGELOG.md)
2. Create a PR to update [what's new page](https://github.com/halestudio/hale/tree/master/doc/plugins/eu.esdihumboldt.hale.doc.user/html/new) by creating a new `<release-tag>.xhtml` page.
3. Once the PRs are merged to the master, update the Infocenter container, so that [what's new page in the documentation](http://help.halestudio.org/latest/index.jsp?topic=%2Feu.esdihumboldt.hale.doc.user%2Fhtml%2Fnew%2F4_1_0.xhtml&cp%3D0_1_0) is updated to show the latest release information. Then proceed with the steps for a HALE release.

Steps for a HALE release
========================

On release branch:

1. Merge origin/master into release
2. Set the release version for application bundles and build
   `./updateversionnumbers.groovy --release <RELEASE-VERSION>`
3. Create a commit
4. Create a tag for the new release
5. Push to origin/release and push tags

On master branch:

1. Set new snapshot version for bundles and features
   `./updateversionnumbers.groovy -o <RELEASE-VERSION> -n <NEW-VERSION>`
2. Set new snapshot version for application
   `./updateversionnumbers.groovy --snapshot <NEW-VERSION>`

Example
=======

On release branch:

```
git merge origin/master
./updateversionnumbers.groovy --release 2.9.0
git add -A
git commit -m "Updated application versions for 2.9.0 release"
git tag 2.9.0
git push origin release
git push --tags
```

On master branch:

```
./updateversionnumbers.groovy -o 2.9.0 -n 3.0.0
./updateversionnumbers.groovy --snapshot 3.0.0
```

After the release
=================
1. Create Windows installer
2. Draft a new release for the latest release at https://github.com/halestudio/hale/releases and publish it.
3. Update [download page](https://github.com/wetransform/www.wetransform.to/blob/deploy/app/downloads/index.html) so that the latest builds are available for download from https://www.wetransform.to/downloads/
