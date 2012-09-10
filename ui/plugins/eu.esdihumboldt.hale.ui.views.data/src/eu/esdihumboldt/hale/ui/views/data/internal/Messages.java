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

package eu.esdihumboldt.hale.ui.views.data.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.views.data.internal.messages"; //$NON-NLS-1$

	public static String InstanceServiceFeatureSelector_defaultReturnText;
	public static String InstanceServiceFeatureSelector_SourceReturnText;
	public static String InstanceServiceFeatureSelector_TargetReturnText;
	public static String TransformedTableView_SynchToolTipText;

	public static String ReferenceTableView_0;
	public static String ReferenceTableView_1;

	public static String TransformedTableView_0;
	public static String TransformedTableView_1;

	public static String DefinitionFeatureTreeViewer_1;
	public static String DefinitionFeatureTreeViewer_2;
	public static String DefinitionFeatureTreeViewer_5;
	public static String DefinitionFeatureTreeViewer_6;

	public static String InstanceContentProvider_metadata;

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
