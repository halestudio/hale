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

package eu.esdihumboldt.hale.ui.views.mapping.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.views.mapping.internal.messages"; //$NON-NLS-1$

	public static String CellDetails_AugmentationTitle;
	public static String CellDetails_Entity1Title;
	public static String CellDetails_Entity2Title;
	public static String CellDetails_FilterTitle;
	public static String CellDetails_NameText;
	public static String CellDetails_TransformationTitle;
	public static String CellDetails_ValueText;
	public static String CellSelector_ConfirmCellText;
	public static String CellSelector_ConfirmCellTitle;
	public static String CellSelector_DeleteButtonToolTipText;
	public static String CellSelector_EditButtonToolTipText;
	public static String CellSelector_NextButtonToolTipText;
	public static String CellSelector_PrevButtonToolTipText;
	public static String CellSelector_SynchButtonToolTipText;

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
