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

package eu.esdihumboldt.hale.ui.functions.geometric;

import eu.esdihumboldt.cst.functions.geometric.NetworkExpansionFunction;
import eu.esdihumboldt.hale.ui.functions.numeric.MathExpressionParameterPage;

/**
 * Parameter page for specifying the buffer width expression.
 * 
 * @author Simon Templer
 */
public class NetworkExpansionParameterPage extends MathExpressionParameterPage implements
		NetworkExpansionFunction {

	/**
	 * @see MathExpressionParameterPage#getParameterName()
	 */
	@Override
	protected String getParameterName() {
		return PARAMETER_BUFFER_WIDTH;
	}

	/**
	 * @see MathExpressionParameterPage#getSourcePropertyName()
	 */
	@Override
	protected String getSourcePropertyName() {
		return NetworkExpansionFunction.ENTITY_VARIABLE;
	}

}
