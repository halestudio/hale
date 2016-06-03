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

import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.functions.AssignFunction;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.MarkdownCellExplanation;

/**
 * Explanation for the assign function.
 * 
 * @author Simon Templer
 */
public class AssignExplanation extends MarkdownCellExplanation implements AssignFunction {

	@Override
	protected void customizeBinding(Map<String, Object> binding) {
		super.customizeBinding(binding);

		// to work with Assign and Bound assign both, add empty _source for
		// Assign
		if (!binding.containsKey("_source")) {
			binding.put("_source", null);
		}
	}

}
