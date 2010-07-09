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

package eu.esdihumboldt.hale.models;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;

import eu.esdihumboldt.hale.gmlparser.GmlHelper.ConfigurationType;

/**
 * The {@link ProjectService} manages additional information on a HaleProject,
 * such as the loaded instance data and paths of used schemas.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public interface ProjectService extends UpdateService {

	public String getSourceSchemaPath();
	
	public void setSourceSchemaPath(String path);
	
	public String getTargetSchemaPath();
	
	public void setTargetSchemaPath(String path);
	
	public String getInstanceDataPath();
	
	public void setInstanceDataPath(String path);
	
	public String getProjectCreatedDate();
	
	public void setProjectCreatedDate(String date);
	
	public String getHaleVersion();
	
	/**
	 * Set the configuration type of the instance data
	 * 
	 * @param type the configuiration type
	 */
	public void setInstanceDataType(ConfigurationType type);
	
	/**
	 * Get the configuration type of the instance data
	 * 
	 * @return the configuration type of the instance data
	 */
	public ConfigurationType getInstanceDataType();
	
	/**
	 * Clean the project, reset all services
	 */
	public void clean();
	
	/**
	 * Load a project from a file
	 * 
	 * @param filename the project filename
	 * @param monitor the progress monitor
	 */
	public void load(String filename, IProgressMonitor monitor);
	
	/**
	 * Save the project
	 * @return false, if no project file name was set
	 * 
	 * @throws JAXBException if saving the project fails 
	 */
	public boolean save() throws JAXBException;
	
	/**
	 * Save the project to the given file
	 * 
	 * @param filename the file name
	 * @param projectName the project name
	 * 
	 * @throws JAXBException if saving the project fails
	 */
	public void saveAs(String filename, String projectName) throws JAXBException;
	
}
