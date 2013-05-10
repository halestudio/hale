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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.io.xslt.xpath.XPathFilter;
import eu.esdihumboldt.hale.ui.filter.TypeFilterDialog;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Handler for editing conditions to selected {@link EntityDefinition}.
 * 
 * @author Kai Schwierczek
 */
public class EditConditionContextHandler extends AbstractHandler {

	/**
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
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
					title = "Edit type condition";
				else
					title = "Edit property condition";
				AbstractAddConditionContextHandler addHandler;
				Condition condition = AlignmentUtil.getContextCondition(entityDef);
				if (condition != null && condition.getFilter() instanceof XPathFilter)
					addHandler = new AddXPathConditionContextHandler();
				else
					addHandler = new AddConditionContextHandler();

				TypeFilterDialog tfd = addHandler.createDialog(HandlerUtil.getActiveShell(event),
						entityDef, title, "Define the condition for the new context");
				if (tfd.open() == TypeFilterDialog.OK) {
					EntityDefinitionService eds = (EntityDefinitionService) PlatformUI
							.getWorkbench().getService(EntityDefinitionService.class);
					eds.editConditionContext((EntityDefinition) element, tfd.getFilter());
				}
			}
		}

		return null;
	}

}
