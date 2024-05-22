/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.views.properties.entity;

import java.util.List;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.GroupPropertyDefinition;

/**
 * Add a condition context on the parent of an entity based on a value list.
 * 
 * @author Simon Templer
 */
public class AddParentConditionAction extends AddConditionAction {

	private final EntityDefinition parent;

	/**
	 * Create an action for creating a condition context.
	 * 
	 * @param entity the entity for which parent to create a context for
	 * @param values the values the condition should match
	 * @param combine if the values should be combined to one condition
	 */
	public AddParentConditionAction(EntityDefinition entity, List<String> values, boolean combine) {
		super(entity, values, combine);

		// determine parent
		EntityDefinition parent = AlignmentUtil.getParent(entity);
		// ignore groups
		while (parent.getDefinition() instanceof GroupPropertyDefinition) {
			parent = AlignmentUtil.getParent(parent);
		}

		setText("Create condition on parent " + parent.getDefinition().getDisplayName()
				+ (combine ? " (combined)" : ""));

		this.parent = parent;
	}

	@Override
	protected String getPropertyReference() {
		// XXX include namespace?
		String property = getEntity().getDefinition().getName().getLocalPart();
		if (!getContextEntity().getPropertyPath().isEmpty()) {
			// parent is not a type
			property = "value." + property;
		}
		// Put property name in quotes to make sure that names that have a
		// special meaning in CQL (e.g. "id") are properly quoted.
		return "\"" + property + "\"";
	}

	@Override
	protected EntityDefinition getContextEntity() {
		return parent;
	}

}
