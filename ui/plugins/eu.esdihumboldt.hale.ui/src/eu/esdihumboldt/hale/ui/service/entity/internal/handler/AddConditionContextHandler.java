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

package eu.esdihumboldt.hale.ui.service.entity.internal.handler;

import javax.xml.namespace.QName;

import org.eclipse.swt.widgets.Shell;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.ui.filter.CQLFilterDialog;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;

/**
 * Adds a new condition context for a selected {@link EntityDefinition}.
 * 
 * @author Simon Templer
 */
public class AddConditionContextHandler extends AbstractAddConditionContextHandler {

	/**
	 * @see eu.esdihumboldt.hale.ui.service.entity.internal.handler.AbstractAddConditionContextHandler#createDialog(org.eclipse.swt.widgets.Shell,
	 *      eu.esdihumboldt.hale.common.align.model.EntityDefinition,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	protected TypeFilterDialog createDialog(Shell shell, EntityDefinition entityDef, String title,
			String message) {
		TypeEntityDefinition parentType;
		if (entityDef.getPropertyPath().isEmpty())
			parentType = AlignmentUtil.getTypeEntity(entityDef);
		else {
			Definition<?> def = entityDef.getDefinition();
			TypeDefinition propertyType = ((PropertyDefinition) def).getPropertyType();
			// create a dummy type for the filter
			TypeDefinition dummyType = new DefaultTypeDefinition(new QName("ValueFilterDummy"));
			parentType = new TypeEntityDefinition(dummyType, entityDef.getSchemaSpace(), null);
			// with the property type being contained as value
			// property
			new DefaultPropertyDefinition(new QName("value"), dummyType, propertyType);
			// and the parent type as parent property
			new DefaultPropertyDefinition(new QName("parent"), dummyType,
					((PropertyDefinition) def).getParentType());
		}

		return new CQLFilterDialog(shell, parentType, title, message);
	}

}
