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

package eu.esdihumboldt.hale.common.align.model.functions.explanations;

import java.text.MessageFormat;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Explanation for the Create function.
 * 
 * @author Simon Templer
 */
public class CreateExplanation extends AbstractCellExplanation implements CreateFunction {

	private static final String EXPLANATION_PATTERN = "Creates {0} of type {1}.";

	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		int number = CellUtil.getOptionalParameter(cell, PARAM_NUMBER, Value.of(1)).as(
				Integer.class);
		String instancesString;
		if (number == 1) {
			instancesString = "one instance";
		}
		else {
			instancesString = number + " instances";
		}

		if (target != null)
			return MessageFormat.format(EXPLANATION_PATTERN, instancesString,
					formatEntity(target, html, true));

		return null;
	}
}
