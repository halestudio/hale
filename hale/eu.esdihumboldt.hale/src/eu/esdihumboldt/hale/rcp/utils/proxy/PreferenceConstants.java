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

package eu.esdihumboldt.hale.rcp.utils.proxy;

import org.eclipse.equinox.security.storage.ISecurePreferences;

/**
 * This interface defines preference constants
 * @author Michel Kraemer
 */
public interface PreferenceConstants {
	
	/**
	 * Name of the node where passwords are stored in the {@link ISecurePreferences}
	 */
	public static final String SECURE_NODE_NAME = "de.cs3d";
	
	/**
	 * The default spatial reference system
	 */
	public static final String GENERAL_DEFAULT_SRS =
		"admintool.general.default_srs"; //$NON-NLS-1$
	
	/**
	 * The server to connect to
	 */
	public static final String CONNECTION_SERVER_HOST =
		"admintool.connection.server.host"; //$NON-NLS-1$
	
	/**
	 * The server port to connect to
	 */
	public static final String CONNECTION_SERVER_PORT =
		"admintool.connection.server.port"; //$NON-NLS-1$
	
	/**
	 * True if the proxy should be used for the server connection
	 */
	public static final String CONNECTION_SERVER_USE_PROXY =
		"admintool.connection.server.useproxy"; //$NON-NLS-1$
	
	/**
	 * The username for the server connection
	 */
	public static final String CONNECTION_SERVER_USERNAME =
		"admintool.connection.server.username"; //$NON-NLS-1$
	
	/**
	 * The password for the server connection
	 */
	public static final String CONNECTION_SERVER_PASSWORD =
		"admintool.connection.server.password"; //$NON-NLS-1$
	
	/**
	 * The proxy to use for the server connection
	 */
	public static final String CONNECTION_PROXY_HOST =
		"http.proxyHost"; //$NON-NLS-1$
	
	/**
	 * The proxy port to use for the server connection
	 */
	public static final String CONNECTION_PROXY_PORT =
		"http.proxyPort"; //$NON-NLS-1$
	
	/**
	 * The proxy user name to use for the server connection
	 */
	public static final String CONNECTION_PROXY_USER =
		"http.proxyUser"; //$NON-NLS-1$
	
	/**
	 * The proxy password to use for the server connection
	 */
	public static final String CONNECTION_PROXY_PASSWORD =
		"http.proxyPassword"; //$NON-NLS-1$
	
	/**
	 * A list of hosts to connect to without using the proxy (separated by
	 * a | character)
	 */
	public static final String CONNECTION_NON_PROXY_HOSTS =
		"http.nonProxyHosts"; //$NON-NLS-1$
	
	/**
	 * A comma-separated list of metadata item names recently used in the
	 * search-for-metadata dialog
	 * @see de.cs3d.ui.application.commands.SearchDialog
	 */
	public static final String SEARCH_METADATA_RECENT_NAMES =
		"admintool.search.metadata.recentNames";
	
	/**
	 * A comma-separated list of metadata item values recently used in the
	 * search-for-metadata dialog
	 * @see de.cs3d.ui.application.commands.SearchDialog
	 */
	public static final String SEARCH_METADATA_RECENT_VALUES =
		"admintool.search.metadata.recentValues";
	
	/**
	 * A comma-separated list of feature types recently entered by the user
	 * (for example in the edit-type dialog)
	 */
	public static final String RECENT_FEATURE_TYPES =
		"admintool.recent.featureTypes";
	
	/**
	 * A comma-separated list of geometric types recently entered by the user
	 * (for example in the edit-type dialog)
	 */
	public static final String RECENT_GEOMETRIC_TYPES =
		"admintool.recent.geometricTypes";
	
	/**
	 * A comma-separated list of urls to import
	 */
	public static final String URL_USED_FOR_IMPORT =
		"admintool.import.url";
	
	/**
	 * a character to use as ASCII grid decimal point
	 */
	public static final String EXPORT_GRID_TEXT_COMMA = 
		"admintool.export.pages.grid.commata";
	
	/**
	 * grid export precision as number of digits
	 */
	public static final String EXPORT_GRID_TEXT_Z_PRECISION = 
		"admintool.export.pages.grid.precision.z";
}
