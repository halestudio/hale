# hale-core BOM-aligned Target Platform — Spike Design

**Status:** approved (brainstorming), 2026-09-10
**Branch:** `hale-core-platform`
**Type:** spike (feasibility validation with persistent build changes + external review)

## Purpose

Validate that the `hale-core-platform` approach can provide a **reproducible, BOM-aligned
target platform** for hale studio using bnd-platform: pin the tooling and the agreed
hale-core version, resolve the platform purely from the hale-core BOM (no locally published
artifacts, no snapshot build plugins), generate a p2 repository + target definition through
the normal build entry point, and prove that a Tycho probe containing
`eu.esdihumboldt.hale.io.gml` and its required dependencies resolves against that target.

The output is an **answer plus a recommendation** for continuing the migration, backed by a
reproducible command. Full UI compilation and product assembly are explicitly follow-up work.

## Agreed decisions

| Decision | Value | Notes |
|---|---|---|
| hale-core version | **6.4.1** (latest release) | BOM + features + `io.gml` confirmed present on `artifactory.wetransform.to/artifactory/local` (HTTP 200), 2026-09-10 |
| bnd-platform plugin | **3.2.1** (release) | Replaces `3.2.0-SNAPSHOT` from `mavenLocal()`. 4.0.0 exists but is a major bump — recorded as a follow-up upgrade, not in scope |
| Build entry point | Gradle `generatePlatform` (unchanged path) + a thin wrapper | `validate-platform.sh` runs `./gradlew … generatePlatform` then the Tycho probe |
| Toolchain pinning | **mise**: `java = "temurin-17"`, `maven = "3.9.6"` | Matches CI (`.github/workflows/check.yml`). Gradle stays on the wrapper (`8.14.5`) |

## Scope

**In scope**
- Pin tooling (mise) and hale-core version (6.4.1).
- Remove `mavenLocal()` and the Sonatype **snapshots** repo from `build/platform/build.gradle` buildscript; resolve everything from release repositories only.
- Resolve the platform from the hale-core BOM (`platform("eu.esdihumboldt.hale:bom:6.4.1")`).
- Generate the p2 repository (`updateSite`) and `platform/local-platform.target` through the normal Gradle entry point (`build/build.gradle:generatePlatform`).
- Create a minimal Tycho probe reactor resolving `eu.esdihumboldt.hale.io.gml` against the generated target; apply the bnd fixes required to make it resolve.
- Check resolved dependency versions against the hale-core BOM; document intentional deviations and required bnd fixes.
- Produce a findings document: confirmed availability, the reproducible command, BOM deviations, required bnd fixes, remaining blockers, and the recommended next migration increment.

**Out of scope (follow-up)**
- Full UI compilation, product assembly, and switching the product build's `platformFileName` from `hale-platform` to the generated target.
- Upgrading bnd-platform to 4.0.0.
- Runtime validation (the probe proves *target resolution*, not runtime behaviour).

## Hard constraints

1. The generated target **must not** depend on a local hale-platform checkout or its published
   `eu.esdihumboldt.hale.platform` p2 feature. `local-platform.target` must reference only the
   locally generated `updateSite` plus the remote Eclipse p2 mirrors already in
   `base-platform.target` (EMF/Xtext/ECF from `build-artifacts.wetransform.to`) — those mirrors
   are not the hale-platform feature and stay.
2. No `mavenLocal()`, no snapshot plugin, no snapshot repositories in the platform build.
3. Availability of the agreed hale-core artifacts is confirmed **before** relying on them;
   any unavailable artifact is recorded as a blocker. (6.4.1 BOM/features/io.gml confirmed.)

## Design

### B. Tooling & version pinning
- **`mise.toml`** (repo root, new): `[tools] java = "temurin-17"`, `maven = "3.9.6"`.
- **`build/platform/build.gradle`**:
  - `version = '6.4.1'`.
  - buildscript dependency `org.standardout:bnd-platform:3.2.0-SNAPSHOT` → `3.2.1`.
  - Remove `mavenLocal()` and the `oss.sonatype.org/.../snapshots` repo from the buildscript block;
    remove the now-unnecessary `configurations.all { resolutionStrategy.cacheChangingModulesFor 0 }`.
  - Keep release repos: Gradle Plugin Portal, wetransform artifactory `local`, Maven Central, OSGeo.

### C. Target generation wired to the entry point
- Generation path is unchanged and *is* the normal entry point:
  `build/build.gradle:generatePlatform` (GradleBuild) → `build/platform` `updateSite` (p2 repo)
  → `generateTargetFile` → `platform/local-platform.target`.
- Verify `local-platform.target` references the locally generated `updateSiteDir` and the
  hale library feature `eu.esdihumboldt.hale.platform.feature.group` produced *by this build*,
  with no reference to a published hale-platform feature or a local checkout (constraint 1).
- `io.gml` and `io.gml.geometry` ship inside `eu.esdihumboldt.hale.io.feature.core`, which the
  `halecore` configuration already lists — so the generated site contains io.gml with no extra
  dependency.
- Product build's `platformFileName` stays `hale-platform` (switching is follow-up).

### D. The probe (Tycho reactor)
- **`build/platform/probe/`** (new): a minimal Tycho reactor —
  - a parent `pom.xml` with `tycho-maven-plugin` (extensions), `target-platform-configuration`
    pointing at `platform/local-platform.target`, and an `eclipse-plugin` module;
  - one empty consumer bundle (`eu.esdihumboldt.hale.platform.probe`) with a `MANIFEST.MF`
    declaring `Require-Bundle: eu.esdihumboldt.hale.io.gml` and no code.
- `mvn -f build/platform/probe/pom.xml -B package` succeeds only if io.gml's full transitive
  requirement chain resolves against the generated target. Tycho reports the first unsatisfiable
  requirement chain, which is how each required bnd fix is discovered.

### E. BOM alignment verification
- The `halecore` configuration already imports `platform("eu.esdihumboldt.hale:bom:6.4.1")`.
- Capture resolved versions (`./gradlew :platform:dependencies` / `dependencyInsight`) and diff
  key libraries against the BOM's managed versions.
- If POM-only BOMs imported transitively (e.g. `jackson-bom`) fail to contribute constraints,
  that surfaces here as a config gap to fix (e.g. the Gradle `jvm-ecosystem` platform behaviour) —
  discovered by evidence, not assumed.
- Each divergence is recorded as either an **intentional deviation** (with reason) or a **bnd fix**.

### F. Reproducibility — the documented command & caches
- **`build/platform/validate-platform.sh`** (new): runs under mise; step 1
  `./gradlew … generatePlatform` (clean); step 2 `mvn -f build/platform/probe/pom.xml -B package`.
- "Empty task-specific dependency caches" is scoped to explicit paths the reviewer clears before
  the challenge (the platform build's Gradle caches and the probe's local Maven/Tycho p2 cache),
  documented in the findings so the clean-checkout challenge is deterministic.
- Any environment workaround required merely to *run* bnd-platform (e.g. a JVM/arch quirk) is
  discovered fresh during execution and documented — none is presumed by this design.

### G. bnd fixes & findings
- Existing bnd fixups in `build/platform/build.gradle` are retained; new ones are added only as the
  probe/BOM check demands, each with a one-line rationale in the gradle file.
- **Findings document** (`docs/superpowers/specs/2026-09-10-hale-core-platform-target-findings.md`)
  records: confirmed availability (6.4.1 BOM/features/io.gml ✓), the reproducible command and cache
  paths, resolved-vs-BOM deviations, required bnd fixes, remaining blockers, and a recommendation
  for the next migration increment. It explicitly distinguishes **target resolution** (what the
  probe proves) from **runtime validation** (out of scope).

## Definition of done (mapping)

| DoD item | Satisfied by |
|---|---|
| Spike implementation + findings reviewed externally | Process: external review of the branch + findings doc (G) |
| Documented command challenged from clean checkout + empty caches; generates p2 repo + target, then resolves io.gml probe through Tycho | `validate-platform.sh` (F) + generation (C) + probe (D) |
| Resolved versions checked against the BOM; intentional deviations + required bnd fixes documented | BOM diff (E) + findings (G) |
| Findings distinguish target resolution from runtime validation and identify the next increment | Findings doc (G) |

## Risks / open unknowns (to be resolved by evidence during execution)
- Whether dropping the `3.2.0-SNAPSHOT` for `3.2.1` regresses any behaviour the local snapshot carried (discover via probe).
- Whether transitive POM-only BOMs contribute version constraints under the pinned setup (E).
- Known-suspect chains outside io.gml's requirements (e.g. GeoTools version ranges) are out of the
  probe's scope but recorded as blockers if observed.
