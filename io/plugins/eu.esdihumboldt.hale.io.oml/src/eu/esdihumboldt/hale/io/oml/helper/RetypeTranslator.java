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
import eu.esdihumboldt.hale.common.align.model.functions.RetypeFunction;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

/**
 * Class to translate rename feature function to retype function
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class RetypeTranslator implements FunctionTranslator, RetypeFunction {

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
		for (ParameterValue val : params) {
			if (val.getName().equals("split") && val.getValue() != null) {
				reporter.error(new IOMessageImpl("The 'split' value has been removed.", null));
			}
			if (val.getName().equals("merge") && val.getValue() != null) {
				reporter.error(new IOMessageImpl("The 'merge' value has been removed.", null));
			}
		}
		// the retype function has no parameters, so just return an empty list
		return new ArrayList<ParameterValue>();
	}

}
