/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.jface.action.Action;

/**
 * Enabling this action will switch the affected SchemaExplorer to display it's 
 * elements in a inheritance hierarchy. This represents the default style of 
 * ordering.
 * 
 * When both it and the aggregation hierarchy are inactive, a simple list will 
 * be shown.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class UseInheritanceHierarchyAction 
	extends Action {

	public UseInheritanceHierarchyAction() {
		super();
	}

}
