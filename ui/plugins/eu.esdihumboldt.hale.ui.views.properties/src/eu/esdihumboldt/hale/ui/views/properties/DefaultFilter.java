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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;

/**
 * The default filter for all filters
 * @author Patrick Lieb
 */
public abstract class DefaultFilter implements IFilter{
	
	/**
	 * @param input the definition type
	 * @return true if should be filtered, false otherwise
	 */
	public abstract boolean isFiltered(Definition<?> input);

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object input) {
		if (input instanceof Entity) {
			input = ((Entity) input).getDefinition();
		}
		
		if (input instanceof EntityDefinition){
			input = ((EntityDefinition) input).getDefinition();
		}
		
		if (input instanceof Definition<?>){
			return !isFiltered((Definition<?>) input);
		}
		
		return false;
	}
}
