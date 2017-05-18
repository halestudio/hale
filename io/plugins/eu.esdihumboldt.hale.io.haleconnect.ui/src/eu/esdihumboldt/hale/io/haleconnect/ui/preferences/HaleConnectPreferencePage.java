package eu.esdihumboldt.hale.io.haleconnect.ui.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.MessageDialog;
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

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
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

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectPreferencePage.class);

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
		final HaleConnectService hcs = HaleUI.getServiceProvider()
				.getService(HaleConnectService.class);

		StringFieldEditor usernameEditor = new StringFieldEditor(
				PreferenceConstants.HALE_CONNECT_USERNAME, "&User name:", getFieldEditorParent());
		addField(usernameEditor);

		usernameEditor.getTextControl(getFieldEditorParent())
				.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						clearResult();
					}
				});

		PasswordFieldEditor passwordEditor = new PasswordFieldEditor(
				PreferenceConstants.SECURE_NODE_NAME, PreferenceConstants.HALE_CONNECT_PASSWORD,
				"&Password:", // $NON-NLS-1$
				getFieldEditorParent());
		addField(passwordEditor);

		passwordEditor.getTextControl().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				clearResult();
			}
		});

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

		resultLabel = new Label(getFieldEditorParent(), SWT.NONE);
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		clearResult();

		Button validate = new Button(getFieldEditorParent(), SWT.NONE);
		validate.setText("Validate credentials");

		Button clear = new Button(getFieldEditorParent(), SWT.NONE);
		clear.setText("Clear credentials");

		validate.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				performApply();

				try {
					String username = HaleConnectUIPlugin.getStoredUsername();
					String password = HaleConnectUIPlugin.getStoredPassword();
					if (hcs.verifyCredentials(username, password)) {
						setResultSuccess("Credentials are valid.");
					}
					else {
						setResultError("Credentials were rejected.");
					}
				} catch (HaleConnectException ex) {
					log.userError("Error accessing hale connect", ex);
				} catch (StorageException ex) {
					log.userError("Error accessing secure storage", ex);
				}
			}

		});

		clear.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(getShell(), "Remove credentials",
						"This will remove the stored credentials and log you out.")) {
					usernameEditor.setStringValue("");
					passwordEditor.getTextControl().setText("");
					performApply();
					clearResult();
					hcs.clearSession();
					log.userInfo("Credentials removed.");
				}
			}

		});

		if (hcs.isLoggedIn()) {
			setResult(MessageFormat.format("Logged in as \"{0}\"", hcs.getSession().getUsername()));
		}
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

	private void setResult(String text) {
		resultLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		resultLabel.setText(text);
	}
}