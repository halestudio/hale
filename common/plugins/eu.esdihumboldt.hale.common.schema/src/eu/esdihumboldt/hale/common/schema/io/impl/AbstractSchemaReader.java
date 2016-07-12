/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.schema.io.impl;

import eu.esdihumboldt.hale.common.core.io.impl.GZipEnabledImport;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Base implementation for {@link SchemaReader}s
 * 
 * @author Simon Templer
 */
public abstract class AbstractSchemaReader extends GZipEnabledImport implements SchemaReader {

	private TypeIndex sharedTypes;

	private SchemaSpaceID schemaSpace;

	private Boolean isSchemaNameQuoted;

	@Override
	public void setSchemaSpace(SchemaSpaceID schemaSpace) {
		this.schemaSpace = schemaSpace;
	}

	/**
	 * @return the schema space the loaded schema will be associated to
	 */
	public SchemaSpaceID getSchemaSpace() {
		return schemaSpace;
	}

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

	/**
	 * Set value of parameter that depicts Schema name with quotation or not.
	 * 
	 * @param isSchemaNameQuoted true or false
	 */
	public void setIsSchemaNameQuoted(boolean isSchemaNameQuoted) {
		this.isSchemaNameQuoted = Boolean.valueOf(isSchemaNameQuoted);
	}

	/**
	 * Get the boolean value of parameter that depicts SchemaName with quotation
	 * set or not.
	 * 
	 * @return boolean value true or false
	 */
	public boolean getIsSchemaNameQuoted() {
		if (isSchemaNameQuoted == null)
			return true;
		return isSchemaNameQuoted.booleanValue();
	}

}
