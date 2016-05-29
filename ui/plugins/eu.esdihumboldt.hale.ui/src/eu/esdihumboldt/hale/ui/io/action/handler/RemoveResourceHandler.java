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

package eu.esdihumboldt.hale.ui.io.action.handler;

import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOAction;
import eu.esdihumboldt.hale.common.core.io.extension.IOActionExtension;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.ui.io.action.ActionUI;
import eu.esdihumboldt.hale.ui.io.action.ActionUIAdvisor;
import eu.esdihumboldt.hale.ui.io.action.ActionUIExtension;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Removes a resource from the project.
 * 
 * @author Simon Templer
 */
public class RemoveResourceHandler extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(RemoveResourceHandler.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof Resource) {
				Resource resource = (Resource) element;

				// retrieve action UI advisor
				ActionUI actionUI = ActionUIExtension.getInstance()
						.findActionUI(resource.getActionId());
				if (actionUI != null) {
					IOAction action = IOActionExtension.getInstance().get(resource.getActionId());
					ActionUIAdvisor<?> advisor = actionUI.getUIAdvisor();
					if (advisor != null && advisor.supportsRemoval()) {
						String name = null;
						if (resource.getSource() != null) {
							String location = resource.getSource().toString();
							int index = location.lastIndexOf('/');
							if (index > 0 && index < location.length()) {
								name = location.substring(index + 1);
							}
						}

						String resourceType = null;
						if (action != null) {
							resourceType = action.getResourceName();
						}
						if (resourceType == null) {
							resourceType = "resource";
						}

						String message;
						if (name == null) {
							message = MessageFormat.format("Do you really want to remove this {0}?",
									resourceType);
						}
						else {
							message = MessageFormat.format(
									"Do you really want to remove the {0} {1}?", resourceType,
									name);
						}

						if (MessageDialog.openQuestion(HandlerUtil.getActiveShell(event),
								"Remove resource", message)) {
							// do the actual removal

							String id = resource.getResourceId();
							if (advisor.removeResource(id)) {
								// removal succeeded, so remove from project as
								// well
								ProjectService ps = PlatformUI.getWorkbench()
										.getService(ProjectService.class);
								ps.removeResource(id);
							}
						}
					}
					else {
						log.userError("Removing this resource is not supported.");
					}
				}
			}
		}

		return null;
	}

}
