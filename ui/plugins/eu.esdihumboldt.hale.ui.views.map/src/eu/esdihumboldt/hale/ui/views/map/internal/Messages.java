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

package eu.esdihumboldt.hale.ui.views.map.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.views.map.internal.messages"; //$NON-NLS-1$

	public static String FeatureTilePainter_0;
	public static String FeatureTilePainter_1;
	public static String FeatureTilePainter_2;
	public static String AbstractTilePainter_ZoomIn;
	public static String AbstractTilePainter_ZoomOut;
	public static String MapView_ChangeBackgroundText;
	public static String SplitStyle_ComboBoxText1;
	public static String SplitStyle_ComboBoxText2;
	public static String SplitStyle_ComboBoxText3;
	public static String SplitStyle_ComboBoxText4;
	public static String SplitStyle_ComboBoxText5;
	public static String SplitStyle_ComboBoxText6;
	public static String SplitStyle_ComboBoxText7;
	public static String TileCache_JobLoadTitle;
	public static String MapView_0;

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
