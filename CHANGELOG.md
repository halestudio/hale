# Change Log
All notable changes to this project will be documented in this file.
See the [change log guidelines](http://keepachangelog.com/) for information on how to structure the file.

## [Unreleased]

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
- PostgreSQL: classify columns with type `geometry` as geometry columns, even if there is no corresponding entry in the `geometry_columns` table
- Correctly use cursors in JDBC connections (loading tables in batches, not completely)

## 2.9.4 - 2015-11-01

Changes so far have been documented in the [hale help](http://hale.igd.fraunhofer.de/2.9.4/help/topic/eu.esdihumboldt.hale.doc.user/html/new/2_9_0.xhtml?cp=2_1_0).

[Unreleased]: https://github.com/halestudio/hale/compare/2.9.4...HEAD
