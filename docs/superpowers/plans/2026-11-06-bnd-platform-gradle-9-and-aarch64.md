# bnd-platform: aarch64 and Gradle 9 dependency resolution Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make bnd-platform `master` run on aarch64 hosts (PR A) and fetch sources on Gradle 9 (PR C), so a release can be used by the hale»studio build on Gradle 9.

**Architecture:** Two independent PRs against `stempler/bnd-platform` `master`, each on its own branch. PR A brings two commits of the maintenance branch `fix/aarch64-3.2.x` (`1196c82`, `b7cccbf`) to `master`. PR C replaces two calls of Gradle APIs that Gradle 9 removed. The maintainer releases both together (semantic-release; `feat:` in PR A gives 4.1.0).

**Tech Stack:** Groovy 4 / Java 17 Gradle plugin, Gradle 9.8.1 wrapper, JUnit 6 + AssertJ + Gradle TestKit, spotless via `to.wetransform.conventions`.

**Spec:** `docs/superpowers/specs/2026-11-06-gradle-9-design.md` (hale»studio repository), sections "Constraints" and "How it is split".

## Global Constraints

- Work in the bnd-platform repository, branches off `master`, one PR per branch against `stempler/bnd-platform`.
- Conventional Commits. No Jira key in bnd-platform commits (public project).
- Java from mise (`mise.toml`: temurin 17); run Gradle as `mise exec -- ./gradlew …`.
- `./gradlew check` (unit tests, functional tests, `spotlessCheck`) must pass; format with `./spotless.sh` before committing. Test files carry the license header like the existing ones.
- Functional tests need network access (Maven Central; `UpdateSiteTest` also archive.eclipse.org).
- Push and open PRs only after the user confirmed it.

## Review Focus

- Unknown OS/arch with a fully configured environment: applying the plugin must not fail. Pinned by `EnvironmentTest.configuredEnvironmentSkipsDetection` (Task 1).
- Only partly configured environment: detection still runs and fills the missing values. Pinned by `EnvironmentTest.partialConfigurationIsCompleted` (Task 1).
- macOS on Apple Silicon is now detected as `aarch64` instead of `x86_64` — intended; covered by `OSTest` (Task 2).
- Project or file dependencies next to external modules with `fetchSources = true`: sources are resolved only for external modules, as before (the removed `ExternalDependencySpec` selected `ExternalDependency`). Pinned by `SourceBundleTest.sourcesFetchedWithFileDependency` (Task 3).
- External dependencies whose jars come through transitive children: children are still traversed for sources. Pinned by the `slf4j-simple` → `slf4j-api` assertion in `SourceBundleTest.sourcesFetched` (Task 3).

---

### Task 1: PR A — only detect the environment if it is not configured

**Files:**
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/PlatformPlugin.groovy` (`configureEnvironment`, around line 400-413)
- Create: `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/EnvironmentTest.groovy`

**Interfaces:**
- Consumes: `AbstractFunctionalTest` (`writeBuildFile(String)`, `runTask(String...)`, `projectDir`).
- Produces: `configureEnvironment(Project)` returns early when `ext` has all of `osgiOS`, `osgiWS`, `osgiArch`.

- [ ] **Step 1: Create the branch and cherry-pick**

```bash
git switch -c feat/aarch64 master
git cherry-pick 1196c82
```

Expected: conflict in `PlatformPlugin.groovy`. Keep the early return inserted by `1196c82` and master's line `SwtPlatform swt = SwtPlatform.getRunning()` (no semicolon), then `git add` and `git cherry-pick --continue`.

- [ ] **Step 2: Write the functional tests**

`EnvironmentTest extends AbstractFunctionalTest`, with a task in the build file that prints the environment:

```groovy
// build file block: task printEnv { doLast { println "env=${osgiOS}/${osgiWS}/${osgiArch}" } }

@Test
void configuredEnvironmentSkipsDetection() {
    // osgiOS=linux, osgiWS=gtk, osgiArch=ppc64le (an arch the detection does not know)
    def result = runTask('printEnv')
    assertThat(result.output).contains('env=linux/gtk/ppc64le')
}

@Test
void partialConfigurationIsCompleted() {
    // only osgiWS=gtk
    def result = runTask('printEnv')
    assertThat(result.output).containsPattern(/env=(linux|macosx|win32)\/gtk\/(x86|x86_64|aarch64)/)
}
```

The values must be visible through `project.ext` when the plugin is applied. First try `gradle.properties` in the test project (`projectDir`); if `ext.properties.containsKey(...)` does not see them, add a helper to `AbstractFunctionalTest` that writes the `ext.osgi…` assignments before the `plugins`-applied code runs (e.g. a `settings.gradle` `gradle.beforeProject { ext.osgiOS = … }`). Use the first mechanism that works and say which in a test comment.

- [ ] **Step 3: Run the tests**

Run: `mise exec -- ./gradlew functionalTest --tests org.standardout.gradle.plugin.platform.EnvironmentTest`
Expected: PASS. Sanity check that the first test pins the fix: temporarily remove the early return; the test must fail with `Unknown os.arch ppc64le`-like errors only on Linux (on other hosts the detection ignores the configured arch either way) — note the observed result in the PR.

- [ ] **Step 4: Format and amend the cherry-picked commit with the tests**

Run: `./spotless.sh && mise exec -- ./gradlew test functionalTest --tests '*EnvironmentTest'`
Expected: `BUILD SUCCESSFUL`
```bash
git add -A && git commit --amend --no-edit
```

### Task 2: PR A — detect aarch64 platforms

**Files:**
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/internal/osdetect/OS.java`, `Arch.java`, `SwtPlatform.java`
- Create: `src/test/groovy/org/standardout/gradle/plugin/platform/internal/osdetect/OSTest.groovy` (from `b7cccbf`)

**Interfaces:**
- Produces (as in `b7cccbf`): `OS.WIN_aarch64`, `OS.LINUX_aarch64`, `OS.MAC_aarch64`; `Arch.aarch64`; `Arch.getOsgiArch() : String`; `OS.detect(String osName, String osArch, boolean hasProgramFilesX86) : OS`.

- [ ] **Step 1: Cherry-pick**

```bash
git cherry-pick b7cccbf
```

Expected: conflict in `OS.java` (master reformatted the switch statements). Apply the changes of `b7cccbf` (`git show b7cccbf -- '*OS.java'`) onto master's version; `Arch.java`, `SwtPlatform.java` and `OSTest.groovy` apply cleanly.

- [ ] **Step 2: Run the unit tests**

Run: `mise exec -- ./gradlew test --tests '*OSTest'`
Expected: PASS

- [ ] **Step 3: Format, check, continue the cherry-pick**

Run: `./spotless.sh && mise exec -- ./gradlew check`
Expected: `BUILD SUCCESSFUL` (spotless adds the license header to `OSTest.groovy` and reformats the `getOsgiArch()` switch).
```bash
git add -A && git cherry-pick --continue
```

- [ ] **Step 4: Push `feat/aarch64` and open PR A**

PR text: macOS on Apple Silicon was detected as x86_64; Linux on aarch64 failed with `Unknown os.arch aarch64` even with a configured environment; both commits already exist on the 3.2.x maintenance branch, a 3.x release is not needed.

### Task 3: PR C — fetch sources with the dependency APIs of Gradle 9

**Files:**
- Create: `src/functionalTest/groovy/org/standardout/gradle/plugin/platform/SourceBundleTest.groovy`
- Modify: `src/main/groovy/org/standardout/gradle/plugin/platform/internal/util/gradle/DependencyHelper.groovy` (`resolveAllDependencies` around line 169-174; `getArtifacts(Specs.satisfyAll())` around line 202; `ExternalDependencySpec`/`SatisfyAllSpec` around line 62-79)

**Interfaces:**
- Consumes: `AbstractFunctionalTest` helpers `writeBuildFile`, `runTask`, `findBundle`, `readManifest`.
- Produces: `DependencyHelper.resolveAllDependencies(Configuration) : Set<ResolvedDependency>` (signature unchanged); `SourceBundleTest` (extended by the testing mode plan).

- [ ] **Step 1: Create the branch**

```bash
git switch -c fix/gradle-9-dependency-resolution master
```

- [ ] **Step 2: Write the failing functional tests**

```groovy
@Test
void sourcesFetched() {
    writeBuildFile("""
        fetchSources = true
        bundle 'com.google.code.gson:gson:2.10.1'
        bundle 'org.slf4j:slf4j-simple:2.0.9'
    """)
    runTask('bundles')

    def gson = findBundle('com.google.gson_')
    def gsonSource = findBundle('com.google.gson.source_')
    assertThat(gsonSource).as('source bundle of gson').isNotNull()
    def bundleVersion = readManifest(gson).mainAttributes.getValue('Bundle-Version')
    def sourceManifest = readManifest(gsonSource).mainAttributes
    assertThat(sourceManifest.getValue('Bundle-Version')).isEqualTo(bundleVersion)
    assertThat(sourceManifest.getValue('Eclipse-SourceBundle'))
        .startsWith("com.google.gson;version=\"${bundleVersion}\"")
    assertThat(findBundle('slf4j.api.source_')).as('source bundle of a transitive dependency').isNotNull()
}

@Test
void sourcesFetchedWithFileDependency() {
    // additionally a plain jar created in the test (java.util.jar.JarOutputStream) and declared as
    // file based dependency as described in the README
    runTask('bundles')
    assertThat(findBundle('com.google.gson.source_')).isNotNull()
}
```

`writeBuildFile` places the block after `fetchSources = false`, so the later assignment wins. Check the symbolic names in `build/plugins` of a test project once and adjust the `findBundle` arguments if needed.

- [ ] **Step 3: Run the tests to verify they fail**

Run: `mise exec -- ./gradlew functionalTest --tests org.standardout.gradle.plugin.platform.SourceBundleTest`
Expected: FAIL with `No signature of method: …DefaultLenientConfiguration.getFirstLevelModuleDependencies() is applicable for argument types: (…ExternalDependencySpec)`

- [ ] **Step 4: Replace the removed APIs in `DependencyHelper`**

- `resolveAllDependencies`: take `lenientConfiguration.firstLevelModuleDependencies` and keep those whose `moduleGroup`/`moduleName` match a declared `ExternalDependency` (`configuration.allDependencies.withType(ExternalDependency)`); the recursion over children stays.
- `lenientConfiguration.getArtifacts(Specs.satisfyAll())` → `lenientConfiguration.artifacts`.
- Remove `ExternalDependencySpec`, `SatisfyAllSpec`/`SATISFY_ALL` and unused imports.
- Search for other `Spec`-taking calls of removed APIs: `grep -rn "getFirstLevelModuleDependencies(\|getArtifacts(\|getFiles(\|Specs\." src/main` — replace the same way.

- [ ] **Step 5: Run the tests to verify they pass**

Run: `mise exec -- ./gradlew functionalTest --tests org.standardout.gradle.plugin.platform.SourceBundleTest`
Expected: PASS

- [ ] **Step 6: Format and run the whole check**

Run: `./spotless.sh && mise exec -- ./gradlew check`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add src/functionalTest/groovy/org/standardout/gradle/plugin/platform/SourceBundleTest.groovy \
  src/main/groovy/org/standardout/gradle/plugin/platform/internal/util/gradle/DependencyHelper.groovy
git commit -m "fix: fetch sources with the dependency APIs of Gradle 9" \
  -m "Gradle 9 removed LenientConfiguration.getFirstLevelModuleDependencies(Spec) and getArtifacts(Spec), so creating source bundles failed."
```

- [ ] **Step 8: Push and open PR C**

PR text: the error from step 3, that all functional tests ran with `fetchSources = false`, and the new tests.

### Task 4: Release

- [ ] **Step 1:** When PR A and PR C are merged, ask the maintainer for a release (4.1.0 expected). If PR A is delayed, ask for a release of PR C alone (4.0.1).
- [ ] **Step 2:** Verify: `curl -s https://plugins.gradle.org/m2/org/standardout/bnd-platform/maven-metadata.xml | grep '<latest>'` shows the new version; it is the input for Task 3 of the hale»studio Gradle 9 plan.
