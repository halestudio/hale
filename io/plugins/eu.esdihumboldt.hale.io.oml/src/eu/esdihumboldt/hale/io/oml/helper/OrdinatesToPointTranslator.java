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

import java.util.List;

import eu.esdihumboldt.cst.functions.geometric.OrdinatesToPointFunction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.NamedEntityBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Class to translate the old ordinates to point function to the new ordinates
 * to point function
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class OrdinatesToPointTranslator implements FunctionTranslator, OrdinatesToPointFunction {

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
	 *      eu.esdihumboldt.specification.cst.align.ICell)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {

		reporter.warn(new IOMessageImpl("Behavior of this function has changed.", null));

		List<NamedEntityBean> src = cellBean.getSource();

		for (int i = 0; i < src.size(); i++) {

			NamedEntityBean bean = src.get(i);

			if (i == 0) {
				bean.setName("X");
			}
			if (i == 1) {
				bean.setName("Y");
			}
			if (i == 2) {
				bean.setName("Z");
			}
		}
		return params;
	}

}
