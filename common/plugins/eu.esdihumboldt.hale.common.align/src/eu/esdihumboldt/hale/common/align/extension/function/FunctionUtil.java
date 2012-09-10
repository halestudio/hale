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

package eu.esdihumboldt.hale.common.align.extension.function;

/**
 * Function utility methods
 * 
 * @author Simon Templer
 */
public abstract class FunctionUtil {

	/**
	 * Get the function w/ the given identifier.
	 * 
	 * @param id the function ID
	 * @return the function or <code>null</code> if no function with the given
	 *         identifier was found
	 */
	public static AbstractFunction<?> getFunction(String id) {
		AbstractFunction<?> result = null;

		result = TypeFunctionExtension.getInstance().get(id);

		if (result == null) {
			result = PropertyFunctionExtension.getInstance().get(id);
		}

		return result;
	}

}
