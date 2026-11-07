# hale»studio build on Gradle 9 (PR 4) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build hale»studio with Gradle 9.8.2 and generate its platform with bnd-platform 4.x.

**Architecture:** The build in `build/` (with `buildSrc` and the nested platform build in `build/platform`, run through the `GradleBuild` task `generatePlatform`) moves to Gradle 9 in one go: Groovy 4 only requires the XML classes to come from `groovy.xml`. The platform build then moves to the first bnd-platform 4.x release that can fetch sources on Gradle 9. The Tycho part of the build is not touched.

**Tech Stack:** Gradle 9.8.2 (Groovy 4 build scripts), bnd-platform 4.x, Java 21 (mise), Tycho via embedded Maven.

**Spec:** `docs/superpowers/specs/2026-11-06-gradle-9-design.md`, section "PR 4 in detail".

## Global Constraints

- Branch `ING-5508-gradle-9`, based on `ING-4439-current-eclipse`; the PR targets `ING-4439-current-eclipse` (stacked PR chain).
- Commits: Conventional Commits, footer `ING-5508`; corrections to earlier commits of the branch as `fixup!` commits.
- Gradle: the latest 9.x release, 9.8.2 at the time of writing (`curl -s https://services.gradle.org/versions/current`).
- bnd-platform: the first released 4.x that contains the Gradle 9 fix (PR C of the bnd-platform plan), or a newer 4.x release. Until it exists, use a locally built bnd-platform via `-PbndPlatformVersion=<version>` (published with `publishToMavenLocal`); the PR stays a draft until the release.
- Plugin `de.undercouch.download` stays at 5.7.0.
- Deprecations that do not fail the build may stay unless trivial; the remaining Gradle 10 deprecations are listed in the PR description.
- Run Gradle as `mise exec -- ./gradlew …` in `build/`.
- Building products needs more than 8 GB of free memory (`org.gradle.jvmargs=-Xmx8048m` in `build/gradle.properties`).
- Push and open the PR only after the user confirmed it.

## Review Focus

- `generatePlatform` run twice without changes: the second run must skip (`Platform is up to date…`) with Gradle 9 as with Gradle 8. Pinned by the skip sequence in Task 3.
- `-PforceGeneratePlatform` and other project properties still reach the nested build through `GradleBuild.startParameter.projectProperties`. Pinned by step 3 of the skip sequence (the forced run must actually regenerate).
- Release tasks that parse `feature.xml`/`.product` files (`releaseTasks.gradle`, `BundleParser`) only fail at execution time if a class is missing. Pinned by the grep in Task 1, step 4 (no unqualified or `groovy.util.` XML class left) and by running `./build.sh clean` (executes `BundleParser`).
- The smoke test (`smokeTest.gradle`) and the product builds exercise script code that configuration alone does not reach. Pinned by the local product build and the CI checks in Task 5.
- Renovate must not offer bnd-platform 3.x updates anymore and must propose 4.x updates. Pinned by the config change in Task 3 and Renovate's dependency dashboard after merge.

---

### Task 1: Use the XML classes of `groovy.xml`

Works on Gradle 8 (Groovy 3 has `groovy.xml`) and is required for Gradle 9 (Groovy 4 removed the `groovy.util` variants).

**Files:**
- Modify: `build/buildSrc/src/main/groovy/BundleParser.groovy` (lines 117, 253), `build/gradle/commitAndProductionStage.gradle` (all `XmlSlurper` uses, among them lines 43-304 and 1175), `build/gradle/releaseTasks.gradle` (lines 114, 123, 168), `build/gradle/smokeTest.gradle` (line 46), `build/platform/build.gradle` (`XmlParser`/`XmlNodePrinter` in `generateTargetFile`)

**Interfaces:**
- Produces: nothing new; all XML parsing uses `groovy.xml.XmlSlurper`, `groovy.xml.XmlParser`, `groovy.xml.XmlNodePrinter`.

- [ ] **Step 1: Generate the Gradle 8 baseline platform** (for the comparison in Task 3)

Run: `mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform` in `build/`, then
`ls platform/build/updatesite/plugins | sed -E 's/\.jar$//; s/[.-]?bnd-[A-Za-z0-9]+$//' | sort > /tmp/platform-gradle8.txt` (outside the repository).
Expected: `BUILD SUCCESSFUL`; the list has one line per bundle and source bundle.

- [ ] **Step 2: Replace the XML classes**

Import `groovy.xml.XmlSlurper` / `groovy.xml.XmlParser` / `groovy.xml.XmlNodePrinter` at the top of each file (scripts and `BundleParser.groovy`) and drop the `groovy.util.` qualifiers.

- [ ] **Step 3: Verify on Gradle 8**

Run: `mise exec -- ./gradlew tasks && ./build.sh clean && mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform` in `build/`
Expected: `BUILD SUCCESSFUL` for all three; `platform/local-platform.target` is regenerated and identical to before (`git status` shows no change, the file is ignored — compare with a copy taken before).

- [ ] **Step 4: Check nothing is left**

Run: `git grep -n -E "(groovy\.util\.)?Xml(Slurper|Parser|NodePrinter)\b" -- build | grep -v "groovy\.xml\.\|^.*import groovy\.xml"`
Expected: no output.

- [ ] **Step 5: Commit**

```bash
git commit -am "build: use the XML classes of groovy.xml" -m "Groovy 4, which Gradle 9 uses for build scripts, removed the classes in groovy.util." -m "ING-5508"
```

### Task 2: Update Gradle to 9.8.2

**Files:**
- Modify: `build/build.gradle` (`wrapper { gradleVersion = '8.14.6' }` → `'9.8.2'`), `build/gradle/wrapper/gradle-wrapper.properties`, `build/gradle/wrapper/gradle-wrapper.jar`, `build/gradlew`, `build/gradlew.bat`
- Modify (trivial deprecations): `build/buildSrc/build.gradle` (multi-string notation of `maven-resolver-api`), `build/platform/build.gradle` and `build/platform/modules/*.gradle` (`group`, `description`, `url` without `=`; multi-string notation of `org.geotools:gt-render`)

**Interfaces:**
- Consumes: Task 1.

- [ ] **Step 1: Update the wrapper**

Run: `mise exec -- ./gradlew wrapper --gradle-version 9.8.2 && mise exec -- ./gradlew wrapper` in `build/` (second run lets the new Gradle write its own wrapper files), and set `gradleVersion = '9.8.2'` in the `wrapper` block.
Expected: `gradle-wrapper.properties` points to `gradle-9.8.2-bin.zip`.

- [ ] **Step 2: Fix the trivial deprecations**

Assignments with `=` (`group = …`, `description = …`, `url = …`) and single-string dependency notation (`'org.apache.maven.resolver:maven-resolver-api:1.9.27'`, `'org.geotools:gt-render:35.0'` or the version variable used there).

- [ ] **Step 3: Verify configuration and the remaining deprecations**

Run: `mise exec -- ./gradlew tasks --warning-mode all 2>&1 | grep -i -A2 deprecat` in `build/`
Expected: `BUILD SUCCESSFUL`; no deprecation from `buildSrc/build.gradle` or `platform/build.gradle`. Note every remaining one with file and line for the PR description (expected: `Task.project` at execution time in `commitAndProductionStage.gradle` lines ~928/1008/1023/1063/1064 and `tycho.gradle` line ~27 — these show up only when the tasks run, see Task 5).

- [ ] **Step 4: Commit**

```bash
git add -A build/gradle/wrapper build/gradlew build/gradlew.bat build/build.gradle build/buildSrc/build.gradle build/platform
git commit -m "build: update Gradle to 9.8.2" -m "ING-5508"
```

### Task 3: Generate the platform with bnd-platform 4.x

**Files:**
- Modify: `build/platform/build.gradle` (default of `bndPlatformVersion`, currently `'3.2.1'`, line ~13), `.github/renovate.json5` (rule `matchPackageNames: ["org.standardout:bnd-platform"]`, `allowedVersions: "< 4"` and its comment)

**Interfaces:**
- Consumes: the released bnd-platform version from Task 4 of the bnd-platform plan (`<bnd-platform-version>` below).

- [ ] **Step 1: Set the version and remove the Renovate rule**

`def bndPlatformVersion = project.findProperty('bndPlatformVersion') ?: '<bnd-platform-version>'`; delete the Renovate rule block with its comment. While the release is pending, run all commands with `-PbndPlatformVersion=<local version>`.

- [ ] **Step 2: Run the skip sequence** (in `build/`)

1. `mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform` → `BUILD SUCCESSFUL`
2. `mise exec -- ./gradlew generatePlatform` → output contains `Platform is up to date, skipping its generation`; `platform/build/updatesite` unchanged (compare `ls -l --time-style=+%s platform/build/updatesite/plugins` before and after)
3. `mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform` → regenerates (no skip message, `:platform:bundles` executed)
4. `mise exec -- ./gradlew generatePlatform` → skip message again, update site unchanged compared to step 3

- [ ] **Step 3: Compare the bundles with the Gradle 8 baseline**

Run: `diff /tmp/platform-gradle8.txt <(ls platform/build/updatesite/plugins | sed -E 's/\.jar$//; s/[.-]?bnd-[A-Za-z0-9]+$//' | sort)`
Expected: no output. Explain every difference in the PR description (a spike with bnd-platform `master` showed none).

- [ ] **Step 4: Commit**

```bash
git add build/platform/build.gradle .github/renovate.json5
git commit -m "build: generate the platform with bnd-platform <bnd-platform-version>" -m "bnd-platform 4 is the version line for Gradle 9." -m "ING-5508"
```

### Task 4: Describe the change

**Files:**
- Modify: `docs/hale-studio-on-hale-core.md` (places that name Gradle 8, bnd-platform 3.x or the Renovate limit; add that bnd-platform 4 requires Gradle 9 and that 4.0.0 cannot fetch sources on Gradle 9)

- [ ] **Step 1:** Update the document; check with `git grep -n -E "Gradle 8|8\.14|3\.2\.1|bnd-platform 3|< 4" -- docs README.md CONTRIBUTING.md` that no outdated statement is left.
- [ ] **Step 2: Commit**

```bash
git commit -am "docs: describe the build on Gradle 9 and bnd-platform 4" -m "ING-5508"
```

### Task 5: PR and CI

- [ ] **Step 1:** Push `ING-5508-gradle-9` and open a draft PR against `ING-4439-current-eclipse` (after the user confirmed). Description: what changed, the skip sequence and comparison results, and the remaining Gradle 10 deprecations with file and line.
- [ ] **Step 2:** CI checks pass: platform generation, `commitStage` (tests), products for linux/windows/macosx x86_64, smoke test. For the deprecations that only appear when tasks run, run `mise exec -- ./build/gradlew -p build --warning-mode all cli -Pargs="product --arch x86_64 --os linux HALE"`; it prints them before Tycho starts. Add them to the description.
- [ ] **Step 3:** When the bnd-platform release is out, make sure Task 3 uses it without `-PbndPlatformVersion`, re-run the skip sequence, and mark the PR ready for review.
