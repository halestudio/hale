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

package eu.esdihumboldt.hale.common.core.io.project;

import java.util.Date;

import org.osgi.framework.Version;

/**
 * General information on a project
 * 
 * @author Simon Templer
 * @since 2.5
 */
public interface ProjectInfo {

	/**
	 * Get the project name
	 * 
	 * @return the project name, may be <code>null</code> if not set
	 */
	public String getName();

	/**
	 * @return the author
	 */
	public String getAuthor();

	/**
	 * @return the haleVersion
	 */
	public Version getHaleVersion();

	/**
	 * @return the created
	 */
	public Date getCreated();

	/**
	 * @return the modified
	 */
	public Date getModified();

	/**
	 * @return the description
	 */
	public String getDescription();

}