# hale-core BOM-aligned Target Platform Spike — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prove that a reproducible, BOM-aligned target platform for hale studio can be generated from hale-core 6.4.1 via bnd-platform and that a Tycho probe containing `eu.esdihumboldt.hale.io.gml` resolves against it, using only pinned release tooling.

**Architecture:** Pin the toolchain with mise and pin versions in the existing bnd-platform Gradle build; drop `mavenLocal()` and snapshot sources so the platform resolves purely from the hale-core BOM. Generate the p2 site + `local-platform.target` through the existing Gradle entry point (`./build/gradlew -p build generatePlatform`). Add a minimal Tycho reactor that requires `io.gml` and resolves against the generated target; apply bnd fixes until it resolves. Wrap both steps in one documented command and record findings.

**Tech Stack:** Gradle 8.14.5 (wrapper) + `org.standardout:bnd-platform:3.2.1`, Maven 3.9.6 + Tycho 4.0.8, Temurin Java 17, mise for toolchain pinning.

**Spec:** `docs/superpowers/specs/2026-09-10-hale-core-platform-target-spike-design.md`

## Global Constraints

- hale-core version is **6.4.1** exactly (latest release). Copy verbatim wherever a version is needed.
- bnd-platform plugin is **3.2.1** exactly. No `-SNAPSHOT`.
- **No `mavenLocal()`, no snapshot repositories, no snapshot plugins** anywhere in `build/platform`.
- The generated `local-platform.target` **must not** reference a local hale-platform checkout or a published `eu.esdihumboldt.hale.platform` p2 feature. Only the locally generated `updateSite` plus the pre-existing remote Eclipse p2 mirrors in `base-platform.target` are allowed.
- Toolchain pinned via mise: `java = "temurin-17"`, `maven = "3.9.6"`. Gradle from the wrapper.
- The probe proves **target resolution only**, never runtime behaviour — keep that distinction explicit in all findings.
- Commits follow Conventional Commits. Branch `hale-core-platform` carries no JIRA id, so omit the footer reference. End every commit body with:
  ```
  Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
  Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7
  ```

---

### Task 1: Pin the toolchain with mise

**Files:**
- Create: `mise.toml` (repo root)

**Interfaces:**
- Consumes: nothing.
- Produces: a pinned toolchain (`java`, `maven`) that Tasks 3–8 run under. Commands in later tasks assume a mise-activated shell (`mise install` has been run).

- [ ] **Step 1: Write `mise.toml`**

```toml
[tools]
java = "temurin-17"
maven = "3.9.6"
```

- [ ] **Step 2: Install and verify the pinned tools resolve**

Run: `mise install && mise exec -- java -version && mise exec -- mvn -v`
Expected: `openjdk version "17..."` (Temurin) and `Apache Maven 3.9.6`. If `mise` is not installed, that is an environment prerequisite — record it, install mise, and retry. Do not fall back to ambient tools.

- [ ] **Step 3: Commit**

```bash
git add mise.toml
git commit -m "build: pin java 17 and maven 3.9.6 via mise

Match CI toolchain (temurin-17, maven 3.9.6) so the platform build and
Tycho probe are reproducible from a clean checkout.

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

### Task 2: Pin versions and drop snapshot/local sources in the platform build

**Files:**
- Modify: `build/platform/build.gradle` (buildscript block lines ~1–25; `version` line ~33)

**Interfaces:**
- Consumes: the `halecore` configuration importing `platform("eu.esdihumboldt.hale:bom:$version")` (already present).
- Produces: a platform build that resolves the bnd-platform plugin and all hale-core artifacts from release repositories only, at hale-core 6.4.1.

- [ ] **Step 1: Set the hale-core version**

In `build/platform/build.gradle`, change:
```gradle
version = '6.1.0'
```
to:
```gradle
version = '6.4.1'
```

- [ ] **Step 2: Pin the bnd-platform plugin to the release**

Change:
```gradle
classpath 'org.standardout:bnd-platform:3.2.0-SNAPSHOT'
```
to:
```gradle
classpath 'org.standardout:bnd-platform:3.2.1'
```

- [ ] **Step 3: Remove local + snapshot repositories from the buildscript block**

Delete these lines from the `buildscript { repositories { … } }` block:
```gradle
        mavenLocal() //XXX for testing
```
and
```gradle
        maven {
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
        }
```
Then delete the now-unnecessary snapshot-refresh block:
```gradle
    configurations.all {
        // ensure SNAPSHOTs are updated every time if needed
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
    }
```
Leave the remaining buildscript repos (`gradlePluginPortal()`, wetransform artifactory `local`, `mavenCentral()`).

- [ ] **Step 4: Verify the buildscript + halecore config resolve from release repos only**

Run: `./build/gradlew -p build/platform --no-daemon dependencies --configuration halecore`
Expected: BUILD SUCCESSFUL, the dependency tree shows `eu.esdihumboldt.hale:bom:6.4.1` and hale features at `6.4.1`, and no artifact is resolved from `mavenLocal` or a snapshots repo. If resolution fails because the daemon cached a prior failure, retry with `--no-daemon` (already set). If bnd-platform's `osdetect` throws `Unknown os.arch <arch>` while the plugin is applied, that is an environment blocker (non-x86 JVM) — record it in the findings and apply the minimal documented workaround for this run (a Gradle init script setting `System.setProperty('os.arch','amd64')` plus `--no-daemon`); do not presume it is needed on x86 hosts.

- [ ] **Step 5: Commit**

```bash
git add build/platform/build.gradle
git commit -m "build: pin hale-core 6.4.1 and bnd-platform 3.2.1, drop snapshot sources

Resolve the platform purely from the hale-core BOM via release
repositories: remove mavenLocal and the Sonatype snapshots repo, and
pin the bnd-platform plugin to the 3.2.1 release.

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

### Task 3: Generate the p2 site + target through the entry point and verify the isolation constraint

**Files:**
- Generated (not committed as source): `platform/local-platform.target`, the p2 `updateSite` under `build/platform/build/…`
- No source file changes expected unless generation reveals a wiring gap.

**Interfaces:**
- Consumes: the pinned build from Task 2.
- Produces: `platform/local-platform.target` referencing the locally generated `updateSite` and the `eu.esdihumboldt.hale.platform` feature built by this run — consumed by the probe in Task 4.

- [ ] **Step 1: Generate the platform through the normal entry point**

Run: `./build/gradlew -p build generatePlatform`
Expected: BUILD SUCCESSFUL; a p2 repository is written under `build/platform/build/updateSite` (contains `content.jar`/`artifacts.jar`) and `platform/local-platform.target` is (re)created. Note: a full `clean updateSite` can take a long time (tens of minutes) as it fetches and re-bnds all bundles — let it run to completion. If it fails, capture the first error for the findings before retrying.

- [ ] **Step 2: Verify the target satisfies the isolation constraint**

Run: `grep -nE 'location=|unit id=' platform/local-platform.target`
Expected: the appended local location's `<repository location="file:…/build/platform/build/updateSite/"/>` points at the generated site, and its `<unit id="eu.esdihumboldt.hale.platform.feature.group" …/>` is the feature produced by this build. Confirm NO location references a published `hale-platform` p2 feature URL or a local hale-platform checkout path. The pre-existing `build-artifacts.wetransform.to` Eclipse mirrors (EMF/Xtext/ECF) from `base-platform.target` are expected and allowed.

- [ ] **Step 3: Record the generated feature version**

Run: `grep -n 'eu.esdihumboldt.hale.platform.feature.group' platform/local-platform.target`
Capture the exact `version` string (e.g. `6.4.1.<qualifier>`) — Task 4's probe target reference relies on the target file, so no manual copy is needed, but note it for the findings.

- [ ] **Step 4: Commit any wiring fix (only if Step 1/2 required a source change)**

If generation or the isolation check required editing `build/platform/build.gradle` or `platform/base-platform.target`, commit it:
```bash
git add build/platform/build.gradle platform/base-platform.target
git commit -m "fix: correct generated target wiring for local platform site

<one line on the concrete wiring problem found>

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```
If no source change was needed, skip the commit (the target file is a generated artifact and is deleted by `clean`).

---

### Task 4: Create the Tycho probe reactor

**Files:**
- Create: `build/platform/probe/pom.xml`
- Create: `build/platform/probe/eu.esdihumboldt.hale.platform.probe/pom.xml`
- Create: `build/platform/probe/eu.esdihumboldt.hale.platform.probe/META-INF/MANIFEST.MF`
- Create: `build/platform/probe/eu.esdihumboldt.hale.platform.probe/build.properties`

**Interfaces:**
- Consumes: `platform/local-platform.target` from Task 3.
- Produces: a runnable resolution probe: `mvn -f build/platform/probe/pom.xml -B package` resolves iff `io.gml`'s transitive requirement chain is satisfied by the generated target.

- [ ] **Step 1: Write the reactor parent POM**

`build/platform/probe/pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>eu.esdihumboldt.hale.platform</groupId>
  <artifactId>probe-parent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <packaging>pom</packaging>

  <properties>
    <tycho.version>4.0.8</tycho.version>
    <maven.compiler.release>17</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <modules>
    <module>eu.esdihumboldt.hale.platform.probe</module>
  </modules>

  <build>
    <plugins>
      <plugin>
        <groupId>org.eclipse.tycho</groupId>
        <artifactId>tycho-maven-plugin</artifactId>
        <version>${tycho.version}</version>
        <extensions>true</extensions>
      </plugin>
      <plugin>
        <groupId>org.eclipse.tycho</groupId>
        <artifactId>target-platform-configuration</artifactId>
        <version>${tycho.version}</version>
        <configuration>
          <target>
            <file>${maven.multiModuleProjectDirectory}/../../platform/local-platform.target</file>
          </target>
          <environments>
            <environment>
              <os>linux</os><ws>gtk</ws><arch>x86_64</arch>
            </environment>
          </environments>
          <pomDependencies>ignore</pomDependencies>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```
Note on the target reference: `${maven.multiModuleProjectDirectory}` resolves to `build/platform/probe`, so `../../platform/local-platform.target` reaches the repo-root `platform/`. If Tycho 4.0.8 rejects the `<file>` form, fall back to an absolute path passed on the command line via `-Dtarget.file=…` or convert the target into a `target-definition` module — record whichever was needed.

- [ ] **Step 2: Write the consumer bundle POM**

`build/platform/probe/eu.esdihumboldt.hale.platform.probe/pom.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>eu.esdihumboldt.hale.platform</groupId>
    <artifactId>probe-parent</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>
  <artifactId>eu.esdihumboldt.hale.platform.probe</artifactId>
  <packaging>eclipse-plugin</packaging>
</project>
```

- [ ] **Step 3: Write the bundle manifest requiring io.gml**

`build/platform/probe/eu.esdihumboldt.hale.platform.probe/META-INF/MANIFEST.MF` (must end with a trailing newline):
```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: hale platform resolution probe
Bundle-SymbolicName: eu.esdihumboldt.hale.platform.probe
Bundle-Version: 1.0.0
Automatic-Module-Name: eu.esdihumboldt.hale.platform.probe
Require-Bundle: eu.esdihumboldt.hale.io.gml
```

- [ ] **Step 4: Write build.properties**

`build/platform/probe/eu.esdihumboldt.hale.platform.probe/build.properties`:
```
source.. = src/
output.. = bin/
bin.includes = META-INF/,\
               .
```

- [ ] **Step 5: Run the probe (expect it to run and report resolution status)**

Run: `mvn -f build/platform/probe/pom.xml -B clean package`
Expected: Tycho starts and either (a) BUILD SUCCESS — io.gml already resolves — or (b) BUILD FAILURE naming the first unsatisfiable requirement. Either outcome is acceptable at this step; a hard configuration error (e.g. target file not found, Tycho cannot start) is NOT — fix that before proceeding. Record the first unsatisfied requirement, if any, for Task 5.

- [ ] **Step 6: Commit the probe**

```bash
git add build/platform/probe
git commit -m "test: add Tycho resolution probe requiring io.gml

Minimal Tycho reactor with an empty consumer bundle
(Require-Bundle: eu.esdihumboldt.hale.io.gml) resolved against the
generated local-platform.target, to validate target resolution.

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

### Task 5: Iterate bnd fixes until the io.gml probe resolves

This is a **discovery loop**, not a fixed edit. Tycho reports one unsatisfiable requirement chain at a time; each iteration adds a targeted, justified fix to `build/platform/build.gradle`, regenerates, and re-runs the probe. A reviewer gates on: probe reaches BUILD SUCCESS, and every added fix carries a one-line rationale.

**Files:**
- Modify: `build/platform/build.gradle` (the `platform { … bnd … }` fix blocks, ~lines 189–325)

**Interfaces:**
- Consumes: the probe from Task 4 and the generated target from Task 3.
- Produces: a green io.gml probe and a set of documented bnd fixes.

- [ ] **Step 1: Read the current first failure**

From Task 4 Step 5 output, identify the first unsatisfiable requirement (a bundle/package + version constraint). If Task 4 already reported BUILD SUCCESS, skip to Step 5.

- [ ] **Step 2: Add one targeted bnd fix**

Add a single fix to the `platform { … }` block in `build/platform/build.gradle`, matching the existing style, addressing exactly that failure. Example shape (adapt group/name/instruction to the actual failure — do not copy verbatim):
```gradle
    bnd group: '<group>', name: '<artifact>', {
        // <one-line reason: which import/export version the probe rejected>
        instruction 'Import-Package', '<package>;version="<range>"'
    }
```
Keep each fix minimal (one requirement) and annotated with why.

- [ ] **Step 3: Regenerate the target**

Run: `./build/gradlew -p build generatePlatform`
Expected: BUILD SUCCESSFUL; `platform/local-platform.target` regenerated.

- [ ] **Step 4: Re-run the probe**

Run: `mvn -f build/platform/probe/pom.xml -B clean package`
Expected: either BUILD SUCCESS (done — go to Step 5) or a *different* first failure (return to Step 1). If the same failure recurs unchanged, the fix did not take effect — inspect the regenerated bundle manifest in the updateSite before adding another fix. Stop and record as a blocker if a failure is outside io.gml's transitive chain (e.g. an unrelated GeoTools version range) or cannot be resolved by a bnd manifest fix.

- [ ] **Step 5: Confirm green and commit the accumulated fixes**

Run: `mvn -f build/platform/probe/pom.xml -B clean package`
Expected: `BUILD SUCCESS`.
```bash
git add build/platform/build.gradle
git commit -m "fix: resolve io.gml requirement chain in generated target

Add bnd manifest fixes so io.gml and its transitive requirements
resolve via Tycho against the BOM-aligned target. Each fix is
annotated with the requirement it satisfies.

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```
If no fix was needed (probe was green immediately), skip the commit and note that in the findings.

---

### Task 6: Check resolved versions against the hale-core BOM

**Files:**
- Create: `docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md` (BOM section; the file is completed in Task 8)
- Reference: `build/platform/build.gradle` `halecore` configuration

**Interfaces:**
- Consumes: the pinned build (Task 2) and the BOM `eu.esdihumboldt.hale:bom:6.4.1`.
- Produces: a resolved-vs-BOM comparison classifying each divergence as intentional deviation or bnd fix.

- [ ] **Step 1: Capture the resolved version tree**

Run: `./build/gradlew -p build/platform --no-daemon dependencies --configuration halecore > /tmp/halecore-resolved.txt 2>&1; echo done`
Expected: `done`, and `/tmp/halecore-resolved.txt` lists resolved versions.

- [ ] **Step 2: Fetch the BOM's managed versions for comparison**

Run: `mvn -B -q dependency:tree -f /dev/stdin <<'EOF' 2>/dev/null || true` — if constructing a synthetic consumer POM is awkward, instead read the managed versions directly:
`curl -s https://artifactory.wetransform.to/artifactory/local/eu/esdihumboldt/hale/bom/6.4.1/bom-6.4.1.pom | grep -A2 '<dependency>' | head -60`
Expected: the BOM's `<dependencyManagement>` entries with their pinned versions for spot-checking key libraries (e.g. Jackson, GeoTools, JTS, SLF4J/Log4j, Guava).

- [ ] **Step 3: Diff and classify**

For each key library, compare the resolved version (Step 1) against the BOM-managed version (Step 2). Record in the findings file a table with columns: library, BOM version, resolved version, verdict (`match` / `intentional deviation: <reason>` / `bnd fix: <task 5 entry>`). Pay specific attention to whether transitive POM-only BOMs (e.g. `jackson-bom`) actually constrained versions; if a library resolved *below or above* its BOM version unexpectedly, investigate whether the platform build needs the Gradle `jvm-ecosystem` behaviour to honour POM-only BOMs, and record the finding.

- [ ] **Step 4: Commit the BOM findings section**

```bash
git add docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md
git commit -m "docs: record resolved-vs-BOM version comparison for the platform spike

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

### Task 7: Wrap generation + probe in one documented command and challenge it clean

**Files:**
- Create: `build/platform/validate-platform.sh`

**Interfaces:**
- Consumes: everything from Tasks 1–5.
- Produces: the single documented DoD command; consumed by the reviewer and Task 8's findings.

- [ ] **Step 1: Write the wrapper script**

`build/platform/validate-platform.sh`:
```bash
#!/usr/bin/env bash
# Reproducible BOM-aligned target platform validation for hale-core.
# Generates the p2 site + target through the normal Gradle entry point,
# then resolves the io.gml probe through Tycho. Run under mise
# (`mise install` first) so java/maven are pinned.
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$repo_root"

echo "==> [1/2] Generating p2 repository + target (bnd-platform)"
./build/gradlew -p build generatePlatform

echo "==> [2/2] Resolving io.gml probe through Tycho"
mvn -f build/platform/probe/pom.xml -B clean package

echo "==> OK: p2 site + target generated and io.gml probe resolved."
```

- [ ] **Step 2: Make it executable**

Run: `chmod +x build/platform/validate-platform.sh`
Expected: no output; `test -x build/platform/validate-platform.sh` succeeds.

- [ ] **Step 3: Challenge from empty task-specific caches**

Clear the task-specific caches, then run the one command end to end:
```bash
rm -rf build/platform/build build/platform/.gradle
rm -rf ~/.m2/repository/.meta ~/.m2/repository/p2 ~/.m2/repository/.cache/tycho
mise exec -- build/platform/validate-platform.sh
```
Expected: the script prints `==> OK…` and exits 0; the run regenerates the p2 site and target from scratch and the Tycho probe ends in `BUILD SUCCESS`. Record the exact cache paths cleared and the wall-clock time for the findings. If clearing a broader cache is required for true reproducibility, note the corrected paths.

- [ ] **Step 4: Commit the wrapper**

```bash
git add build/platform/validate-platform.sh
git commit -m "build: add documented validate-platform.sh (generate target + resolve probe)

Single reproducible command: generate the p2 site and target via the
normal Gradle entry point, then resolve the io.gml probe through Tycho.

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

### Task 8: Write the findings, blockers and recommendation

**Files:**
- Modify/complete: `docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md`

**Interfaces:**
- Consumes: results of all prior tasks.
- Produces: the spike's primary deliverable.

- [ ] **Step 1: Complete the findings document**

Ensure `docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md` contains these sections with concrete, evidence-backed content (no placeholders):
1. **Summary** — one paragraph: does the approach work? (yes/partly/no, with the io.gml probe result).
2. **Confirmed availability** — hale-core 6.4.1 BOM/features/io.gml present on the wetransform artifactory (with the 2026-09-10 HTTP-200 evidence); any unavailable artifact listed as a blocker.
3. **Reproducible command** — the exact `mise exec -- build/platform/validate-platform.sh` invocation, the cache paths cleared before the challenge, and observed wall-clock time.
4. **BOM alignment** — the resolved-vs-BOM table from Task 6, with each deviation classified.
5. **Required bnd fixes** — the list from Task 5, each with its rationale.
6. **Environment workarounds** — anything needed merely to run the build (e.g. a non-x86 `os.arch` workaround), or "none required on x86_64".
7. **Remaining blockers** — unresolved chains outside io.gml (e.g. GeoTools ranges), the bnd-platform 4.0.0 upgrade question, and any POM-only-BOM constraint gaps.
8. **Target resolution vs. runtime validation** — explicit statement that the probe proves the io.gml requirement chain resolves and does NOT prove runtime behaviour or resolution of the whole target.
9. **Recommended next increment** — the concrete next migration step (e.g. widen the probe to more importers, then switch the product `platformFileName` and attempt UI compilation).

- [ ] **Step 2: Self-check against the Definition of Done**

Confirm each DoD row in the spec maps to a section above. Fix any gap inline.

- [ ] **Step 3: Commit**

```bash
git add docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md
git commit -m "docs: record hale-core platform spike findings and recommendation

Co-Authored-By: Claude Opus 4.8 (1M context) <noreply@anthropic.com>
Claude-Session: https://claude.ai/code/session_012TcpRazGeWywBc6aVGs7o7"
```

---

## Self-Review

**Spec coverage:**
- Pin tooling → Task 1 (mise) + Task 2 (bnd-platform 3.2.1). ✓
- Pin hale-core 6.4.1 → Task 2. ✓
- Resolve from BOM, no mavenLocal/snapshots → Task 2. ✓
- Generate p2 + target via entry point → Task 3. ✓
- Isolation constraint (no hale-platform checkout/feature) → Task 3 Step 2. ✓
- io.gml Tycho probe → Tasks 4 + 5. ✓
- bnd fixes documented → Task 5 + findings §5. ✓
- BOM alignment check → Task 6 + findings §4. ✓
- Confirm availability / blockers → findings §2 (evidence gathered in brainstorming). ✓
- Reproducible documented command + clean-cache challenge → Task 7. ✓
- Findings distinguish resolution vs runtime + next increment → Task 8 §8–9. ✓

**Placeholder scan:** Example bnd fix in Task 5 Step 2 is intentionally a template (the actual failure is unknown until Tycho reports it) — the surrounding steps make the discovery loop concrete. No other placeholders.

**Type consistency:** Artifact/module names consistent throughout: reactor `probe-parent`, bundle `eu.esdihumboldt.hale.platform.probe`, target `platform/local-platform.target`, entry command `./build/gradlew -p build generatePlatform`, wrapper `build/platform/validate-platform.sh`, findings path `docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md`.
