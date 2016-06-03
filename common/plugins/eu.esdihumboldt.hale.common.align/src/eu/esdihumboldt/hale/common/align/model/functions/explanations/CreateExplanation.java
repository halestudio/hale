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
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.functions.CreateFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Explanation for the Create function.
 * 
 * @author Simon Templer
 */
public class CreateExplanation extends AbstractCellExplanation implements CreateFunction {

	@Override
	protected String getExplanation(Cell cell, boolean html, ServiceProvider services,
			Locale locale) {
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		int number = CellUtil.getOptionalParameter(cell, PARAM_NUMBER, Value.of(1))
				.as(Integer.class);
		String instancesString;
		if (number == 1) {
			instancesString = getMessage("one", locale);
		}
		else {
			instancesString = MessageFormat.format(getMessage("many", locale), number);
		}

		if (target != null)
			return MessageFormat.format(getMessage("main", locale), instancesString,
					formatEntity(target, html, true, locale));

		return null;
	}
}
