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

package eu.esdihumboldt.hale.io.oml.helper;

import java.util.ArrayList;
import java.util.List;

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

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
	 *      eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean, eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params,
			CellBean cellBean, IOReporter reporter) {
		List<ParameterValue> newList = new ArrayList<ParameterValue>();

		for (ParameterValue val : params) {
			// translate "defaultValue" to "value"
			if (val.getName().equals("defaultValue")) {
				newList.add(new ParameterValue(PARAMETER_VALUE, val.getValue()));
			} else {
				newList.add(val);
			}
		}

		return newList;
	}

}
