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
 * Dedicated listener for {@link SchemaService} events
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public interface SchemaServiceListener {

	/**
	 * Called when a schema has been added to the source or target schema space.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 * @param schema the schema that was added
	 */
	public void schemaAdded(SchemaSpaceID spaceID, Schema schema);

	/**
	 * Called when the source or target schema have been removed from the
	 * project(resources) view.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public void schemaRemoved(SchemaSpaceID spaceID);

	/**
	 * Called when the source or target schema space have been cleared.
	 * 
	 * @param spaceID the schema space ID, either {@link SchemaSpaceID#SOURCE}
	 *            or {@link SchemaSpaceID#TARGET}
	 */
	public void schemasCleared(SchemaSpaceID spaceID);

	/**
	 * Called when the mappable flag of some types changed.
	 * 
	 * @param spaceID the schema space of the changed types
	 * @param types the changed types
	 */
	public void mappableTypesChanged(SchemaSpaceID spaceID,
			Collection<? extends TypeDefinition> types);
}
