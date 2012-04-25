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

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * The interface for all translator functions
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public interface FunctionTranslator {

	/**
	 * Returns the new version of the transformation ID
	 * 
	 * @return the transformation ID
	 */
	public String getTransformationId();
	
	/**
	 * Returns the list with the translated parameters
	 * 
	 * @param params the pre-translation parameters
	 * @param cellBean the cell bean containing source and target cells
	 * @param reporter the warning/error reporter
	 * @return the post-translation parameters
	 */
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean, IOReporter reporter);

}
