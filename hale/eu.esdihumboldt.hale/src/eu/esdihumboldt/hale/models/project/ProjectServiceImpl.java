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

import org.apache.log4j.Logger;

import eu.esdihumboldt.hale.models.HaleServiceListener;
import eu.esdihumboldt.hale.models.ProjectService;
import eu.esdihumboldt.hale.models.TaskService;
import eu.esdihumboldt.hale.models.UpdateMessage;
import eu.esdihumboldt.hale.rcp.HALEActivator;

/**
 * Default implementation of the {@link ProjectService}.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class ProjectServiceImpl 
	implements ProjectService {
	
	static ProjectService instance = new ProjectServiceImpl();
	
	private static Logger _log = Logger.getLogger(ProjectServiceImpl.class);
	
	private Set<HaleServiceListener> listeners = new HashSet<HaleServiceListener>();

	String instanceDataPath = null;
	
	String projectCreatedDate = Calendar.getInstance().getTime().toString();
	
	String sourceSchemaPath = null;
	
	String targetSchemaPath = null;
	
	String haleVersion = null;
	
	private ProjectServiceImpl(){
		this.haleVersion = (String) 
				HALEActivator.getDefault().getBundle().getHeaders().get("Bundle-Version");
	}
	
	public static ProjectService getInstance() {
		return instance;
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
	private void updateListeners() {
		for (HaleServiceListener hsl : this.listeners) {
			hsl.update(new UpdateMessage(TaskService.class, null));
		}
	}

}
