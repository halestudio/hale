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

package eu.esdihumboldt.hale.ui.service.entity.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Abstract base handler for adding conditions to selected
 * {@link EntityDefinition}.
 * 
 * @author Kai Schwierczek
 */
public abstract class AbstractAddConditionContextHandler extends AbstractHandler {

	/**
	 * @see IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty() && selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof EntityDefinition) {
				EntityDefinition entityDef = (EntityDefinition) element;
				String title;
				if (entityDef.getPropertyPath().isEmpty())
					title = "Type condition";
				else
					title = "Property condition";
				TypeFilterDialog tfd = createDialog(HandlerUtil.getActiveShell(event), entityDef,
						title, "Define the condition for the new context");
				if (tfd.open() == TypeFilterDialog.OK) {
					EntityDefinitionService eds = (EntityDefinitionService) PlatformUI
							.getWorkbench().getService(EntityDefinitionService.class);
					eds.addConditionContext((EntityDefinition) element, tfd.getFilter());
				}
			}
		}

		return null;
	}

	/**
	 * Create the input dialog for condition selection.
	 * 
	 * @param shell the parent shell
	 * @param entityDef the selected entity definition
	 * @param title the title
	 * @param message the message
	 * @return a input dialog for a condition for the given entity definition
	 */
	protected abstract TypeFilterDialog createDialog(Shell shell, EntityDefinition entityDef,
			String title, String message);

}
