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

import eu.esdihumboldt.hale.common.align.io.impl.internal.CellBean;
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValueBean;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;
import eu.esdihumboldt.hale.io.oml.internal.model.align.IEntity;

/**
 * Class for all functions not supported anymore
 * 
 * @author Kevin Mais
 */
@SuppressWarnings("restriction")
public class NotSupportedTranslator implements FunctionTranslator {

	/**
	 * @see eu.esdihumboldt.hale.io.oml.helper.FunctionTranslator#getTransformationId()
	 */
	@Override
	public String getTransformationId() {
		return null;
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

		IEntity entity1 = cell.getEntity1();
		IEntity entity2 = cell.getEntity2();

		String id = null;

		if (entity1.getTransformation() != null || entity2.getTransformation() != null) {
			if (entity1.getTransformation() != null) {
				id = entity1.getTransformation().getService().getLocation();
			}
			else {
				id = entity2.getTransformation().getService().getLocation();
			}

		}

		reporter.error(new IOMessageImpl(
				"The function '" + id + "' is not supported in this hale»studio version", null));

		return params;
	}
}
