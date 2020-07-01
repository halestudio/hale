# Change Log
All notable changes to this project will be documented in this file.
See the [change log guidelines](http://keepachangelog.com/) for information on how to structure the file.

## [4.0.0]

### Added

- Support for reading and writing GeoPackage files
- Improved support for writing XPlanGML files ([#814](https://github.com/halestudio/hale/issues/814))
- XPlanGML schema presets ([#799](https://github.com/halestudio/hale/issues/799))
- Additional AAA NAS schema presets ([#615](https://github.com/halestudio/hale/issues/615))
- TN-ITS schema preset
- ISO 19139 GMD schema preset
- Support for comparing Integer and Float values in a Join ([#737](https://github.com/halestudio/hale/issues/737))
- Support for MongoDB URI scheme when automatically updating paths ([#762](https://github.com/halestudio/hale/issues/762))
- Continuous integration with Travis CI
- Support for loading hale connect projects in headless environment
- Ability to filter spatial index query by type in Groovy scripts
- New options in the deegree configuration exporter to allow inclusion of abstract and feature collection types in mappings
- Groovy wrapper function for parsing JSON data
- Allow use of `java.util.TreeMap` in Groovy scripts

### Changed

- Package OpenJDK binaries instead of Oracle ones ([#758](https://github.com/halestudio/hale/issues/758))
- Upgrade GeoTools dependency to 21.0 ([#821](https://github.com/halestudio/hale/issues/821))
- Upgrade RCP to Eclipse 4.15 (2020-03) ([#822](https://github.com/halestudio/hale/issues/822))
- Limit supported TLS version in hale connect integration to v1.2 and cipher suites to those with perfect forward security

### Removed

- Support for 32-bit builds
- Server product

### Fixed

- Fix possible deadlock when copying XML schemas with complete dependencies
- Prevent possible `NullPointerException` in snippet service in headless environment
- Don't fail spatial index query if an instance can't be resolved
- Don't fail deegree export when the Primitive option `All link where code lists are assigned in hale` is selected but there are no code lists in the alignment
- Fix for GML reader to correctly recognize certain elements ([#764](https://github.com/halestudio/hale/issues/764))
- Fix display problems in hale connect integration ([#752](https://github.com/halestudio/hale/issues/752))
- Fix problem with stale cache in hale connect integration
- Fix problem in headless environment that lead to duplication of instances in the transformation output ([#774](https://github.com/halestudio/hale/issues/774))
- Allow type filtering of `LimboInstanceSink` instance collections
- Fix parsing of XML value lists ([#808](https://github.com/halestudio/hale/issues/808))
- Fix rendering of target geometries with default styling ([#732](https://github.com/halestudio/hale/issues/732))
- Fix parsing of GML dictionaries ([#824](https://github.com/halestudio/hale/issues/824))
- Fix code list import where all XML files were being incorrectly detected as SKOS code lists

## [3.5.0]

### Added

- Deegree feature store configuration export
- GML partitioning by spatial extent
- Support for plugin installation via update sites
- Improved Xtraserver export
- Migration support for join conditions and context filters
- Support for alignments with inline transformations in headless mode
- Added geometry metadata to Property Type section of Properties view
- Summary in transformation reports
- Support for matching shortened Shapefile properties

### Changed

- Upgraded RCP to Eclipse 4.8 (Photon)
- Upgraded Groovy to 2.4
- Upgraded PostgreSQL driver to 42.2.4, PostGIS driver to 2.2.1
- Added JTS Geometry classes to Groovy white list

### Removed

- Moved App-Schema configuration export to external plugin

### Fixed

- Fixed file names when partitioning by feature type in a GML export
- Improved performance for inline transformations where inlined transformation contains Groovy scripts
- Prevent change of project resources paths if project is exported to hale connect
- Prevent removal of existing source data when loading additional source data
- Allow loading hale schema definition file even if contraint type can't be recreated
- Ensure spatial index is always available in CLI transformations
- Fixed listing of hale connect projects
- Fixed application freezes on macOS

## [3.4.1]

### Changed

- Updated GML XSD to version 3.2.2

### Fixed

- Fixed problems with AppSchema configuration dialog on Windows
- Allow loading the same Excel lookup table multiple times
- Support partitioning by feature type when transforming external data
- Support srsDimension attribute also at coordinates level
- Fixed Compatibility Mode toolbar on Windows
- Removed invalid AdV CRS code mappings
- Fixed possible `NullPointerException`s in GML export, lookup tables, transformation
- Added missing configuration page to wizard for Excel lookup tables
- Fixed number and length validators to support value lists
- Prevent transformation run when sampling causes reloading of source data
- Force replacement of `\r` line endings in Groovy scripts

## [3.4.0]

### Added

- Support for isolated workspaces to App-Schema plugin
- XtraServer configuration plugin
- Support for merging alignments and viewing related tasks
- Allow to split GML output by feature type
- Capability to import Groovy snippets and use them in transformation scripts
- Preset for AAA XML schema
- Support for several AdV CRS codes
- Option to ignore the total number of features reported by a WFS
- Option to format non-integer decimals in XML/GML output
- Support for replacing source and target entities of a cell
- CLI option to output transformation statistics
- Capability to define custom transformation success conditions based on statistics
- Support to access the same property on all children in Groovy scripts

### Changed

- Allow ".txt" extension for CSV files
- Preserve annotations, ID and re-use existing functions parameters when replacing a cell
- Retain Join configuration when adding/removing types
- Allow to skip entities in remapping wizard
- Added warning to CRS selection dialog if WKT does not contain Bursa-Wolf parameters

### Fixed

- Fixed hale connect integration when using a proxy
- Fixed hale connect project list and versioning support
- Fixed hale connect integration if user is a member of multiple organisations
- Fixed opening hale connect project with subfolders
- Fixed CLI transformations when source data contains unknown or invalid CRS definitions
- Fixed fallback mechanism in index merge handler
- Fixed Spatialite export to destinations when destination path contains spaces
- Apply proxy settings also to HTTPS connections
- Fixed Spatial Index Groovy helper function
- Fixed that defaultSrs parameter of XML/GML readers had no effect in CLI
- Fixed that transformation runs were being triggered without data and/or multiple times during project load
- Fixed that compression could not be used when partitioning GML output

## [3.3.2]

### Added

- Instance index to improve execution of Merge and Join transformations
- Enhanced CRS detection when parsing GML files
- When importing a shapefile resource, prefill character set dialog with encoding read from accompanying `.cpg` file
- Added support for multiple organisations to hale connect integration
- Support to automatically update Join and Merge properties in case of a schema remapping
- Support for ECQL expressions in filters and condition contexts

### Changed

- Partitioning modes `none`, `cut` and `related` for GML output
- Support for `noNamespaceSchemaLocation` in GML output
- Support for loading XLS files multiple times
- Support relogin to hale connect without having to clear stored credentials
- Limit number of messages per message type in a report
- Groovy scripts: Whitelisted use of `java.sql.Date` as well as classes needed for creating geometry properties
- Updated default hale connect endpoints

### Fixed

- Fixed opening a project file on launch (e.g. via double-clicking from a file explorer)
- Allow removing a previously assigned code list
- Fixed automatic resource path update to also work with URIs w/ a query part
- Fixed hale connect login on Welcome Page to work for user names and passwords w/ special characters
- Fixed the CRS definition lookup when importing shapefiles, allowing for automatic detection of CRS details (Bursa-Wolf parameters)
- Fixed application of Groovy restrictions when loading a project
- Fixed handling of JDBC collection sizes

## [3.3.1]

### Added

- Support for saving changes directly to hale connect
- Support for partitioning GML output to multiple files
- Support for table type `MATERIALIZED VIEW` when importing a PostgreSQL database schema
- Check for remote changes when sharing project to hale connect  
- Support for `Double` columns for the CSV reader
- GML reader parameters `ignoreMappingRelevant` and `suppressParsingGeometry`
- Property constraint `CodeListAssociation`
- Type constraint `MappingRelevantIfFeatureType`

### Fixed

- Opening projects that have MS Access database resources
- `IndexOutOfBoundsException` when calling Groovy helper functions
- Do not add `STARTINDEX` parameter to non-paginated WFS `GetFeature` requests
- Loading resources in headless mode from URL when remote server responds with a redirect
- Loading a project in headless mode no longer fails in cases where code lists cannot be imported
- Loading INSPIRE schemas from local resources when online version is not available
- New projects could be saved only as a project archive if the last project loaded was an archive

## [3.3.0]

### Added

- Integration with the online collaboration platform hale connect: log in to hale connect, import shared transformation projects and upload projects.
- Spatial Index for instances with geometries that can be queried via the new Groovy geometry helper "spatialIndexQuery"
- Spatial Join transformation function: join instances based on the spatial relation of their geometry properties
- Groovy geometry helper function "boundaryCovers" that can be used to determine if the boundary of a the geometry covers all points of a line
- Use arbitrary SQL queries as a source schema and data source
- Import/Export hale schema definitions as JSON
- DMG image for macOS installation

### Changed

- Application title is now "hale studio"
- "Load project from templates..." has been removed in favour of hale connect integration
- Cached schema definition is now used always if loading source fails

### Fixed

- Fixed content assistance in RegEx Analysis function
- Fixed resource copying in hale Project Archive writer
- Fixed links on About screen
- Fixed updating a cell when the source or target types are changed to a parent of the original type

## [3.2.0]

### Added

- Added support for several Arc-based GML geometry types to be interpolated when read: Arc, ArcString, Circle, CircleByCenterPoint. The interpolation is based on one of two algorithms that can be selected on import.
- Project Validator that validates exported instances based on validator configuration (e.g. rules or schemas) imported into the project
- New transformation function `Assign collected values` allows the assignment of all values collected by a Groovy transformation function. The new function automatically converts collected values to references if the target property takes references.
- Better usage of available space in Alignment and Mapping views
- Content assistance for project variables in transformation function wizards such as Formatted String, Regex Analysis and Assign
- Request pagination for WFS requests. Users can now choose to activate request pagination for WFS sources.
- IO Provider extensions can now have a configurationContentType to describe the content type of configuration files for this provider
- Total number of imported instances is now shown in progress dialog (if known)
- The `InstanceResolver` interface has been extended to allow resolving multiple references at once. Implementations can use this to optimize resolving of multiple references

### Changed

- The HTML documentation that can be generated for an alignment is now much more performant for large mappings due to lazy loading and rendering
- The Merge function now uses an iterative approach for merging instances which allows for processing more data in a Merge
- Allow using `SimpleDateFormat` and `UUID` classes in groovy scripts by default
- When loading data from CSV files the data is now streamed (similar to XML data sources) and not loaded at once into memory
- When a CSV files has more columns than defined in the schema, this is now a warning, not an error

### Fixed

- Deselecting in a type selector could lead to an exception
- Removed CRS selection dialog and UI dependency from MS SQL plugin
- Schema selection configuration for JDBC driver is optional
- Fixed wrong tooltip in Mapping view
- Fixed error when loading hale schema definitions in respect to schema elements w/ primitive bindings


## [3.1.0]

### Added

- Support reading from and writing to MS SQL databases
- Instance counts are now calculated for condition and index contexts as well
- You can now hide optional properties in the schema explorer
- SKOS format code lists can now be loaded
- Validation based on a Schematron file can now be performed on an encoded XML/GML file
- To ensure topological consistency in respect to interpolated geometries, other geometries may optionally also be moved to the interpolation grid
- GML Encoding: It is now possible to specify a number format for geometry ordinates, e.g. if you want a fixed precision after the decimal point
- During validation in hale also check property values against an assigned code list
- CLI commands can now be marked experimental
- Added extension point for custom validators for the hale pre-encoding validation

### Changed

- GML encoding: Automatic conversion of polygon geometries to line geometries when there are no possibilities to encode a surface has been changed to produce a *LineString* for a *Polygon* and a *MultiLineString* for a *MultiPolygon*
- The contents of XML Alignment files now are sorted where possible, to have a reproducable encoding for the same mapping and a nice diff when used in version control
- The contents of Hale Schema Definition files now are sorted where possible, to have a reproducable encoding for the same schema and a nice diff when used in version control
- When reading GML geometries composite 2D geometries (e.g. CompositeSurface, Surface with multiple patches, CompositeCurve, etc.) are now by default combined to a single geometry if possible

### Removed

- File based databases can no longer be loaded via *From database* - instead use *From file*

### Fixed

- Using a previously as Hale Schema Definition file exported database schema as source schema when loading data from the database now properly supports loading the geometries
- Using the value `unpopulated` for GML *nilReason* attributes does not conform to the GML specification. The proposal to use this value has been changed to `other:unpopulated` to conform with the specification. Also, when encoding GML, `unpopulated` will be replaced by `other:unpopulated` where encountered in *nilReason* attributes to support mappings created in previous versions
- Fixed error in instance partitioning for WFS-T upload when encountering unresolvable references
- Fixed instance partitioning for WFS-T upload producing too many small parts
- Resolving local resources via bundles can no longer yield streams to directories/packages
- Groovy geometry helper functions don't fail for null input
- Exporting project archives for new projects now works as expected
- On export to GeoServer AppSchema via the REST endpoint the provided URL now may end with a slash
- Mapping cells in the mapping view are now ensured to be updated when edited or deleted
- When exporting data to XML files include root element schema in schema location attribute

### Deprecated

- Deprecated old style HTML mapping documentation, instead use the new HTML+SVG mapping documentation
- Deprecated INSPIRE 3.0 specific mapping functions (INSPIRE Identifier and Geographical Name), instead map to sub-properties individually

## [3.0.0]

### Added
- Users can now configure custom maps based on a Tile URL pattern or a Web Map Service
- Generic command line interface with commands that can be added via an extension point, added commands for documentation generation
- Transformation command line interface: Data included as source for the transformation using the now can be filtered, also arguments can now be passed to the command line interface using an arguments file
- MS Access database reader for `.mdb` files based on [UCanAccess](https://sourceforge.net/projects/ucanaccess/)
- Three new priority levels for mapping cells
- `Collector` helper class for easily collecting information or building indexes in Groovy functions, also added a helper function that will create a default collector in a transformation context
- Internal instance validation now includes validation of local XLink references. Local references that cannot be resolved result in a warning
- hale checks the version of a project that is loaded and displays a warning when the project was created with a newer version of hale than the one currently used
- Support for project variables has been added, that allow customizing the behavior of an alignment. The variables can be defined and stored in the project, and overridden for transformation execution
- SHA-256 Groovy helper function
- Several Groovy helper functions for dealing with and creating geometries
- Users can now define custom transformation functions (using Groovy) that are stored in the alignment
- For XML Schema it is now possible to also map types w/o global element definition (option on schema import)
- *Interior Point* transformation function that determines a point that us guaranteed to lie inside a geometry
- Support for 1-dimensional arrays as multi-valued properties for JDBC schema and data sources (only tested with PostgreSQL)
- `XmlSchemaReader` can be configured with specific content for anyType elements (to be able to map them properly; not configurable via the UI)
- WFS-T writers now can be configured for services protected with HTTP Basic Authentication
- New `decimal` parameter added to identify the float value in CSV file with specified separator

### Changed
- New default base map is [Stamen Terrain](http://maps.stamen.com/terrain/#3/42.62/15.29)
- The WFS 2.0 Feature Collection writer can now be configured to skip counting features (allowing direct streaming instead of temporary storing all features)
- Mapping cells are now sorted in a specific order when they are saved, to have easier understandable diffs when using `.halex` projects in version control
- Writing XmlDateTime values when encoding XML now uses a fixed default timezone (UTC) instead of the system time zone
- Type transformations are now executed in order according to their configured priority (this means that for instance a type transformation w/ high priority is guaranteed to be completely executed before a type transformation with normal priority)
- Several improvements to the HTML+SVG mapping documentation
- The winding order of GML geometries is now fixed when encoding a GML file, also the behavior regarding winding order is now configurable
- Groovy restrictions: Access to the `QName` class is now allowed by default
- Process URI and URL values as Strings when comparing keys in a *Join*
- Mapping cell explanations can now be available in multiple languages, by default English and German are supported
- Groovy script input variables are no longer converted to Strings to allow handling instances and other kinds of objects
- An encoding can now be specified when loading a schema from a Shapefile
- hale now requires Java 8
- hale is now based on Eclipse Mars (from Eclipse Luna)
- hale libraries can now be used on a non-OSGi environment (this implies a lot of internal changes regarding service provision and discovery)
- Groovy type transformations now may return multiple results with multiple `_target` closures
- Proxy credentials where the user name includes a backslash (`\`) are interpreted as NTLM credentials with the user name specifying both domain and user
- XML/GML writers will write `nilReason` attributes only if the element is actually nil

### Removed
- The OpenStreetMap based map provided by MapQuest has been removed as the service is [no longer available](http://devblog.mapquest.com/2016/06/15/modernization-of-mapquest-results-in-changes-to-open-tile-access/) (see also #64)

### Fixed
- Errors when converting the alignment model on saving will no longer result in empty files overriding your previously stored alignment
- Time information for dates no longer is lost when stored in the internal database
- Concatenating a Ring geometry to a LinearRing will no longer result in duplicate coordinates
- Concurrent access to the same source instances could result in exceptions and invalid objects being transformed (see #96)
- Drastically reduced threads created by `FinalizableReferenceQueue`s for internal database handle cleanup
- PostGIS: classify columns with type `geometry` as geometry columns, even if there is no corresponding entry in the `geometry_columns` table
- PostGIS: assume lon/lat axis order instead of lat/lon for geographic coordinate reference systems
- Correctly use cursors in JDBC connections (loading tables in batches, not completely)
- Returning muliple instances from Groovy functions now always uses the correct bindings

## 2.9.4 - 2015-11-01

Changes so far have been documented in the [hale help](http://hale.igd.fraunhofer.de/2.9.4/help/topic/eu.esdihumboldt.hale.doc.user/html/new/2_9_0.xhtml?cp=2_1_0).

[4.0.0]: https://github.com/halestudio/hale/compare/3.5.0...4.0.0
[3.5.0]: https://github.com/halestudio/hale/compare/3.4.1...3.5.0
[3.4.1]: https://github.com/halestudio/hale/compare/3.4.0...3.4.1
[3.4.0]: https://github.com/halestudio/hale/compare/3.3.2...3.4.0
[3.3.2]: https://github.com/halestudio/hale/compare/3.3.1...3.3.2
[3.3.1]: https://github.com/halestudio/hale/compare/3.3.0...3.3.1
[3.3.0]: https://github.com/halestudio/hale/compare/3.2.0...3.3.0
[3.2.0]: https://github.com/halestudio/hale/compare/3.1.0...3.2.0
[3.1.0]: https://github.com/halestudio/hale/compare/3.0.0...3.1.0
[3.0.0]: https://github.com/halestudio/hale/compare/2.9.4...3.0.0
