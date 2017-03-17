package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectImages;
import eu.esdihumboldt.hale.io.haleconnect.ui.internal.HaleConnectUIPlugin;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.util.components.PasswordFieldEditor;

/**
 * Preferences page for hale connect settings
 * 
 * @author Florian Esser
 */
public class HaleConnectPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private Label resultLabel;

	/**
	 * Creates a new hale connect preferences page
	 */
	public HaleConnectPreferencePage() {
		super(GRID);
		setPreferenceStore(HaleConnectUIPlugin.getDefault().getPreferenceStore());

		this.setImageDescriptor(HaleConnectImages.getImageRegistry()
				.getDescriptor(HaleConnectImages.IMG_HCLOGO_PREFERENCES));
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		StringFieldEditor usernameEditor = new StringFieldEditor(
				PreferenceConstants.HALE_CONNECT_USERNAME, "&User name:", getFieldEditorParent());
		addField(usernameEditor);

		PasswordFieldEditor passwordEditor = new PasswordFieldEditor(
				PreferenceConstants.SECURE_NODE_NAME, PreferenceConstants.HALE_CONNECT_PASSWORD,
				"&Password:", // $NON-NLS-1$
				getFieldEditorParent());
		addField(passwordEditor);

		Link link = new Link(getFieldEditorParent(), SWT.NONE);
		link.setText(
				"Not registered yet? <a href=\"https://www.haleconnect.com\">Create an account.</a>");
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
							.openURL(new URL(e.text));
				} catch (PartInitException | MalformedURLException e1) {
					setResultError(
							"Error opening external browser. Please visit https://www.haleconnect.com to register.");
				}
			}
		});
		link.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		Button login = new Button(getFieldEditorParent(), SWT.NONE);
		login.setText("Login");

		resultLabel = new Label(getFieldEditorParent(), SWT.NONE);
		resultLabel.setText(" ");
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		usernameEditor.getTextControl(getFieldEditorParent())
				.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						clearResult();
					}
				});

		passwordEditor.getTextControl().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				clearResult();
			}
		});

		login.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				performApply();

				HaleConnectService hcs = HaleUI.getServiceProvider()
						.getService(HaleConnectService.class);

				try {
					String username = HaleConnectUIPlugin.getStoredUsername();
					String password = HaleConnectUIPlugin.getStoredPassword();
					if (hcs.login(username, password)) {
						setResultSuccess("Login successful.");
					}
					else {
						setResultError("Login failed.");
					}
				} catch (HaleConnectException ex) {
					setResultError("Error accessing hale connect.");
				} catch (StorageException ex) {
					setResultError("Error accessing preferences store.");
				}
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// nothing
	}

	private void clearResult() {
		resultLabel.setText(" ");
	}

	private void setResultSuccess(String text) {
		resultLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
		resultLabel.setText(text);
	}

	private void setResultError(String text) {
		resultLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		resultLabel.setText(text);
	}
}