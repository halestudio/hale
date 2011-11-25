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

package eu.esdihumboldt.hale.ui.views.properties.function;

import eu.esdihumboldt.hale.common.align.extension.function.Function;

/**
 * The default functions section
 * @author Patrick Lieb
 * @param <F> the Function
 */
public class DefaultFunctionSection<F extends Function> extends AbstractFunctionSection<F>{

	/**
	 * @see eu.esdihumboldt.hale.ui.views.properties.AbstractSection#setInput(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void setInput(Object input) {
		if (input instanceof Function) {
			setFunction((F) input);
		}
		
	}

}
