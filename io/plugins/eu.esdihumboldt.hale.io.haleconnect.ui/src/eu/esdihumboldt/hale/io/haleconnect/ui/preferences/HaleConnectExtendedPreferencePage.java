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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
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

	private static final ALogger log = ALoggerFactory
			.getLogger(HaleConnectExtendedPreferencePage.class);

	private BooleanFieldEditor defaults;
	private StringFieldEditor clientBasepath;
	private StringFieldEditor usersBasepath;
	private StringFieldEditor dataBasepath;
	private StringFieldEditor projectsBasepath;

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
		updateBasepaths();
		super.performApply();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		updateBasepaths();
		return super.performOk();
	}

	private void updateBasepaths() {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);

		if (defaults.getBooleanValue()) {
			// Force default values
			hcs.getBasePathManager().setBasePath(HaleConnectServices.USER_SERVICE,
					PreferenceInitializer.HALE_CONNECT_BASEPATH_USERS_DEFAULT);
			hcs.getBasePathManager().setBasePath(HaleConnectServices.BUCKET_SERVICE,
					PreferenceInitializer.HALE_CONNECT_BASEPATH_DATA_DEFAULT);
			hcs.getBasePathManager().setBasePath(HaleConnectServices.PROJECT_STORE,
					PreferenceInitializer.HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT);
			hcs.getBasePathManager().setBasePath(HaleConnectServices.WEB_CLIENT,
					PreferenceInitializer.HALE_CONNECT_BASEPATH_CLIENT_DEFAULT);
		}
		else {
			// Use individually configured values
			hcs.getBasePathManager().setBasePath(HaleConnectServices.USER_SERVICE,
					usersBasepath.getStringValue());
			hcs.getBasePathManager().setBasePath(HaleConnectServices.BUCKET_SERVICE,
					dataBasepath.getStringValue());
			hcs.getBasePathManager().setBasePath(HaleConnectServices.PROJECT_STORE,
					projectsBasepath.getStringValue());
			hcs.getBasePathManager().setBasePath(HaleConnectServices.WEB_CLIENT,
					clientBasepath.getStringValue());
		}

		if (hcs.isLoggedIn()) {
			hcs.clearSession();
			MessageDialog.openInformation(getShell(), "hale connect preferences",
					"You have been logged out from hale connect.");
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		addField(defaults = new BooleanFieldEditor(
				PreferenceConstants.HALE_CONNECT_BASEPATH_USE_DEFAULTS,
				"Use defaults for haleconnect.com?", getFieldEditorParent()) {

			/**
			 * @see org.eclipse.jface.preference.FieldEditor#createControl(org.eclipse.swt.widgets.Composite)
			 */
			@Override
			protected void createControl(Composite parent) {
				super.createControl(parent);

				getChangeControl(parent).addSelectionListener(new SelectionAdapter() {

					/**
					 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
					 */
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean enable = !getChangeControl(getFieldEditorParent()).getSelection();
						updateBasepathControls(enable);
					}

				});
			}

		});

		addField(clientBasepath = new StringFieldEditor(
				PreferenceConstants.HALE_CONNECT_BASEPATH_CLIENT, "Web client base path (URL):",
				getFieldEditorParent()));
		addField(usersBasepath = new StringFieldEditor(
				PreferenceConstants.HALE_CONNECT_BASEPATH_USERS, "User service base path (URL):",
				getFieldEditorParent()));
		addField(
				dataBasepath = new StringFieldEditor(PreferenceConstants.HALE_CONNECT_BASEPATH_DATA,
						"Bucket service base path (URL):", getFieldEditorParent()));
		addField(projectsBasepath = new StringFieldEditor(
				PreferenceConstants.HALE_CONNECT_BASEPATH_PROJECTS,
				"Project store base path (URL):", getFieldEditorParent()));

		updateBasepathControls(!HaleConnectUIPlugin.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.HALE_CONNECT_BASEPATH_USE_DEFAULTS));
	}

	private void enableIfExists(FieldEditor control, Composite parent, boolean enabled) {
		if (control != null) {
			control.setEnabled(enabled, parent);
		}
	}

	private void updateBasepathControls(boolean enabled) {
		enableIfExists(clientBasepath, getFieldEditorParent(), enabled);
		enableIfExists(usersBasepath, getFieldEditorParent(), enabled);
		enableIfExists(dataBasepath, getFieldEditorParent(), enabled);
		enableIfExists(projectsBasepath, getFieldEditorParent(), enabled);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// nothing
	}
}
