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

package eu.esdihumboldt.hale.ui.service.project.internal;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * Project preference page.
 * 
 * @author Simon Templer
 */
public class ProjectPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Default constructor.
	 */
	public ProjectPreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);

		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(page);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(page);

		// info icon and message
		Composite warnComp = new Composite(page, SWT.NONE);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(warnComp);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(warnComp);

		Label warnImage = new Label(warnComp, SWT.NONE);
		warnImage.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJS_INFO_TSK));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).applyTo(warnImage);

		Label warn = new Label(warnComp, SWT.WRAP);
		warn.setText(
				"All settings on sub-pages to this page are saved in the current project, not for the application.");
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
				.hint(300, SWT.DEFAULT).applyTo(warn);

		return page;
	}

	@Override
	public void init(IWorkbench workbench) {
		// nothing to do
	}

}
