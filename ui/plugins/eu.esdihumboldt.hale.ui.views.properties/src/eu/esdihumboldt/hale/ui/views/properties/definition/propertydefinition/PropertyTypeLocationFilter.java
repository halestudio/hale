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

import java.net.URI;

import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter;

/**
 * Filter that lets only {@link PropertyDefinition}s with a location that is not
 * <code>null</code> pass.
 * 
 * @author Patrick Lieb
 */
public class PropertyTypeLocationFilter extends DefaultDefinitionFilter {

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.definition.DefaultDefinitionFilter#isFiltered(eu.esdihumboldt.hale.common.schema.model.Definition)
	 */
	@Override
	public boolean isFiltered(Definition<?> input) {
		if (input instanceof PropertyDefinition) {
			URI location;
			try {
				location = ((PropertyDefinition) input).getPropertyType().getLocation();
			} catch (IllegalStateException e) {
				return true;
			}
			return location == null;
		}
		return true;
	}
}
