# bnd-platform: testing mode (PR B) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A testing mode in bnd-platform that gives every published bundle, source bundle and feature a common, time-based qualifier, so each generation of a platform has new, increasing versions.

**Architecture:** Two extension properties switch the mode on (`testingMode`, `testingQualifier`), also through the Gradle property `platformTesting`. `VersionUtil` applies the testing qualifier instead of the generated qualifiers (bnd hash, feature hash, default qualifier) and bypasses the qualifier map. The artifact classes make sure every bundle goes through `VersionUtil`: bundles that are copied today (existing OSGi bundles, file dependencies) are wrapped in testing mode, so manifest, file name, `feature.xml` and p2 metadata agree. Source bundles take the version of their bundle as today.

**Tech Stack:** Groovy 4 / Java 17 Gradle plugin, Gradle 9.8.1 wrapper, JUnit 6 + AssertJ + Gradle TestKit (`ProjectBuilder` for unit tests).

**Spec:** `docs/superpowers/specs/2026-11-06-gradle-9-design.md` (hale»studio repository), section "Testing mode (PR B and PR 5)".

## Global Constraints

- Branch `feat/testing-mode` off `master` of the bnd-platform repository, after PR C (plan `2026-11-06-bnd-platform-gradle-9-and-aarch64.md`) is merged — the tests use `fetchSources = true`.
- Conventional Commits (`feat:`), no Jira key (public project). `./spotless.sh` before committing; `./gradlew check` must pass. Push and open the PR only after the user confirmed it.
- Default qualifier: `i` + `yyyyMMddHHmmss` of the time the extension is created, in UTC; generated once per build, the same for all bundles and the feature.
- The testing qualifier **replaces** the generated qualifiers (bnd hash `bnd-<hash>`, feature hash, `defaultQualifier` such as `autowrapped`); an upstream qualifier stays in front, separated by `-` (`33.7.2-jre` → `33.7.2.jre-i20261106120000`; `2.10.1` → `2.10.1.i20261106120000`).
- In testing mode the hash qualifier map (`hashQualifierMap`) is neither read nor written.
- Update site directory per generation: option, **off by default**; when on, `updateSiteDir` defaults to `build/updatesite-<qualifier>` (an explicitly configured `updateSiteDir` wins).
- *My call (documented in the README):* testing mode also applies to bundles with `addQualifier = false` in their bnd configuration — otherwise "every bundle has a new version" would not hold.

## Review Focus

- An existing OSGi bundle without bnd configuration and without POM information (copied unchanged today, e.g. `org.slf4j:slf4j-api:2.0.9`): must be wrapped and get the testing version in its manifest. Pinned by `TestingModeTest.existingBundleIsWrapped` (Task 3).
- A file dependency that is already a bundle: same. Pinned by `TestingModeTest.fileDependencyGetsTestingQualifier` (Task 3).
- A source bundle created from a `-sources` jar: `Bundle-Version` and `Eclipse-SourceBundle` follow the bundle's testing version. Pinned by `TestingModeTest.sourceBundleFollowsBundle` (Task 3).
- `feature.xml` plugin entries and the p2 `content.xml` must use exactly the manifest versions. Pinned by `TestingModeTest.featureReferencesTestingVersions` (Task 4) and `UpdateSiteTest.testingModeVersionsInContentXml` (Task 4).
- A configured `hashQualifierMap` file must stay untouched in testing mode. Pinned by `TestingModeTest.qualifierMapNotUsed` (Task 2).

---

### Task 1: Configuration of the testing mode

**Files:**
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/PlatformPluginExtension.groovy` (fields; constructor around line 90-104 where `updateSiteDir` gets its default)
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/PlatformPlugin.groovy` (afterEvaluate around line 84-95: resolve the per-generation update site directory)
- Create: `src/test/groovy/org/standardout/gradle/plugin/platform/PlatformPluginExtensionTest.groovy`

**Interfaces:**
- Produces on `PlatformPluginExtension`:
  - `boolean testingMode` — default `false`; initialized from the Gradle property `platformTesting`: `true`/`false` (Boolean or String) set it; any other non-empty string enables it and is used as qualifier.
  - `String getTestingQualifier()` / `void setTestingQualifier(String)` — explicit value, or the generated default (see Global Constraints), stable for the extension's lifetime.
  - `boolean testingUpdateSiteDirPerRun` — default `false`.
  - `static String generateTestingQualifier(java.time.Instant now) : String` (package-visible helper for tests).

- [ ] **Step 1: Write the failing unit tests**

`PlatformPluginExtensionTest`, projects created with `ProjectBuilder.builder().build()` and `project.pluginManager.apply('org.standardout.bnd-platform')`:

```groovy
@Test void disabledByDefault() { assertThat(ext(project()).testingMode).isFalse() }

@Test void propertyTrueEnablesWithGeneratedQualifier() {
    def e = ext(project(platformTesting: 'true'))
    assertThat(e.testingMode).isTrue()
    assertThat(e.testingQualifier).matches(/i\d{14}/)
    assertThat(e.testingQualifier).isEqualTo(e.testingQualifier) // stable
}

@Test void propertyStringIsQualifier() {
    def e = ext(project(platformTesting: 'i20260101000000'))
    assertThat(e.testingMode).isTrue()
    assertThat(e.testingQualifier).isEqualTo('i20260101000000')
}

@Test void generatedQualifiersIncrease() {
    def a = PlatformPluginExtension.generateTestingQualifier(Instant.parse('2026-11-06T11:59:59Z'))
    def b = PlatformPluginExtension.generateTestingQualifier(Instant.parse('2026-11-06T12:00:00Z'))
    assertThat(a).isEqualTo('i20261106115959')
    assertThat(Version.parseVersion("1.0.0.$b")).isGreaterThan(Version.parseVersion("1.0.0.$a"))
}
```

(`project(Map props)`: set the properties with `project.extensions.extraProperties` before applying the plugin, or through `ProjectBuilder`'s gradle user home `gradle.properties` — whichever makes `project.findProperty` see them.)

- [ ] **Step 2: Run to verify they fail**

Run: `mise exec -- ./gradlew test --tests '*PlatformPluginExtensionTest'`
Expected: FAIL (compilation: no property `testingMode`)

- [ ] **Step 3: Implement the properties** as listed under Interfaces; the per-generation directory is set in `PlatformPlugin`'s `afterEvaluate` only if `testingMode && testingUpdateSiteDirPerRun` and `updateSiteDir` is still the default.

- [ ] **Step 4: Run to verify they pass**

Run: `mise exec -- ./gradlew test --tests '*PlatformPluginExtensionTest'`
Expected: PASS

- [ ] **Step 5: Commit** — together with Task 2 (the property alone changes no behaviour).

### Task 2: Testing qualifier in `VersionUtil`

**Files:**
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/internal/util/VersionUtil.groovy` (`getQualifierMap` 74-98, `addQualifier` for bundles 110-171 and for features 181-233)
- Create: `src/test/groovy/org/standardout/gradle/plugin/platform/internal/util/VersionUtilTest.groovy`
- Create: `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/TestingModeTest.groovy` (first test; more in Tasks 3 and 4)

**Interfaces:**
- Consumes: Task 1 (`testingMode`, `testingQualifier`).
- Produces: `VersionUtil.addTestingQualifier(String version, String qualifier) : String`; in testing mode both `addQualifier` overloads return `addTestingQualifier(version, project.platform.testingQualifier)` before any other logic (also before the `addQualifier == false` early exit); `getQualifierMap(project)` returns `null` in testing mode.

- [ ] **Step 1: Write the failing tests**

`VersionUtilTest` (with a `ProjectBuilder` project, `platform.testingMode = true`, `platform.testingQualifier = 'i20260101000000'`):

```groovy
@Test void appendsToVersionWithoutQualifier() {
    assertThat(VersionUtil.addTestingQualifier('2.10.1', 'i20260101000000')).isEqualTo('2.10.1.i20260101000000')
}
@Test void keepsUpstreamQualifier() {
    assertThat(VersionUtil.addTestingQualifier('33.7.2-jre', 'i20260101000000')).isEqualTo('33.7.2.jre-i20260101000000')
}
@Test void replacesBndHash() {
    // BndConfig with a property (e.g. Import-Package) would give 'bnd-<hash>' outside testing mode
    assertThat(VersionUtil.addQualifier('1.2.3', 'x.y', bndConfigWithImport(), project)).isEqualTo('1.2.3.i20260101000000')
}
@Test void ignoresAddQualifierFalse() { /* bndConfig.addQualifier = false */ … isEqualTo('1.2.3.i20260101000000') }
@Test void replacesFeatureHash() {
    assertThat(VersionUtil.addQualifier('6.5.2', featureWithOneBundle(), project)).isEqualTo('6.5.2.i20260101000000')
}
```

`TestingModeTest extends AbstractFunctionalTest`, runs with `-PplatformTesting=i20260101000000`:

```groovy
@Test void qualifierMapNotUsed() {
    writeBuildFile("""
        hashQualifierMap = file('qualifiers.json')
        bundle 'com.google.code.gson:gson:2.10.1'
    """)
    runTask('bundles', '-PplatformTesting=i20260101000000')
    assertThat(new File(projectDir, 'qualifiers.json')).doesNotExist()
    assertThat(readManifest(findBundle('com.google.gson_')).mainAttributes.getValue('Bundle-Version'))
        .isEqualTo('2.10.1.i20260101000000')
}
```

(If `runTask` does not take extra arguments, extend it in `AbstractFunctionalTest`.)

- [ ] **Step 2: Run to verify they fail**

Run: `mise exec -- ./gradlew test --tests '*VersionUtilTest' && mise exec -- ./gradlew functionalTest --tests '*TestingModeTest'`
Expected: FAIL (`addTestingQualifier` missing; then version `2.10.1.bnd-…` instead of the testing version)

- [ ] **Step 3: Implement** as described under Interfaces; reuse the existing "append with `-`" logic (duplicated in both overloads today) for `addTestingQualifier`.

- [ ] **Step 4: Run to verify they pass** (same commands) — Expected: PASS

- [ ] **Step 5: Commit**

```bash
git add src/main src/test src/functionalTest
git commit -m "feat: add a testing mode with a common, time-based qualifier" \
  -m "Enabled with the Gradle property platformTesting or the testingMode setting. The testing qualifier replaces the generated qualifiers, so every generation of a platform has new, increasing versions."
```

### Task 3: Every bundle gets the testing version

**Files:**
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/internal/ResolvedBundleArtifact.groovy` (wrap decision 178-203, qualifier 280-292), `src/main/groovy/org/standardout/gradle/plugin/platform/internal/FileBundleArtifact.groovy` (wrap 94, versions 110-122), and only if a test requires it `MergeBundleArtifact.groovy` / `SourceBundleArtifact.groovy` / `internal/util/bnd/BundleHelper.groovy`
- Modify: `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/TestingModeTest.groovy`

**Interfaces:**
- Consumes: Task 2.
- Produces: in testing mode every non-source artifact has `wrap == true` and a `modifiedVersion` with the testing qualifier; source bundles keep deriving their version from their bundle.

- [ ] **Step 1: Write the failing functional tests** (all with `-PplatformTesting=i20260101000000`; `Q = 'i20260101000000'`)

```groovy
@Test void existingBundleIsWrapped() {          // slf4j-api is copied unchanged today
    // bundle 'org.slf4j:slf4j-api:2.0.9'
    def jar = findBundle('slf4j.api_')
    assertThat(jar.name).contains("2.0.9.$Q")
    assertThat(version(jar)).isEqualTo("2.0.9.$Q")
}
@Test void upstreamQualifierKept() {            // bundle 'com.google.guava:guava:33.3.1-jre'
    assertThat(version(findBundle('com.google.guava_'))).isEqualTo("33.3.1.jre-$Q")
}
@Test void fileDependencyGetsTestingQualifier() {
    // jar created in the test: Bundle-SymbolicName test.file, Bundle-Version 1.0.0; declared as file dependency (README)
    assertThat(version(findBundle('test.file_'))).isEqualTo("1.0.0.$Q")
}
@Test void mergedBundleGetsTestingQualifier() {
    // merge like MergeTest.mergeWithMatchAndBnd, bnd { symbolicName = 'merged'; version = '1.0.0' }
    assertThat(version(findBundle('merged_'))).isEqualTo("1.0.0.$Q")
}
@Test void sourceBundleFollowsBundle() {        // fetchSources = true, gson
    def source = readManifest(findBundle('com.google.gson.source_')).mainAttributes
    assertThat(source.getValue('Bundle-Version')).isEqualTo("2.10.1.$Q")
    assertThat(source.getValue('Eclipse-SourceBundle')).startsWith("com.google.gson;version=\"2.10.1.$Q\"")
}
```

(`version(File)`: `readManifest(file).mainAttributes.getValue('Bundle-Version')`; for each test also assert that the file name contains the same version. Adjust `findBundle` arguments to the actual symbolic names.)

- [ ] **Step 2: Run to verify they fail**

Run: `mise exec -- ./gradlew functionalTest --tests '*TestingModeTest'`
Expected: `existingBundleIsWrapped` and `fileDependencyGetsTestingQualifier` FAIL (manifest keeps `2.0.9` / `1.0.0`); the others may already pass through Task 2.

- [ ] **Step 3: Implement** — in testing mode set `wrap = true` and `addQualifier = true` for non-source artifacts in `ResolvedBundleArtifact`; in `FileBundleArtifact` wrap non-source jars and take symbolic name and version from the jar's manifest when there is no bnd configuration. Do not change behaviour outside testing mode.

- [ ] **Step 4: Run the testing mode tests and the whole check**

Run: `mise exec -- ./gradlew functionalTest --tests '*TestingModeTest' && ./spotless.sh && mise exec -- ./gradlew check`
Expected: PASS / `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git commit -am "feat: give all bundles the testing version in testing mode" \
  -m "Existing bundles and file dependencies that were copied unchanged are wrapped, so manifest, file name and feature agree."
```

### Task 4: Features and update site agree, documentation

**Files:**
- Modify: `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/TestingModeTest.groovy`, `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/UpdateSiteTest.groovy`
- Modify: `README.md` (new section "Testing mode": property and settings, qualifier format, replacement of generated qualifiers, `addQualifier = false` is ignored, update site directory option, qualifier map not used)

**Interfaces:**
- Consumes: Tasks 1-3.

- [ ] **Step 1: Write the tests**

```groovy
// TestingModeTest
@Test void featureReferencesTestingVersions() {
    // gson + slf4j-api, runTask('bundleFeatures', '-PplatformTesting=i20260101000000')
    // parse feature.xml from the platform feature jar in build/features:
    //   <feature version> == "<project version or 1.0.0>.i20260101000000"
    //   every <plugin id version> == Bundle-Version of the jar with that symbolic name in build/plugins
}
@Test void updateSiteDirPerRun() {
    // testingUpdateSiteDirPerRun = true; task printing platform.updateSiteDir.name
    // output contains 'updatesite-i20260101000000'
}

// UpdateSiteTest
@Test void testingModeVersionsInContentXml() {
    // like the existing update site test, with -PplatformTesting=i20260101000000:
    // the unit of gson in content.xml (from content.jar) has version 2.10.1.i20260101000000
}
```

- [ ] **Step 2: Run them**

Run: `mise exec -- ./gradlew functionalTest --tests '*TestingModeTest' --tests '*UpdateSiteTest'`
Expected: PASS (fix the implementation if not — a mismatch here means a version is produced outside `VersionUtil`)

- [ ] **Step 3: Write the README section** (content as listed under Files).

- [ ] **Step 4: Format, check, commit**

Run: `./spotless.sh && mise exec -- ./gradlew check` — Expected: `BUILD SUCCESSFUL`
```bash
git commit -am "docs: describe the testing mode"
```
(Commit the tests with the docs or separately as `test: check feature and update site versions in testing mode`.)

- [ ] **Step 5: Push `feat/testing-mode` and open PR B**, then ask the maintainer for a release; record the released version for the hale»studio PR 5 plan.
