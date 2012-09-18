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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Adds a new condition context for a selected {@link EntityDefinition}.
 * 
 * @author Simon Templer
 */
public class AddConditionContextHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof EntityDefinition) {
				EntityDefinitionService eds = (EntityDefinitionService) PlatformUI.getWorkbench()
						.getService(EntityDefinitionService.class);
				EntityDefinition entityDef = (EntityDefinition) element;
				Filter filter = null;
				if (entityDef.getPropertyPath().isEmpty()) {
					// type filter
					TypeFilterDialog tfd = new TypeFilterDialog(HandlerUtil.getActiveShell(event),
							entityDef.getType(), "Type condition",
							"Define the condition for the new context");
					if (tfd.open() == TypeFilterDialog.OK) {
						filter = tfd.getFilter();
					}
				}
				else {
					// value filter
					Definition<?> def = entityDef.getDefinition();
					if (def instanceof PropertyDefinition) {
						TypeDefinition propertyType = ((PropertyDefinition) def).getPropertyType();
						// create a dummy type for the filter
						TypeDefinition dummyType = new DefaultTypeDefinition(new QName(
								"ValueFilterDummy"));
						// with the property type being contained as value
						// property
						new DefaultPropertyDefinition(new QName("value"), dummyType, propertyType);
						// and the parent type as parent property
						new DefaultPropertyDefinition(new QName("parent"), dummyType,
								((PropertyDefinition) def).getParentType());

						// open the filter dialog
						TypeFilterDialog tfd = new TypeFilterDialog(
								HandlerUtil.getActiveShell(event), dummyType, "Property condition",
								"Define the condition for the new context");
						if (tfd.open() == TypeFilterDialog.OK) {
							filter = tfd.getFilter();
						}
					}
				}
				if (filter != null) {
					eds.addConditionContext((EntityDefinition) element, filter);
				}
			}
		}

		return null;
	}

}
