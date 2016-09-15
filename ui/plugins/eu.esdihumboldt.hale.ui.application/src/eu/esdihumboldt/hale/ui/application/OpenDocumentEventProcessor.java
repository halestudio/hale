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

package eu.esdihumboldt.hale.ui.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.ui.service.project.ProjectService;

/**
 * Processes {@link SWT#OpenDocument} events
 * 
 * @author Simon Templer
 * @see "http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/guide/product_open_file.htm"
 */
public class OpenDocumentEventProcessor implements Listener {

	private final List<String> filesToOpen = new ArrayList<String>(1);

	/**
	 * @see Listener#handleEvent(Event)
	 */
	@Override
	public void handleEvent(Event event) {
		if (event.text != null) {
			synchronized (filesToOpen) {
				filesToOpen.add(event.text);
			}
		}
	}

	/**
	 * Open waiting files
	 */
	public void openFiles() {
		// project service is needed
		ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
		if (ps == null) {
			return;
		}

		String[] filePaths;

		synchronized (filesToOpen) {
			if (filesToOpen.isEmpty())
				return;
			filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
			filesToOpen.clear();
		}

		// open files

		// currently only projects are supported and one project will override
		// another, so just take the last file
		String path = filePaths[filePaths.length - 1];
		File file = new File(path);

		ps.load(file.toURI());
	}

}
