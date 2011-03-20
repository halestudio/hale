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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.opengis.filter.identity.FeatureId;
import org.opengis.metadata.lineage.Lineage;
import org.opengis.metadata.lineage.ProcessStep;

import eu.esdihumboldt.cst.transformer.service.rename.FeatureBuilder;
import eu.esdihumboldt.hale.Messages;
import eu.esdihumboldt.hale.rcp.utils.tree.DefaultTreeNode;
import eu.esdihumboldt.hale.rcp.utils.tree.MultiColumnTreeNodeLabelProvider;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Tree viewer for features of a common feature type, based on the corresponding
 * {@link SchemaElement}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class DefinitionFeatureTreeViewer {
	
	private static Logger _log = Logger.getLogger(DefinitionFeatureTreeViewer.class);
	
	private final TreeViewer treeViewer;
	
	/**
	 * Create a feature tree viewer
	 * 
	 * @param parent the parent composite of the tree widget
	 */
	public DefinitionFeatureTreeViewer(final Composite parent) {
		super();
		
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		
		treeViewer.setContentProvider(new TreeNodeContentProvider());
		
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		
		treeViewer.getTree().setToolTipText(Messages.DefinitionFeatureTreeViewer_0); //$NON-NLS-1$
		
		setInput(null, null);
	}
	
	/**
	 * Set the tree view input
	 * 
	 * @param type the feature type
	 * @param features the features to display
	 */
	public void setInput(SchemaElement type, Iterable<Feature> features) {
		// remove old columns
		TreeColumn[] columns = treeViewer.getTree().getColumns();
		if (columns != null) {
			for (TreeColumn column : columns) {
				column.dispose();
			}
		}
		
		// create row definitions from type definition
		if (type != null) {
			DefaultTreeNode root = new DefaultTreeNode();
			DefaultTreeNode attributes = new DefaultTreeNode(Messages.DefinitionFeatureTreeViewer_1); //$NON-NLS-1$
			root.addChild(attributes);
			addProperties(attributes, type.getType(), new HashSet<TypeDefinition>());
			
			DefaultTreeNode metadata = new DefaultTreeNode(Messages.DefinitionFeatureTreeViewer_2); //$NON-NLS-1$
			root.addChild(metadata);
			
			// create row defs for metadata
			if (features != null) {
				boolean displayLineage = false;
				int lineageLength = 0;
				int featuresSize = 0;
				for (Feature f : features) {
					featuresSize++;
					Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
					if (l != null && l.getProcessSteps().size() > 0) {
						displayLineage = true;
						if (lineageLength < l.getProcessSteps().size()) {
							lineageLength = l.getProcessSteps().size();
						}
					}
				}
				
				if (displayLineage) {
					Object[][] processStepsText = new Object[lineageLength][featuresSize + 1];
					int featureIndex = 0;
					for (Feature f : features) {
						Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
						if (l != null && l.getProcessSteps().size() > 0) {
							int psIndex = 0;
							for (ProcessStep ps : l.getProcessSteps()) {
								processStepsText[psIndex][featureIndex + 1] = ps.getDescription().toString();
								psIndex++;
							}
						}
						featureIndex++;
					}
					
					DefaultTreeNode lineage = new DefaultTreeNode(Messages.DefinitionFeatureTreeViewer_5); //$NON-NLS-1$
					metadata.addChild(lineage);
					for (int i = 0; i < lineageLength; i++) {
						processStepsText[i][0] = Messages.DefinitionFeatureTreeViewer_6 + (i + 1); //$NON-NLS-1$
						DefaultTreeNode processStep = new DefaultTreeNode(processStepsText[i]);
						lineage.addChild(processStep);
					}
				}
			}
			
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
		if (type != null) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
			column.getColumn().setText(type.getElementName().getLocalPart());
			column.setLabelProvider(new TreeColumnViewerLabelProvider(
					new MultiColumnTreeNodeLabelProvider(0)));
			if (layout instanceof TreeColumnLayout) {
				((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
			}
		}
		
		// add columns for features
		int index = 1;
		if (features != null) {
			// sort features
			List<Feature> sortedFeatures = new ArrayList<Feature>();
			for (Feature f : features) {
				sortedFeatures.add(f);
			}
			Collections.sort(sortedFeatures, new Comparator<Feature>() {

				@Override
				public int compare(Feature o1, Feature o2) {
					FeatureId id1 = FeatureBuilder.getSourceID(o1);
					if (id1 == null) {
						id1 = o1.getIdentifier();
					}
					
					FeatureId id2 = FeatureBuilder.getSourceID(o2);
					if (id2 == null) {
						id2 = o2.getIdentifier();
					}
					
					return id1.getID().compareTo(id2.getID());
				}
				
			});
			
			for (Feature feature : sortedFeatures) {
				TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
				FeatureId id = FeatureBuilder.getSourceID(feature);
				if (id == null) {
					id = feature.getIdentifier();
				}
				column.getColumn().setText(id.toString());
				column.setLabelProvider(new TreeColumnViewerLabelProvider(
						new PropertyItemLabelProvider(feature, index)));
				if (layout instanceof TreeColumnLayout) {
					((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
				}
				
				// add tool tip
				new ColumnBrowserTip(treeViewer, 400, 300, true, index, null);
				
				index++;
			}
		}
		
		treeViewer.refresh();
		treeViewer.getTree().getParent().layout(true, true);
		
		// auto-expand attributes/metadata
		treeViewer.expandToLevel(2);
	}

	/**
	 * Add child nodes for the properties of the given feature type
	 * 
	 * @param parent the parent node
	 * @param type the feature type
	 * @param resolving the currently resolving types (to prevent loops)
	 */
	private void addProperties(DefaultTreeNode parent,
			TypeDefinition type, Set<TypeDefinition> resolving) {
		if (resolving.contains(type)) {
			_log.debug("Cycle in properties, skipping adding property items"); //$NON-NLS-1$
			return;
		}
		else {
			resolving.add(type);
		}
		
		SortedMap<String, AttributeDefinition> sortedProperties = new TreeMap<String, AttributeDefinition>();
		
		for (AttributeDefinition attribute : type.getAttributes()) {
			sortedProperties.put(attribute.getName(), attribute);
		}
		
		for (Entry<String, AttributeDefinition> entry : sortedProperties.entrySet()) {
			String name = entry.getKey();
			if (entry.getValue().getAttributeType() != null) {
				String typeName = entry.getValue().getAttributeType().getName().getLocalPart();
				
				DefaultTreeNode childNode = new PropertyItem(name, name + ":<" + //$NON-NLS-1$
						typeName + ">", entry.getValue().isAttribute()); //$NON-NLS-1$
				
				TypeDefinition childType = entry.getValue().getAttributeType();
				addProperties(childNode, childType, new HashSet<TypeDefinition>(resolving));
				
				parent.addChild(childNode);
			}
			else {
				_log.warn("An attribute without an AttributeType was encountered: " + name); //$NON-NLS-1$
			}
		}
	}

}
