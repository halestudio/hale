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

import eu.esdihumboldt.cst.functions.string.DateExtractionFunction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValueBean;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

/**
 * Translator class for date extraction
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class DateExtractionTranslator implements FunctionTranslator, DateExtractionFunction {

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
	public List<ParameterValueBean> getNewParameters(List<ParameterValueBean> params, CellBean cellBean,
			IOReporter reporter, ICell cell) {
		List<ParameterValueBean> newList = new ArrayList<ParameterValueBean>();

		for (ParameterValueBean val : params) {
			if (val.getName().equals("dateFormatTarget") && val.getValue() != null) {
				reporter.warn(new IOMessageImpl(
						"The 'dateFormatTarget' value has been removed, your result could be different.",
						null));
			}
			// translate "dateFormatSource" to "dateFormat"
			if (val.getName().equals("dateFormatSource")) {
				newList.add(new ParameterValueBean(PARAMETER_DATE_FORMAT, val.getValue()));
			}
			else {
				newList.add(val);
			}
		}
		return newList;
	}

}
