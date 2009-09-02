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
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.opengis.feature.type.FeatureType;

import com.sun.org.apache.xpath.internal.operations.Gte;

import eu.esdihumboldt.hale.rcp.views.model.ModelNavigationView;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject;
import eu.esdihumboldt.hale.rcp.views.model.TreeObject.TreeObjectType;

/**
 * Helper class to get the selected feature types from the ModelNavigatioView.
 */
public class ModelNavigationViewHelper {
	
	private static final String SOURCE_SELECTION_TYPE = "SourceSelectionType";
	private static final String TARGET_SELECTION_TYPE = "TargetSelectionType";

	private TreeViewer sourceViewer;
	private TreeViewer targetViewer;

	
	private String getSelectedFeatureType(String selectedFeatureType) {
		String typeName = "";
		ModelNavigationView modelNavigation = getModelNavigationView();
		if (modelNavigation != null) {

			if (selectedFeatureType.equals(SOURCE_SELECTION_TYPE)) {
				this.sourceViewer = modelNavigation.getSourceSchemaViewer();
				this.targetViewer = modelNavigation.getTargetSchemaViewer();
				TreeItem[] sourceTreeSelection = sourceViewer.getTree()
						.getSelection();
				TreeItem[] targetTreeSelection = targetViewer.getTree()
						.getSelection();

				if (sourceTreeSelection.length == 1) {
					// is a Feature Type
					typeName = sourceTreeSelection[0].getText();
				}
			} else if (selectedFeatureType.equals(TARGET_SELECTION_TYPE)) {
				TreeViewer targetViewer = modelNavigation
						.getTargetSchemaViewer();
				TreeItem[] targetTreeSelection = targetViewer.getTree()
						.getSelection();
				if (targetTreeSelection.length == 1)
					typeName = targetTreeSelection[0].getText();
			}
		}
		return typeName;
	}
	
	private ModelNavigationView getModelNavigationView() {
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
	 * Method to return the parent feature type of an attribute.
 	 * @param selectionType SOURCE of TARGET for source schema or target schema.
	 * @return FeatureType The parent feature type.
	 */
	public FeatureType getSelectedAttributeParent(SelectionType selectionType) {
		FeatureType featureType = null;
		
		// Get the selected item
		TreeObject treeObject = getTreeObject(selectionType);
		if (treeObject != null) {
			// The data object of an TreeObject which represents an attribute
			// is the parent feature type.
			featureType = (FeatureType)treeObject.getData();		
		}
		return featureType;
	}
	
	/**
	 * Method to get the selected feature type in the ModelNavigationView.
	 * If the selected item is not a feature type (or abstract feature type)
	 * this method will return false;
 	 * @param selectionType SOURCE of TARGET for source schema or target schema.
	 * @return FeatureType The selected feature type.
	 */
	public FeatureType getSelectedFeatureType(SelectionType selectionType) {
		FeatureType featureType = null;
		
		// Get the selected item
		TreeObject treeObject = getTreeObject(selectionType);
		if (treeObject != null) {
			// Check if the associated data object is a feature type
			if (		treeObject.getType() == TreeObjectType.ABSTRACT_FT 
					|| 	treeObject.getType() == TreeObjectType.CONCRETE_FT) {
				featureType = (FeatureType)treeObject.getData();		
			} else {
				// If the data object is not a feature type, return null
				return null;
			}
		}
		return featureType;
	}
	
	/**
	 * Method to get the selected attribute name from the ModelNavigationView.
	 * If the selected Item is not an attribute, this method will return null.
	 * 
	 * @param selectionType SOURCE of TARGET for source schema or target schema.
	 * 
	 * @return String Selected attribute name.
	 */
	public String getSelectedAttributeName(SelectionType selectionType) {
		String attributeName = null;
		
		// Get the selected item
		
		TreeObject treeObject = getTreeObject(selectionType);
		if (treeObject != null) {
			// Check if the treeObject is of an attribute type
			if (   treeObject.getType() == TreeObjectType.COMPLEX_ATTRIBUTE
				|| treeObject.getType() == TreeObjectType.GEOMETRIC_ATTRIBUTE
				|| treeObject.getType() == TreeObjectType.NUMERIC_ATTRIBUTE
				|| treeObject.getType() == TreeObjectType.STRING_ATTRIBUTE) {
				
					ModelNavigationView view = getModelNavigationView();
					TreeViewer viewer = null;
					if (selectionType == SelectionType.SOURCE) viewer = view.getSourceSchemaViewer();
					else viewer = view.getTargetSchemaViewer();	

					attributeName = viewer.getTree().getSelection()[0].getText();
				}
			else return null;
		}
		
		return attributeName;
	}
	
	public String getSourceFeatureName() {
		return getSelectedFeatureType(SOURCE_SELECTION_TYPE);
	}

	public String getTargetFeatureName() {
		return getSelectedFeatureType(TARGET_SELECTION_TYPE);
	}

	/**
	 * Method to return the selected type. For examlpe (ABSTRACT_FT/STRING_ATTRIBUTE).
	 * 
	 * @param selectionType SOURCE of TARGET for source schema or target schema.
	 * 
	 * @return TreeObjectType The selected type.
	 */
	public TreeObjectType getSelectedType(SelectionType selectionType) {
		return getTreeObject(selectionType).getType();
	}
	
	private TreeObject getTreeObject(SelectionType selectionType) {
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
	
	public enum SelectionType {
		SOURCE,
		TARGET
	}
}
