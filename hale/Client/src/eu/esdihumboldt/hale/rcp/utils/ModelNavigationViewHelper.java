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

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.goml.align.Entity;
import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject;

/**
 * Helper class to get the selected feature types from the ModelNavigatioView.
 */
public abstract class ModelNavigationViewHelper {
	
	private static ModelNavigationView getModelNavigationView() {
		ModelNavigationView attributeView = null;
		// get All Views
		IViewReference[] views = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		// get AttributeView
		// get AttributeView
		for (int count = 0; count < views.length; count++) {
			if (views[count].getId().equals(
					"eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView")) {
				attributeView = (ModelNavigationView) views[count]
						.getView(false);
			}
		}
		return attributeView;
	}
		
	/**
	 * Get the entity of the selected item in the source or target schema
	 * 
	 * @param selectionType specifies if the source or target
	 *   selection shall be used
	 * @return the item's entity
	 */
	public static Entity getEntity(SelectionType selectionType) {
		return getTreeObject(selectionType).getEntity();
	}
	
	/**
	 * Get the name of the selected item in the source or target schema
	 * 
	 * @param selectionType specifies if the source or target
	 *   selection shall be used
	 * @return the item's name
	 */
	public static Name getFeatureTypeName(SelectionType selectionType) {
		return getTreeObject(selectionType).getName();
	}
	
	/**
	 * Get the selected item in the source or target schema
	 * 
	 * @param selectionType specifies if the source or target
	 *   selection shall be used
	 * @return the item
	 */
	public static TreeObject getTreeObject(SelectionType selectionType) {
		TreeObject result = null;
		ModelNavigationView view = getModelNavigationView();
		TreeViewer viewer = null;
		if (selectionType == SelectionType.SOURCE) viewer = view.getSourceSchemaViewer();
		else viewer = view.getTargetSchemaViewer();	
		ITreeSelection selection = (ITreeSelection)viewer.getSelection();
		Object item = selection.getFirstElement();
		if (item instanceof TreeObject) {
			result = (TreeObject)item;
		}
		return result;
	}
	
	/**
	 * Specifies if the source or target schema selection
	 */
	public enum SelectionType {
		/** the source schema selection */
		SOURCE,
		/** the target schema selection */
		TARGET
	}
}
