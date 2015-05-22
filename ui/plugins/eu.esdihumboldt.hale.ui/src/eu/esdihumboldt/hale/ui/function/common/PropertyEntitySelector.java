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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.extension.function.ParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameterDefinition;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.condition.PropertyCondition;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Entity selector for {@link Property} entities
 * 
 * @author Simon Templer
 */
public class PropertyEntitySelector extends EntitySelector<PropertyParameterDefinition> {

	private TypeEntityDefinition parentType;

	/**
	 * Create an entity selector for {@link Property} entities
	 * 
	 * @param ssid the schema space
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 * @param parentType the parent type, may be <code>null</code>
	 */
	public PropertyEntitySelector(SchemaSpaceID ssid, PropertyParameterDefinition field,
			Composite parent, TypeEntityDefinition parentType) {
		super(ssid, field, parent, createFilters(field));

		this.parentType = parentType;
	}

	/**
	 * Set the parent type
	 * 
	 * @param parentType the parentType to set
	 */
	public void setParentType(TypeEntityDefinition parentType) {
		// reset candidates?? refresh viewer?
		if (!Objects.equal(this.parentType, parentType)) {
			this.parentType = parentType;
			// reset selection if necessary
			EntityDefinition selection = getSelectedObject();
			if (selection != null && parentType != null) {
				// maybe also keep selection, if it is a super/sub type of
				// parentType, which also got the selected property?
				if (!AlignmentUtil.getTypeEntity(selection).equals(parentType)) {
					setSelection(StructuredSelection.EMPTY);
				}
			}
		}
	}

	/**
	 * @see EntitySelector#createEntityDialog(Shell, SchemaSpaceID,
	 *      ParameterDefinition)
	 */
	@Override
	protected EntityDialog createEntityDialog(Shell parentShell, SchemaSpaceID ssid,
			PropertyParameterDefinition field) {
		String title;
		switch (ssid) {
		case SOURCE:
			title = "Select source property";
			break;
		case TARGET:
		default:
			title = "Select target property";
			break;
		}
		return new PropertyEntityDialog(parentShell, ssid, parentType, title, getSelectedObject());
	}

	/**
	 * @see EntitySelector#createEntity(EntityDefinition)
	 */
	@Override
	protected Entity createEntity(EntityDefinition element) {
		if (element instanceof PropertyEntityDefinition) {
			Property property = new DefaultProperty((PropertyEntityDefinition) element);
			// TODO configure entity?
			return property;
		}

		throw new IllegalArgumentException("Entity must be a property");
	}

	private static ViewerFilter[] createFilters(PropertyParameterDefinition field) {
		// if no condition is present add a filter that allows all properties
		ViewerFilter propertyFilter = new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof PropertyEntityDefinition;
			}
		};
		if (field == null) {
			return new ViewerFilter[] { propertyFilter };
		}

		List<PropertyCondition> conditions = field.getConditions();

		if (conditions == null || conditions.isEmpty())
			return new ViewerFilter[] { propertyFilter };

		ViewerFilter[] filters = new ViewerFilter[conditions.size()];
		int i = 0;
		for (final PropertyCondition condition : conditions) {
			filters[i] = new ViewerFilter() {

				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof PropertyEntityDefinition) {
						Property property = new DefaultProperty((PropertyEntityDefinition) element);
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
