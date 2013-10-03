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

package eu.esdihumboldt.hale.ui.templates.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.templates.webtemplates.WebTemplate;
import eu.esdihumboldt.hale.ui.templates.webtemplates.WebTemplatesDialog;

/**
 * Opens a dialog for selecting and opening a web template.
 * 
 * @author Simon Templer
 */
public class OpenWebTemplate extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		WebTemplatesDialog dlg = new WebTemplatesDialog(HandlerUtil.getActiveShell(event));
		if (dlg.open() == WebTemplatesDialog.OK) {
			WebTemplate template = dlg.getObject();
			if (template != null) {
				ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
						ProjectService.class);
				ps.load(template.getProject());
			}
		}

		return null;
	}

}
