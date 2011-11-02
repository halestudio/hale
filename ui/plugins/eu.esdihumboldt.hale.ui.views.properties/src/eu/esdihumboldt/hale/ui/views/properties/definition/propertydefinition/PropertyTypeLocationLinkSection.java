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

package eu.esdihumboldt.hale.ui.views.properties.definition.propertydefinition;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionSection;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefinitionLocationLinkSection;

/**
 * Properties section with a link to open the location in editor or browser
 * @author Patrick Lieb
 */
public class PropertyTypeLocationLinkSection extends DefinitionLocationLinkSection{

	/**
	 * @see DefaultDefinitionSection#setInput(Object)
	 */
	@Override
	protected void setInput(Object input) {
		super.setInput(input);
		
		setDefinition(((PropertyDefinition) getDefinition()).getPropertyType());
	}
}
