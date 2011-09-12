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

import eu.esdihumboldt.hale.schema.model.Definition;

/**
 * TODO Type description
 * @author Patrick Lieb
 * @param <T> 
 */
public class AbstractDescriptionSection<T extends Definition<?>> implements IFilter{
	
	/**
	 * @see IFilter#select(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean select(Object toTest) {
//		if (toTest instanceof T) {
//			return ((T) toTest).getDescription() != null;
//		}
//		
		return false;
	}

}
