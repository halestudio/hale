# Gradle 9 for the hale»studio build, and a platform that always has new versions

Part of ING-4439 (hale»studio on hale-core). Follows the PR chain PR0 (CI), PR1 (platform generated
from the hale-core BOM), PR2 (UI on hale-core) and PR3 (current Eclipse); this work builds on
`ING-4439-current-eclipse` (PR3).

## Intent

1. **Move the hale»studio build to Gradle 9.** Gradle 8 is on its way out, Gradle 9 runs build scripts
   with Groovy 4, and bnd-platform 4.x (the version line that is maintained going forward) requires
   Gradle 9. Staying on Gradle 8 would mean maintenance releases of an old bnd-platform line, which is
   not wanted.
2. **Make sure a regenerated platform is picked up by Eclipse.** The platform build (PR1) wraps
   libraries with bnd-platform, which appends a hash of each bundle's bnd configuration to its
   version. Changes that do not go into that configuration (global settings of the `platform { }`
   block other than `override`, an update of bnd-platform, import ranges that change only through
   `determineImportVersions`) change the bundle content but not the version. PDE provisions target
   bundles via p2 into a bundle pool keyed by ID and version, so Eclipse keeps using the old jar.
   This was reproduced:
   - toggling only `addBndPlatformManifestHeaders` left 376 of 431 bundles at the same version with a
     different manifest;
   - with the p2 director and a bundle pool, installing the same bundle from the regenerated site
     (same profile, or a new profile sharing the pool) kept the old jar; only a fresh pool got the new
     one.
   The remedy is the "testing mode" from an earlier spike of bnd-platform: a common, time-based
   qualifier for all bundles and the feature, so every generation has new, increasing versions.

## Goals

- The hale»studio build runs with the latest Gradle 9 release (9.8.2 at the time of writing),
  including `buildSrc` and the nested platform build.
- The platform is generated with bnd-platform 4.x.
- Every generation of the platform produces new versions for all bundles and the feature, and
  generations are skipped as before when nothing changed (fingerprint check of PR1).
- bnd-platform detects aarch64 (Linux, macOS, Windows) and does not try to detect the environment
  when it is configured.

## Non-goals

- **Groovy 4 at runtime.** hale»studio keeps Groovy 2.5.x at runtime, as provided by the hale-core
  BOM. Moving to Groovy 4 has to start in hale-core (after its release without OrientDB) and is a
  separate effort. Groovy 4 here only concerns build scripts and `buildSrc`.
- **aarch64 products** (macOS on Apple Silicon, Linux/Windows on ARM). Separate topic.
- Changes to the Tycho/Maven part of the build beyond what Gradle 9 forces.
- A maintenance release of bnd-platform 3.x.

## Anti-goals

- The platform generation must not regenerate on every build. New versions are only created when
  the platform is actually regenerated.
- The Gradle migration must not change the build output (products, update site) other than what
  follows from the new tool versions.

## Constraints

- **bnd-platform** lives in `stempler/bnd-platform`. Changes go in as PRs from Florian; releases are
  made by the maintainer, so there is some (expected short) waiting time for a release. Releases are
  made with semantic-release from conventional commits (`feat:` gives a minor release).
- bnd-platform 4.0.0 fails on Linux aarch64 when applied (environment detection), even if
  `osgiOS`/`osgiWS`/`osgiArch` are configured. CI (x86_64) and macOS hosts are not affected. Local
  verification on Linux aarch64 (e.g. a Linux VM on Apple Silicon) needs a locally built bnd-platform from
  `master` with the aarch64 changes (PR A below), used via `-PbndPlatformVersion` and `mavenLocal()`.
- bnd-platform 4.0.0 fails on Gradle 9 when sources are fetched (`fetchSources = true`, as in the
  hale»studio platform build): `DependencyHelper` calls `LenientConfiguration.getFirstLevelModuleDependencies(Spec)`
  and `LenientConfiguration.getArtifacts(Spec)`, which Gradle 9 removed. The functional tests of
  bnd-platform all run with `fetchSources = false`, so this went unnoticed. Found in a spike with
  Gradle 9.8.2; with both calls replaced, the platform generation of hale»studio succeeded and
  produced the same bundles (symbolic names and upstream versions) as with Gradle 8.
- The nested platform build runs via a `GradleBuild` task and therefore always uses the Gradle
  version of the hale»studio build.
- The build uses Java 21 (mise), which Gradle 9 supports.
- Commits follow Conventional Commits with the Jira key ING-5508 in the footer.

## How it is split (decided)

### bnd-platform

- **PR A: aarch64.** Bring the two commits of the 3.2.x maintenance branch to `master`:
  - `feat: detect aarch64 platforms` (Linux, macOS and Windows on aarch64; macOS on Apple Silicon was
    detected as x86_64 before), with tests;
  - `fix: only detect the environment if it is not configured`.
  Adapted to the code base of `master` (Groovy 4, formatting conventions) as needed.
- **PR C: Gradle 9 dependency resolution.** Replace the two calls of APIs removed in Gradle 9 in
  `DependencyHelper` (`fix:`), with a functional test that fetches sources.
- **PR B: testing mode.** Implemented anew on `master`; the spike branch is based on 3.1 and only
  serves as a reference.
- PR C and PR A come first and should be released together (or PR C alone if PR A is delayed);
  PR B may follow in a later release.

### hale»studio

- **PR 4: Gradle 9** on `ING-4439-current-eclipse`, with the first released bnd-platform 4.x that
  contains PR C. PR 4 waits for that release (decided; the alternative of a temporary
  `fetchSources = false` was rejected).
- **PR 5: testing mode**, a small follow-up as soon as the first released bnd-platform 4.x version
  that contains both PR A and PR B is available: bump bnd-platform to that version and enable the
  testing mode in the platform build.

## PR 4 in detail

Findings of a spike with Gradle 9.8.2 (configuration, platform generation, and a Linux product
build up to the Tycho build, where the machine ran out of memory):

- Wrapper: `build/gradle/wrapper/gradle-wrapper.properties` and the `wrapper { gradleVersion }` block
  in `build/build.gradle`; regenerate the wrapper files with the new Gradle.
- **The only change that was required:** Groovy 4 removed `groovy.util.XmlSlurper`/`XmlParser`/
  `XmlNodePrinter` (moved to `groovy.xml` with Groovy 3). Unqualified and `groovy.util.`-qualified
  uses in `build/buildSrc/src/main/groovy/BundleParser.groovy` (fails compiling `buildSrc`),
  `build/gradle/commitAndProductionStage.gradle`, `releaseTasks.gradle`, `smokeTest.gradle` and
  `build/platform/build.gradle`.
- Deprecations announcing a removal in Gradle 10 (allowed to stay, see below):
  - `propName value` assignments (`group`, `description`, `url`) in `build/platform/build.gradle`
    and its module scripts;
  - multi-string dependency notation in `build/buildSrc/build.gradle` and in the platform build;
  - `Task.project` at execution time in `commitAndProductionStage.gradle` and `tycho.gradle`.
- `buildDir` and `GradleBuild` with `startParameter.projectProperties` still work on Gradle 9.
- Plugin `de.undercouch.download` 5.7.0 (latest release) is kept. Its release notes list integration
  tests up to Gradle 9.3.1; that it works with the chosen Gradle version is verified by the builds
  below. A replacement is only considered if it actually fails.
- `build/gradle/deployArtifacts.gradle` and `checkDependencies.gradle` are not applied; they only need
  to stay parseable if they are referenced anywhere, otherwise they are left as they are.
- `build/platform/build.gradle`: default `bndPlatformVersion` to the first released 4.x that contains
  PR C (or the latest 4.x release, if newer). Until it is released, PR 4 is developed against a
  locally built bnd-platform via `-PbndPlatformVersion`.
- `.github/renovate.json5`: remove the rule limiting `org.standardout:bnd-platform` to `< 4`.
- `docs/hale-studio-on-hale-core.md`: update where it mentions Gradle or bnd-platform versions.

Gradle deprecations that do not fail the build may stay unless they are trivial to fix; the build
must run without errors on Gradle 9, and new deprecation warnings that announce a removal in
Gradle 10 are listed in the PR description.

Verification:

- The skip sequence described under "Verification of the skip and the versions" in the testing mode
  section below, without the expectation of new versions in step 3, which only applies to PR 5.
- The CI checks of the PR (platform generation, product builds for linux/windows/macosx x86_64,
  smoke test) pass.
- The generated update site has the same bundles (symbolic names and upstream versions) as with
  Gradle 8 / bnd-platform 3.2.1; differences are explained.

The Linux product build could not run to the end on the spike machine (about 7 GB of memory,
while the build sets `-Xmx8048m`); the product builds are verified by CI.

## Testing mode (PR B and PR 5)

Behaviour in bnd-platform (decided in principle, details left to the builder):

- Enabled through a Gradle property (as in the spike: `platformTesting`, `true` or an explicit
  qualifier) and through the `platform { }` extension, so a build script can enable it permanently.
- A common qualifier for all bundles and the feature, by default `i` + date and time
  (`yyyyMMddHHmmss`), so it is increasing between generations.
- **The testing qualifier replaces the generated qualifiers** (bnd hash, feature hash, default
  qualifier such as `autowrapped`). OSGi compares qualifiers lexicographically, so a timestamp behind
  a changing hash would not give increasing versions. An unchanged upstream qualifier (e.g. `jre` of
  Guava 33.7.2-jre, `orientDB151`) stays in front of the timestamp, separated by `-` as today, so
  versions increase per generation for the same upstream version.
- **All bundles** means every bundle and source bundle the platform publishes, including:
  - existing OSGi bundles that would otherwise be copied unchanged (they have to be wrapped so their
    version can change);
  - file based dependencies (where bnd-platform adds no qualifier by default);
  - merged bundles;
  - source bundles.
  For each of them the version in the manifest, the p2 metadata of the update site and the
  references in the feature must agree, and `Eclipse-SourceBundle` of a source bundle must refer to
  the new version of its bundle. Explicit opt-outs in a bnd configuration (`addQualifier = false`)
  are the builder's call, as long as they are documented.
- PR B has tests for each of these cases.
- The version history / hash qualifier map is not used or updated in testing mode.
- An update site directory per generation (as in the spike) is available as an option, **off by
  default**.

In hale»studio (PR 5):

- Testing mode is always enabled in the platform build, locally and in CI.
- The update site stays in its fixed directory (`build/platform/build/updatesite`). *My call:* the
  fingerprint of PR1 contains the absolute path of the update site, so a directory per generation
  would defeat the skip and leave old directories behind; new versions are already enough for p2 to
  fetch the bundles again.
- Verification of the skip and the versions, in this order:
  1. generate the platform;
  2. run the generation again without changes: skipped, update site (content and versions)
     unchanged;
  3. regenerate with `-PforceGeneratePlatform`: every bundle, source bundle and the feature has a
     new, higher version;
  4. run the generation again without changes: skipped, update site unchanged compared to step 3.
- Acceptance test with a shared p2 bundle pool (repeat of the reproduction above): install from the
  update site of step 1 with the p2 director, then from the one of step 3 into the same profile and
  into a new profile sharing the pool; the pool contains the new jars. Optionally confirmed in the
  Eclipse IDE by reloading `platform/local-platform.target` and checking the bundle pool.

A side effect is that the unstable hash qualifiers (the order of generated `Import-Package` entries
varies between runs, so about 60 bundles got new qualifiers without any change) no longer matter.
Fixing that instability in bnd-platform is not part of this work.

## Left to the builder

- How to fix each Gradle 9 / Groovy 4 incompatibility (e.g. `layout.buildDirectory` vs. keeping
  deprecated API where it still works).
- Whether the download plugin needs a replacement if it does not work with Gradle 9.
- The exact API of the testing mode in bnd-platform (property names, extension fields), staying close
  to the spike.
- Tests for bnd-platform PR A and PR B, following the existing tests (unit tests, functional tests).
- Commit structure within each PR.
