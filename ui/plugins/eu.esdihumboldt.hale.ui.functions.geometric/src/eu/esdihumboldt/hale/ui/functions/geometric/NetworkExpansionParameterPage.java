/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
