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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Condition;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.filter.extension.FilterDialogDefinition;
import eu.esdihumboldt.hale.ui.filter.extension.FilterUIExtension;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;
import eu.esdihumboldt.util.Pair;

/**
 * Handler for editing conditions to selected {@link EntityDefinition}.
 * 
 * @author Kai Schwierczek
 */
public class EditConditionContextHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(EditConditionContextHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof EntityDefinition) {
				EntityDefinition entityDef = (EntityDefinition) element;

				String title;
				if (entityDef.getPropertyPath().isEmpty())
					title = "Edit type condition";
				else
					title = "Edit property condition";

				Condition condition = AlignmentUtil.getContextCondition(entityDef);
				if (condition != null && condition.getFilter() != null) {
					Pair<String, String> filterDef = FilterDefinitionManager.getInstance()
							.asPair(condition.getFilter());
					if (filterDef != null && filterDef.getFirst() != null) {
						String filterId = filterDef.getFirst();

						// retrieve filter UI from extension point
						FilterDialogDefinition def = FilterUIExtension.getInstance()
								.getFactory(filterId);
						if (def != null) {
							Filter filter = null;
							try {
								filter = def.createExtensionObject().openDialog(
										HandlerUtil.getActiveShell(event), entityDef, title,
										"Define the condition for the new context");
							} catch (Exception e) {
								log.userError("Failed to create editor for filter", e);
							}
							if (filter != null) {
								EntityDefinitionService eds = PlatformUI.getWorkbench()
										.getService(EntityDefinitionService.class);
								eds.editConditionContext((EntityDefinition) element, filter);
							}
						}
						else {
							log.userError("No editor for this kind of filter available");
						}
					}
					else {
						log.error(
								"No filter definition for filter found, definition ID could not be determined");
					}
				}
			}
		}

		return null;
	}
}
