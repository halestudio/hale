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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.extension.function.AbstractParameter;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.schema.SchemaSpaceID;

/**
 * TODO Type description
 * @author sitemple
 */
public class PropertyEntitySelector extends EntitySelector {
	
	private TypeDefinition parentType;

	/**
	 * @param ssid
	 * @param candidates
	 * @param field
	 * @param parent
	 * @param parentType 
	 */
	public PropertyEntitySelector(SchemaSpaceID ssid,
			Set<EntityDefinition> candidates, AbstractParameter field,
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
			SchemaSpaceID ssid, AbstractParameter field) {
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

}
