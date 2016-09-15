General rules 
=============

* HALE releases should always be done on the *release* branch
* Release specific changes (e.g. excluded features) should not be merged into the *master* branch (only merge from *master* into *release*, not vice versa)

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
