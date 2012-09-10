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
