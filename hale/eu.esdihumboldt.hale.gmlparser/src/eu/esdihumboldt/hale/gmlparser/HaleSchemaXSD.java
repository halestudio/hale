/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlparser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.eclipse.xsd.XSDSchema;
import org.geotools.data.DataUtilities;
import org.geotools.gml3.GML;
import org.geotools.xml.SchemaLocationResolver;
import org.geotools.xml.XSD;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class HaleSchemaXSD extends XSD {

	/** application schema namespace */
    private String namespaceURI;

    /** location of the application schema itself */
    private String schemaLocation;

    public HaleSchemaXSD(String namespaceURI, String schemaLocation) {
        this.namespaceURI = namespaceURI;
        this.schemaLocation = schemaLocation;
    }

    protected void addDependencies(Set dependencies) {
        dependencies.add(GML.getInstance());
    }

    public String getNamespaceURI() {
        return namespaceURI;
    }

    public String getSchemaLocation() {
        return schemaLocation;
    }
	
	public SchemaLocationResolver createSchemaLocationResolver() {
        return new SchemaLocationResolver(this) {
        	
                public String resolveSchemaLocation(XSDSchema schema, String uri, String location) {
                    String schemaLocation;

                    if (schema == null) {
                        schemaLocation = getSchemaLocation();
                    } else {
                        schemaLocation = schema.getSchemaLocation();
                    }

                    String locationUri = null;

                    if ((null != schemaLocation) && !("".equals(schemaLocation))) {
                        String schemaLocationFolder = schemaLocation;
                        int lastSlash = schemaLocation.lastIndexOf('/');

                        if (lastSlash > 0) {
                            schemaLocationFolder = schemaLocation.substring(0, lastSlash);
                        }

                        if (schemaLocationFolder.startsWith("file:")) {
                            try {
                                schemaLocationFolder = DataUtilities.urlToFile(
                                        new URL(schemaLocationFolder)).getPath();
                            } catch (MalformedURLException e) {
                                // this can't be a good outcome, but try anyway
                                schemaLocationFolder = schemaLocationFolder.substring("file:".length());
                            }
                        }

                        File locationFile = new File(schemaLocationFolder, location);

                        if (locationFile.exists()) {
                            locationUri = locationFile.toURI().toString();
                        }
                    }

                    if ((locationUri == null) && (location != null) && location.startsWith("http:")) {
                        locationUri = location;
                    }
                    
                    java.net.URI u;
					try {
						u = new java.net.URI(locationUri);
						u = u.normalize();
	                    locationUri = u.toString();
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
                    return locationUri;
                }
            };
    }

}
