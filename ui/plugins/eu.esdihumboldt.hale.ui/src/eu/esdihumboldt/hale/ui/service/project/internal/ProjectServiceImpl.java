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

package eu.esdihumboldt.hale.ui.service.project.internal;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Version;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import de.fhg.igd.osgi.util.configuration.AbstractDefaultConfigurationService;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.core.io.ExportProvider;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOAdvisor;
import eu.esdihumboldt.hale.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.core.io.project.ProjectReaderFactory;
import eu.esdihumboldt.hale.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.core.io.project.ProjectWriterFactory;
import eu.esdihumboldt.hale.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.core.io.project.model.Project;
import eu.esdihumboldt.hale.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorFactory;
import eu.esdihumboldt.hale.ui.io.project.OpenProjectWizard;
import eu.esdihumboldt.hale.ui.io.project.SaveProjectWizard;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.io.util.ThreadProgressMonitor;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;
import eu.esdihumboldt.hale.ui.service.report.ReportService;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 * @author Simon Templer
 */
public class ProjectServiceImpl extends AbstractProjectService 
	implements ProjectService {
	
	/**
	 * Configuration service backed by the internal {@link Project}
	 */
	private class ProjectConfigurationService extends
			AbstractDefaultConfigurationService implements
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
				return main.getProperties().get(key);
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
				main.getProperties().put(key, value);
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
	
	private String loc;		//added to get the location
	
	/**
	 * Default constructor
	 */
	public ProjectServiceImpl(){
		haleVersion = Version.parseVersion(Display.getAppVersion());
		main = createDefaultProject();
		
		// create advisors
		openProjectAdvisor = new AbstractIOAdvisor<ProjectReader>() {
			
			@Override
			public void handleResults(ProjectReader provider) {
				clean();
				
				synchronized (ProjectServiceImpl.this) {
					main = provider.getProject();
					loc = provider.getSource().getLocation().toASCIIString(); // for the location
					updatePaths(main);
					projectFile = new File(provider.getSource().getLocation());
					changed = false;
					RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
					rfs.add(projectFile.getAbsolutePath());
				}
				
				updateWindowTitle();
				
				// execute loaded I/O configurations
				executeConfigurations(main.getResources());
				
				// notify listeners
				Map<String, ProjectFile> projectFiles = provider.getProjectFiles(); //TODO store somewhere for later use?
				notifyAfterLoad(projectFiles);
			}

			// uses paths based on "/" in FilePathUpdate
			private void updatePaths(Project main) {
				FilePathUpdate update = new FilePathUpdate();
				IOConfiguration saveconfig = main.getSaveConfiguration();
				if(saveconfig == null)
					return;
				String exptargetfile = saveconfig.getProviderConfiguration().get(ExportProvider.PARAM_TARGET);
				String exptarget = exptargetfile.substring(0, exptargetfile.lastIndexOf("/"));
				String location = loc.substring(loc.indexOf("/")+1, loc.length());
				
				if(!location.equals(exptarget)){
					List<IOConfiguration> configuration = main.getResources();
					for(IOConfiguration providerconf : configuration){
						Map<String, String> conf = providerconf.getProviderConfiguration();
						String impsrc = conf.get(ImportProvider.PARAM_SOURCE);
						String target = impsrc.substring(impsrc.lastIndexOf("/") + 1);
						String extension = "*" + impsrc.substring(impsrc.lastIndexOf("."));
						String[] extensions = new String[]{extension};
						try { 
							URI uri = new URI(impsrc);
							File file = new File(uri);
							if(!file.exists()){
								String newsrc = update.changePath(impsrc, location);
								
								URI newuri = new URI(newsrc);
								File newfile = new File(newuri);
								if(newfile.exists()){
									conf.remove(ImportProvider.PARAM_SOURCE);
									conf.put(ImportProvider.PARAM_SOURCE, newsrc);
								} else {
									MessageBox error = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
									error.setText("Loading Error");
									error.setMessage("Can't load " + impsrc);
									error.open();
									FileDialog filedialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN | SWT.SHEET);
									filedialog.setFilterExtensions(extensions);
									filedialog.setFileName(target);
									
							        String openfile = filedialog.open();
							        if (openfile != null) {
							            openfile = openfile.trim();
							            if (openfile.length() > 0) {
							            	openfile = "file:/" + openfile;
							            	openfile = openfile.replace("\\", "/");
							            	conf.remove(ImportProvider.PARAM_SOURCE);
											conf.put(ImportProvider.PARAM_SOURCE, openfile);
										}
							        }
							        }
								}
							
						} catch (URISyntaxException e) {
							// ignore?
						}
					}
				}

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
				Map<String, ProjectFile> projectFiles = new HashMap<String, ProjectFile>();
				notifyBeforeSave(projectFiles); // get additional files from listeners
				provider.setProjectFiles(projectFiles);
			}
			
			@Override
			public void handleResults(ProjectWriter provider) {
				synchronized (ProjectServiceImpl.this) {
					projectFile = new File(provider.getTarget().getLocation());
					changed = false;
					RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
					rfs.add(projectFile.getAbsolutePath());
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
	 * Execute a set of I/O configurations
	 * @param configurations the I/O configurations
	 */
	protected void executeConfigurations(final List<IOConfiguration> configurations) {
		//TODO sort by dependencies
		
		// combine inside job (or with progress monitor?) if no monitor is running
		if (ThreadProgressMonitor.getCurrent() == null) {
			Job job = new Job("Load I/O configurations") {
				
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					ThreadProgressMonitor.register(monitor);
					try {
						monitor.beginTask("Load I/O configurations", configurations.size());
						for (IOConfiguration conf : configurations) {
							executeConfiguration(conf);
							monitor.worked(1);
						}
						return new Status(IStatus.OK, HALEUIPlugin.PLUGIN_ID, "Loaded I/O configurations");
					} finally {
						monitor.done();
						ThreadProgressMonitor.remove(monitor);
					}
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else {
			for (IOConfiguration conf : configurations) {
				executeConfiguration(conf);
			}
		}
	}

	private void executeConfiguration(IOConfiguration conf) {
		// get provider ...
		Collection<? extends IOProviderFactory<?>> providers = HaleIO.getProviderFactories(conf.getProviderType());
		IOProvider provider = null;
		for (IOProviderFactory<?> factory : providers) {
			if (factory.getIdentifier().equals(conf.getProviderId())) {
				provider = factory.createProvider();
			}
		}
		
		if (provider != null) {
			// ... and advisor
			IOAdvisorFactory advisorFactory = IOAdvisorExtension.getInstance().getFactory(conf.getAdvisorId());
			try {
				IOAdvisor<?> advisor = advisorFactory.createExtensionObject();
				// configure settings
				provider.loadConfiguration(conf.getProviderConfiguration());
				// execute provider
				executeProvider(provider, advisor);
			} catch (Exception e) {
				log.error(MessageFormat.format(
						"Could not execute I/O configuration, advisor with ID {0} could not be created.",
						conf.getAdvisorId()), e);
			}
		}
		else {
			log.error(MessageFormat.format(
					"Could not execute I/O configuration, provider with ID {0} not found.",
					conf.getProviderId()));
		}
	}

	private void executeProvider(final IOProvider provider, @SuppressWarnings("rawtypes") final IOAdvisor advisor) {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException,
					InterruptedException {
				IOReporter reporter = provider.createReporter();
				ATransaction trans = log.begin(reporter.getTaskName());
				ThreadProgressMonitor.register(monitor);
				try {
					// use advisor to configure provider
					advisor.prepareProvider(provider);
					advisor.updateConfiguration(provider);
					
					// execute
					IOReport report = provider.execute(new ProgressMonitorIndicator(monitor));
					
					// publish report
					ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
					rs.addReport(report);
					
					// handle results
					if (report.isSuccess()) {
						advisor.handleResults(provider);
					}
				} catch (Exception e) {
					log.error("Error executing an I/O provider.", e);
				} finally {
					ThreadProgressMonitor.remove(monitor);
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

	/**
	 * @see ProjectService#clean()
	 */
	@Override
	public void clean() {
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
				} finally {
					monitor.done();
					trans.end();
				}
			}
		};
		
		try {
			ThreadProgressMonitor.runWithProgressDialog(op, false);
		} catch (Exception e) {
			log.error("Error cleaning the project.", e);
		}
	}

	/**
	 * @see ProjectService#load(File)
	 */
	@Override
	public void load(File file) {
		// use I/O provider and content type mechanisms to enable loading of a project file
		ProjectReader reader = HaleIO.findIOProvider(ProjectReaderFactory.class, 
				new FileIOSupplier(file), file.getAbsolutePath());
		if (reader != null) {
			// configure reader
			reader.setSource(new FileIOSupplier(file));
			
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
						appTitle = PlatformUI.getWorkbench()./*getWorkbenchWindows()[0]*/getActiveWorkbenchWindow().getShell().getText();
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
					title = appTitle + " - " + ((pn == null || pn.isEmpty())?("Unnamed"):(pn)) + " - " + projectFile;
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
			Collection<ProjectWriterFactory> providers = 
				HaleIO.getProviderFactories(ProjectWriterFactory.class);
			
			// use configuration from previous save if possible
			if (saveConfig != null) {
				// get provider ...
				ProjectWriter writer = null;
				for (ProjectWriterFactory factory : providers) {
					if (factory.getIdentifier().equals(saveConfig.getProviderId())) {
						writer = factory.createProvider();
					}
				}
				
				if (writer != null) {
					// configure provider
					writer.loadConfiguration(saveConfig.getProviderConfiguration());
					// overwrite target with projectFile (as it may have been moved externally)
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
				// use I/O provider and content type mechanisms to try saving the project file
				ProjectWriter writer = HaleIO.findIOProvider(ProjectWriterFactory.class, 
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
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

	/**
	 * @see ProjectService#open()
	 */
	@Override
	public void open() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				OpenProjectWizard wizard = new OpenProjectWizard();
				wizard.setAdvisor(openProjectAdvisor, null);
				
				Shell shell = Display.getCurrent().getActiveShell();
				WizardDialog dialog = new WizardDialog(shell, wizard);
				dialog.open();
			}
		});
	}

	/**
	 * Create a project with default values
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
	 * @see ProjectService#rememberIO(IOAdvisorFactory, Class, String, IOProvider)
	 */
	@Override
	public void rememberIO(IOAdvisorFactory advisorFactory,
			Class<? extends IOProviderFactory<?>> providerType, 
			String providerId, IOProvider provider) {
		// populate an IOConfiguration from the given data
		IOConfiguration conf = new IOConfiguration();
		conf.setAdvisorId(advisorFactory.getIdentifier());
		conf.setProviderId(providerId);
		conf.setProviderType(providerType);
		conf.getDependencies().addAll(advisorFactory.getDependencies());
		provider.storeConfiguration(conf.getProviderConfiguration());
		
		// add configuration to project
		synchronized (this) {
			main.getResources().add(conf);
		}
		setChanged();
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

	
}
