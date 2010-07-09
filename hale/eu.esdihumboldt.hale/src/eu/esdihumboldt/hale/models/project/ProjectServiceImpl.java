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

package eu.esdihumboldt.hale.models.project;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;
import eu.esdihumboldt.hale.models.AlignmentService;
import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.InstanceService;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.SchemaService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.models.UpdateService;
import eu.esdihumboldt.hale.models.project.generated.HaleProject;
import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectServiceImpl 
	implements ProjectService {
	
	private static ProjectService instance = new ProjectServiceImpl();
	
	private static Logger _log = Logger.getLogger(ProjectServiceImpl.class);
	
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
	
	private String projectFile;
	
	private String appTitle;
	
	/**
	 * Default constructor
	 */
	private ProjectServiceImpl(){
		haleVersion = HALEActivator.getDefault().getBundle().getVersion().toString();
		parser = new ProjectParser(this);
		generator = new ProjectGenerator(this);
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
		TaskService taskService = (TaskService) PlatformUI.getWorkbench().getService(TaskService.class);
		taskService.clearUserTasks();
		
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
			projectFile = filename;
			updateWindowTitle();
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
					title = appTitle + " - " + projectName + " - " + projectFile;
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
				projectName = "default";
			}
			generator.write(projectFile, projectName);
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
		generator.write(filename, projectName);
		this.projectFile = filename;
		this.projectName = projectName;
		updateWindowTitle();
	}

	/**
	 * @see eu.esdihumboldt.hale.models.ProjectService#getInstanceDataPath()
	 */
	@Override
	public String getInstanceDataPath() {
		return this.instanceDataPath;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.ProjectService#getProjectCreatedDate()
	 */
	@Override
	public String getProjectCreatedDate() {
		return this.projectCreatedDate;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.ProjectService#getSourceSchemaPath()
	 */
	@Override
	public String getSourceSchemaPath() {
		return this.sourceSchemaPath;
	}

	/**
	 * @see eu.esdihumboldt.hale.models.ProjectService#getTargetSchemaPath()
	 */
	@Override
	public String getTargetSchemaPath() {
		return this.targetSchemaPath;
	}

	@Override
	public void setInstanceDataPath(String path) {
		this.instanceDataPath = path;
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
		updateListeners();
	}

	@Override
	public void setTargetSchemaPath(String path) {
		this.targetSchemaPath = path;
		updateListeners();
	}
	
	/**
	 * @see eu.esdihumboldt.hale.models.ProjectService#getHaleVersion()
	 */
	@Override
	public String getHaleVersion() {
		return this.haleVersion;
	}
	
	// UpdateService operations ................................................

	/**
	 * @see eu.esdihumboldt.hale.models.UpdateService#addListener(eu.esdihumboldt.hale.models.HaleServiceListener)
	 */
	public boolean addListener(HaleServiceListener sl) {
		return this.listeners.add(sl);
	}
	
	/**
	 * Inform {@link HaleServiceListener}s of an update.
	 */
	@SuppressWarnings("unchecked")
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(TaskService.class, null));
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
	}

}
