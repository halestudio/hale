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

package eu.esdihumboldt.hale.ui.views.properties.function.typefunction;

import org.eclipse.jface.viewers.IFilter;

import eu.esdihumboldt.hale.common.align.extension.function.TypeFunction;

/**
 * Filter that lets only {@link TypeFunction}s with a source or a target that is not 
 * empty pass.
 * @author Patrick Lieb
 */
public class TypeFunctionGraphFilter implements IFilter{

	/**
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	@Override
	public boolean select(Object toTest) {
		if(toTest instanceof TypeFunction){
			return !(((TypeFunction)toTest).getSource().isEmpty() && ((TypeFunction)toTest).getTarget().isEmpty());
		}
		return false;
	}
}
