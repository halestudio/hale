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

import java.util.List;

import eu.esdihumboldt.cst.functions.geometric.CalculateAreaFunction;
import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

/**
 * Translator class for the calculate area function
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class CalculateAreaTranslator implements FunctionTranslator, CalculateAreaFunction {

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
		return params;
	}

}
