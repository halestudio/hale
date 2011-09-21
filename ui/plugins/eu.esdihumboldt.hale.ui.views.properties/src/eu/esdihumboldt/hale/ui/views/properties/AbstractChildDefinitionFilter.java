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

import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;

/**
 * Filter that lets only {@link ChildDefinition}s with a ParentType that is
 * defined pass.
 * @author Patrick Lieb
 */
public class AbstractChildDefinitionFilter implements IFilter{

	/**
	 * @see IFilter#select(Object)
	 */
	@Override
	public boolean select(Object toTest) {
		if (toTest instanceof PropertyEntityDefinition) {
			try {
				((PropertyEntityDefinition) toTest).getDefinition().getParentType();
			} catch(IllegalStateException e){
				return false;
			}
			return true;
		}
		
		return false;
	}
}
