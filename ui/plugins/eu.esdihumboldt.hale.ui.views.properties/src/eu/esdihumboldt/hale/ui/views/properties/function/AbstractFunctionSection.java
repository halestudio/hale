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
import eu.esdihumboldt.hale.ui.views.properties.AbstractSection;

/**
 * Abstract section for function properties
 * 
 * @author Patrick Lieb
 * @param <F>
 *            the function
 */
public abstract class AbstractFunctionSection<F extends Function> extends
		AbstractSection {

	/**
	 * the Function for this section
	 */
	private F function;

	/**
	 * @param fun
	 *            the Function
	 */
	protected void setFunction(F fun) {
		function = fun;
	}

	/**
	 * @return the Function
	 */
	public F getFunction() {
		return function;
	}
}
