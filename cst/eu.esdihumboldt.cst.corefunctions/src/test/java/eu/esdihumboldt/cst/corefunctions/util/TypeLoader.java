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
package eu.esdihumboldt.cst.corefunctions.util;

import java.net.URI;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * The type loader is a utility class that can be used to get an actual type 
 * definition from a GML Application Schema, so that one doesn't have to create 
 * it manually in a test (which is also error-prone).
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class TypeLoader {
	
	/**
	 * Retrieve a feature type with a given local name from the GML application 
	 * schema indicated by its path.
	 * 
	 * @param localname
	 * @param schemaFilename
	 * @return the feature type
	 */
	public static FeatureType getType(String localname, String schemaFilename) {
		ApacheSchemaProvider asp = new ApacheSchemaProvider();
		FeatureType type = null;
		try {
			Schema schema = asp.loadSchema(new URI(schemaFilename), null);
			if (schema != null) {
				for (SchemaElement element : schema.getElements().values()) {
					if (element.getFeatureType() != null) {
						if (localname.equals(element.getElementName().getLocalPart())) {
							type = element.getFeatureType();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Parsing the schema Filename to a URI " + //$NON-NLS-1$
					"failed.", e); //$NON-NLS-1$
		}
		return type;
	}

}
