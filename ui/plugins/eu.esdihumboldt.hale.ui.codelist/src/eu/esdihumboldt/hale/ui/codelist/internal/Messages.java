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
