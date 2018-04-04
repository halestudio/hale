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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectException;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectProjectInfo;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectService;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectServices;
import eu.esdihumboldt.hale.io.haleconnect.HaleConnectUserInfo;
import eu.esdihumboldt.hale.io.haleconnect.Owner;
import eu.esdihumboldt.hale.io.haleconnect.OwnerType;
import eu.esdihumboldt.hale.io.haleconnect.ui.preferences.PreferenceInitializer;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizard;
import eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage;

/**
 * Wizard page for listing and choosing one of the available hale connect
 * projects.
 * 
 * @author Florian Esser
 */
public class ChooseHaleConnectProjectWizardPage
		extends ConfigurationWizardPage<HaleConnectProjectConfig> {

	private static final ALogger log = ALoggerFactory
			.getLogger(ChooseHaleConnectProjectWizardPage.class);
	private static final OwnerFilterEntry NULL_FILTER = new OwnerFilterEntry(null, "All projects");

	private final HaleConnectService haleConnect;

	private Text keywordFilter;
	private ComboViewer ownerFilter;
	private TableViewer projects;

	private class GetProjectsCallback implements FutureCallback<List<HaleConnectProjectInfo>> {

		@Override
		public void onSuccess(List<HaleConnectProjectInfo> result) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (projects == null) {
						return;
					}

					synchronized (projects) {
						Object currentInput = projects.getInput();
						if (currentInput != null && currentInput instanceof List<?>) {
							@SuppressWarnings("unchecked")
							Set<HaleConnectProjectInfo> updatedProjects = new LinkedHashSet<>(
									(List<HaleConnectProjectInfo>) currentInput);
							updatedProjects.addAll(result);
							projects.setInput(updatedProjects);
						}
						else {
							projects.setInput(result);
						}
						projects.getTable().setEnabled(true);
					}
				}
			});
		}

		@Override
		public void onFailure(Throwable t) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (haleConnect == null) {
						log.userError(
								"Unable to connect to hale connect, please check your network connection.",
								t);
						return;
					}

					if (t instanceof HaleConnectException
							&& ((HaleConnectException) t).getStatusCode() == 401) {
						// In case of 401 (Unauthorized) the most likely cause
						// is that the API token has expired
						log.userError(
								"Unable to retrieve projects from hale connect due to missing permissions. Please re-login and try again.");
						return;
					}

					String configuredBasePath = haleConnect.getBasePathManager()
							.getBasePath(HaleConnectServices.PROJECT_STORE);
					if (configuredBasePath
							.equals(PreferenceInitializer.HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT)) {
						log.userError(
								"Unable to connect to hale connect, please check your network connection.",
								t);
					}
					else {
						// Inform user that the default base path was modified
						log.userError(
								MessageFormat.format(
										"Unable to connect to hale connect, please check your network connection.\n\nNote that the configured project store base path ({0}) differs from the default value ({1}), which may also be the cause of this error.",
										configuredBasePath,
										PreferenceInitializer.HALE_CONNECT_BASEPATH_PROJECTS_DEFAULT),
								t);
					}

					if (projects != null) {
						projects.setInput(
								Arrays.asList("Error retrieving projects from hale connect."));
					}
				}
			});
		}
	}

	/**
	 * Create the wizard page
	 * 
	 * @param wizard Wizard this page is associated with
	 * 
	 */
	public ChooseHaleConnectProjectWizardPage(
			ConfigurationWizard<HaleConnectProjectConfig> wizard) {
		super(wizard, "hcLoadProject");
		setTitle("Load project");
		setMessage("Please select a project");

		haleConnect = HaleUI.getServiceProvider().getService(HaleConnectService.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage#updateConfiguration(java.lang.Object)
	 */
	@Override
	public boolean updateConfiguration(HaleConnectProjectConfig configuration) {
		if (projects.getSelection().isEmpty()) {
			return false;
		}

		StructuredSelection selection = (StructuredSelection) projects.getSelection();
		if (!(selection.getFirstElement() instanceof HaleConnectProjectInfo)) {
			return false;
		}

		HaleConnectProjectInfo pi = (HaleConnectProjectInfo) selection.getFirstElement();
		configuration.setProjectId(pi.getId());
		configuration.setProjectName(pi.getName());
		configuration.setOwner(pi.getOwner());
		configuration.setLastModified(pi.getLastModified());

		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.util.wizard.ConfigurationWizardPage#createContent(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContent(Composite parent) {
		Composite page = new Composite(parent, SWT.NONE);
		page.setLayout(new GridLayout(4, false));

		keywordFilter = new Text(page, SWT.SEARCH | SWT.ICON_SEARCH | SWT.BORDER | SWT.ICON_CANCEL);
		keywordFilter.setMessage("Enter search text here...");
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(keywordFilter);

		ownerFilter = new ComboViewer(page);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(ownerFilter.getControl());
		ownerFilter.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				new Object();
			}

			@Override
			public void dispose() {
				//
			}

			@Override
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof List<?>) {
					List<?> elements = (List<?>) inputElement;
					return elements.toArray();
				}
				else if (inputElement instanceof Object[]) {
					return (Object[]) inputElement;
				}

				return new Object[] {};
			}
		});
		ownerFilter.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof OwnerFilterEntry) {
					return ((OwnerFilterEntry) element).getLabel();
				}

				return super.getText(element);
			}

		});

		List<OwnerFilterEntry> availableFilters = new ArrayList<>();
		availableFilters.add(NULL_FILTER);
		availableFilters
				.add(new OwnerFilterEntry(
						new Owner[] {
								new Owner(OwnerType.USER, haleConnect.getSession().getUserId()) },
						"My projects"));

		List<Owner> orgs = new ArrayList<>();
		for (String orgId : haleConnect.getSession().getOrganisationIds()) {
			orgs.add(new Owner(OwnerType.ORGANISATION, orgId));
		}
		if (!orgs.isEmpty()) {
			availableFilters.add(new OwnerFilterEntry(orgs.toArray(new Owner[] {}),
					"My organisations' projects"));
		}

		ownerFilter.setInput(availableFilters);
		ownerFilter.setSelection(new StructuredSelection(NULL_FILTER));

		Composite tableComposite = new Composite(page, SWT.NONE);
		TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);

		projects = new TableViewer(tableComposite,
				SWT.V_SCROLL | SWT.BORDER | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		projects.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				update();
			}
		});

		TableViewerColumn nameColumn = new TableViewerColumn(projects, SWT.LEAD);
		nameColumn.getColumn().setText("Project name");
		nameColumn.setLabelProvider(new ColumnLabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof HaleConnectProjectInfo) {
					return ((HaleConnectProjectInfo) element).getName();
				}

				return element.toString();
			}

		});
		columnLayout.setColumnData(nameColumn.getColumn(), new ColumnWeightData(45, 200, true));

		TableViewerColumn authorColumn = new TableViewerColumn(projects, SWT.LEAD);
		authorColumn.getColumn().setText("Author");
		authorColumn.setLabelProvider(new ColumnLabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof HaleConnectProjectInfo) {
					return ((HaleConnectProjectInfo) element).getAuthor();
				}

				return element.toString();
			}

		});
		columnLayout.setColumnData(authorColumn.getColumn(), new ColumnWeightData(20, 50, true));

		TableViewerColumn ownerColumn = new TableViewerColumn(projects, SWT.LEAD);
		ownerColumn.getColumn().setText("Owner");
		ownerColumn.setLabelProvider(new ColumnLabelProvider() {

			/**
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				if (element instanceof HaleConnectProjectInfo) {
					Owner owner = ((HaleConnectProjectInfo) element).getOwner();
					if (owner.isUser()) {
						try {
							HaleConnectUserInfo user = haleConnect.getUserInfo(owner.getId());
							if (!StringUtils.isEmpty(user.getFullName())) {
								return user.getFullName();
							}
							else if (!StringUtils.isEmpty(user.getScreenName())) {
								return user.getScreenName();
							}
							else {
								return MessageFormat.format("<user {0}>", user.getUserId());
							}
						} catch (HaleConnectException e) {
							log.error(e.getMessage(), e);
							return MessageFormat.format("<user {0}>", owner.getId());
						}
					}
					else if (owner.isOrganisation()) {
						try {
							return haleConnect.getOrganisationInfo(owner.getId()).getName();
						} catch (HaleConnectException e) {
							log.error(e.getMessage(), e);
							return MessageFormat.format("<organisation {0}>", owner.getId());
						}
					}
					else {
						return "<unknown owner type> ID: " + owner.getId();
					}
				}

				return element.toString();
			}

		});
		columnLayout.setColumnData(ownerColumn.getColumn(), new ColumnWeightData(35, 100, true));

		final Table projectsTable = projects.getTable();
		projectsTable.setHeaderVisible(true);
		projectsTable.setLinesVisible(true);

		tableComposite
				.setLayoutData(GridDataFactory.fillDefaults().span(4, 1).grab(true, true).create());
		projects.setContentProvider(ArrayContentProvider.getInstance());

		populateProjects();

		final ProjectFilter projectFilter = new ProjectFilter(projects);
		projects.addFilter(projectFilter);

		ownerFilter.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof StructuredSelection) {
					StructuredSelection selection = (StructuredSelection) event.getSelection();
					if (selection.getFirstElement() instanceof OwnerFilterEntry) {
						OwnerFilterEntry selectedFilter = (OwnerFilterEntry) selection
								.getFirstElement();
						if (selectedFilter.equals(NULL_FILTER)) {
							projectFilter.clearOwnerFilter();
						}
						else {
							projectFilter.filterByOwners(Arrays.asList(selectedFilter.getOwner()));
						}
					}
				}
			}
		});

		keywordFilter.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				projectFilter.setSearchText(keywordFilter.getText());
			}
		});

		final Button refresh = new Button(page, SWT.FLAT);
		refresh.setText("Refresh");
		GridDataFactory.fillDefaults().span(1, 1).grab(false, false).applyTo(refresh);
		refresh.addSelectionListener(new SelectionAdapter() {

			/**
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				populateProjects();
			}

		});

		ownerFilter.getControl().setFocus();
		setControl(page);

		update();

	}

	private void populateProjects() {
		// Use String[] input here to allow GetProjectsCallback to differentiate
		// between loading/error messages and actual content
		projects.setInput(new String[] { "Loading..." });
		projects.getTable().setEnabled(false);

		try {
			Futures.addCallback(haleConnect.getProjectsAsync(null), new GetProjectsCallback());
			for (String orgId : haleConnect.getSession().getOrganisationIds()) {
				Futures.addCallback(haleConnect.getProjectsAsync(orgId), new GetProjectsCallback());
			}

		} catch (HaleConnectException e1) {
			log.error(e1.getMessage(), e1);
			projects.setInput(new String[] { "Error retrieving projects from hale connect." });
		}
	}

	private void update() {
		StructuredSelection selection = (StructuredSelection) projects.getSelection();
		if (selection.getFirstElement() instanceof HaleConnectProjectInfo) {
			setPageComplete(true);
		}
		else {
			setPageComplete(false);
		}
	}
}
