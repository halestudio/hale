#!/usr/bin/perl
#  2010-02-03 http://www.perlmonks.org/?node_id=490846
# see also http://stackoverflow.com/questions/2039143/how-can-i-access-attributes-and-elements-from-xmllibxml-in-perl

  use strict;
  use warnings;

  my $filename = 'wpsAll.xsd';

# For this example, we're going to locate the data for the book with ISBN
# 076455106X and correct its page count from 392 to 394:

my $string="foooooo";

  use XML::LibXML;

  my $isbn   = '076455106X';

  my $parser = XML::LibXML->new();
  my $doc    = $parser->parse_file($filename);

  my $query  = "/schema/\@version";
# /schema/@version

  my($node)  = $doc->findnodes($query);
  #$node->setData('394');
#  $node = $attr->getValue();
  $node = setValue( $string );
#  $node->setData('394');


#my $xc = XML::LibXML::XPathContext->new( $doc->documentElement()  );
$xc->registerNs('ns', 'http://moleculardevices.com/microplateML');

my $xc = XML::LibXML::XPathContext->new( $doc->documentElement()  );
$xc->registerNs('ns', 'http://moleculardevices.com/microplateML');

my @n = $xc->findnodes($query);
foreach $nod (@n) {
    print "A: ".$nod->getAttribute("name")."\n";

    my @c = $xc->findnodes("./ns:common-name", $nod);
    foreach $cod (@c) {
        print "B: ".$cod->nodeName;
        print " = ";
        print $cod->getFirstChild()->getData()."\n";
    }
}



  print $doc->toString;

exit

## same 
# 
# foreach my $title ($doc->findnodes('/library/book/title')) {
#     print $title->to_literal, "\n" 
#   }
# ==
# print $_->data . "\n" foreach ($doc->findnodes('//book/title/text()'));

# query book pages > 900 and print isbn
# xpath -q -e '//book[pages > 900]/isbn/text()' library.xml

__DATA__
<?xml version="1.0" encoding="UTF-8"?>
<schema xmlns="http://www.w3.org/2001/XMLSchema" xmlns:ows="http://www.opengis.net/ows/1.1" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:wps="http://www.opengis.net/wps/1.0.0" targetNamespace="http://www.opengis.net/wps/1.0.0" elementFormDefault="unqualified" xml:lang="en" version="1.0.0 2010-02-03">
	<annotation>
		<appinfo>$Id: wpsAll.xsd 2007-10-09 $</appinfo>
		<documentation>
			<description>This XML Schema includes and imports, directly and indirectly, all the XML Schemas defined by the WPS Implemetation Specification.</description>
			<copyright>
				WPS is an OGC Standard.
				Copyright (c) 2007,2010 Open Geospatial Consortium, Inc. All Rights Reserved.
				To obtain additional rights of use, visit http://www.opengeospatial.org/legal/ .
			</copyright>
		</documentation>
	</annotation>
	<!-- ==============================================================
		includes
	============================================================== -->
	<include schemaLocation="wpsDescribeProcess_request.xsd"/>
	<include schemaLocation="wpsDescribeProcess_response.xsd"/>
	<include schemaLocation="wpsExecute_request.xsd"/>
	<include schemaLocation="wpsExecute_response.xsd"/>
	<include schemaLocation="wpsGetCapabilities_request.xsd"/>
	<include schemaLocation="wpsGetCapabilities_response.xsd"/>
</schema>
