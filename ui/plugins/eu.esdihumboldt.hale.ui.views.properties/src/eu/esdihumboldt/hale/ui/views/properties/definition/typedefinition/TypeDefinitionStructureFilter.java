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

package eu.esdihumboldt.hale.ui.views.properties.definition.typedefinition;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter;

/**
 * Filter that only accepts type definitions with childrens.
 * 
 * @author Simon Templer
 */
public class TypeDefinitionStructureFilter extends DefaultDefinitionFilter {

	/**
	 * @see DefaultDefinitionFilter#isFiltered(Definition)
	 */
	@Override
	public boolean isFiltered(Definition<?> input) {
		if (input instanceof TypeDefinition) {
			TypeDefinition type = ((TypeDefinition) input);
			return type.getChildren().isEmpty();
		}
		return true;
	}

}
