/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.wfs.ui.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.io.wfs.ui.internal.messages"; //$NON-NLS-1$

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
