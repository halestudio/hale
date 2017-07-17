/*
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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.ui.HaleConnectLoginDialog;
import eu.esdihumboldt.hale.io.haleconnect.ui.HaleConnectLoginHandler;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.ImportSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractProviderSource;
import eu.esdihumboldt.hale.ui.io.source.AbstractSource;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;

/**
 * Provider source for hale connect projects
 * 
 * @author Florian Esser
 * @param <P> Import provider
 * 
 */
public class HaleConnectSource<P extends ImportProvider> extends AbstractProviderSource<P> {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectSource.class);

	private Label loginStatusLabel;
	private Button loginButton;
	private StringFieldEditor projectName;
	private Button selectProjectButton;
	private HaleConnectProjectConfig selectedProject;
	private Set<IContentType> supportedTypes;

	private class ProjectLoader implements IRunnableWithProgress {

		public LocatableInputSupplier<InputStream> source;
		public Throwable error;

		/**
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Load project from hale connect", IProgressMonitor.UNKNOWN);

			HaleConnectService hcs = HaleUI.getServiceProvider()
					.getService(HaleConnectService.class);

			try {
				this.source = hcs.loadProject(selectedProject.getOwner(),
						selectedProject.getProjectId());
			} catch (Throwable t) {
				error = t;
			}

			monitor.done();
		}

		/**
		 * @return true if source was successfully initialized
		 */
		public boolean success() {
			return error != null && source != null;
		}
	}

	/**
	 * @see ImportSource#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);

		/*
		 * Login status label
		 */
		loginStatusLabel = new Label(parent, SWT.NONE);
		loginStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		loginButton = new Button(parent, SWT.PUSH);
		loginButton.setText("Login");
		loginButton.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				HaleConnectLoginDialog loginDialog = HaleConnectLoginHandler
						.createLoginDialog(Display.getCurrent().getActiveShell());
				if (loginDialog.open() == Dialog.OK) {
					HaleConnectLoginHandler.performLogin(loginDialog);
					updateLoginStatus();
				}
			}

		});

		/*
		 * Project name text field
		 */
		projectName = new StringFieldEditor("project", "Project", parent) {

			// the following methods are overridden so the button
			// may appear on the same line

			@Override
			public int getNumberOfControls() {
				return super.getNumberOfControls() + 1;
			}

			@Override
			protected void doFillIntoGrid(Composite parent, int numColumns) {
				super.doFillIntoGrid(parent, numColumns - 1);
			}
		};
		projectName.setEmptyStringAllowed(false);
		projectName.setErrorMessage("Please select a project before continuing.");
		projectName.setPage(getPage());
		projectName.getTextControl(parent).setEditable(false);
		projectName.getTextControl(parent).addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				selectProject();
			}

		});

		projectName.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
					getPage().setMessage(null);
					updateState(false);
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					getPage().setMessage(null);
					updateState(false);
				}
			}
		});

		/*
		 * Select project button
		 */
		selectProjectButton = new Button(parent, SWT.PUSH);
		selectProjectButton.setText("Select");
		selectProjectButton.setToolTipText("Select project");
		selectProjectButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectProjectButton.setEnabled(hcs.isLoggedIn());
		selectProjectButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectProject();
			}
		});

		/*
		 * Supported types
		 */
		supportedTypes = new HashSet<IContentType>();
		if (getConfiguration().getContentType() != null) {
			supportedTypes.add(getConfiguration().getContentType());
		}
		else {
			// set content types for file field
			Collection<IOProviderDescriptor> factories = getConfiguration().getFactories();
			for (IOProviderDescriptor factory : factories) {
				supportedTypes.addAll(factory.getSupportedTypes());
			}
		}

		// types combo
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		group.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		// label
		Label providerLabel = new Label(parent, SWT.NONE);
		providerLabel.setText("Import as");

		// create provider combo
		ComboViewer providers = createProviders(parent);
		providers.getControl()
				.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));

		updateLoginStatus();

		// initial state update
		updateState(true);
	}

	/**
	 * @see AbstractProviderSource#updateContentType()
	 */
	@Override
	protected void updateContentType() {
		IContentType contentType = getConfiguration().getContentType();

		if (projectName.isValid()) {
			// determine content type
			Collection<IContentType> filteredTypes = HaleIO.findContentTypesFor(supportedTypes,
					getSource(), projectName.getStringValue());
			if (!filteredTypes.isEmpty()) {
				contentType = filteredTypes.iterator().next();
			}
		}

		getConfiguration().setContentType(contentType);
		if (contentType != null) {
			getPage().setMessage(contentType.getName(), DialogPage.INFORMATION);
		}
		else {
			getPage().setMessage(null);
		}

		super.updateContentType();
	}

	private void selectProject() {
		selectedProject = ChooseHaleConnectProjectWizard.openSelectProject();
		if (selectedProject != null) {
			projectName.setStringValue(selectedProject.getProjectName());
		}
		else {
			projectName.setStringValue("");
		}
	}

	/**
	 * @see AbstractProviderSource#isValidSource()
	 */
	@Override
	protected boolean isValidSource() {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		return hcs.isLoggedIn() && selectedProject != null;
	}

	/**
	 * @see AbstractProviderSource#getSource()
	 */
	@Override
	protected LocatableInputSupplier<? extends InputStream> getSource() {

		ProjectLoader loader = new ProjectLoader();
		try {
			ThreadProgressMonitor.runWithProgressDialog(loader, false);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		if (!loader.success()) {
			log.userError("Error loading project from hale connect", loader.error);
			return null;
		}

		return loader.source;
	}

	/**
	 * @see AbstractSource#onActivate()
	 */
	@Override
	public void onActivate() {
		selectProjectButton.setFocus();
	}

	private void updateLoginStatus() {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		loginButton.setEnabled(!hcs.isLoggedIn());
		selectProjectButton.setEnabled(hcs.isLoggedIn());
		if (hcs.isLoggedIn()) {
			loginStatusLabel
					.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
			loginStatusLabel.setText(
					MessageFormat.format("Logged in as {0}", hcs.getSession().getUsername()));
		}
		else {
			loginStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			loginStatusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			loginStatusLabel.setText(
					"You are not logged in to hale connect. Please login before opening a project.");
		}
	}

}
