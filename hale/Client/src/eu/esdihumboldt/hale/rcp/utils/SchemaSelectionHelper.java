/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : HALE2
 * 	 
 * Classname    : eu.esdihumboldt.hale.rcp.utils/ModelNavigationViewHelper.java 
 * 
 * Author       : schneidersb
 * 
 * Created on   : Aug 31, 2009 -- 10:50:42 AM
 *
 */
package eu.esdihumboldt.hale.rcp.utils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.SchemaSelection;

/**
 * Helper class to get the selected feature types from the ModelNavigatioView.
 */
public abstract class SchemaSelectionHelper {
	
	/**
	 * Get the current schema selection
	 * 
	 * @return the current schema selection or an empty model selection if it
	 *   can't be determined
	 */
	public static SchemaSelection getSchemaSelection() {
		ISelectionService ss = (ISelectionService) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		
		ISelection selection = ss.getSelection(ModelNavigationView.ID);
		
		if (selection instanceof SchemaSelection) {
			return (SchemaSelection) selection;
		}
		else {
			return new SchemaSelection();
		}
	}
	
}
