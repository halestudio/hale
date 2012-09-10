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

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Provides support for reading schemas
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface SchemaReader extends ImportProvider {

	/**
	 * Set the shared types. Shared types may originate from schemas that were
	 * loaded previously.
	 * 
	 * @param sharedTypes the shared types
	 */
	public void setSharedTypes(TypeIndex sharedTypes);

	/**
	 * Get the loaded schema
	 * 
	 * @return the schema
	 */
	public Schema getSchema();

}
