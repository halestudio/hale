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

package eu.esdihumboldt.hale.rcp.views.table.tree;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TreeColumn;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;

/**
 * Tree viewer for features of a common feature type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class FeatureTreeViewer {
	
	/**
	 * Label provider for a feature column
	 */
	public static class PropertyItemLabelProvider extends LabelProvider {
		
		/**
		 * The feature representing the column
		 */
		private final Feature feature;

		/**
		 * Creates a new feature column label provider
		 * 
		 * @param feature the feature representing the column
		 */
		public PropertyItemLabelProvider(Feature feature) {
			super();
			
			this.feature = feature;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof PropertyItem) {
				return ((PropertyItem) element).getText(feature);
			}
			else {
				return super.getText(element);
			}
		}

	}

	private final TreeViewer treeViewer;
	
	/**
	 * Create a feature tree viewer
	 * 
	 * @param parent the parent composite of the tree widget
	 */
	public FeatureTreeViewer(final Composite parent) {
		super();
		
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		
		setInput(null, null);
	}
	
	/**
	 * Set the tree view input
	 * 
	 * @param featureType the feature type
	 * @param features the features to display
	 */
	public void setInput(FeatureType featureType, Iterable<Feature> features) {
		// remove old columns
		TreeColumn[] columns = treeViewer.getTree().getColumns();
		if (columns != null) {
			for (TreeColumn column : columns) {
				column.dispose();
			}
		}
		
		// create row definitions from feature type
		if (featureType != null) {
			MultiColumnTreeNode root = new MultiColumnTreeNode();
			addProperties(root, featureType);
			// remove parent
			for (TreeNode child : root.getChildren()) {
				child.setParent(null);
			}
			// set children as input
			treeViewer.setInput(root.getChildren());
		}
		else {
			treeViewer.setInput(null);
		}
		
		Layout layout = treeViewer.getTree().getParent().getLayout();
		
		// add type column
		if (featureType != null) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
			column.getColumn().setText(featureType.getName().getLocalPart());
			column.setLabelProvider(new TreeColumnViewerLabelProvider(
					new MultiColumnTreeNodeLabelProvider(0)));
			if (layout instanceof TreeColumnLayout) {
				((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
			}
		}
		
		// add columns for features
		if (features != null) {
			for (Feature feature : features) {
				TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
				column.getColumn().setText(feature.getIdentifier().toString());
				column.setLabelProvider(new TreeColumnViewerLabelProvider(
						new PropertyItemLabelProvider(feature))); //XXX
				if (layout instanceof TreeColumnLayout) {
					((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
				}
			}
		}
		
		treeViewer.refresh();
		treeViewer.getTree().getParent().layout(true, true);
	}

	/**
	 * Add child nodes for the properties of the given feature type
	 * 
	 * @param parent the parent node
	 * @param type the feature type
	 */
	private void addProperties(MultiColumnTreeNode parent,
			FeatureType type) {
		SortedMap<String, PropertyDescriptor> sortedProperties = new TreeMap<String, PropertyDescriptor>();
		
		for (PropertyDescriptor pd : type.getDescriptors()) {
			sortedProperties.put(pd.getName().getLocalPart(), pd);
		}
		
		for (Entry<String, PropertyDescriptor> entry : sortedProperties.entrySet()) {
			String name = entry.getKey();
			String typeName = entry.getValue().getType().getName().getLocalPart();
			
			MultiColumnTreeNode childNode = new PropertyItem(name, name + ":<" +
					typeName + ">");
			
			PropertyType childType = entry.getValue().getType();
			if (childType instanceof FeatureType) {
				addProperties(childNode, (FeatureType) childType);
			}
			
			parent.addChild(childNode);
		}
	}

}
