hale studio [![Build Status](https://builds.wetransform.to/buildStatus/icon?job=hale/hale~publish(master))](https://builds.wetransform.to/job/hale/job/hale~publish(master)/)
====

(Spatial) data harmonisation with hale studio (formerly HUMBOLDT Alignment Editor)

hale studio is a desktop and server environment to analyse, transform and validate complex data sets. These are the top use cases it's applied to:

* Creation of INSPIRE data sets
* Creation of GML/CityGML/XPlanGML data sets
* Environmental reporting (Air Quality, WFS, CDDA, ...)
* Creation of Geoserver app-schema mappings
* Database-to-database migration
* Integration of data from multiple sources into harmonised, consistent data sets
* Assessment of migration risks, mismatches, data quality

![App](https://www.wetransform.to/images/halestudio/hale-complete.png "hale studio's main perspective")


## Features

* Real-time, interactive geodata transformation and validation
* Makes work with large and complex models easy
* SLD-Compatible Map display
* Support for all major databases (Oracle, PostgreSQL, SQL Server) and their spatial extensions
* Import and Export GML 3.2.1, SQLite, Shapefile, JSON/GeoJSON and many many other formats
* Create INSPIRE DataSets for Download Services, write to Transactional Web Feature Services
* Share Transfromation projects and collaborate to improve them with haleconnect.com integration
* Built on Java and Eclipse RCP


## Instructions

To start working with hale studio, download a [release](https://www.wetransform.to/downloads/) or current [development builds](https://builds.wetransform.to/job/hale/job/hale~publish(master)/).

To contribute please check the [contribution guidelines](CONTRIBUTING.md) and read here [how to set up your development environment](https://github.com/halestudio/hale/wiki/Set-up-your-development-environment).


## System Requirements

* Windows XP/Vista/7/8 32bit or 64bit
* MacOS X 10.5
* Linux 
* 4 GB RAM recommended

## Resources

* [hale User Guide](http://help.halestudio.org/)
* [hale Community Forum](http://discuss.wetransform.to)
* [hale Community Blog](https://www.wetransform.to/category/news/)
* [hale Video Tutorial (4 Minutes)](https://www.youtube.com/watch?v=95Krki4thgs)
* [hale in-depth Video Turorial (37 minutes)](https://www.youtube.com/watch?v=BKNMV-Jp9HM)

## License

* The main hale studio components/libaries are released under the GNU Lesser General Public License (LGPL) v3.0.
* The hale studio continuous delivery build (*hale-build* in **build/**) is released under the GNU General Public License (GPL) v3.0.
* Different licenses may apply to the extensions residing in **ext/**, please see the respective subfolders.
