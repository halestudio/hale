# hale»studio platform in testing mode (PR 5) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Every regeneration of the hale»studio platform gives all bundles and the feature new, higher versions, so Eclipse picks them up instead of keeping outdated jars from its bundle pool.

**Architecture:** The platform build (`build/platform/build.gradle`) moves to the first bnd-platform 4.x release with the testing mode and enables it permanently. The fingerprint check of the platform build stays as it is, so new versions are only created when the platform is actually regenerated. The update site stays in its fixed directory.

**Tech Stack:** Gradle 9, bnd-platform 4.x, p2 director of an Eclipse platform for the acceptance test.

**Spec:** `docs/superpowers/specs/2026-11-06-gradle-9-design.md`, section "Testing mode (PR B and PR 5)".

## Global Constraints

- Starts after PR 4 (plan `2026-11-06-hale-gradle-9.md`) and after the release of bnd-platform with PR A, PR B and PR C. Branch `ING-5508-testing-mode` based on `ING-5508-gradle-9` (or on its target branch once PR 4 is merged).
- Commits: Conventional Commits, footer `ING-5508`.
- bnd-platform settings used (from the bnd-platform testing mode plan): `testingMode = true`; `testingUpdateSiteDirPerRun` stays `false`.
- Push and open the PR only after the user confirmed it.

## Review Focus

- Enabling the testing mode changes the platform build configuration and therefore the fingerprint: the first run after the change regenerates once — expected, not a bug. Pinned by step 1 of the skip sequence in Task 1.
- Skipped runs must not touch the update site (no new versions without regeneration). Pinned by steps 2 and 4 of the skip sequence.
- Products (CI) built from a regenerated platform: product and feature definitions reference the platform feature without version or with 0.0.0, so the new feature qualifier must not break them. Pinned by the CI product builds in Task 2.
- Eclipse with an existing bundle pool: after regeneration and target reload the new jars are used. Pinned by the director acceptance test in Task 1, optionally confirmed in the IDE.
- A developer passing an explicit qualifier (`-PplatformTesting=<qualifier>`) for a reproducible local run: the given qualifier is used. Pinned by step 5 of Task 1.

---

### Task 1: Enable the testing mode

**Files:**
- Modify: `build/platform/build.gradle` (default `bndPlatformVersion`; `platform { … }` general settings block: `testingMode = true` with a comment why)
- Modify: `docs/hale-studio-on-hale-core.md` (platform generation: testing mode, why, explicit qualifier via `-PplatformTesting=<qualifier>`)

**Interfaces:**
- Consumes: released bnd-platform version `<bnd-platform-version>` with `PlatformPluginExtension.testingMode`.

- [ ] **Step 1: Set version and testing mode**, then run the skip sequence in `build/` and keep the plugin lists of steps 1 and 3:

1. `mise exec -- ./gradlew generatePlatform` → regenerates (configuration changed); `ls platform/build/updatesite/plugins > /tmp/gen1.txt`
2. `mise exec -- ./gradlew generatePlatform` → `Platform is up to date…`; `diff /tmp/gen1.txt <(ls platform/build/updatesite/plugins)` empty
3. `mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform` → regenerates; `ls platform/build/updatesite/plugins > /tmp/gen3.txt`
4. `mise exec -- ./gradlew generatePlatform` → skip; `diff /tmp/gen3.txt <(ls platform/build/updatesite/plugins)` empty

- [ ] **Step 2: Check that every version increased between step 1 and step 3**

Compare per symbolic name (file names `<bsn>_<version>.jar`, also the feature in `platform/build/updatesite/features`) with `org.osgi.framework.Version` semantics; a small Groovy or Python script outside the repository is enough.
Expected: the same set of symbolic names; for each, version in step 3 > version in step 1; every version ends with `.i<14 digits>` or `-i<14 digits>`.

- [ ] **Step 3: Acceptance test with a shared p2 bundle pool** (outside the repository)

Keep a copy of the update site of step 1 (`site1`) before step 3 and use the one of step 3 (`site3`). Download an Eclipse platform (e.g. `https://archive.eclipse.org/eclipse/downloads/drops4/R-4.29-202309031000/eclipse-platform-4.29-linux-gtk-x86_64.tar.gz`) and run the director via the launcher jar:

```bash
L=eclipse/plugins/org.eclipse.equinox.launcher_*.jar
JRE=https://download.eclipse.org/eclipse/updates/4.29/R-4.29-202309031000/
d() { java -jar $L -nosplash -consoleLog -application org.eclipse.equinox.p2.director \
  -repository file:$1,$JRE -installIU com.google.guava.failureaccess,a.jre.javase \
  -destination $2 -bundlepool pool -profile $3 -p2.os linux -p2.ws gtk -p2.arch x86_64; }
d site1 destA A; d site3 destA A; d site3 destC C
ls pool/plugins | grep failureaccess
```

Expected: the pool contains the `failureaccess` jar with the version of `site3` (it has a different version than in `site1`, so p2 fetches it). Optionally: reload `platform/local-platform.target` in the Eclipse IDE and check the bundle pool (`<workspace>/.metadata/.plugins/org.eclipse.pde.core/.bundle_pool/plugins`).

- [ ] **Step 4: Update the documentation** (content as listed under Files).

- [ ] **Step 5: Explicit qualifier**

Run: `mise exec -- ./gradlew generatePlatform -PforceGeneratePlatform -PplatformTesting=i20260101000000` → every file name in `platform/build/updatesite/plugins` contains `i20260101000000`. Afterwards regenerate without the property (`-PforceGeneratePlatform`), so the local platform has a current qualifier again.

- [ ] **Step 6: Commit**

```bash
git add build/platform/build.gradle docs/hale-studio-on-hale-core.md
git commit -m "build: give a regenerated platform new versions" \
  -m "bnd-platform's testing mode gives all bundles and the feature a common, time-based qualifier, so Eclipse does not keep using outdated bundles from its bundle pool after the platform was regenerated." \
  -m "ING-5508"
```

### Task 2: PR and CI

- [ ] **Step 1:** Push and open the PR (after the user confirmed); description with the results of the skip sequence, the version comparison and the director test.
- [ ] **Step 2:** CI checks pass: platform generation, tests, products for linux/windows/macosx x86_64, smoke test.
