# Change Log
All notable changes to this project will be documented in this file.
See the [change log guidelines](http://keepachangelog.com/) for information on how to structure the file.

## [Unreleased]

## [3.2.0]

### Added

- Added support for several Arc-based GML geometry types to be interpolated when read: Arc, ArcString, Circle, CircleByCenterPoint. The interpolation is based on one of two algorithms that can be selected on import.
- Project Validator that validates exported instances based on validator configuration (e.g. rules or schemas) imported into the project
- New transformation function `Assign collected values` allows the assignment of all values collected by a Groovy transformation function. The new function automatically converts collected values to references if the target property takes references.
- Better usage of available space in Alignment and Mapping views
- Content assistance for project variables in several UI wizards
- Request pagination for WFS requests. Users can now choose to activate request pagination for WFS sources.
- IO Provider extensions can now have a configurationContentType to describe the content type of configuration files for this provider
- Total number of imported instances is now shown in progress dialog (if known)
- The `InstanceResolver` interface has been extended to allow resolving multiple references at once. Implementations can use this to optimize resolving of multiple references

### Changed

- The HTML documentation that can be generated for an alignment is now much more performant for large mappings due to lazy loading and rendering
- `Validator rules` are now called `Validator configurations`
- The Merge function now uses an iterative approach for merging instances which allows for processing more data in a Merge
- Allow using `SimpleDateFormat` and `UUID` classes in groovy scripts by default
- When loading data from CSV files the data is now streamed (similar to XML data sources) and not loaded at once into memory
- When a CSV files has more columns than defined in the schema, this is now a warning, not an error

### Fixed

- Prevent multiple message boxes during validation when multiple validators are executed
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

[Unreleased]: https://github.com/halestudio/hale/compare/3.2.0...HEAD
[3.2.0]: https://github.com/halestudio/hale/compare/3.1.0...3.2.0
[3.1.0]: https://github.com/halestudio/hale/compare/3.0.0...3.1.0
[3.0.0]: https://github.com/halestudio/hale/compare/2.9.4...3.0.0
