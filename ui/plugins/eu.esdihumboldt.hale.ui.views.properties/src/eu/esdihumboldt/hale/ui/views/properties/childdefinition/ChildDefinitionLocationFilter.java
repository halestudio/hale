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

package eu.esdihumboldt.hale.ui.views.properties.childdefinition;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.ui.views.properties.DefaultFilter;

/**
 * Filter that lets only {@link ChildDefinition}s with a location that is not 
 * <code>null</code> pass.
 * @author Patrick Lieb
 */
public class ChildDefinitionLocationFilter extends DefaultFilter{

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.DefaultFilter#isFiltered(eu.esdihumboldt.hale.common.align.model.EntityDefinition)
	 */
	@Override
	public boolean isFiltered(EntityDefinition input) {
		if(input instanceof PropertyEntityDefinition){
			return ((PropertyEntityDefinition)input).getDefinition().getParentType().getLocation() == null;
		}
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.DefaultFilter#isFiltered(eu.esdihumboldt.hale.common.schema.model.Definition)
	 */
	@Override
	public boolean isFiltered(Definition<?> input) {
		if(input instanceof PropertyDefinition){
			return ((PropertyDefinition)input).getParentType().getLocation() == null;
		}
		return true;
	}
}
