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
import eu.esdihumboldt.hale.common.align.io.impl.internal.ParameterValue;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.io.oml.internal.model.align.ICell;

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
	 * @param cellBean the cell being constructed
	 * @param reporter the warning/error reporter
	 * @param cell the initial cell loaded from OML
	 * @return the post-translation parameters
	 */
	public List<ParameterValue> getNewParameters(List<ParameterValue> params, CellBean cellBean,
			IOReporter reporter, ICell cell);

}
