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

package eu.esdihumboldt.hale.ui.function.common;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.condition.TypeCondition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Entity selector for {@link Type} entities
 * 
 * @author Simon Templer
 */
public class TypeEntitySelector extends EntitySelector<TypeParameterDefinition> {

	private final boolean onlyMappingRelevant;

	/**
	 * Create an entity selector for {@link Type} entities
	 * 
	 * @param ssid the schema space
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 * @param onlyMappingRelevant whether to only show mapping relevant types
	 */
	public TypeEntitySelector(SchemaSpaceID ssid, TypeParameterDefinition field, Composite parent,
			boolean onlyMappingRelevant) {
		super(ssid, field, parent, createFilters(field));
		this.onlyMappingRelevant = onlyMappingRelevant;
	}

	/**
	 * @see EntitySelector#createEntityDialog(Shell, SchemaSpaceID,
	 *      ParameterDefinition)
	 */
	@Override
	protected EntityDialog createEntityDialog(Shell parentShell, SchemaSpaceID ssid,
			TypeParameterDefinition field) {
		String title;
		switch (ssid) {
		case SOURCE:
			title = "Select source type";
			break;
		case TARGET:
		default:
			title = "Select target type";
			break;
		}
		return new TypeEntityDialog(parentShell, ssid, title, getSelectedObject(),
				onlyMappingRelevant);
	}

	/**
	 * @see EntitySelector#createEntity(EntityDefinition)
	 */
	@Override
	protected Entity createEntity(EntityDefinition element) {
		if (element instanceof TypeEntityDefinition) {
			Type type = new DefaultType((TypeEntityDefinition) element);
			// TODO configure entity?
			return type;
		}

		throw new IllegalArgumentException("Entity must be a type");
	}

	private static ViewerFilter[] createFilters(TypeParameterDefinition field) {
		if (field == null) {
			return null;
		}

		List<TypeCondition> conditions = field.getConditions();

		if (conditions == null)
			return new ViewerFilter[0];

		ViewerFilter[] filters = new ViewerFilter[conditions.size()];
		int i = 0;
		for (final TypeCondition condition : conditions) {
			filters[i] = new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof TypeEntityDefinition) {
						Type property = new DefaultType((TypeEntityDefinition) element);
						return condition.accept(property);
					}
					else
						return false;
				}
			};
		}
		return filters;
	}

}
