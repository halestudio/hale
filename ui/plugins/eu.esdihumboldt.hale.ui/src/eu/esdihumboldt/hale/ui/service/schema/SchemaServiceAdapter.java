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
