OGC(r) WPS schema - ReadMe.txt
==============================

OpenGIS(r) Web Processing Service (WPS) Interface Standard 
-----------------------------------------------------------------------

The OpenGIS(r) Web Processing Service (WPS) Interface Standard provides
rules for standardizing how inputs and outputs (requests and responses)
for geospatial processing services, such as polygon overlay. The
standard also defines how a client can request the execution of a
process, and how the output from the process is handled. It defines an
interface that facilitates the publishing of geospatial processes and
clients’ discovery of and binding to those processes. The data required
by the WPS can be delivered across a network or they can be available at
the server.

The root (all-components) XML Schema Document, which includes
directly and indirectly all the XML Schema Documents, defined by
WPS 1.0.0 is wpsAll.xsd .

The WPS schema are described in the OGC WPS 1.0.0 document 05-007r7.

 Note: check each OGC numbered document for detailed changes.

More information may be found at
 http://www.opengeospatial.org/standards/wps

The most current schema are available at http://schemas.opengis.net/ .

-----------------------------------------------------------------------

2010-02-03  Kevin Stegemoller
	* v1.0.0: updated xsd:schema:@version to 1.0.0 2010-02-03 (06-135r7 s#13.4)
	* v1.0.0:
    + updated xsd:schema:@version attribute (06-135r7 s#13.4)
    + update relative schema imports to absolute URLs (06-135r7 s#15)
    + update/verify copyright (06-135r7 s#3.2)
    + add archives (.zip) files of previous versions
    + create/update ReadMe.txt (06-135r7 s#17)

2007-12-05  Peter Schut, WPS RWG
  * v1.0.0: error in ows/1.1.0 causes validation error see below 
	  or OGC 07-141

-----------------------------------------------------------------------

Policies, Procedures, Terms, and Conditions of OGC(r) are available
  http://www.opengeospatial.org/ogc/legal/ .

Copyright (c) 2010 Open Geospatial Consortium, Inc. All Rights Reserved.

-----------------------------------------------------------------------
-----------------------------------------------------------------------

CHANGES
=======

Change 07-141
-------------

There is a obsolete reference in the OWS Common 1.1.0
ExceptionReport.xsd schema which causes the WPS 1.0.0
examples/90_wpsExceptionReport.xml not to validate correctly in some
validators.  Below is the summary of the issue detailed in the OGC
Change Request 07-141.  The full Change Request 07-141 is available at
http://portal.opengeospatial.org/files/?artifact_id=24601

The current OWS Common 1.1.0 ExceptionReport.xsd schema references an
obsolete version of the XML schema, and therefore does not validate
properly if an XML validator actually attempts to import the XML
schema at this obsolete location.  As a consequence other OGC schemas
cannot import the exception report schema.  

In ows/1.1.0/owsExceptionReport.xsd 
replace
	<import namespace="http://www.w3.org/XML/1998/namespace"/>
with
	<import namespace=" http://www.w3.org/XML/1998/namespace"
		schemaLocation="http://www.w3.org/2001/xml.xsd"/>

 -- from Change Request OGC 07-141 by Peter Schut, WPS RWG

