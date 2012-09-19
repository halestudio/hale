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

package eu.esdihumboldt.hale.io.oml.helper;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

/**
 * Class to translate the constant value function to the assign function
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class AssignTranslator implements FunctionTranslator, AssignFunction {

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getTransformationId()
	 */
	@Override
	public String getTransformationId() {
		return ID;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getNewParameters(java.util.List,
	 *      eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter,
	 *      eu.esdihumboldt.hale.io.oml.internal.model.align.ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {
		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		for (ParameterValue val : params) {
			// translate "defaultValue" to "value"
			if (val.getName().equals("defaultValue")) {
				newList.add(new ParameterValue(PARAMETER_VALUE, val.getValue()));
			}
			else {
				newList.add(val);
			}
		}

		return newList;
	}

}
