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
