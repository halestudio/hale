/**
 * 
 */
package eu.esdihumboldt.hale.rcp.utils;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.Messages;


/**
 * Dialog for network settings
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class NetworkSettingsDialog extends Dialog {

	/**
	 * Constructor
	 * 
	 * @param parent the parent shell
	 */
	public NetworkSettingsDialog(Shell parent) {
		super(parent);
	}
	
	/**
	 * Open the dialog
	 */
	public void open () {
		Shell parent = super.getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(400, 190);
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
		shell.setText("Configure your Network Settings"); //$NON-NLS-1$
		
		this.createControls(shell);
		
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
	}

	private void createControls(Shell shell) {
		// Create Fields for Proxy definition
		final Composite c = new Composite(shell, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL |
				GridData.GRAB_VERTICAL | GridData.FILL_VERTICAL));
		
		final Group proxyDefinitionArea = new Group(c, SWT.NONE);
		proxyDefinitionArea.setText("Configure HTTP Proxy"); //$NON-NLS-1$
		proxyDefinitionArea.setLayout(new GridLayout());
		proxyDefinitionArea.setLayoutData( new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		
		// Proxy Host
		final Label proxyHostLabel = new Label(proxyDefinitionArea, SWT.NONE);
		proxyHostLabel.setText("Proxy Server Host"); //$NON-NLS-1$
		proxyHostLabel.setToolTipText("Enter the Hostname of your Proxy Server"); //$NON-NLS-1$
		final Text proxyHostText = new Text (proxyDefinitionArea, SWT.BORDER | SWT.SINGLE);
		proxyHostText.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		proxyHostText.setText(
				System.getProperty("http.proxyHost") == null  //$NON-NLS-1$
						? ""  //$NON-NLS-1$
						: System.getProperty("http.proxyHost")); //$NON-NLS-1$
		proxyHostText.setEditable(true);
		
		// Proxy Port
		final Label proxyPortLabel = new Label(proxyDefinitionArea, SWT.NONE);
		proxyPortLabel.setText("Proxy Server Port"); //$NON-NLS-1$
		proxyPortLabel.setToolTipText("Enter the Port of your Proxy Server"); //$NON-NLS-1$
		final Text proxyPortText = new Text (proxyDefinitionArea, SWT.BORDER | SWT.SINGLE);
		proxyPortText.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		proxyPortText.setText(
				System.getProperty("http.proxyPort") == null  //$NON-NLS-1$
				? ""  //$NON-NLS-1$
				: System.getProperty("http.proxyPort")); //$NON-NLS-1$
		proxyPortText.setEditable(true);
		
		// Cancel/Finish buttons
		Composite buttons = new Composite(c, SWT.BOTTOM);
		buttons.setLayoutData( new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		GridLayout buttonsLayout = new GridLayout();
		buttonsLayout.numColumns = 3;
		buttonsLayout.makeColumnsEqualWidth = false;
		buttons.setLayout(buttonsLayout);
		
		final Button finish = new Button(buttons, SWT.NONE);
		finish.setAlignment(SWT.RIGHT);
		finish.setText("Save Network Settings"); //$NON-NLS-1$
		finish.setEnabled(true);
		finish.setSize(100, 24);
		finish.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				String port = proxyPortText.getText();
				String host = proxyHostText.getText();
				// set System properties
				if (port != null && !port.equals("")) { //$NON-NLS-1$
					System.setProperty("http.proxyPort", port); //$NON-NLS-1$
				}
				else {
					if (System.getProperty("http.proxyPort") != null) { //$NON-NLS-1$
						System.clearProperty("http.proxyPort"); //$NON-NLS-1$
					}
				}
				if (host != null && !host.equals("")) { //$NON-NLS-1$
					System.setProperty("http.proxyHost", host); //$NON-NLS-1$
				}
				else {
					if (System.getProperty("http.proxyHost") != null) { //$NON-NLS-1$
						System.clearProperty("http.proxyHost"); //$NON-NLS-1$
					}
				}
				
				// store eclipse preferences for making settings persistent
				IPreferenceStore preferences = HALEActivator.getDefault().getPreferenceStore();
				preferences.putValue("http.proxyPort", port); //$NON-NLS-1$
				preferences.putValue("http.proxyHost", host); //$NON-NLS-1$
				
				finish.getParent().getParent().getShell().dispose();
			}
		});
		
		Button cancel = new Button(buttons, SWT.NONE);
		cancel.setAlignment(SWT.RIGHT);
		cancel.setText(Messages.WFSFeatureTypesReaderDialog_CancelText);
		cancel.setSize(100, 24);
		cancel.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				finish.getParent().getParent().getShell().dispose();
			}
		});
	}

}
