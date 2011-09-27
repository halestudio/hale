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

import java.util.Set;

import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.extension.function.PropertyParameter;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * Entity selector for {@link Property} entities
 * @author Simon Templer
 */
public class PropertyEntitySelector extends EntitySelector<PropertyParameter> {
	
	private TypeDefinition parentType;

	/**
	 * Create an entity selector for {@link Property} entities
	 * @param ssid the schema space
	 * @param candidates the entity candidates
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 * @param parentType the parent type
	 */
	public PropertyEntitySelector(SchemaSpaceID ssid,
			Set<EntityDefinition> candidates, PropertyParameter field,
			Composite parent, TypeDefinition parentType) {
		super(ssid, candidates, field, parent);
		
		this.parentType = parentType;
	}

	/**
	 * Set the parent type
	 * @param parentType the parentType to set
	 */
	public void setParentType(TypeDefinition parentType) {
		this.parentType = parentType;
		// reset candidates?? refresh viewer?
	}

	/**
	 * @see EntitySelector#createEntityDialog(Shell, SchemaSpaceID, AbstractParameter)
	 */
	@Override
	protected EntityDialog createEntityDialog(Shell parentShell,
			SchemaSpaceID ssid, PropertyParameter field) {
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
		return new PropertyEntityDialog(parentShell, ssid, parentType, title);
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

	/**
	 * @see EntitySelector#createFilters(AbstractParameter)
	 */
	@Override
	protected ViewerFilter[] createFilters(PropertyParameter field) {
		// TODO Auto-generated method stub
		return super.createFilters(field);
	}

}
