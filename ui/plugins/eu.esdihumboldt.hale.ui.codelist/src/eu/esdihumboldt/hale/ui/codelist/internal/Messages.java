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

package eu.esdihumboldt.hale.ui.codelist.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.codelist.internal.messages"; //$NON-NLS-1$

	public static String CodeListSelectionDialog_0;
	public static String CodeListSelectionDialog_1;
	public static String CodeListSelectionDialog_2;
	public static String CodeListSelectionDialog_3;
	public static String CodeListSelectionDialog_4;
	public static String CodeListSelectionDialog_5;
	public static String CodeListSelectionDialog_6;

	public static String CodeListPreferencePage_0;
	public static String CodeListPreferencePage_1;
	public static String CodeListPreferencePage_2;
	public static String CodeListPreferencePage_3;
	public static String CodeListPreferencePage_4;

	public static String CodeListPreferencePage_5;
	public static String CodeListPreferencePage_6;
	public static String CodeListPreferencePage_7;
	
	public static String ListSelector_0;
	public static String ListSelector_1;
	public static String ListSelector_4;
	public static String ListSelector_5;
	
	public static String FileSelector_1;

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
