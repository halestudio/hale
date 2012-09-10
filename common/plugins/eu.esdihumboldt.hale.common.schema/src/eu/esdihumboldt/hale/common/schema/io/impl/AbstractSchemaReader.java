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

package eu.esdihumboldt.hale.common.schema.io.impl;

import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Base implementation for {@link SchemaReader}s
 * 
 * @author Simon Templer
 */
public abstract class AbstractSchemaReader extends AbstractImportProvider implements SchemaReader {

	private TypeIndex sharedTypes;

	/**
	 * @see SchemaReader#setSharedTypes(TypeIndex)
	 */
	@Override
	public void setSharedTypes(TypeIndex sharedTypes) {
		this.sharedTypes = sharedTypes;
	}

	/**
	 * Get the shared types
	 * 
	 * @return the shared types
	 */
	public TypeIndex getSharedTypes() {
		return sharedTypes;
	}

}
