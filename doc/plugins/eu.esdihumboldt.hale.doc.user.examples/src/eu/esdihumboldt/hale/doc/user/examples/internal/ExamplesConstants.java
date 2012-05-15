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

package eu.esdihumboldt.hale.doc.user.examples.internal;

import org.eclipse.help.ITopic;

/**
 * Constants related to examples and help.
 * @author Simon Templer
 */
public interface ExamplesConstants {
	
	/**
	 * The ID of this plug-in.
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.hale.doc.user.examples";
	
	/**
	 * The path referencing the plug-ins root.
	 */
	public static final String PLUGINS_ROOT = "PLUGINS_ROOT";
	
	/**
	 * The pat of the overview page in this bundle.
	 */
	public static final String PATH_OVERVIEW = "overview.html";
	
	/**
	 * The prefix for project pages
	 */
	public static final String PATH_PREFIX_PROJECT = "projects/";
	
	/**
	 * Empty topics array
	 */
	public static final ITopic[] NO_TOPICS = new ITopic[]{};

}
