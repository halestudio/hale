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

package eu.esdihumboldt.hale.ui.service.project;

import java.io.File;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;

/**
 * Location updater that asks the user if updating a path fails.
 * 
 * @author Simon Templer
 */
public class UILocationUpdater extends LocationUpdater {

	/**
	 * Default constructor.
	 * 
	 * @param project the project to update
	 * @param newLocation the new location of the project file
	 */
	public UILocationUpdater(Project project, URI newLocation) {
		super(project, newLocation);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater#updatePathFallback(java.net.URI)
	 */
	@Override
	protected URI updatePathFallback(final URI oldLocation) {
		if (oldLocation == null) {
			return null;
		}

		final String uriString = oldLocation.toString();
		final String target = uriString.substring(uriString.lastIndexOf("/") + 1);
		int extIndex = uriString.lastIndexOf(".");
		if (extIndex >= 0) {
			final String extension = "*" + uriString.substring(extIndex);

			// let user choose alternative location
			final Display display = PlatformUI.getWorkbench().getDisplay();
			final AtomicReference<URI> result = new AtomicReference<URI>();
			display.syncExec(new Runnable() {

				@Override
				public void run() {
					MessageDialog.openWarning(display.getActiveShell(), "Loading Error",
							"Can't find " + uriString);

					String[] extensions = new String[] { extension };
					FileDialog filedialog = new FileDialog(Display.getCurrent().getActiveShell(),
							SWT.OPEN | SWT.SHEET);
					filedialog.setFilterExtensions(extensions);
					filedialog.setFileName(target);

					String openfile = filedialog.open();
					if (openfile != null) {
						openfile = openfile.trim();
						if (openfile.length() > 0)
							result.set(new File(openfile).toURI());
					}
				}
			});

			return result.get();
		}

		return null;
	}

}
