Release process
---------------

Releases are created from the `master` branch using the [Release](./.github/workflows/release.yml) action.
Run it to create a release based on the changes since the last release.

Usually no tasks need to be performed manually for a release other than launching the release action.

Any changes that should be included in a release should be on `master` beforehand, including any documentation changes.
