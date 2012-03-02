/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.function.common;

import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Objects;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
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
public class PropertyEntitySelector extends EntitySelector<PropertyParameter> {

	private TypeEntityDefinition parentType;

	/**
	 * Create an entity selector for {@link Property} entities
	 * 
	 * @param ssid the schema space
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 * @param parentType the parent type
	 */
	public PropertyEntitySelector(SchemaSpaceID ssid, PropertyParameter field, Composite parent,
			TypeEntityDefinition parentType) {
		super(ssid, field, parent, createFilters(field));

		this.parentType = parentType;
	}

	/**
	 * Set the parent type
	 * 
	 * @param parentType the parentType to set
	 */
	public void setParentType(TypeEntityDefinition parentType) {
		boolean forceUpdate = this.parentType != null && 
				!Objects.equal(this.parentType, parentType);
		
		this.parentType = parentType;
		// reset candidates?? refresh viewer?
		if (forceUpdate) {
			// reset selection
			setSelection(new StructuredSelection());
		}
	}

	/**
	 * @see EntitySelector#createEntityDialog(Shell, SchemaSpaceID,
	 *      AbstractParameter)
	 */
	@Override
	protected EntityDialog createEntityDialog(Shell parentShell, SchemaSpaceID ssid, PropertyParameter field) {
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
			//TODO configure entity?
			return property;
		}

		throw new IllegalArgumentException("Entity must be a property");
	}

	private static ViewerFilter[] createFilters(PropertyParameter field) {
		List<PropertyCondition> conditions = field.getConditions();
				
		if (conditions == null)
			return new ViewerFilter[0];

		ViewerFilter[] filters = new ViewerFilter[conditions.size()];
		int i = 0;
		for (final PropertyCondition condition : conditions) {
			filters[i] = new ViewerFilter() {				
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					if (element instanceof PropertyEntityDefinition) {
						Property property = new DefaultProperty((PropertyEntityDefinition) element);
						return condition.accept(property);
					} else
						return false;
				}
			};
		}
		return filters;
	}
	
}
