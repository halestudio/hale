/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.collector.explanations;

import java.util.Locale;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.mdexpl.MarkdownCellExplanation;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Explanation for the Assign collected values function.
 * 
 * @author Florian Esser
 */
public class AssignFromCollectorExplanation extends MarkdownCellExplanation {

	@Override
	protected void customizeBinding(Map<String, Object> binding, Cell cell, boolean html,
			ServiceProvider provider, Locale locale) {

		// Default values for all bindings
		binding.put("_constraintsEvaluated", false);
		binding.put("_hasValue", false);
		binding.put("_isReference", false);

		Entity entity = CellUtil.getFirstEntity(cell.getTarget());
		if (entity != null) {
			ChildContext childContext = entity.getDefinition().getPropertyPath().iterator().next();
			PropertyDefinition resultProperty = childContext.getChild().asProperty();
			TypeDefinition resultPropertyType = resultProperty.getPropertyType();

			boolean isReference = resultProperty.getConstraint(Reference.class).isReference();
			binding.put("_isReference", isReference);

			// If the target is a Reference, the collected values can be
			// assigned here, otherwise the HasValueFlag has to be checked to
			// determine this.
			if (isReference) {
				binding.put("_hasValue", true);
			}
			else {
				boolean hasValue = resultPropertyType.getConstraint(HasValueFlag.class).isEnabled();
				binding.put("_hasValue", hasValue);
			}

			binding.put("_constraintsEvaluated", true);
		}
	}

}
