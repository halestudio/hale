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
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Version;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.fhg.igd.osgi.util.configuration.AbstractConfigurationService;
import de.fhg.igd.osgi.util.configuration.AbstractDefaultConfigurationService;
import de.fhg.igd.osgi.util.configuration.IConfigurationService;
import eu.esdihumboldt.hale.core.io.ExportProvider;
import eu.esdihumboldt.hale.core.io.HaleIO;
import eu.esdihumboldt.hale.core.io.IOAdvisor;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderFactory;
import eu.esdihumboldt.hale.core.io.ImportProvider;
import eu.esdihumboldt.hale.core.io.project.ProjectReader;
import eu.esdihumboldt.hale.core.io.project.ProjectWriter;
import eu.esdihumboldt.hale.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.core.io.project.model.Project;
import eu.esdihumboldt.hale.core.io.project.model.ProjectFile;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorExtension;
import eu.esdihumboldt.hale.ui.io.advisor.IOAdvisorFactory;
import eu.esdihumboldt.hale.ui.io.project.OpenProjectWizard;
import eu.esdihumboldt.hale.ui.io.project.SaveProjectWizard;
import eu.esdihumboldt.hale.ui.io.util.ProgressMonitorIndicator;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
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
		}

		/**
		 * @see AbstractConfigurationService#setValue(String, String)
		 */
		@Override
		protected void setValue(String key, String value) {
			synchronized (ProjectServiceImpl.this) {
				main.getProperties().put(key, value);
			}
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
	
//	private boolean changed = false;
	
	/**
	 * Default constructor
	 */
	public ProjectServiceImpl(){
		haleVersion = HALEUIPlugin.getDefault().getBundle().getVersion(); //FIXME: consistency with application plugin?
		main = createDefaultProject();
		
		// create advisors
		openProjectAdvisor = new IOAdvisor<ProjectReader>() {
			
			@Override
			public void updateConfiguration(ProjectReader provider) {
				// do nothing
			}
			
			@Override
			public void handleResults(ProjectReader provider) {
				main = provider.getProject();
				projectFile = new File(provider.getSource().getLocation());
//				changed = false;
//				RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
//				rfs.add(file.getAbsolutePath());
				
				// execute loaded I/O configurations
				executeConfigurations(main.getConfigurations());
				
				// notify listeners
				Map<String, ProjectFile> projectFiles = provider.getProjectFiles(); //TODO store somewhere for later use?
				notifyAfterLoad(projectFiles);
			}
		};
		
		saveProjectAdvisor = new IOAdvisor<ProjectWriter>() {
			
			@Override
			public void updateConfiguration(ProjectWriter provider) {
				provider.setProject(main);
				Map<String, ProjectFile> projectFiles = new HashMap<String, ProjectFile>();
				notifyBeforeSave(projectFiles); // get additional files from listeners
				provider.setProjectFiles(projectFiles);
			}
			
			@Override
			public void handleResults(ProjectWriter provider) {
//				changed = false;
				updateWindowTitle();
			}
		};
		
		// add listeners
//		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
//		as.addListener(new AlignmentServiceListener() {
//			
//			@Override
//			public void update(UpdateMessage<?> message) {
//				// ignore
//			}
//			
//			@Override
//			public void cellsUpdated(Iterable<ICell> cells) {
//				setChanged();
//			}
//			
//			@Override
//			public void cellsAdded(Iterable<ICell> cells) {
//				setChanged();
//			}
//			
//			@Override
//			public void cellRemoved(ICell cell) {
//				setChanged();
//			}
//			
//			@Override
//			public void alignmentCleared() {
//				setChanged();
//			}
//		});
		
		//XXX styles and tasks in project deactivated for now
//		StyleService ss = (StyleService) PlatformUI.getWorkbench().getService(StyleService.class);
//		ss.addListener(new HaleServiceListener() {
//			
//			@Override
//			public void update(UpdateMessage<?> message) {
//				setChanged();
//			}
//		});
//		
//		TaskService ts = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
//		ts.addListener(new TaskServiceListener() {
//			
//			@Override
//			public void update(UpdateMessage<?> message) {
//				// ignore
//			}
//			
//			@Override
//			public void tasksRemoved(Iterable<Task> tasks) {
//				// ignore
//			}
//			
//			@Override
//			public void tasksAdded(Iterable<Task> tasks) {
//				// ignore
//			}
//			
//			@Override
//			public void taskUserDataChanged(ResolvedTask task) {
//				setChanged();
//			}
//		});
		
//		ConfigSchemaService css = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
//		css.addListener(new ConfigSchemaServiceListener() {
//			
//			@Override
//			public void update(String section, Message message) {
//				switch (message) {
//				case ITEM_ADDED:
//				case ITEM_CHANGED:
//				case ITEM_REMOVED:
//				case SECTION_ADDED:
//				case SECTION_REMOVED:
//					setChanged();
//					break;
//				case CONFIG_PARSED: // fall through 
//				case CONFIG_GENERATED: // fall through
//					// do nothing
//				}
//			}
//		}, null);
	}
	
//	/**
//	 * @see ProjectService#isChanged()
//	 */
//	@Override
//	public boolean isChanged() {
//		return changed;
//	}
//
//	/**
//	 * Set that the project content has changed
//	 */
//	protected void setChanged() {
//		changed = true;
//		updateWindowTitle();
//	}

	/**
	 * Execute a set of I/O configurations
	 * @param configurations the I/O configurations
	 */
	protected void executeConfigurations(List<IOConfiguration> configurations) {
		//TODO sort by dependencies
		
		for (IOConfiguration conf : configurations) {
			executeConfiguration(conf);
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
				// configure location
//				if (provider instanceof ImportProvider) {
//					((ImportProvider) provider).setSource(new DefaultInputSupplier(conf.getLocation()));
//				}
//				else if (provider instanceof ExportProvider) {
//					((ExportProvider) provider).setTarget(new FileIOSupplier(new File(conf.getLocation())));
//				}
//				else {
//					log.warn("Could not set location on I/O provider.");
//				}
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
		Display display = PlatformUI.getWorkbench().getDisplay();
		try {
			IRunnableWithProgress op = new IRunnableWithProgress() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					try {
						// use advisor to configure provider
						advisor.updateConfiguration(provider);
						
						// execute
						IOReport report = provider.execute(new ProgressMonitorIndicator(monitor));
						
						// publish report
						ReportService rs = (ReportService) PlatformUI.getWorkbench().getService(ReportService.class);
						rs.addReport(report);
						
						// handle results
						advisor.handleResults(provider);
					} catch (Exception e) {
						log.error("Error executing an I/O provider.", e);
					}
				}
			};
			//TODO instead in job?
		    new ProgressMonitorDialog(display.getActiveShell()).run(true, 
		    		provider.isCancelable(), op);
		} catch (Throwable e) {
			log.error("Error executing an I/O provider.");
		}
	}

	/**
	 * @see ProjectService#clean()
	 */
	@Override
	public void clean() {
		synchronized (this) {
			main = createDefaultProject();
		}
		
		//TODO
//		projectFile = null;
//		projectName = null;
////		projectAuthor = null;
//		changed = false;
//		updateWindowTitle();
//		
//		// clean alignment service
//		AlignmentService as = (AlignmentService) 
//				PlatformUI.getWorkbench().getService(AlignmentService.class);
//		as.cleanModel();
//		
//		// clean instance service
//		InstanceService is = (InstanceService) 
//				PlatformUI.getWorkbench().getService(InstanceService.class);
//		is.cleanInstances();
//		
//		// clean schema service
//		SchemaService ss = (SchemaService) 
//				PlatformUI.getWorkbench().getService(SchemaService.class);
//		ss.cleanSourceSchema();
//		ss.cleanTargetSchema();
//		
//		// clear user tasks
//		//XXX tasks in project deactivated for now
////		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
////		taskService.clearUserTasks();
//		
//		// clean the project Service
//		ProjectService ps = (ProjectService) 
//				PlatformUI.getWorkbench().getService(ProjectService.class);
//		ps.setInstanceDataPath(null);
//		ps.setProjectCreatedDate(Calendar.getInstance().getTime().toString());
//		ps.setSourceSchemaPath(null);
//		ps.setTargetSchemaPath(null);
//		
//		System.gc();
		notifyClean();
	}

//	/**
//	 * @see ProjectService#load(File, IProgressMonitor)
//	 */
//	@Override
//	public synchronized void load(File file, IProgressMonitor monitor) {
//		ProjectFileService pfs = (ProjectFileService) PlatformUI.getWorkbench().getService(ProjectFileService.class);
//		
//		ZipProjectReader reader = new ZipProjectReader();
//		reader.setProjectFiles(pfs.getProjectFiles()); //TODO advisor?
//		
//		IOReport report;
//		try {
//			report = reader.execute(new ProgressMonitorIndicator(monitor));
//		} catch (Exception e) {
//			// TODO how to handle?
//			report = null;
//		}
//		if (report != null && report.isSuccess()) {
//			//TODO advisor?
//			main = reader.getProject();
//			projectFile = file;
////			changed = false;
//			RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
//			rfs.add(file.getAbsolutePath());
//			//TODO publish report?
//		}
//		else {
//			projectFile = null; //XXX ?
//			//TODO reset project???
//		}
//		
//		updateWindowTitle();
//	}

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
					title = appTitle + " - " + getProjectName() + " - " + projectFile; //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				//XXX
//				if (changed) {
//					title = title + "*"; //$NON-NLS-1$
//				}
				
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(title);
			}
		};
		
		PlatformUI.getWorkbench().getDisplay().syncExec(run);
	}

//	/**
//	 * @see ProjectService#save(IProgressMonitor)
//	 */
//	@Override
//	public synchronized void save(IProgressMonitor monitor) {
//		if (projectFile != null) {
//			ProjectFileService pfs = (ProjectFileService) PlatformUI.getWorkbench().getService(ProjectFileService.class);
//			
//			ZipProjectWriter writer = new ZipProjectWriter();
//			//TODO advisor?
//			writer.setProject(main);
//			writer.setProjectFiles(pfs.getProjectFiles());
//			
//			IOReport report;
//			try {
//				report = writer.execute(new ProgressMonitorIndicator(monitor));
//			} catch (Exception e) {
//				// TODO how to handle?
//				report = null;
//			}
//			if (report != null && report.isSuccess()) {
//				//TODO advisor?
////				changed = false;
//				updateWindowTitle();
//				//TODO publish report?
//			}
//		}
//		else {
//			saveAs();
//		}
//	}

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
		project.setName("Unnamed");
		
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
//		URI loc = null;
//		if (provider instanceof ImportProvider) {
//			loc = ((ImportProvider) provider).getSource().getLocation();
//		}
//		else if (provider instanceof ExportProvider) {
//			loc = ((ExportProvider) provider).getTarget().getLocation();
//		}
//		if (loc == null) {
//			throw new IllegalStateException("Could not extract location from I/O provider.");
//		}
//		conf.setLocation(loc);
		conf.getDependencies().addAll(advisorFactory.getDependencies());
		provider.storeConfiguration(conf.getProviderConfiguration());
		
		// add configuration to project
		synchronized (this) {
			main.getConfigurations().add(conf);
		}
	}

	/**
	 * @see ProjectService#getProjectName()
	 */
	@Override
	public String getProjectName() {
		synchronized (this) {
			return main.getName();
		}
	}

}
