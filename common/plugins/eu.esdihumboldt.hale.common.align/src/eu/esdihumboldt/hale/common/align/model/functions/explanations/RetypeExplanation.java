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
import eu.esdihumboldt.hale.common.align.model.functions.RenameFunction;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for the retype function.
 * 
 * @author Simon Templer
 */
public class RetypeExplanation extends AbstractCellExplanation {

	private static final String EXPLANATION_PATTERN = "Creates a {1} instance for each {0} instance in the source data set.";

	@Override
	protected String getExplanation(Cell cell, boolean html) {
		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		if (source != null && target != null) {
			String text = EXPLANATION_PATTERN;
			boolean structuralRename = CellUtil.getFirstParameter(cell,
					RenameFunction.PARAMETER_STRUCTURAL_RENAME).as(Boolean.class, false);
			boolean ignoreNamespaces = CellUtil.getFirstParameter(cell,
					RenameFunction.PARAMETER_IGNORE_NAMESPACES).as(Boolean.class, false);
			if (structuralRename) {
				text += " Furthermore child properties are copied, if the property names in source and target match.";
				if (ignoreNamespaces)
					text += " When comparing child property names, differing namespaces may be ignored.";
			}

			return MessageFormat.format(text, formatEntity(source, html, true),
					formatEntity(target, html, true));
		}

		return null;
	}
}
