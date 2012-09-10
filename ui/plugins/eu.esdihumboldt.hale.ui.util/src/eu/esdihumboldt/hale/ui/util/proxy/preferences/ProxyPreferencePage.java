/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.ui.util.proxy.preferences;

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

import eu.esdihumboldt.hale.ui.util.components.PasswordFieldEditor;
import eu.esdihumboldt.hale.ui.util.internal.Messages;
import eu.esdihumboldt.hale.ui.util.internal.UIUtilitiesPlugin;
import eu.esdihumboldt.hale.ui.util.proxy.ProxySettings;

/**
 * A preference page for the proxy connection
 * 
 * @author Michel Kraemer
 */
public class ProxyPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

//	private static final ALogger _log = ALoggerFactory.getLogger(ProxyPreferencePage.class);

	/**
	 * Default constructor
	 */
	public ProxyPreferencePage() {
		super(GRID);
		setPreferenceStore(UIUtilitiesPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ProxyPreferencePage_0); //$NON-NLS-1$
	}

	/**
	 * @see FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors() {
		// proxy host
		addField(new StringFieldEditor(PreferenceConstants.CONNECTION_PROXY_HOST,
				Messages.ProxyPreferencePage_1, //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy port
		addField(new IntegerFieldEditor(PreferenceConstants.CONNECTION_PROXY_PORT,
				Messages.ProxyPreferencePage_2, //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy user name
		addField(new StringFieldEditor(PreferenceConstants.CONNECTION_PROXY_USER,
				Messages.ProxyPreferencePage_5, //$NON-NLS-1$
				getFieldEditorParent()));
		// proxy password
		addField(new PasswordFieldEditor(PreferenceConstants.SECURE_NODE_NAME,
				PreferenceConstants.CONNECTION_PROXY_PASSWORD, Messages.ProxyPreferencePage_6, //$NON-NLS-1$
				getFieldEditorParent()));
		// non proxy hosts
		addField(new StringFieldEditor(PreferenceConstants.CONNECTION_NON_PROXY_HOSTS,
				Messages.ProxyPreferencePage_3, //$NON-NLS-1$
				getFieldEditorParent()));
		// placeholder
		Composite ph = new Composite(getFieldEditorParent(), SWT.NONE);
		ph.setLayoutData(GridDataFactory.swtDefaults().hint(0, 0).create());
		// info label
		Label info = new Label(getFieldEditorParent(), SWT.NONE);
		info.setText(Messages.ProxyPreferencePage_4); //$NON-NLS-1$
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
		// nothing to do here
	}
}
