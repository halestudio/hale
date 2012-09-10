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

package eu.esdihumboldt.hale.ui.views.schemas.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.views.schemas.internal.messages"; //$NON-NLS-1$

	public static String ModelNavigationView_FunctionButtonToolTipText;
	public static String ModelNavigationView_GeometryHide;
	public static String ModelNavigationView_GeometryShow;
	public static String ModelNavigationView_NumericHide;
	public static String ModelNavigationView_NumericShow;
	public static String ModelNavigationView_PropertyHide;
	public static String ModelNavigationView_PropertyShow;
	public static String ModelNavigationView_StringHide;
	public static String ModelNavigationView_StringShow;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	/**
	 * Hidden constructor
	 */
	private Messages() {
		// nothing to do here
	}

}
