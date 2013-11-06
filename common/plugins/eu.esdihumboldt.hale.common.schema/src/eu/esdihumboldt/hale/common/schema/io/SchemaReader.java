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

package eu.esdihumboldt.hale.common.schema.io;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
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
	 * Set the schema space for which the schema is loaded.
	 * 
	 * @param schemaSpace the schema space
	 */
	public void setSchemaSpace(SchemaSpaceID schemaSpace);

	/**
	 * Get the loaded schema
	 * 
	 * @return the schema
	 */
	public Schema getSchema();

}
