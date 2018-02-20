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

package eu.esdihumboldt.hale.ui.views.tasks.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Provides means to access externalized strings.
 */
@SuppressWarnings("all")
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eu.esdihumboldt.hale.ui.views.tasks.internal.messages"; //$NON-NLS-1$

	public static String TaskTreeView_CommentText;
	public static String TaskTreeView_description_tooltip;
	public static String TaskTreeView_NumberText;
	public static String TaskTreeView_SourceNodeTitle;
	public static String TaskTreeView_StatusText;
	public static String TaskTreeView_TargetNodeTitle;
	public static String TaskTreeView_TitleDescriptionText;
	public static String TaskTreeView_value_tooltip;

	public static String TaskUserData_0;
	public static String TaskUserData_1;
	public static String TaskUserData_2;
	public static String TaskUserData_3;

	public static String NoGeometryTaskFactory_0;
	public static String NoGeometryTaskFactory_1;

	public static String MapNilAttributeTaskFactory_0;
	public static String MapNilAttributeTaskFactory_1;

	public static String MapAttributeTaskFactory_0;
	public static String MapAttributeTaskFactory_1;

	public static String MapElementTaskFactory_0;
	public static String MapElementTaskFactory_1;

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
