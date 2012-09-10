/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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

import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;

/**
 * Location updater that asks the user if updating a path fails.
 * 
 * @author Simon Templer
 */
public class UILocationUpdater extends LocationUpdater {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater#updatePathFallback(java.net.URI)
	 */
	@Override
	protected URI updatePathFallback(final URI oldLocation) {
		// let user choose alternative location
		final Display display = PlatformUI.getWorkbench().getDisplay();
		final AtomicReference<URI> result = new AtomicReference<URI>();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				String uriString = oldLocation.toString();
				MessageDialog.openWarning(display.getActiveShell(), "Loading Error", "Can't find "
						+ uriString);

				String target = uriString.substring(uriString.lastIndexOf("/") + 1);
				String extension = "*" + uriString.substring(uriString.lastIndexOf("."));
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

}
