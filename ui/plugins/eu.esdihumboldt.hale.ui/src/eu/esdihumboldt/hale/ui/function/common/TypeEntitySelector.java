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
import eu.esdihumboldt.hale.common.align.extension.function.TypeParameter;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Entity selector for {@link Type} entities
 * @author Simon Templer
 */
public class TypeEntitySelector extends EntitySelector<TypeParameter> {

	/**
	 * Create an entity selector for {@link Type} entities
	 * @param ssid the schema space
	 * @param candidates the entity candidates
	 * @param field the field definition, may be <code>null</code>
	 * @param parent the parent composite
	 */
	public TypeEntitySelector(SchemaSpaceID ssid,
			Set<EntityDefinition> candidates, TypeParameter field,
			Composite parent) {
		super(ssid, candidates, field, parent);
	}

	/**
	 * @see EntitySelector#createEntityDialog(Shell, SchemaSpaceID, AbstractParameter)
	 */
	@Override
	protected EntityDialog createEntityDialog(Shell parentShell,
			SchemaSpaceID ssid, TypeParameter field) {
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
		return new TypeEntityDialog(parentShell, ssid, title,
				getEntityDefinition());
	}

	/**
	 * @see EntitySelector#createEntity(EntityDefinition)
	 */
	@Override
	protected Entity createEntity(EntityDefinition element) {
		if (element instanceof TypeEntityDefinition) {
			Type type = new DefaultType((TypeEntityDefinition) element);
			//TODO configure entity?
			return type;
		}
		
		throw new IllegalArgumentException("Entity must be a type");
	}

	/**
	 * @see EntitySelector#createFilters(AbstractParameter)
	 */
	@Override
	protected ViewerFilter[] createFilters(TypeParameter field) {
		// TODO Auto-generated method stub
		return super.createFilters(field);
	}

}
