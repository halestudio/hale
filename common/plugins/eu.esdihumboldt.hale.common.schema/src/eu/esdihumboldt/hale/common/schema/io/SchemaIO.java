/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.schema.io;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.HaleIO;

/**
 * Schema I/O utilities
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.5
 */
public abstract class SchemaIO {

	/**
	 * ID of the action to load a source schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_SOURCE_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.source";

	/**
	 * ID of the action to load a target schema. Reflects the ID defined in the
	 * extension.
	 */
	public static final String ACTION_LOAD_TARGET_SCHEMA = "eu.esdihumboldt.hale.io.schema.read.target";

	/**
	 * Creates a schema reader instance
	 * 
	 * @param contentType the content type the provider must match, may be
	 *            <code>null</code> if providerId is set
	 * @param providerId the id of the provider to use, may be <code>null</code>
	 *            if contentType is set
	 * @return the I/O provider preconfigured with the content type if it was
	 *         given or <code>null</code> if no matching I/O provider is found
	 */
	public static SchemaReader createSchemaReader(IContentType contentType, String providerId) {
		return HaleIO.createIOProvider(SchemaReader.class, contentType, providerId);
	}

}
