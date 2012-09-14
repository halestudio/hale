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
