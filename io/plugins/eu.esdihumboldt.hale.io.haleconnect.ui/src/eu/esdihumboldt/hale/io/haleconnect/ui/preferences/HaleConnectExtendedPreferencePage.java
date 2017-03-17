/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectImages;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;
import eu.esdihumboldt.hale.ui.HaleUI;

/**
 * Preferences page for extended hale connect settings
 * 
 * @author Florian Esser
 */
public class HaleConnectExtendedPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * Creates new extended preferences page
	 */
	public HaleConnectExtendedPreferencePage() {
		super(GRID);
		setPreferenceStore(HaleConnectUIPlugin.getDefault().getPreferenceStore());
		setDescription(
				"Extended preferences for hale connect. Changing these values may break hale connect integration.");
		this.setImageDescriptor(HaleConnectImages.getImageRegistry()
				.getDescriptor(HaleConnectImages.IMG_HCLOGO_PREFERENCES));
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	@Override
	protected void performApply() {
		super.performApply();

		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		hcs.setBasePath(HaleConnectUIPlugin.getStoredBasePath());
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(PreferenceConstants.HALE_CONNECT_BASEPATH_USERS,
				"User service base path (URL):", getFieldEditorParent()));
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// nothing
	}
}
