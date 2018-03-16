/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
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
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Handler for purging the condition of selected {@link EntityDefinition} and
 * updating the respective cells.
 * 
 * @author Simon Templer
 */
public class PurgeConditionContextHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof EntityDefinition) {
				EntityDefinition entityDef = (EntityDefinition) element;

				Condition condition = AlignmentUtil.getContextCondition(entityDef);
				if (condition != null && condition.getFilter() != null) {
					EntityDefinitionService eds = PlatformUI.getWorkbench()
							.getService(EntityDefinitionService.class);
					eds.editConditionContext((EntityDefinition) element, null);
				}
			}
		}

		return null;
	}
}
