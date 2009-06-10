package eu.esdihumboldt.hale.rcp.views.model;

import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is a Container for the source/target
 * FeatureTypes selected in the ModelNavigationView.
 * It is used by SelectionService to fire selection event
 * 
 * @author Anna Pitaev, Logica
 * 
 *
 */
public class FeatureTypeSelection {
	
	/** container for the source selection */
	private TreeItem [] sourceFeatureTypes;
   
	public TreeItem[] getSourceFeatureTypes() {
		return sourceFeatureTypes;
	}

	public TreeItem[] getTargetFeatureType() {
		return targetFeatureType;
	}

	/** container for the target selection */
	
	private TreeItem [] targetFeatureType;
	
	/**
	 * constructor
	 */
	public FeatureTypeSelection(TreeItem [] sourceSelection, TreeItem [] targetSelection){
		this.sourceFeatureTypes = sourceSelection;
		this.targetFeatureType = targetSelection;
		
	}
	
}
