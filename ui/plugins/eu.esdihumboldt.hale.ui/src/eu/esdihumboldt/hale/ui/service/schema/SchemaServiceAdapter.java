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

package eu.esdihumboldt.hale.ui.service.schema;

import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Schema service listener adapter
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class SchemaServiceAdapter implements SchemaServiceListener {

	/**
	 * @see SchemaServiceListener#schemaAdded(SchemaSpaceID, Schema)
	 */
	@Override
	public void schemaAdded(SchemaSpaceID spaceID, Schema schema) {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener#schemaRemoved(eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public void schemaRemoved(SchemaSpaceID spaceID) {
		// override me
	}

	/**
	 * @see SchemaServiceListener#schemasCleared(SchemaSpaceID)
	 */
	@Override
	public void schemasCleared(SchemaSpaceID spaceID) {
		// override me
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.schema.SchemaServiceListener#mappableTypesChanged(eu.esdihumboldt.hale.common.schema.SchemaSpaceID,
	 *      java.util.Collection)
	 */
	@Override
	public void mappableTypesChanged(SchemaSpaceID spaceID,
			Collection<? extends TypeDefinition> types) {
		// override me
	}
}
