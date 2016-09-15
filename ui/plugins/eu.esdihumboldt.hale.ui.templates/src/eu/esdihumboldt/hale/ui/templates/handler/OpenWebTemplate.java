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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.templates.webtemplates.WebTemplate;
import eu.esdihumboldt.hale.ui.templates.webtemplates.WebTemplateLoader;
import eu.esdihumboldt.hale.ui.templates.webtemplates.WebTemplatesDialog;

/**
 * Opens a dialog for selecting and opening a web template.
 * 
 * @author Simon Templer
 */
public class OpenWebTemplate extends AbstractHandler {

	private static final ALogger log = ALoggerFactory.getLogger(OpenWebTemplate.class);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final Display display = HandlerUtil.getActiveShell(event).getDisplay();

		ProgressMonitorDialog taskDlg = new ProgressMonitorDialog(
				HandlerUtil.getActiveShell(event));
		try {
			taskDlg.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Downloading template list", IProgressMonitor.UNKNOWN);

					// load templates
					final List<WebTemplate> templates;
					try {
						templates = WebTemplateLoader.load();
					} catch (Exception e) {
						log.userError("Failed to download template list", e);
						return;
					} finally {
						monitor.done();
					}

					if (templates != null) {
						// launch dialog asynchronously in display thread
						display.asyncExec(new Runnable() {

							@Override
							public void run() {
								WebTemplatesDialog dlg = new WebTemplatesDialog(
										display.getActiveShell(), templates);
								if (dlg.open() == WebTemplatesDialog.OK) {
									WebTemplate template = dlg.getObject();
									if (template != null) {
										ProjectService ps = PlatformUI.getWorkbench()
												.getService(ProjectService.class);
										ps.load(template.getProject());
									}
								}
							}
						});
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			log.userError("Failed to download template list", e);
		}

		return null;
	}
}
