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

package eu.esdihumboldt.hale.ui.common.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {

	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.common.internal.messages"; //$NON-NLS-1$

	public static String URIAttributeEditor_0;

	public static String CodeListSelectionDialog_0;
	public static String CodeListSelectionDialog_1;
	public static String CodeListSelectionDialog_2;
	public static String CodeListSelectionDialog_3;
	public static String CodeListSelectionDialog_4;
	public static String CodeListSelectionDialog_5;
	public static String CodeListSelectionDialog_6;

	public static String DoubleAttributeEditor_0;
	public static String DoubleAttributeEditor_1;

	public static String CodeListPreferencePage_0;
	public static String CodeListPreferencePage_1;
	public static String CodeListPreferencePage_2;
	public static String CodeListPreferencePage_3;
	public static String CodeListPreferencePage_4;

	public static String CodeListPreferencePage_5;
	public static String CodeListPreferencePage_6;
	public static String CodeListPreferencePage_7;

	public static String StringValidatingAttributeEditor_1;

	public static String LongAttributeEditor_0;
	public static String LongAttributeEditor_1;

	public static String IntegerAttributeEditor_0;
	public static String IntegerAttributeEditor_1;

	public static String FloatAttributeEditor_0;
	public static String FloatAttributeEditor_1;

	public static String CodeListAttributeEditor_0;
	public static String CodeListAttributeEditor_2;

	public static String FeatureFilterField_0;
	public static String FeatureFilterField_3;
	public static String FeatureFilterField_6;
	public static String FeatureFilterField_7;
	public static String FeatureFilterField_8;
	public static String FeatureFilterField_9;

	public static String FeatureFilterFormDialog_0;
	public static String FeatureFilterFormDialog_1;

	public static String CRSPreferencePage_0;
	public static String CRSPreferencePage_2;
	public static String CRSPreferencePage_3;
	public static String CRSPreferencePage_4;
	public static String CRSPreferencePage_5;
	public static String CRSPreferencePage_6;
	public static String CRSPreferencePage_7;
	public static String CRSPreferencePage_8;
	public static String CRSPreferencePage_11;
	public static String CRSPreferencePage_12;

	public static String ListFunctionsDialog_1;
	public static String ListFunctionsDialog_2;
	public static String ListFunctionsDialog_4;
	public static String ListFunctionsDialog_5;

	public static String FunctionContentProvider_description;

	public static String FunctionContentProvider_others;

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
