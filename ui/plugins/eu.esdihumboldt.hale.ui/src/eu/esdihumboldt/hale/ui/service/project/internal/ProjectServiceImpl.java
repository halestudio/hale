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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.osgi.framework.Version;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;

import de.fhg.igd.osgi.util.configuration.AbstractDefaultConfigurationService;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.CachingImportProvider;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOAdvisor;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderExtension;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.common.core.io.project.ComplexConfigurationService;
import eu.esdihumboldt.hale.common.core.io.project.ProjectDescription;
import eu.esdihumboldt.hale.common.core.io.project.ProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.common.core.io.project.ProjectWriter.ProjectWriterMode;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfigurationResource;
import eu.esdihumboldt.hale.common.core.io.project.model.Project;
import eu.esdihumboldt.hale.common.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.common.core.io.project.model.Resource;
import eu.esdihumboldt.hale.common.core.io.project.util.LocationUpdater;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.NoStreamOutputSupplier;
import eu.esdihumboldt.hale.common.core.service.cleanup.Cleanup;
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupContext;
import eu.esdihumboldt.hale.common.core.service.cleanup.CleanupService;
import eu.esdihumboldt.hale.common.core.service.cleanup.TemporaryFiles;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.io.InstanceIO;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.io.project.OpenProjectWizard;
import eu.esdihumboldt.hale.ui.io.project.SaveProjectWizard;
import eu.esdihumboldt.hale.ui.io.project.update.SchemaUpdateDialog;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.project.CacheCallback;
import eu.esdihumboldt.hale.ui.service.project.ProjectResourcesUtil;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentProjectsService;
import eu.esdihumboldt.hale.ui.service.project.UILocationUpdater;
import eu.esdihumboldt.hale.ui.service.report.ReportService;
import eu.esdihumboldt.hale.ui.util.io.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ProjectServiceImpl extends AbstractProjectService implements ProjectService {

	/**
	 * Advisor for opening a project.
	 */
	private final class OpenProjectAdvisor extends AbstractIOAdvisor<ProjectReader> {

		private final boolean updateSchema;

		/**
		 * Create an advisor for opening a project.
		 * 
		 * @param updateSchema if the option to update the schema should be
		 *            offered
		 */
		public OpenProjectAdvisor(boolean updateSchema) {
			super();
			this.updateSchema = updateSchema;
			setActionId(ProjectIO.ACTION_LOAD_PROJECT);
		}

		@Override
		public void updateConfiguration(ProjectReader provider) {
			super.updateConfiguration(provider);

			// set project files
			Map<String, ProjectFile> projectFiles = ProjectIO
					.createDefaultProjectFiles(HaleUI.getServiceProvider());
			provider.setProjectFiles(projectFiles);
		}

		@Override
		public void handleResults(ProjectReader provider) {
			// no change check as this is performed by clean
			if (!internalClean()) {
				return;
			}

			// check if project reader requires clean-up
			if (provider instanceof TemporaryFiles || provider instanceof Cleanup) {
				CleanupService cs = HalePlatform.getService(CleanupService.class);
				if (provider instanceof TemporaryFiles) {
					cs.addTemporaryFiles(CleanupContext.PROJECT, Iterables
							.toArray(((TemporaryFiles) provider).getTemporaryFiles(), File.class));
				}
				if (provider instanceof Cleanup) {
					cs.addCleaner(CleanupContext.PROJECT, ((Cleanup) provider));
				}
			}

			synchronized (ProjectServiceImpl.this) {
				main = provider.getProject();
				if (provider.getSource() != null) {
					// loaded project
					updater = new UILocationUpdater(main, provider.getSource().getLocation());
					updater.updateProject(true);
					if ("file".equalsIgnoreCase(provider.getSource().getLocation().getScheme())) {
						projectFile = new File(provider.getSource().getLocation());
					}
					else {
						projectFile = null;
					}
					projectLocation = provider.getSource().getLocation();
				}
				else {
					// project template (from object)
					projectFile = null;
					projectLocation = null;
				}

				changed = false;
				RecentProjectsService rfs = PlatformUI.getWorkbench()
						.getService(RecentProjectsService.class);
				if (projectFile != null) {
					rfs.add(projectFile.getAbsolutePath(), main.getName());
				}
				// XXX safe history in case of non-file loaded projects?
				// possibly always safe URI raw paths (and show the history
				// with decoded paths and removed file:/ in case of files)?
				// else
				// rfs.add(provider.getSource().getLocation().getRawPath(),
				// main.getName());

				// store the content type the project was loaded with
				IContentType ct = provider.getContentType();
				if (ct == null && provider.getSource() != null) {
					log.warn(
							"Project content type was not determined during load, trying auto-detection");
					try {
						URI loc = provider.getSource().getLocation();
						String filename = null;
						if (loc != null) {
							filename = loc.getPath();
						}
						ct = HaleIO.findContentType(ProjectReader.class, provider.getSource(),
								filename);
					} catch (Exception e) {
						// ignore
					}
					if (ct == null) {
						log.error("Could not determine content type of loaded project");
					}
				}
				projectLoadContentType = ct;
			}

			updateWindowTitle();

			// execute loaded I/O configurations
			List<IOConfiguration> confs;
			synchronized (ProjectServiceImpl.this) {
				confs = new ArrayList<IOConfiguration>(main.getResources());
			}

			// before execution, perform eventual schema update
			boolean updated = false;
			if (updateSchema) {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				final AtomicReference<List<IOConfiguration>> updatedConfigs = new AtomicReference<>();
				final List<IOConfiguration> original = confs;
				display.syncExec(new Runnable() {

					@Override
					public void run() {
						SchemaUpdateDialog dlg = new SchemaUpdateDialog(display.getActiveShell(),
								original);
						if (dlg.open() == Dialog.OK) {
							updatedConfigs.set(dlg.getConfigurations());
						}
					}
				});

				List<IOConfiguration> newConfs = updatedConfigs.get();
				if (newConfs != null && !newConfs.equals(confs)) {
					// update project
					synchronized (ProjectServiceImpl.this) {
						main.getResources().clear();
						main.getResources().addAll(newConfs);
					}
					// replace confs
					confs = newConfs;

					updated = true;
				}
			}

			// Defer execution of source data providers until after Alignment
			// is loaded
			List<IOConfiguration> sourceDataConfigurations = new ArrayList<>();
			for (IOConfiguration conf : confs) {
				IOProviderDescriptor descriptor = IOProviderExtension.getInstance()
						.getFactory(conf.getProviderId());
				if (InstanceReader.class.isAssignableFrom(descriptor.getProviderType())) {
					sourceDataConfigurations.add(conf);
				}
			}
			confs.removeAll(sourceDataConfigurations);

			executeConfigurations(confs);

			// notify listeners
			Map<String, ProjectFile> projectFiles = provider.getProjectFiles();
			notifyExportConfigurationChanged();
			// apply remaining project files
			for (ProjectFile file : projectFiles.values()) {
				// XXX do this in a Job or something?
				file.apply();
			}

			InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
			boolean txWasEnabled = is.isTransformationEnabled();
			is.setTransformationEnabled(false);

			executeConfigurations(sourceDataConfigurations);
			if (txWasEnabled) {
				is.setTransformationEnabled(true);
			}

			// reset changed to false if it was altered through the project
			// files being applied
			// FIXME this is ugly XXX what if there actually is a real
			// resulting change?
			synchronized (ProjectServiceImpl.this) {
				changed = updated;
			}
			notifyAfterLoad();
			updateWindowTitle();
		}
	}

	/**
	 * Configuration service backed by the internal {@link Project}
	 */
	private class ProjectConfigurationService extends AbstractDefaultConfigurationService
			implements ComplexConfigurationService {

		/**
		 * Default constructor
		 */
		public ProjectConfigurationService() {
			super(new Properties());
		}

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

		@Override
		protected void removeValue(String key) {
			synchronized (ProjectServiceImpl.this) {
				main.getProperties().remove(key);
			}
			setChanged();
			notifyProjectSettingChanged(key, Value.NULL);
		}

		@Override
		protected void setValue(String key, String value) {
			synchronized (ProjectServiceImpl.this) {
				main.getProperties().put(key, Value.of(value));
			}
			setChanged();
			notifyProjectSettingChanged(key, Value.of(value));
		}

		@Override
		public void setProperty(String name, Value value) {
			synchronized (ProjectServiceImpl.this) {
				if (value == null || value.getValue() == null) {
					main.getProperties().remove(name);
				}
				else {
					main.getProperties().put(name, value);
				}
			}
			setChanged();
			notifyProjectSettingChanged(name, (value != null) ? (value) : (Value.NULL));
		}

		@Override
		public Value getProperty(String name) {
			synchronized (ProjectServiceImpl.this) {
				Value value = main.getProperties().get(name);
				return (value != null) ? (value) : (Value.NULL);
			}
		}

	}

	private static final ALogger log = ALoggerFactory.getLogger(ProjectServiceImpl.class);

	private Project main;

	private final Version haleVersion;

	/**
	 * The project file the project was loaded from.
	 */
	private File projectFile;

	/**
	 * Location the project was loaded from, even if it was not a file.
	 */
	private URI projectLocation;

	private String appTitle;

	private final IOAdvisor<ProjectWriter> saveProjectAdvisor;

	private final IOAdvisor<ProjectReader> openProjectAdvisor;

	private final ProjectConfigurationService configurationService = new ProjectConfigurationService();

	private final Map<String, Value> temporarySettings = new HashMap<>();

	private boolean changed = false;

	private UILocationUpdater updater = new UILocationUpdater(null, null);

	/**
	 * Stores the content type of the loaded project.
	 */
	private IContentType projectLoadContentType;

	private final OpenProjectAdvisor updateProjectAdvisor;

	/**
	 * Default constructor
	 */
	public ProjectServiceImpl() {
		haleVersion = HalePlatform.getCoreVersion(); // Version.parseVersion(Display.getAppVersion());
		synchronized (this) {
			main = createDefaultProject();
		}

		// create advisors
		openProjectAdvisor = new OpenProjectAdvisor(false);
		updateProjectAdvisor = new OpenProjectAdvisor(true);
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
				Map<String, ProjectFile> projectFiles = ProjectIO
						.createDefaultProjectFiles(HaleUI.getServiceProvider());
				provider.setProjectFiles(projectFiles);
				if (projectLocation != null) {
					provider.setPreviousTarget(projectLocation);
				}
			}

			@Override
			public void handleResults(ProjectWriter provider) {
				synchronized (ProjectServiceImpl.this) {
					if (provider.getLastWriterMode() == ProjectWriterMode.EXPORT) {
						return;
					}

					projectLocation = provider.getTarget().getLocation();
					if ("file".equals(projectLocation.getScheme())) {
						projectFile = new File(projectLocation);
						RecentProjectsService rfs = PlatformUI.getWorkbench()
								.getService(RecentProjectsService.class);
						rfs.add(projectFile.getAbsolutePath(), provider.getProject().getName());
					}
					else {
						projectFile = null;
					}

					changed = false;

					// override the project load content type
					projectLoadContentType = provider.getContentType();
				}

				notifyAfterSave();
				updateWindowTitle();
			}
		};
		saveProjectAdvisor.setActionId(ProjectIO.ACTION_SAVE_PROJECT);
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
	private void executeConfiguration(final IOConfiguration conf) {
		// work with a cloned configuration for the case that we make a relative
		// URI absolute
		IOConfiguration cloned = conf.clone();
		updater.updateIOConfiguration(cloned, false);

		ProjectResourcesUtil.executeConfiguration(cloned, new CacheCallback() {

			@Override
			public void update(Value cache) {
				// update the original configuration with the new cache value
				conf.setCache(cache);
				// set the project status to changed
				setChanged();
			}
		});
	}

	private boolean internalClean() {
		if (!changeCheck()) {
			return false;
		}

		// reset current session descriptor
		ReportService repService = PlatformUI.getWorkbench().getService(ReportService.class);
		repService.updateCurrentSessionDescription();

		// clean
		final IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				ATransaction trans = log.begin("Clean project");

				CleanupService cs = HalePlatform.getService(CleanupService.class);
				if (cs != null) {
					cs.triggerProjectCleanup();
				}

				monitor.beginTask("Clean project", IProgressMonitor.UNKNOWN);
				try {
					synchronized (this) {
						main = createDefaultProject();
						projectFile = null;
						projectLocation = null;
						changed = false;
						projectLoadContentType = null;
						temporarySettings.clear();
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
		ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class,
				new DefaultInputSupplier(uri), uri.getPath());
		if (reader != null) {
			// configure reader
			reader.setSource(new DefaultInputSupplier(uri));

			ProjectResourcesUtil.executeProvider(reader, openProjectAdvisor, null);
		}
		else {
			log.userError("The project format is not supported.");
		}
	}

	@Override
	public void loadTemplate(Project project) {
		// no change check as this is done by clean before a new project is
		// loaded

		ProjectReader reader = new DummyProjectReader(project);
		ProjectResourcesUtil.executeProvider(reader, openProjectAdvisor, null);
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
						appTitle = PlatformUI.getWorkbench()
								./*
									 * getWorkbenchWindows( )[0]
									 */getActiveWorkbenchWindow().getShell().getText();
					}
					else {
						return;
					}
				}

				String projectName = getProjectInfo().getName();
				String title = appTitle + " - " + ((projectName == null || projectName.isEmpty())
						? "Unnamed" : projectName);
				if (projectFile == null) {
					// TODO Use scheme to discover plugin that can provide title
					// information
					if (projectLocation != null && "hc".equals(projectLocation.getScheme())) {
						title += " - [hale connect]";
					}
				}
				else {
					title += " - " + projectFile;
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

		if (projectFile != null || canSaveTo(projectLocation)) {
			Collection<IOProviderDescriptor> providers = HaleIO
					.getProviderFactories(ProjectWriter.class);

			// use configuration from previous save if possible
			if (saveConfig != null) {
				// get provider ...
				ProjectWriter writer = null;
				for (IOProviderDescriptor factory : providers) {
					if (factory.getIdentifier().equals(saveConfig.getProviderId())) {
						// found the matching factory

						/*
						 * Check if the content type the project was loaded with
						 * is supported for saving.
						 * 
						 * Example for a changed content type: A saved project
						 * archive may have been extracted and the internal XML
						 * project file loaded.
						 */
						if (projectLoadContentType != null) {
							if (factory.getSupportedTypes() == null || !factory.getSupportedTypes()
									.contains(projectLoadContentType)) {
								log.warn(
										"Project cannot be saved with the same settings it was originally saved with, as the content type has changed.");
								break;
							}
						}

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
					if (projectFile != null) {
						writer.setTarget(new FileIOSupplier(projectFile));
					}
					else {
						writer.setTarget(new NoStreamOutputSupplier(projectLocation));
					}

					ListenableFuture<IOReport> result = ProjectResourcesUtil.executeProvider(writer,
							saveProjectAdvisor, true, null);

					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {
							try {
								IOReport report = result.get();
								if (!report.isSuccess()) {
									log.userError(
											"The project could not be saved. Please check the report for more details.");
								}
							} catch (InterruptedException | ExecutionException e) {
								log.userError("The project could not be saved.", e);
							}
						}
					});
				}
				else {
					log.info(
							"The project cannot be saved because the format the project was saved with is not available or has changed.");
					// use save as instead
					saveAs();
				}
			}
			else if (projectFile != null) {
				// use I/O provider and content type mechanisms to try saving
				// the project file
				ProjectWriter writer = HaleIO.findIOProvider(ProjectWriter.class,
						new FileIOSupplier(projectFile), projectFile.getAbsolutePath());
				if (writer != null) {
					ProjectResourcesUtil.executeProvider(writer, saveProjectAdvisor, null);
				}
				else {
					log.error("The project cannot be saved because the format is not available.");
					// use save as instead
					saveAs();
				}
			}
			else {
				saveAs();
			}
		}
		else {
			saveAs();
		}
	}

	private boolean canSaveTo(URI target) {
		// TODO Discover plugin responsible for the target scheme and delegate
		if (target == null) {
			return false;
		}

		return "hc".equals(target.getScheme());
	}

	/**
	 * @see ProjectService#getConfigurationService()
	 */
	@Override
	public ComplexConfigurationService getConfigurationService() {
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
				SaveProjectWizard wizard;

				IContentType archiveContentType = HalePlatform.getContentTypeManager()
						.getContentType(ProjectIO.PROJECT_ARCHIVE_CONTENT_TYPE_ID);

				if (archiveContentType != null && projectLoadContentType != null
						&& projectLoadContentType.isKindOf(archiveContentType)) {
					/*
					 * For project archives, saving the project has to be
					 * restricted to project archives again, as the files only
					 * reside in a temporary location.
					 */
					List<IContentType> archiveTypes = HaleIO.findContentTypesOfKind(
							HalePlatform.getContentTypeManager().getAllContentTypes(),
							archiveContentType);
					wizard = new SaveProjectWizard(archiveTypes);
				}
				else {
					wizard = new SaveProjectWizard();
				}
				wizard.setAdvisor(saveProjectAdvisor, null);

				Shell shell = Display.getCurrent().getActiveShell();
				HaleWizardDialog dialog = new HaleWizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

	@Override
	public ListenableFuture<IOReport> export(ProjectWriter writer) {
		IOAdvisor<ProjectWriter> exportAdvisor = new AbstractIOAdvisor<ProjectWriter>() {

			@Override
			public void prepareProvider(ProjectWriter provider) {
				saveProjectAdvisor.prepareProvider(provider);
			}

			@Override
			public void updateConfiguration(ProjectWriter provider) {
				saveProjectAdvisor.updateConfiguration(provider);
			}

		};

		return ProjectResourcesUtil.executeProvider(writer, exportAdvisor, true, null);
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

	@Override
	public void update() {
		// no change check as this is done by clean before a new project is
		// loaded

		URI currentLocation = null;
		synchronized (this) {
			currentLocation = projectLocation;
		}

		if (currentLocation != null
				&& Arrays.asList("file", "http", "https").contains(currentLocation.getScheme())) {
			// use I/O provider and content type mechanisms to enable loading of
			// a project file
			ProjectReader reader = HaleIO.findIOProvider(ProjectReader.class,
					new DefaultInputSupplier(currentLocation), currentLocation.getPath());
			if (reader != null) {
				// configure reader
				reader.setSource(new DefaultInputSupplier(currentLocation));

				ProjectResourcesUtil.executeProvider(reader, updateProjectAdvisor, null);
			}
			else {
				log.userError("The project format is not supported.");
			}
		}
		else {
			log.userError("The project needs to be saved to a file before you can reload it.");
		}
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
				MessageBox mb = new MessageBox(display.getActiveShell(),
						SWT.YES | SWT.NO | SWT.CANCEL | SWT.ICON_QUESTION);
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

	@Override
	public void updateProjectInfo(ProjectDescription info) {
		synchronized (this) {
			if (main != null) {
				main.setAuthor(info.getAuthor());
				main.setDescription(info.getDescription());
				main.setName(info.getName());
			}
		}

		notifyProjectInfoChanged(getProjectInfo());
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
		if (provider instanceof CachingImportProvider) {
			conf.setCache(((CachingImportProvider) provider).getCache());
		}

		// add configuration to project
		synchronized (this) {
			main.getResources().add(conf);
		}
		setChanged();

		notifyResourceAdded(actionId, new IOConfigurationResource(conf, projectLocation));
	}

	@Override
	public List<? extends Resource> removeResources(String actionId) {
		Builder<Resource> removedBuilder = ImmutableList.builder();
		synchronized (this) {
			Iterator<IOConfiguration> iter = main.getResources().iterator();
			while (iter.hasNext()) {
				IOConfiguration conf = iter.next();
				if (conf.getActionId().equals(actionId)) {
					iter.remove();
					removedBuilder.add(new IOConfigurationResource(conf, projectLocation));
				}
			}
		}
		setChanged();

		List<Resource> removedResources = removedBuilder.build();

		notifyResourcesRemoved(actionId, removedResources);

		return removedResources;
	}

	@Override
	public void removeResource(String resourceId) {
		Resource removedResource = null;
		synchronized (this) {
			Iterator<IOConfiguration> iter = main.getResources().iterator();
			while (iter.hasNext()) {
				IOConfiguration conf = iter.next();
				Value idValue = conf.getProviderConfiguration()
						.get(ImportProvider.PARAM_RESOURCE_ID);
				if (idValue != null) {
					String id = idValue.as(String.class);
					if (resourceId.equals(id)) {
						// match found, remove
						iter.remove();
						removedResource = new IOConfigurationResource(conf, projectLocation);
						break;
					}
				}
			}
		}

		if (removedResource != null) {
			setChanged();
			notifyResourcesRemoved(removedResource.getActionId(),
					Collections.singletonList(removedResource));
		}
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

	@Override
	public void executeAndRemember(IOConfiguration conf) {
		executeConfiguration(conf);
		synchronized (this) {
			main.getResources().add(conf);
		}
		setChanged();

		notifyResourceAdded(conf.getActionId(), new IOConfigurationResource(conf, projectLocation));
	}

	@Override
	public void reloadSourceData() {
		IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Reload source data", IProgressMonitor.UNKNOWN);

				monitor.subTask("Clear loaded instances");

				// drop the existing instances
				InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
				is.dropInstances();

				// reload the instances
				for (IOConfiguration conf : main.getResources()) {
					if (InstanceIO.ACTION_LOAD_SOURCE_DATA.equals(conf.getActionId())) {
						executeConfiguration(conf);
					}
				}

				monitor.done();
			}
		};
		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Exception e) {
			log.error("Executing data reload failed", e);
		}
	}

	@Override
	public Iterable<? extends Resource> getResources() {
		synchronized (this) {
			return Collections2.transform(main.getResources(),
					new Function<IOConfiguration, Resource>() {

						@Override
						public Resource apply(IOConfiguration conf) {
							return new IOConfigurationResource(conf, projectLocation);
						}
					});
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getLocationUpdater()
	 */
	@Override
	public LocationUpdater getLocationUpdater() {
		return updater;
	}

	@Override
	public void addExportConfiguration(String name, IOConfiguration conf) {
		main.getExportConfigurations().put(name, conf);
		setChanged();
		notifyExportConfigurationChanged();
	}

	@Override
	public void removeExportConfiguration(String name) {
		main.getExportConfigurations().remove(name);
		setChanged();
		notifyExportConfigurationChanged();
	}

	@Override
	public IOConfiguration getExportConfiguration(String name) {
		IOConfiguration conf = main.getExportConfigurations().get(name);
		if (conf != null) {
			return conf.clone();
		}
		return null;
	}

	@Override
	public Collection<String> getExportConfigurationNames() {
		return Collections.unmodifiableSet(main.getExportConfigurations().keySet());
	}

	@Override
	public Collection<String> getExportConfigurationNames(
			Class<? extends IOProvider> providerClass) {
		Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, IOConfiguration> entry : main.getExportConfigurations().entrySet()) {
			IOConfiguration conf = entry.getValue();
			String providerId = conf.getProviderId();
			IOProviderDescriptor descr = IOProviderExtension.getInstance().getFactory(providerId);
			if (descr != null && providerClass.isAssignableFrom(descr.getProviderType())) {
				result.add(entry.getKey());
			}
		}
		return result;
	}

	@Override
	public URI getLoadLocation() {
		return projectLocation;
	}

	@Override
	public Value getProperty(String name) {
		return getConfigurationService().getProperty(name);
	}

	@Override
	public void setTemporaryProperty(String key, Value value) {
		temporarySettings.put(key, value);
	}

	@Override
	public Value getTemporaryProperty(String key) {
		return temporarySettings.get(key);
	}

	@Override
	public Value getTemporaryProperty(String key, Value defaultValue) {
		if (temporarySettings.containsKey(key)) {
			return temporarySettings.get(key);
		}
		else {
			return defaultValue;
		}
	}
}
