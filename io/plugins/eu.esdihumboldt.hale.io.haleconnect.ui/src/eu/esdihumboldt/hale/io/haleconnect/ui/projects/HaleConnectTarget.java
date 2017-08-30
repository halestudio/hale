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

package eu.esdihumboldt.hale.io.haleconnect.ui.projects;

import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectService.PERMISSION_CREATE;
import static eu.esdihumboldt.hale.io.haleconnect.HaleConnectService.RESOURCE_TRANSFORMATION_PROJECT;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfoService;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUrnBuilder;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.OwnerType;
import eu.esdihumboldt.hale.io.haleconnect.project.HaleConnectProjectReader;
import eu.esdihumboldt.hale.io.haleconnect.project.HaleConnectProjectWriter;
import eu.esdihumboldt.hale.io.haleconnect.ui.HaleConnectLoginDialog;
import eu.esdihumboldt.hale.io.haleconnect.ui.HaleConnectLoginHandler;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.target.AbstractTarget;

/**
 * hale connect export target
 * 
 * @author Florian Esser
 */
public class HaleConnectTarget extends AbstractTarget<HaleConnectProjectWriter> {

	private static final ALogger log = ALoggerFactory.getLogger(HaleConnectTarget.class);

	private final HaleConnectService haleConnect;

	private Label loginStatusLabel;
	private Button loginButton;
	private Button enableVersioning;
	private Button publicAccess;
	private Composite ownershipGroup;
	private Button ownerUser;
	private Button ownerOrg;
	private Button includeWebResources;
	private Button excludeData;
	private Button excludeCachedResources;
	private Group updateOrNewGroup;
	private Button newProject;
	private Button updateProject;
	private Composite controlsStack;
	private StackLayout controlsStackLayout;
	private Composite newProjectControls;
	private Composite updateProjectControls;
	private StringFieldEditor projectName;
	private Button selectProjectButton;
	private Label upstreamModifiedWarning;

	private boolean createNewProject;
	private HaleConnectProjectConfig targetProject;

	/**
	 * Default constructor
	 */
	public HaleConnectTarget() {
		haleConnect = HaleUI.getServiceProvider().getService(HaleConnectService.class);
	}

	@Override
	public void createControls(Composite parent) {
		getPage().setDescription("Please select a destination file for the export");

		parent.setLayout(new GridLayout(3, false));

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
					updateState();
					prefillTargetProject();
				}
			}

		});

		updateOrNewGroup = new Group(parent, SWT.NONE);
		updateOrNewGroup.setText("Please choose whether you would like to...");
		updateOrNewGroup.setLayout(new GridLayout(3, true));
		updateOrNewGroup.setLayoutData(new GridData(SWT.LEAD, SWT.LEAD, true, false, 3, 1));

		newProject = new Button(updateOrNewGroup, SWT.RADIO);
		newProject.setText("create a new project on hale connect");
		newProject.setSelection(true);
		newProject.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateState();
			}

		});

		updateProject = new Button(updateOrNewGroup, SWT.RADIO);
		updateProject.setText("update an existing project");
		updateProject.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateState();
			}

		});

		controlsStackLayout = new StackLayout();
		controlsStack = new Composite(parent, SWT.NONE);
		controlsStack.setLayout(controlsStackLayout);

		newProjectControls = new Composite(controlsStack, SWT.NONE);
		newProjectControls.setLayout(new GridLayout(3, true));
		GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		newProjectControls.setLayoutData(grid);

		ownershipGroup = new Composite(newProjectControls, SWT.NONE);
		ownershipGroup.setLayout(new GridLayout(3, false));
		ownershipGroup.setLayoutData(new GridData(SWT.LEAD, SWT.LEAD, false, false, 3, 1));

		Label ownerLabel = new Label(ownershipGroup, SWT.NONE);
		ownerLabel.setText("Who should own the uploaded project?");

		ownerUser = new Button(ownershipGroup, SWT.RADIO);
		ownerUser.setText("You");

		ownerOrg = new Button(ownershipGroup, SWT.RADIO);
		ownerOrg.setText("Your organisation");

		enableVersioning = new Button(newProjectControls, SWT.CHECK);
		enableVersioning.setText("Enable versioning?");
		enableVersioning.setLayoutData(new GridData(SWT.LEAD, SWT.LEAD, true, false, 3, 1));

		publicAccess = new Button(newProjectControls, SWT.CHECK);
		publicAccess.setText("Allow public access?");
		publicAccess.setLayoutData(new GridData(SWT.LEAD, SWT.LEAD, true, false, 3, 1));

		updateProjectControls = new Composite(controlsStack, SWT.NONE);
		updateProjectControls.setVisible(false);
		updateProjectControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		/*
		 * Project name text field
		 */
		projectName = new StringFieldEditor("project", "Project to update", updateProjectControls) {

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
		projectName.getTextControl(updateProjectControls).setEditable(false);
		projectName.getTextControl(updateProjectControls).addMouseListener(new MouseAdapter() {

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
					updateState();
				}
				else if (event.getProperty().equals(FieldEditor.VALUE)) {
					getPage().setMessage(null);
					updateState();
				}
			}
		});

		/*
		 * Select project button
		 */
		selectProjectButton = new Button(updateProjectControls, SWT.PUSH);
		selectProjectButton.setText("Select");
		selectProjectButton.setToolTipText("Select project");
		selectProjectButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		selectProjectButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectProject();
			}
		});

		FontData currentFont = loginStatusLabel.getFont().getFontData()[0];

		upstreamModifiedWarning = new Label(updateProjectControls, SWT.WRAP);
		upstreamModifiedWarning
				.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false, 3, 1));
		upstreamModifiedWarning.setFont(new Font(upstreamModifiedWarning.getDisplay(),
				new FontData(currentFont.getName(), currentFont.getHeight(), SWT.BOLD)));
		upstreamModifiedWarning.setVisible(false);

		Composite writerOptions = new Composite(parent, SWT.NONE);
		writerOptions.setLayout(new RowLayout());
		writerOptions.setLayoutData(new GridData(SWT.LEAD, SWT.LEAD, true, true, 3, 2));

		includeWebResources = new Button(writerOptions, SWT.CHECK);
		includeWebResources.setText("Include web resources?");

		excludeData = new Button(writerOptions, SWT.CHECK);
		excludeData.setText("Exclude source data?");
		excludeData.setSelection(true);

		excludeCachedResources = new Button(writerOptions, SWT.CHECK);
		excludeCachedResources.setText(
				"Use cached internal schema representation (required for big schema files)?");
		excludeCachedResources.setSelection(true);

		prefillTargetProject();

		updateState();
	}

	/**
	 * 
	 */
	private void prefillTargetProject() {
		if (!haleConnect.isLoggedIn()) {
			return;
		}

		ProjectInfoService pis = HaleUI.getServiceProvider().getService(ProjectInfoService.class);
		String projectUrnProperty = pis
				.getProperty(HaleConnectProjectReader.HALECONNECT_URN_PROPERTY)
				.getStringRepresentation();

		if (projectUrnProperty != null) {
			// If project was loaded from hale connect, prefill project name

			URI projectUrn = URI.create(projectUrnProperty);
			if (HaleConnectUrnBuilder.isValidProjectUrn(projectUrn)) {
				String projectId = HaleConnectUrnBuilder.extractProjectId(projectUrn);
				Owner owner = HaleConnectUrnBuilder.extractProjectOwner(projectUrn);

				try {
					if (!haleConnect.testProjectPermission(HaleConnectService.PERMISSION_EDIT,
							owner, projectId)) {
						return;
					}

					HaleConnectProjectInfo projectInfo = haleConnect.getProject(owner, projectId);
					if (projectInfo != null) {
						targetProject = new HaleConnectProjectConfig();
						targetProject.setOwner(owner);
						targetProject.setProjectId(projectId);
						targetProject.setProjectName(projectInfo.getName());
						targetProject.setLastModified(projectInfo.getLastModified());
						newProject.setSelection(false);
						updateProject.setSelection(true);
						projectName.setStringValue(projectInfo.getName());
					}
				} catch (Throwable t) {
					// Non-fatal
					log.warn(MessageFormat.format("Unable to prefill target project: {0}",
							t.getMessage()), t);
				}
			}
		}

	}

	/**
	 * Update the page state.
	 */
	protected void updateState() {
		updateLoginStatus();
		updateOverwriteWarning();

		setValid(haleConnect.isLoggedIn() && (ownerUser.isEnabled() || ownerOrg.isEnabled())
				&& (newProject.getSelection() || targetProject != null));
		if (newProject.getSelection()) {
			controlsStackLayout.topControl = newProjectControls;
			createNewProject = true;
		}
		else {
			controlsStackLayout.topControl = updateProjectControls;
			createNewProject = false;
		}
		controlsStack.layout();
	}

	@Override
	public void onShowPage(boolean firstShow) {
		super.onShowPage(firstShow);
		updateState();
	}

	@Override
	public boolean updateConfiguration(HaleConnectProjectWriter provider) {
		provider.setParameter(HaleConnectProjectWriter.ENABLE_VERSIONING,
				Value.of(enableVersioning.getSelection()));
		provider.setParameter(HaleConnectProjectWriter.SHARING_PUBLIC,
				Value.of(publicAccess.getSelection()));
		Value ownerValue = Value.of(ownerUser.getSelection() ? OwnerType.USER.getJsonValue()
				: OwnerType.ORGANISATION.getJsonValue());
		provider.setParameter(HaleConnectProjectWriter.OWNER_TYPE, ownerValue);
		provider.setParameter(ArchiveProjectWriter.EXLUDE_DATA_FILES,
				Value.of(excludeData.getSelection()));
		provider.setParameter(ArchiveProjectWriter.INCLUDE_WEB_RESOURCES,
				Value.of(includeWebResources.getSelection()));
		provider.setParameter(ArchiveProjectWriter.EXCLUDE_CACHED_RESOURCES,
				Value.of(excludeCachedResources.getSelection()));

		provider.setTarget(new LocatableOutputSupplier<OutputStream>() {

			@Override
			public OutputStream getOutput() throws IOException {
				return null;
			}

			@Override
			public URI getLocation() {
				if (createNewProject) {
					// Returning null will advise HaleConnectProjectWriter to
					// create a new hale connect transformation project
					return null;
				}
				else if (targetProject != null) {
					return HaleConnectUrnBuilder.buildProjectUrn(targetProject.getOwner(),
							targetProject.getProjectId());
				}
				else {
					throw new IllegalStateException("No target project selected.");
				}
			}

		});
		return true;
	}

	private void updateLoginStatus() {
		HaleConnectService hcs = HaleUI.getServiceProvider().getService(HaleConnectService.class);
		boolean loggedIn = hcs.isLoggedIn();
		loginButton.setEnabled(!loggedIn);
		ownershipGroup.setEnabled(loggedIn);
		enableVersioning.setEnabled(loggedIn);
		publicAccess.setEnabled(loggedIn);
		ownerUser.setEnabled(loggedIn);
		includeWebResources.setEnabled(loggedIn);
		excludeData.setEnabled(loggedIn);
		excludeCachedResources.setEnabled(loggedIn);
		selectProjectButton.setEnabled(loggedIn);
		newProject.setEnabled(loggedIn);
		updateProject.setEnabled(loggedIn);

		if (loggedIn) {
			loginStatusLabel
					.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
			loginStatusLabel.setText(
					MessageFormat.format("Logged in as {0}", hcs.getSession().getUsername()));

			boolean orgAllowed;
			if (hcs.getSession().getOrganisationIds().isEmpty()) {
				orgAllowed = false;
			}
			else {
				try {
					orgAllowed = hcs.testUserPermission(RESOURCE_TRANSFORMATION_PROJECT,
							hcs.getSession().getOrganisationIds().iterator().next(),
							PERMISSION_CREATE);
				} catch (Throwable t) {
					log.userError(
							"A problem occurred while contacting hale connect. Functionality may be limited.",
							t);
					orgAllowed = false;
				}
			}

			ownerOrg.setEnabled(orgAllowed);
			ownerOrg.setSelection(orgAllowed);

			boolean userAllowed;
			try {
				userAllowed = hcs.testUserPermission(RESOURCE_TRANSFORMATION_PROJECT,
						OwnerType.USER.getJsonValue(), PERMISSION_CREATE);
			} catch (Throwable t) {
				log.userError(
						"A problem occurred while contacting hale connect. Functionality may be limited.",
						t);
				userAllowed = false;
			}
			ownerUser.setEnabled(userAllowed);
			ownerUser.setSelection(userAllowed);

			if (!userAllowed && !orgAllowed) {
				loginStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
				loginStatusLabel
						.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
				loginStatusLabel.setText(
						"You do not have sufficient permissions to upload transformation projects to hale connect.");
			}
		}
		else {
			loginStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
			loginStatusLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			loginStatusLabel.setText(
					"You are not logged in to hale connect. Please login before sharing a project.");

			ownerOrg.setEnabled(false);
		}
	}

	private void selectProject() {
		targetProject = ChooseHaleConnectProjectWizard.openSelectProject();
		if (targetProject != null) {
			projectName.setStringValue(targetProject.getProjectName());
			updateOverwriteWarning();
		}
		else {
			projectName.setStringValue("");
		}
	}

	private void updateOverwriteWarning() {
		if (createNewProject || targetProject == null) {
			upstreamModifiedWarning.setVisible(false);
			return;
		}

		ProjectInfoService pis = HaleUI.getServiceProvider().getService(ProjectInfoService.class);

		String lastModifiedProperty = pis
				.getProperty(HaleConnectProjectReader.HALECONNECT_LAST_MODIFIED_PROPERTY)
				.getStringRepresentation();
		if (lastModifiedProperty != null && targetProject != null
				&& targetProject.getLastModified() != null) {
			Long lastModified = Long.parseLong(lastModifiedProperty);
			boolean hasNewerVersion = lastModified < targetProject.getLastModified();
			if (hasNewerVersion) {
				upstreamModifiedWarning
						.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
				upstreamModifiedWarning.setText(
						"The project on hale connect has been updated since it was last imported.\nChanges may be lost if you continue with this export.");
				upstreamModifiedWarning.setVisible(true);
			}
			else {
				upstreamModifiedWarning.setVisible(false);
			}
		}
		else {
			upstreamModifiedWarning
					.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
			upstreamModifiedWarning.setText(
					"The current project to be exported was not originally imported from hale connect.\nThe project to update on hale connect will be replaced by this project if you continue with this export.");
			upstreamModifiedWarning.setVisible(true);
		}

		updateProjectControls.layout();
	}
}
