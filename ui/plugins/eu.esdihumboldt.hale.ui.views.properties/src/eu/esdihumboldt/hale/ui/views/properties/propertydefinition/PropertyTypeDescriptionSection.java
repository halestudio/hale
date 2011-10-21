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

package eu.esdihumboldt.hale.ui.views.properties.propertydefinition;

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefinitionDescriptionSection;

/**
 * Properties section with description
 * @author Patrick Lieb
 */
public class PropertyTypeDescriptionSection extends DefinitionDescriptionSection{

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.DefaultDefinitionSection#setInput(java.lang.Object)
	 */
	@Override
	protected void setInput(Object input) {
		if (input instanceof PropertyEntityDefinition) {
			setDefinition(((PropertyEntityDefinition) input).getDefinition().getPropertyType());
		}
		else {
			setDefinition(((PropertyDefinition) input).getPropertyType());
		}
	}
}
