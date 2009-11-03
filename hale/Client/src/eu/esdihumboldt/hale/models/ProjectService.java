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
	
}
