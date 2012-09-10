/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.schemaprovider;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;

/**
 * A {@link SchemaProvider} loads a schema from a given URI. A type that
 * implements this interface must have a default constructor
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
@Deprecated
public interface SchemaProvider {

	/**
	 * Method to load a XSD schema file and build a collection of
	 * {@link FeatureType}s.
	 * 
	 * @param location
	 *            URI which represents a file
	 * @param progress
	 *            the progress indicator, may be <code>null</code>
	 * @return the schema object containing the {@link FeatureType}s
	 * @throws IOException
	 *             if loading the schema fails
	 */
	public Schema loadSchema(URI location, ProgressIndicator progress)
			throws IOException;

	/**
	 * Determines if the schema provider supports the given schema format
	 * 
	 * @param schemaFormat
	 *            the schema format
	 * 
	 * @return true if the schema format is supported
	 */
	public boolean supportsSchemaFormat(String schemaFormat);

	/**
	 * Get the supported schema formats
	 * 
	 * @return the supported schema formats
	 */
	public Collection<? extends String> getSupportedSchemaFormats();

}
