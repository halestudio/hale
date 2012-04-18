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

import eu.esdihumboldt.cst.functions.geometric.NetworkExpansionFunction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;

/**
 * Translator class for network expansion
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class NetworkExpansionTranslator implements FunctionTranslator,
		NetworkExpansionFunction {

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getTransformationId()
	 */
	@Override
	public String getTransformationId() {
		return ID;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getNewParameters(java.util.List, eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean)
	 */
	@Override
	public List<ParameterValue> getNewParameters(List<ParameterValue> params,
			CellBean cellBean) {
		// TODO Auto-generated method stub
		return params;
	}


}
