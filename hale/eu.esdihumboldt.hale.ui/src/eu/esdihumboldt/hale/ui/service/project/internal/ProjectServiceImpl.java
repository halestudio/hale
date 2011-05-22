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

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.ui.internal.HALEUIPlugin;
import eu.esdihumboldt.hale.ui.service.HaleServiceListener;
import eu.esdihumboldt.hale.ui.service.UpdateMessage;
import eu.esdihumboldt.hale.ui.service.UpdateService;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaService;
import eu.esdihumboldt.hale.ui.service.config.ConfigSchemaServiceListener;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentService;
import eu.esdihumboldt.hale.ui.service.mapping.AlignmentServiceListener;
import eu.esdihumboldt.hale.ui.service.project.ProjectService;
import eu.esdihumboldt.hale.ui.service.project.RecentFilesService;
import eu.esdihumboldt.hale.ui.service.project.internal.generated.HaleProject;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService;
import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 */
public class ProjectServiceImpl 
	implements ProjectService {
	
	private static ProjectService instance = new ProjectServiceImpl();
	
	private Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();

	private String instanceDataPath = null;
	
	private String projectCreatedDate = Calendar.getInstance().getTime().toString();
	
	private String sourceSchemaPath = null;
	
	private String targetSchemaPath = null;
	
	private String haleVersion = null;
	
	private ConfigurationType instanceDataType;
	
	private final ProjectParser parser;
	
	private final ProjectGenerator generator;
	
	private String projectName;
	
	//XXX there is no author defined in the HaleProject class
//	private String projectAuthor;
	
	private String projectFile;
	
	private String appTitle;
	
	private boolean changed = false;
	
	/**
	 * Default constructor
	 */
	private ProjectServiceImpl(){
		haleVersion = HALEUIPlugin.getDefault().getBundle().getVersion().toString();
		parser = new ProjectParser(this);
		generator = new ProjectGenerator(this);
		
		// add listeners
		AlignmentService as = (AlignmentService) PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.addListener(new AlignmentServiceListener() {
			
			@Override
			public void update(UpdateMessage<?> message) {
				// ignore
			}
			
			@Override
			public void cellsUpdated(Iterable<ICell> cells) {
				setChanged();
			}
			
			@Override
			public void cellsAdded(Iterable<ICell> cells) {
				setChanged();
			}
			
			@Override
			public void cellRemoved(ICell cell) {
				setChanged();
			}
			
			@Override
			public void alignmentCleared() {
				setChanged();
			}
		});
		
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
		
		ConfigSchemaService css = (ConfigSchemaService) PlatformUI.getWorkbench().getService(ConfigSchemaService.class);
		css.addListener(new ConfigSchemaServiceListener() {
			
			@Override
			public void update(String section, Message message) {
				switch (message) {
				case ITEM_ADDED:
				case ITEM_CHANGED:
				case ITEM_REMOVED:
				case SECTION_ADDED:
				case SECTION_REMOVED:
					setChanged();
					break;
				case CONFIG_PARSED: // fall through 
				case CONFIG_GENERATED: // fall through
					// do nothing
				}
			}
		}, null);
	}
	
	/**
	 * @see ProjectService#isChanged()
	 */
	@Override
	public boolean isChanged() {
		return changed;
	}

	/**
	 * Set that the project content has changed
	 */
	protected void setChanged() {
		changed = true;
		updateWindowTitle();
	}

	/**
	 * Get the project service instance
	 * 
	 * @return the project service instance
	 */
	public static ProjectService getInstance() {
		return instance;
	}
	
	/**
	 * @see ProjectService#clean()
	 */
	@Override
	public synchronized void clean() {
		projectFile = null;
		projectName = null;
//		projectAuthor = null;
		changed = false;
		updateWindowTitle();
		
		// clean alignment service
		AlignmentService as = (AlignmentService) 
				PlatformUI.getWorkbench().getService(AlignmentService.class);
		as.cleanModel();
		
		// clean instance service
		InstanceService is = (InstanceService) 
				PlatformUI.getWorkbench().getService(InstanceService.class);
		is.cleanInstances();
		
		// clean schema service
		SchemaService ss = (SchemaService) 
				PlatformUI.getWorkbench().getService(SchemaService.class);
		ss.cleanSourceSchema();
		ss.cleanTargetSchema();
		
		// clear user tasks
		//XXX tasks in project deactivated for now
//		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
//		taskService.clearUserTasks();
		
		// clean the project Service
		ProjectService ps = (ProjectService) 
				PlatformUI.getWorkbench().getService(ProjectService.class);
		ps.setInstanceDataPath(null);
		ps.setProjectCreatedDate(Calendar.getInstance().getTime().toString());
		ps.setSourceSchemaPath(null);
		ps.setTargetSchemaPath(null);
		
		System.gc();
	}

	/**
	 * @see ProjectService#load(String, IProgressMonitor)
	 */
	@Override
	public synchronized void load(String filename, IProgressMonitor monitor) {
		// load project
		HaleProject project = parser.read(filename, monitor);
		
		if (project != null) {
			projectName = project.getName();
//			projectAuthor = project.get
			projectFile = filename;
			changed = false;
			updateWindowTitle();
			
			RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
			rfs.add(filename);
		}
		else {
			projectName = null;
			projectFile = null;
			updateWindowTitle();
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
					title = appTitle + " - " + projectName + " - " + projectFile; //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				if (changed) {
					title = title + "*"; //$NON-NLS-1$
				}
				
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setText(title);
			}
		};
		
		if (Display.getCurrent() != null) {
			run.run();
		}
		else {
			PlatformUI.getWorkbench().getDisplay().syncExec(run);
		}
	}

	/**
	 * @see ProjectService#save()
	 */
	@Override
	public synchronized boolean save() throws JAXBException {
		if (projectFile != null) {
			if (projectName == null) {
				projectName = "default"; //$NON-NLS-1$
			}
			generator.write(projectFile, projectName);
			changed = false;
			updateWindowTitle();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * @see ProjectService#saveAs(String, String)
	 */
	@Override
	public synchronized void saveAs(String filename, String projectName) throws JAXBException {
		if (projectName == null) {
			projectName = "default"; //$NON-NLS-1$
		}
		generator.write(filename, projectName);
		this.projectFile = filename;
		this.projectName = projectName;
		changed = false;
		updateWindowTitle();
		
		RecentFilesService rfs = (RecentFilesService) PlatformUI.getWorkbench().getService(RecentFilesService.class);
		rfs.add(filename);
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getInstanceDataPath()
	 */
	@Override
	public String getInstanceDataPath() {
		return this.instanceDataPath;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getProjectCreatedDate()
	 */
	@Override
	public String getProjectCreatedDate() {
		return this.projectCreatedDate;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getSourceSchemaPath()
	 */
	@Override
	public String getSourceSchemaPath() {
		return this.sourceSchemaPath;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getTargetSchemaPath()
	 */
	@Override
	public String getTargetSchemaPath() {
		return this.targetSchemaPath;
	}

	@Override
	public void setInstanceDataPath(String path) {
		this.instanceDataPath = path;
		changed = true;
		updateWindowTitle();
		updateListeners();
	}

	@Override
	public void setProjectCreatedDate(String date) {
		this.projectCreatedDate = date;
		updateListeners();
	}

	@Override
	public void setSourceSchemaPath(String path) {
		this.sourceSchemaPath = path;
		changed = true;
		updateWindowTitle();
		updateListeners();
	}

	@Override
	public void setTargetSchemaPath(String path) {
		this.targetSchemaPath = path;
		changed = true;
		updateWindowTitle();
		updateListeners();
	}
	
	/**
	 * @see ProjectService#getProjectName()
	 */
	@Override
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.service.project.ProjectService#getHaleVersion()
	 */
	@Override
	public String getHaleVersion() {
		return this.haleVersion;
	}
	
	// UpdateService operations ................................................

	/**
	 * @see eu.esdihumboldt.hale.ui.service.UpdateService#addListener(eu.esdihumboldt.hale.ui.service.HaleServiceListener)
	 */
	@Override
	public void addListener(HaleServiceListener sl) {
		this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(ProjectService.class, null));
		}
	}

	/**
	 * @see UpdateService#removeListener(HaleServiceListener)
	 */
	@Override
	public void removeListener(HaleServiceListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @see ProjectService#getInstanceDataType()
	 */
	@Override
	public ConfigurationType getInstanceDataType() {
		return instanceDataType;
	}

	/**
	 * @see ProjectService#setInstanceDataType(ConfigurationType)
	 */
	@Override
	public void setInstanceDataType(ConfigurationType type) {
		this.instanceDataType = type;
		changed = true;
		updateWindowTitle();
	}

}
