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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.ui.common.CommonSharedImages;
import eu.esdihumboldt.hale.ui.common.service.compatibility.CompatibilityService;
import eu.esdihumboldt.hale.ui.filter.FilterDialogFactory;
import eu.esdihumboldt.hale.ui.filter.extension.FilterDialogDefinition;
import eu.esdihumboldt.hale.ui.filter.extension.FilterUIExtension;
import eu.esdihumboldt.hale.ui.service.entity.EntityDefinitionService;

/**
 * Contribution that creates items to add condition contexts based on the
 * currently selected entity definition.
 * 
 * @author Simon Templer
 * @author Kai Schwierczek
 */
public class AddConditionContextContribution extends ContributionItem {

	/**
	 * Action to add a new condition context.
	 */
	private static class AddConditionAction extends Action {

		private final EntityDefinition entityDef;
		private final String dialogMessage;
		private final String dialogTitle;
		private final FilterDialogDefinition dialogDefinition;

		/**
		 * Create an action to create a new condition context.
		 * 
		 * @param filterId the filter definition ID
		 * @param dialogDefinition the filter dialog definition
		 * @param entityDef the selected entity definition to add the context to
		 * @param dialogTitle the dialog title
		 * @param dialogMessage the dialog message
		 */
		public AddConditionAction(String filterId, FilterDialogDefinition dialogDefinition,
				EntityDefinition entityDef, String dialogTitle, String dialogMessage) {
			super("Add condition context (" + filterId + ")", AS_PUSH_BUTTON);
			this.dialogDefinition = dialogDefinition;
			this.dialogTitle = dialogTitle;
			this.dialogMessage = dialogMessage;
			this.entityDef = entityDef;

			setImageDescriptor(CommonSharedImages.getImageRegistry()
					.getDescriptor(CommonSharedImages.IMG_ADD));
		}

		@Override
		public void run() {
			FilterDialogFactory factory;
			try {
				factory = dialogDefinition.createExtensionObject();
			} catch (Exception e) {
				log.userError("Failed to create editor for filter", e);
				return;
			}
			Filter filter = factory.openDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), entityDef,
					dialogTitle, dialogMessage);
			if (filter != null) {
				EntityDefinitionService eds = PlatformUI.getWorkbench()
						.getService(EntityDefinitionService.class);
				eds.addConditionContext(entityDef, filter);
			}
		}
	}

	private static final ALogger log = ALoggerFactory
			.getLogger(AddConditionContextContribution.class);

	/**
	 * Get the selection the condition actions should be based on.
	 * 
	 * @return the selection
	 */
	protected ISelection getSelection() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
				.getSelection();
	}

	@Override
	public void fill(Menu menu, int index) {
		ISelection selection = getSelection();

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof EntityDefinition) {
				/*
				 * Determine available filters and filter UI for current
				 * compatibility mode.
				 */

				// get all filters supported by the compatibility mode
				CompatibilityService cs = PlatformUI.getWorkbench()
						.getService(CompatibilityService.class);
				Set<String> supportedFilters = cs.getCurrentDefinition().getSupportedFilters();

				// get filter dialog definitions for those filters
				Map<String, FilterDialogDefinition> definitions = new HashMap<>();
				for (String filterId : supportedFilters) {
					FilterDialogDefinition def = FilterUIExtension.getInstance()
							.getFactory(filterId);
					if (def != null) {
						definitions.put(filterId, def);
					}
				}

				if (definitions.isEmpty()) {
					return;
				}

				// basics for dialog configuration
				EntityDefinition entityDef = (EntityDefinition) element;
				String title;
				if (entityDef.getPropertyPath().isEmpty())
					title = "Type condition";
				else
					title = "Property condition";
				String message = "Define the condition for the new context";

				// add items for each filter type
				for (Entry<String, FilterDialogDefinition> entry : definitions.entrySet()) {
					IAction action = new AddConditionAction(entry.getKey(), entry.getValue(),
							entityDef, title, message);
					IContributionItem item = new ActionContributionItem(action);
					item.fill(menu, index++);
				}
			}
		}
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

}
