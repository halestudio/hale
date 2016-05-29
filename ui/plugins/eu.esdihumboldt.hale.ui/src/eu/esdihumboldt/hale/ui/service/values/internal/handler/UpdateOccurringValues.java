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

package eu.esdihumboldt.hale.ui.service.values.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.ui.service.values.OccurringValuesService;

/**
 * Update the occurring values for a selected {@link PropertyEntityDefinition}.
 * 
 * @author Simon Templer
 */
public class UpdateOccurringValues extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(UpdateOccurringValues.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (selection != null && !selection.isEmpty()
				&& selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection) selection).getFirstElement();

			if (element instanceof PropertyEntityDefinition) {
				OccurringValuesService ovs = PlatformUI.getWorkbench()
						.getService(OccurringValuesService.class);

				if (ovs != null) {
					try {
						boolean updating = ovs
								.updateOccurringValues((PropertyEntityDefinition) element);
						if (!updating) {
							log.userInfo("Occurring values already up-to-date.");
						}
					} catch (IllegalArgumentException e) {
						// ignore
					}
				}
			}
		}

		return null;
	}

}
