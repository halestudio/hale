// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Graphical Information Systems (GIS)
//
// Copyright (c) 2004-2010 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package eu.esdihumboldt.hale.rcp.utils.proxy;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.Messages;

/**
 * A preference page for the proxy connection
 * @author Michel Kraemer
 */
public class ProxyPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
//	private static final ALogger _log = ALoggerFactory.getLogger(ProxyPreferencePage.class);

	/**
	 * Default constructor
	 */
	public ProxyPreferencePage() {
		super(GRID);
		setPreferenceStore(HALEActivator.getDefault().getPreferenceStore());
		setDescription(Messages.getString("ProxyPreferencePage.0")); //$NON-NLS-1$
	}
	
	/**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// proxy host
		addField(new StringFieldEditor(
				PreferenceConstants.CONNECTION_PROXY_HOST, Messages.getString("ProxyPreferencePage.1"), //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy port
		addField(new IntegerFieldEditor(
				PreferenceConstants.CONNECTION_PROXY_PORT, Messages.getString("ProxyPreferencePage.2"), //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy user name
		addField(new StringFieldEditor(
				PreferenceConstants.CONNECTION_PROXY_USER, Messages.getString("ProxyPreferencePage.5"), //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy password
		addField(new PasswordFieldEditor(
				PreferenceConstants.SECURE_NODE_NAME, 
				PreferenceConstants.CONNECTION_PROXY_PASSWORD,
				Messages.getString("ProxyPreferencePage.6"), //$NON-NLS-1$
				getFieldEditorParent()));
		// non proxy hosts
		addField(new StringFieldEditor(
				PreferenceConstants.CONNECTION_NON_PROXY_HOSTS, Messages.getString("ProxyPreferencePage.3"), //$NON-NLS-1$
				getFieldEditorParent()));
		// placeholder
		Composite ph = new Composite(getFieldEditorParent(), SWT.NONE);
		ph.setLayoutData(GridDataFactory.swtDefaults().hint(0, 0).create());
		// info label
		Label info = new Label(getFieldEditorParent(), SWT.NONE);
		info.setText(Messages.getString("ProxyPreferencePage.4")); //$NON-NLS-1$
		info.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));
	}
	
	/**
	 * @see FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		if (!result) {
			return false;
		}
		
		ProxySettings.applyCurrentSettings();
		
		return true;
	}

	/**
	 * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		//nothing to do here
	}
}
