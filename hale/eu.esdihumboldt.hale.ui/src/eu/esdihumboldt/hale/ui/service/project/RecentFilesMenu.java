/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
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
 * A menu filled with the list of recently opened
 * files (MRU).
 * @author Michel Kraemer
 * @author Simon Templer
 */
public class RecentFilesMenu extends ContributionItem {
	
	/**
	 * A selection listener for the menu items
	 */
	private static class MenuItemSelectionListener extends SelectionAdapter {
		
		/**
		 * The data source to open when the menu item has been selected
		 */
		private File file;
		
		/**
		 * Default constructor
		 * @param file the project file to open when the menu item has
		 * been selected
		 */
		public MenuItemSelectionListener(File file) {
			this.file = file;
		}
		
		/**
		 * @see SelectionAdapter#widgetSelected(SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ProjectService ps = (ProjectService) PlatformUI.getWorkbench().getService(ProjectService.class);
			ps.load(file);
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
		RecentFilesService rfs = (RecentFilesService)PlatformUI
			.getWorkbench().getService(RecentFilesService.class);
		String[] files = rfs.getRecentFiles();
		if (files == null || files.length == 0) {
			return;
		}
		
		//add separator
		new MenuItem(menu, SWT.SEPARATOR, index);
		
		int i = files.length;
		for (String file : files) {
			MenuItem mi = new MenuItem(menu, SWT.PUSH, index);
			String ustr = FilenameUtils.getName(file);
			String nr = String.valueOf(i);
			if (i <= 9) {
				//add mnemonic for the first 9 items
				nr = "&" + nr; //$NON-NLS-1$
			}
			mi.setText(nr + "  " + ustr); //$NON-NLS-1$
			mi.setData(file);
			mi.addSelectionListener(new MenuItemSelectionListener(new File(file)));
			--i;
		}
	}
}
