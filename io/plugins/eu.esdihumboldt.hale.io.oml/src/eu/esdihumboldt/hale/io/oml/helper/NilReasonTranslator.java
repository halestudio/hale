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

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ChildContextBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.io.impl.internal.PropertyBean;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

/**
 * Class to translate the NilReasonFucntion to the assign function.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class NilReasonTranslator implements FunctionTranslator, AssignFunction {

	/**
	 * Name of the NilReasonType parameter
	 */
	public static final String PARAMETER_NIL_REASON_TYPE = "NilReasonType"; //$NON-NLS-1$

	/**
	 * @see FunctionTranslator#getTransformationId()
	 */
	@Override
	public String getTransformationId() {
		return ID;
	}

	/**
	 * @see FunctionTranslator#getNewParameters(List, CellBean, IOReporter,
	 *      ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {
		// update target, adding nilReason child to path
		ChildContextBean nilReason = new ChildContextBean();
		nilReason.setChildName(new QName("nilReason"));
		PropertyBean target = (PropertyBean) cellBean.getTarget().get(0).getEntity();
		target.getProperties().add(nilReason);

		// updated parameters
		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		for (ParameterValue val : params) {
			// translate the nilReason type to the value being assigned
			if (val.getName().equals(PARAMETER_NIL_REASON_TYPE)) {
				newList.add(new ParameterValue(PARAMETER_VALUE, val.getValue()));
			}
			else {
				newList.add(val);
			}
		}

		return newList;
	}

}
