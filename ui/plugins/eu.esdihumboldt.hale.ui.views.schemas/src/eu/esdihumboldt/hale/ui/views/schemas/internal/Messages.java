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
