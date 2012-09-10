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

package eu.esdihumboldt.cst.doc.functions;

import org.eclipse.help.ITopic;

/**
 * Constants related to the function reference
 * 
 * @author Simon Templer
 */
public interface FunctionReferenceConstants {

	/**
	 * The identifier of this plug-in
	 */
	public static final String PLUGIN_ID = "eu.esdihumboldt.cst.doc.functions";

	/**
	 * The path referencing the plug-ins root
	 */
	public static final String PLUGINS_ROOT = "PLUGINS_ROOT";

	/**
	 * The base path of function topics
	 */
	public static final String FUNCTION_TOPIC_PATH = "functions/";

	/**
	 * Empty topics array
	 */
	public static final ITopic[] NO_TOPICS = new ITopic[] {};

}
