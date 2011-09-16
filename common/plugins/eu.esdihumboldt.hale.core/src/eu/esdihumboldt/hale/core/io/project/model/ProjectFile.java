/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.core.io.project.model;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * File associated/stored with a project. Implementations must have a default
 * constructor if they shall be used in a project.
 * @author Simon Templer
 */
public interface ProjectFile {
	
	/**
	 * Load the file from an input stream when loading a project.
	 * @param in the input stream
	 */
	public void load(InputStream in);
	
	/**
	 * Reset the file when a project is loaded but no data for the file was 
	 * present or loading the file failed. 
	 */
	public void reset();
	
	/**
	 * Store the file to an output stream when saving a project.
	 * @param out the output stream
	 */
	public void store(OutputStream out);

}
