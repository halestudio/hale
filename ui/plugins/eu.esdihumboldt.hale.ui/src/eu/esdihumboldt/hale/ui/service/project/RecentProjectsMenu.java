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

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;

/**
 * A menu filled with the list of recently opened files (MRU).
 * 
 * @author Michel Kraemer
 * @author Simon Templer
 */
public class RecentProjectsMenu extends ContributionItem {

	/**
	 * The string filled in for the gap in the filename
	 */
	public static final String FILLSTRING = "...";

	/**
	 * Maximum length of the string displayed in the menu
	 */
	public static final int MAX_LENGTH = 40;

	/**
	 * A selection listener for the menu items
	 */
	private static class MenuItemSelectionListener extends SelectionAdapter {

		/**
		 * The data source to open when the menu item has been selected
		 */
		private final File file;

		/**
		 * Default constructor
		 * 
		 * @param file the project file to open when the menu item has been
		 *            selected
		 */
		public MenuItemSelectionListener(File file) {
			this.file = file;
		}

		/**
		 * @see SelectionAdapter#widgetSelected(SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ProjectService ps = PlatformUI.getWorkbench().getService(ProjectService.class);
			ps.load(file.toURI());
		}
	}

	/**
	 * @see ContributionItem#isDynamic()
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}

	/**
	 * @see ContributionItem#fill(Menu, int)
	 */
	@Override
	public void fill(final Menu menu, int index) {
		RecentProjectsService rfs = PlatformUI.getWorkbench()
				.getService(RecentProjectsService.class);
		RecentProjectsService.Entry[] entries = rfs.getRecentFiles();
		if (entries == null || entries.length == 0) {
			return;
		}

		// add separator
		new MenuItem(menu, SWT.SEPARATOR, index);

		int i = entries.length;
		for (RecentProjectsService.Entry entry : entries) {
			String file = entry.getFile();
			MenuItem mi = new MenuItem(menu, SWT.PUSH, index);
			String filename = FilenameUtils.getName(file);
			String shortened = shorten(file, MAX_LENGTH, filename.length());
			String nr = String.valueOf(i);
			if (i <= 9) {
				// add mnemonic for the first 9 items
				nr = "&" + nr; //$NON-NLS-1$
			}
			mi.setText(nr + "  " + shortened); //$NON-NLS-1$
			mi.setData(file);
			mi.addSelectionListener(new MenuItemSelectionListener(new File(file)));
			--i;
		}
	}

	/**
	 * Shortens the given file path.
	 * 
	 * @param file the complete file path to shorten
	 * @param maxLength the maximum length the shortened verison should have
	 * @param endKeep the length at the end of the string that should not be
	 *            removed
	 * @return a shortened version of the file path
	 */
	public static String shorten(String file, int maxLength, int endKeep) {
		if (file.length() <= maxLength) {
			return file;
		}
		else if (maxLength - endKeep > (FILLSTRING.length() + 2)) {
			int partLength = (maxLength - endKeep - FILLSTRING.length()) / 2;
			StringBuffer buff = new StringBuffer();
			buff.append(file.substring(0, partLength));
			buff.append(FILLSTRING);
			buff.append(file.substring(file.length() - endKeep - partLength));
			return buff.toString();
		}
		else {
			return file.substring(file.length() - endKeep);
		}
	}
}
