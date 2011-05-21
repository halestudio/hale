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

	public static String ModelNavigationView_Target;
	public static String ModelNavigationView_Source;
	public static String ModelNavigationView_ActionText;
	public static String ModelNavigationView_FunctionButtonToolTipText;
	public static String ModelNavigationView_GeometryHide;
	public static String ModelNavigationView_GeometryShow;
	public static String ModelNavigationView_NumericHide;
	public static String ModelNavigationView_NumericShow;
	public static String ModelNavigationView_PropertyHide;
	public static String ModelNavigationView_PropertyShow;
	public static String ModelNavigationView_StringHide;
	public static String ModelNavigationView_StringShow;
	public static String PropertiesAction_PropertiesText;
	public static String PropertiesDialog_col12Text;
	public static String PropertiesDialog_col1Text;
	public static String PropertiesDialog_ShellTitle;
	public static String PropertiesDialog_Title;
	public static String PropertiesDialog_TreeNodeIndentifier;
	public static String PropertiesDialog_TreeNodeTitleAttributes;
	public static String PropertiesDialog_TreeNodeTitleBinding;
	public static String PropertiesDialog_TreeNodeTitleCardinality;
	public static String PropertiesDialog_TreeNodeTitleEnumeration;
	public static String PropertiesDialog_TreeNodeTitleLocalpart;
	public static String PropertiesDialog_TreeNodeTitleName;
	public static String PropertiesDialog_TreeNodeTitleNamespace;
	public static String PropertiesDialog_TreeNodeTitleNillable;
	public static String PropertiesDialog_TreeNodeTitleType;
	public static String UseAggregationHierarchyAction_PropertyAggregationToolTipText;
	public static String UseFlatHierarchyAction_OrganizeTooltipText;
	public static String UseInheritanceHierarchyAction_ShowInheritedTooltipText;

	public static String SetAsDefaultGeometryAction_0;
	public static String SetAsDefaultGeometryAction_1;
	public static String SetAsDefaultGeometryAction_2;
	public static String SetAsDefaultGeometryAction_3;
	public static String SetAsDefaultGeometryAction_5;
	public static String SetAsDefaultGeometryAction_8;
	public static String SetAsDefaultGeometryAction_9;
	public static String SetAsDefaultGeometryAction_12;
	public static String SetAsDefaultGeometryAction_4;
	public static String SetAsDefaultGeometryAction_6;
	public static String SetAsDefaultGeometryAction_7;

	public static String ModelNavigationView_2;
	public static String ModelNavigationView_3;

	public static String SchemaItemContribution_0;

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
