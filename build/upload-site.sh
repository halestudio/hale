#!/bin/bash

# delete all existing site builds
for file in target/eu.esdihumboldt.hale.all.feature.site-*.zip
do
  if [ -e "$file" ]; then
    rm $file
  fi
done

# build sites
./build.sh site

for file in target/eu.esdihumboldt.hale.all.feature.site-*.zip
do
  # determine version
  VERSION=$(echo $file | sed -r 's/.+\.site-([0-9a-zA-Z\.\-]+)\.zip/\1/')
  echo "Update site version $VERSION"
  
  # extract to temporary folder
  rm -r tmp-update-site
  mkdir tmp-update-site
  unzip -q $file -d tmp-update-site

  # sync update site (replace SNAPSHOTS)
  aws s3 sync tmp-update-site s3://build-artifacts.wetransform.to/p2/hale/$VERSION --acl public-read --delete --region eu-central-1
  rc=$?

  rm -r tmp-update-site

  if [ $rc -ne 0 ]; then
    echo "Error uploading update site"
    exit $rc
  fi
done
