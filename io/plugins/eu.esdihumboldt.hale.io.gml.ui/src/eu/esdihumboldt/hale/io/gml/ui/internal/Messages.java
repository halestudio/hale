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

package eu.esdihumboldt.hale.io.gml.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.io.gml.ui.internal.messages"; //$NON-NLS-1$

	public static String FeatureTypeList_LabelFilter;

	public static String FeatureTypeList_LabelFeature;

	public static String FeatureTypeSelection_LabelNamespace;

	public static String FeatureTypeSelection_LabelFeature;

	public static String GetCapabilititiesRetriever_Retriever;

	public static String CapabilitiesPage_0;
	public static String CapabilitiesPage_1;
	public static String CapabilitiesPage_2;
	public static String CapabilitiesPage_4;

	public static String WfsGetFeatureWizard_0;

	public static String WfsDescribeFeatureWizard_0;

	public static String OGCFilterBuilder_0;
	public static String OGCFilterBuilder_1;
	public static String OGCFilterBuilder_2;
	public static String OGCFilterBuilder_3;
	public static String OGCFilterBuilder_4;
	public static String OGCFilterBuilder_5;
	public static String OGCFilterBuilder_6;
	public static String OGCFilterBuilder_7;
	public static String OGCFilterBuilder_8;
	public static String OGCFilterBuilder_9;
	public static String OGCFilterDialog_0;
	public static String OGCFilterDialog_1;
	public static String OGCFilterDialog_2;

	public static String FilterPage_0;
	public static String FilterPage_1;
	public static String FilterPage_2;
	public static String FilterPage_3;
	public static String FilterPage_4;
	public static String FilterPage_6;
	public static String FilterPage_7;
	public static String FilterPage_8;

	public static String FeatureTypesPage_0;
	public static String FeatureTypesPage_1;
	public static String FeatureTypesPage_2;
	public static String FeatureTypesPage_3;
	public static String FeatureTypesPage_4;

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
