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
		ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(
				ProjectService.class);
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
