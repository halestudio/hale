/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.osgi.framework.Version;

import de.cs3d.util.eclipse.extension.ExtensionObjectFactoryCollection;
import de.cs3d.util.eclipse.extension.FactoryFilter;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import de.fhg.igd.osgi.util.configuration.AbstractDefaultConfigurationService;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorExtension;
import eu.esdihumboldt.hale.common.core.io.extension.IOAdvisorFactory;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectImport;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.project.OpenProjectWizard;
import eu.esdihumboldt.hale.ui.io.project.SaveProjectWizard;
import eu.esdihumboldt.hale.ui.io.util.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;
import eu.esdihumboldt.hale.ui.service.project.UILocationUpdater;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ProjectServiceImpl extends AbstractProjectService implements ProjectService {

	/**
	 * Configuration service backed by the internal {@link Project}
	 */
	private class ProjectConfigurationService extends AbstractDefaultConfigurationService implements
			IConfigurationService {

		/**
		 * Default constructor
		 */
		public ProjectConfigurationService() {
			super(new Properties());
		}

		/**
		 * @see AbstractConfigurationService#getValue(String)
		 */
		@Override
		protected String getValue(String key) {
			synchronized (ProjectServiceImpl.this) {
				Value value = main.getProperties().get(key);
				if (value == null) {
					return null;
				}
				return value.as(String.class);
			}
		}

		/**
		 * @see AbstractConfigurationService#removeValue(String)
		 */
		@Override
		protected void removeValue(String key) {
			synchronized (ProjectServiceImpl.this) {
				main.getProperties().remove(key);
			}
			setChanged();
		}

		/**
		 * @see AbstractConfigurationService#setValue(String, String)
		 */
		@Override
		protected void setValue(String key, String value) {
			synchronized (ProjectServiceImpl.this) {
				main.getProperties().put(key, Value.of(value));
			}
			setChanged();
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(ProjectServiceImpl.class);

	private Project main;

	private final Version haleVersion;

	private File projectFile;

	private String appTitle;

	private final IOAdvisor<ProjectWriter> saveProjectAdvisor;

	private final IOAdvisor<ProjectReader> openProjectAdvisor;

	private final ProjectConfigurationService configurationService = new ProjectConfigurationService();

	private boolean changed = false;

	private UILocationUpdater updater = new UILocationUpdater(null, null);

	/**
	 * Default constructor
	 */
	public ProjectServiceImpl() {
		haleVersion = Version.parseVersion(Display.getAppVersion());
		synchronized (this) {
			main = createDefaultProject();
		}

		// create advisors
		openProjectAdvisor = new AbstractIOAdvisor<ProjectReader>() {

			@Override
			public void updateConfiguration(ProjectReader provider) {
				super.updateConfiguration(provider);

				// set project files
				Map<String, ProjectFile> projectFiles = ProjectIO.createDefaultProjectFiles(HaleUI
						.getServiceProvider());
				provider.setProjectFiles(projectFiles);
			}

			@Override
			public void handleResults(ProjectReader provider) {
				// no change check as this is performed by clean
				if (!internalClean()) {
					return;
				}

				synchronized (ProjectServiceImpl.this) {
					main = provider.getProject();
					updater = new UILocationUpdater(main, provider.getSource().getLocation());
					updater.updateProject();
					if ("file".equalsIgnoreCase(provider.getSource().getLocation().getScheme())) {
						// the source of ArchiveProjectReader is a temporary
						// directory. need the originally source to show
						// correct archive file in the RecentFilesService.
						// otherwise complications with UILocationUpdater above
						if (provider instanceof ArchiveProjectImport)
							projectFile = new File(((ArchiveProjectImport) provider)
									.getOriginallySource().getLocation());
						else
							projectFile = new File(provider.getSource().getLocation());
					}

					changed = false;
					RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench()
							.getService(RecentFilesService.class);
					if (projectFile != null)
						rfs.add(projectFile.getAbsolutePath(), main.getName());
					// XXX safe history in case of non-file loaded projects?
					// possibly always safe URI raw paths (and show the history
					// with decoded paths and removed file:/ in case of files)?
					// else
					// rfs.add(provider.getSource().getLocation().getRawPath(),
					// main.getName());
				}

				updateWindowTitle();

				// execute loaded I/O configurations
				List<IOConfiguration> confs;
				synchronized (ProjectServiceImpl.this) {
					confs = new ArrayList<IOConfiguration>(main.getResources());
				}
				executeConfigurations(confs);

				// notify listeners
				Map<String, ProjectFile> projectFiles = provider.getProjectFiles();
				notifyAfterLoad(projectFiles);
				notifyExportConfigurationChanged();
				// apply remaining project files
				for (ProjectFile file : projectFiles.values()) {
					// XXX do this in a Job or something?
					file.apply();
				}
				// reset changed to false if it was altered through the project
				// files being applied
				// FIXME this is ugly XXX what if there actually is a real
				// resulting change?
				synchronized (ProjectServiceImpl.this) {
					changed = false;
				}
				updateWindowTitle();
			}
		};

		saveProjectAdvisor = new AbstractIOAdvisor<ProjectWriter>() {

			@Override
			public void prepareProvider(ProjectWriter provider) {
				synchronized (ProjectServiceImpl.this) {
					provider.setProject(main);
				}
			}

			@Override
			public void updateConfiguration(ProjectWriter provider) {
				provider.getProject().setModified(new Date());
				provider.getProject().setHaleVersion(haleVersion);
				Map<String, ProjectFile> projectFiles = ProjectIO.createDefaultProjectFiles(HaleUI
						.getServiceProvider());
				notifyBeforeSave(projectFiles); // get additional files from
												// listeners
				provider.setProjectFiles(projectFiles);
			}

			@Override
			public void handleResults(ProjectWriter provider) {
				synchronized (ProjectServiceImpl.this) {
					projectFile = new File(provider.getTarget().getLocation());
					changed = false;
					RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench()
							.getService(RecentFilesService.class);
					rfs.add(projectFile.getAbsolutePath(), provider.getProject().getName());
				}

				updateWindowTitle();
			}
		};
	}

	/**
	 * @see ProjectService#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @see ProjectService#setChanged()
	 */
	@Override
	public void setChanged() {
		synchronized (this) {
			changed = true;
		}
		updateWindowTitle();
	}

	/**
	 * Execute a set of I/O configurations.
	 * 
	 * @param configurations the I/O configurations
	 */
	private void executeConfigurations(final List<IOConfiguration> configurations) {
		// TODO sort by dependencies
		for (IOConfiguration conf : configurations) {
			executeConfiguration(conf);
		}
	}

	/**
	 * Execute a single I/O configuration.
	 * 
	 * @param conf the I/O configuration
	 */
	private void executeConfiguration(IOConfiguration conf) {
		// get provider ...
		IOProvider provider = null;
		IOProviderDescriptor descriptor = IOProviderExtension.getInstance().getFactory(
				conf.getProviderId());
		if (descriptor != null) {
			try {
				provider = descriptor.createExtensionObject();
			} catch (Exception e) {
				log.error(
						MessageFormat
								.format("Could not execute I/O configuration, provider with ID {0} could not be created.",
										conf.getProviderId()), e);
				return;
			}

			// ... and advisor
			final String actionId = conf.getActionId();
			List<IOAdvisorFactory> advisors = IOAdvisorExtension.getInstance().getFactories(
					new FactoryFilter<IOAdvisor<?>, IOAdvisorFactory>() {

						@Override
						public boolean acceptFactory(IOAdvisorFactory factory) {
							return factory.getActionID().equals(actionId);
						}

						@Override
						public boolean acceptCollection(
								ExtensionObjectFactoryCollection<IOAdvisor<?>, IOAdvisorFactory> collection) {
							return true;
						}
					});
			if (advisors != null && !advisors.isEmpty()) {
				IOAdvisor<?> advisor;
				try {
					advisor = advisors.get(0).createAdvisor(HaleUI.getServiceProvider());
				} catch (Exception e) {
					log.error(
							MessageFormat
									.format("Could not execute I/O configuration, advisor with ID {0} could not be created.",
											advisors.get(0).getIdentifier()), e);
					return;
				}
				// configure settings
				provider.loadConfiguration(conf.getProviderConfiguration());
				// execute provider
				executeProvider(provider, advisor);
			}
			else {
				log.error(MessageFormat.format(
						"Could not execute I/O configuration, no advisor for action {0} found.",
						actionId));
			}
		}
		else {
			log.error(MessageFormat.format(
					"Could not execute I/O configuration, provider with ID {0} not found.",
					conf.getProviderId()));
		}
	}

	/**
	 * Execute the given I/O provider with the given I/O advisor.
	 * 
	 * @param provider the I/O provider
	 * @param advisor the I/O advisor
	 */
	private void executeProvider(final IOProvider provider,
			@SuppressWarnings("rawtypes") final IOAdvisor advisor) {
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@SuppressWarnings("unchecked")
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				IOReporter reporter = provider.createReporter();
				ATransaction trans = log.begin(reporter.getTaskName());
				try {
					// use advisor to configure provider
					advisor.prepareProvider(provider);
					advisor.updateConfiguration(provider);

					// execute
					IOReport report = provider.execute(new ProgressMonitorIndicator(monitor));

					// publish report
					ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(
							ReportService.class);
					rs.addReport(report);

					// handle results
					if (report.isSuccess()) {
						advisor.handleResults(provider);
					}
				} catch (Exception e) {
					log.error("Error executing an I/O provider.", e);
				} finally {
					trans.end();
				}
			}
		};
		try {
			ThreadProgressMonitor.runWithProgressDialog(op, provider.isCancelable());
		} catch (Exception e) {
			log.error("Error executing an I/O provider.", e);
		}
	}

	private boolean internalClean() {
		if (!changeCheck()) {
			return false;
		}

		// reset current session descriptor
		ReportService repService = (ReportService) PlatformUI.getWorkbench().getService(
				ReportService.class);
		repService.updateCurrentSessionDescription();

		// clean
		final IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				ATransaction trans = log.begin("Clean project");

				monitor.beginTask("Clean project", IProgressMonitor.UNKNOWN);
				try {
					synchronized (this) {
						main = createDefaultProject();
						projectFile = null;
						changed = false;
					}
					updateWindowTitle();
					notifyClean();

					// schemas aren't valid anymore, clear property resolver
					// cache
					PropertyResolver.clearCache();
				} finally {
					monitor.done();
					trans.end();
				}

				// clean workbench history AFTER other cleans since they can
				// create operations
				IWorkbenchOperationSupport os = PlatformUI.getWorkbench().getOperationSupport();
				os.getOperationHistory().dispose(os.getUndoContext(), true, true, false);

				// suppress the status being set to changed by the clean
				synchronized (ProjectServiceImpl.this) {
					changed = false;
				}
				updateWindowTitle();
			}
		};

		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Exception e) {
			log.error("Error cleaning the project.", e);
		}
		return true;
	}

	/**
	 * @see ProjectService#clean()
	 */
	@Override
	public void clean() {
		internalClean();
	}

	/**
	 * @see ProjectService#load(URI)
	 */
	@Override
	public void load(URI uri) {
		// no change check as this is done by clean before a new project is
		// loaded

		// use I/O provider and content type mechanisms to enable loading of a
		// project file
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class, new DefaultInputSupplier(
				uri), uri.getPath());
		if (reader != null) {
			// configure reader
			reader.setSource(new DefaultInputSupplier(uri));

			executeProvider(reader, openProjectAdvisor);
		}
		else {
			log.userError("The project format is not supported.");
		}
	}

	/**
	 * Update the window title
	 */
	private void updateWindowTitle() {
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// init appTitle
				if (appTitle == null) {
					if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0) {
						appTitle = PlatformUI.getWorkbench()./*
															 * getWorkbenchWindows(
															 * )[0]
															 */getActiveWorkbenchWindow()
								.getShell().getText();
					}
					else {
						return;
					}
				}

				String title;
				if (projectFile == null) {
					title = appTitle;
				}
				else {
					String pn = getProjectInfo().getName();
					title = appTitle + " - " + ((pn == null || pn.isEmpty()) ? ("Unnamed") : (pn))
							+ " - " + projectFile;
				}

				if (changed) {
					title = title + "*"; //$NON-NLS-1$
				}

				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(title);
			}
		};

		PlatformUI.getWorkbench().getDisplay().syncExec(run);
	}

	/**
	 * @see ProjectService#save()
	 */
	@Override
	public void save() {
		File projectFile;
		IOConfiguration saveConfig;
		synchronized (this) {
			projectFile = this.projectFile;
			saveConfig = main.getSaveConfiguration();
		}

		if (projectFile != null) {
			Collection<IOProviderDescriptor> providers = HaleIO
					.getProviderFactories(ProjectWriter.class);

			// use configuration from previous save if possible
			if (saveConfig != null) {
				// get provider ...
				ProjectWriter writer = null;
				for (IOProviderDescriptor factory : providers) {
					if (factory.getIdentifier().equals(saveConfig.getProviderId())) {
						try {
							writer = (ProjectWriter) factory.createExtensionObject();
						} catch (Exception e) {
							log.error("Could not create project writer", e);
						}
					}
				}

				if (writer != null) {
					// configure provider
					writer.loadConfiguration(saveConfig.getProviderConfiguration());
					// overwrite target with projectFile (as it may have been
					// moved externally)
					writer.setTarget(new FileIOSupplier(projectFile));

					executeProvider(writer, saveProjectAdvisor);
				}
				else {
					log.error("The project cannot be saved because the format is not available.");
					// use save as instead
					saveAs();
				}
			}
			else {
				// use I/O provider and content type mechanisms to try saving
				// the project file
				ProjectWriter writer = HaleIO.findIOProvider(ProjectWriter.class,
						new FileIOSupplier(projectFile), projectFile.getAbsolutePath());
				if (writer != null) {
					executeProvider(writer, saveProjectAdvisor);
				}
				else {
					log.error("The project cannot be saved because the format is not available.");
					// use save as instead
					saveAs();
				}
			}
		}
		else {
			saveAs();
		}
	}

	/**
	 * @see ProjectService#getConfigurationService()
	 */
	@Override
	public IConfigurationService getConfigurationService() {
		return configurationService;
	}

	/**
	 * @see ProjectService#saveAs()
	 */
	@Override
	public void saveAs() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				SaveProjectWizard wizard = new SaveProjectWizard();
				wizard.setAdvisor(saveProjectAdvisor, null);

				Shell shell = Display.getCurrent().getActiveShell();
				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

	/**
	 * @see ProjectService#open()
	 */
	@Override
	public void open() {
		// no change check as this is done by clean before a new project is
		// loaded

		// open
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				OpenProjectWizard wizard = new OpenProjectWizard();
				wizard.setAdvisor(openProjectAdvisor, null);

				Shell shell = Display.getCurrent().getActiveShell();
				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

	/**
	 * Check if there are changes and offer the user to save the project.
	 * 
	 * @return if the caller should continue
	 */
	private boolean changeCheck() {
		if (!isChanged()) {
			return true;
		}

		final Display display = PlatformUI.getWorkbench().getDisplay();
		final AtomicBoolean returnValue = new AtomicBoolean();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				MessageBox mb = new MessageBox(display.getActiveShell(), SWT.YES | SWT.NO
						| SWT.CANCEL | SWT.ICON_QUESTION);
				mb.setMessage("Save changes to the current project?"); //$NON-NLS-1$
				mb.setText("Unsaved changes"); //$NON-NLS-1$
				int result = mb.open();
				if (result == SWT.CANCEL) {
					returnValue.set(false);
				}
				else if (result == SWT.YES) {
					// try saving project
					save();

					if (isChanged()) {
						returnValue.set(false);
					}
					else {
						returnValue.set(true);
					}
				}
				else {
					returnValue.set(true);
				}
			}
		});
		return returnValue.get();
	}

	/**
	 * Create a project with default values
	 * 
	 * @return the created project
	 */
	private Project createDefaultProject() {
		Project project = new Project();

		project.setCreated(new Date());
		project.setAuthor(System.getProperty("user.name"));
		project.setHaleVersion(haleVersion);
		project.setName(null);

		return project;
	}

	/**
	 * @see ProjectService#getProjectInfo()
	 */
	@Override
	public ProjectInfo getProjectInfo() {
		synchronized (this) {
			return main;
		}
	}

	/**
	 * @see ProjectService#rememberIO(String, String, IOProvider)
	 */
	@Override
	public void rememberIO(String actionId, String providerId, IOProvider provider) {
		// populate an IOConfiguration from the given data
		IOConfiguration conf = new IOConfiguration();
		conf.setActionId(actionId);
		conf.setProviderId(providerId);
		provider.storeConfiguration(conf.getProviderConfiguration());

		// add configuration to project
		synchronized (this) {
			main.getResources().add(conf);
		}
		setChanged();

		notifyResourceAdded(actionId);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#removeResources(java.lang.String)
	 */
	@Override
	public List<IOConfiguration> removeResources(String actionId) {
		List<IOConfiguration> removedResources = new LinkedList<IOConfiguration>();
		synchronized (this) {
			Iterator<IOConfiguration> iter = main.getResources().iterator();
			while (iter.hasNext()) {
				IOConfiguration conf = iter.next();
				if (conf.getActionId().equals(actionId)) {
					iter.remove();
					removedResources.add(conf);
				}
			}
		}
		setChanged();

		notifyResourcesRemoved(actionId);

		return removedResources;
	}

	@Override
	public boolean hasResources(String actionId) {
		synchronized (this) {
			Iterator<IOConfiguration> iter = main.getResources().iterator();
			while (iter.hasNext()) {
				IOConfiguration conf = iter.next();
				if (conf.getActionId().equals(actionId)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#executeAndRemember(eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration)
	 */
	@Override
	public void executeAndRemember(IOConfiguration conf) {
		executeConfiguration(conf);
		synchronized (this) {
			main.getResources().add(conf);
		}
		setChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getLocationUpdater()
	 */
	@Override
	public LocationUpdater getLocationUpdater() {
		return updater;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#addExportConfigurations(java.util.List)
	 */
	@Override
	public void addExportConfigurations(List<IOConfiguration> confs) {
		for (IOConfiguration conf : confs) {
			main.getExportConfigurations().add(conf);
		}
		notifyExportConfigurationChanged();

	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#removeExportConfigurations(java.util.List)
	 */
	@Override
	public void removeExportConfigurations(List<IOConfiguration> confs) {
		main.getExportConfigurations().removeAll(confs);
		notifyExportConfigurationChanged();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getExportConfigurationNames()
	 */
	@Override
	public List<String> getExportConfigurationNames() {
		List<String> names = new ArrayList<String>();
		for (IOConfiguration conf : main.getExportConfigurations()) {
			String name = conf.getProviderConfiguration().get("configurationName")
					.getStringRepresentation();
			if (name != null)
				names.add(name);
		}
		return names;
	}
}
