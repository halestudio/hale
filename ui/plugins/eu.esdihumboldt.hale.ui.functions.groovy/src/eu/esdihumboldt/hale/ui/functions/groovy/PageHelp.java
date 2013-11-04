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

package eu.esdihumboldt.hale.ui.functions.groovy;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.esdihumboldt.hale.ui.common.CommonSharedImages;

/**
 * Tools for dialog page help.
 * 
 * @author Simon Templer
 */
public class PageHelp {

	/**
	 * Create a tool item that calls {@link IDialogPage#performHelp()} on the
	 * given dialog page.
	 * 
	 * @param bar the tool bar
	 * @param page the dialog page
	 */
	public static void createToolItem(ToolBar bar, final IDialogPage page) {
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setToolTipText("Show help");
		item.setImage(CommonSharedImages.getImageRegistry().get(CommonSharedImages.IMG_HELP));

		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				page.performHelp();
			}
		});
	}

}
