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

package eu.esdihumboldt.hale.ui.views.data.internal.compare;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.TreeColumn;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionComparator;
import eu.esdihumboldt.hale.ui.common.definition.viewer.DefinitionLabelProvider;
import eu.esdihumboldt.hale.ui.common.definition.viewer.TypeDefinitionContentProvider;
import eu.esdihumboldt.hale.ui.views.data.InstanceViewer;
import eu.esdihumboldt.hale.ui.views.data.internal.SimpleInstanceSelectionProvider;

/**
 * Tree viewer for {@link Instance}s of a common type, based on the corresponding
 * {@link TypeDefinition}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class DefinitionInstanceTreeViewer implements InstanceViewer {
	
//	private static ALogger _log = ALoggerFactory.getLogger(DefinitionInstanceTreeViewer.class);
	
	private TreeViewer treeViewer;
	
	private Composite main;
	
	private final SimpleInstanceSelectionProvider selectionProvider = new SimpleInstanceSelectionProvider();
	
	/**
	 * @see InstanceViewer#createControls(Composite)
	 */
	@Override
	public void createControls(Composite parent) {
		main = new Composite(parent, SWT.NONE);
		main.setLayout(new TreeColumnLayout());
		
		treeViewer = new TreeViewer(main, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		
		treeViewer.setContentProvider(new TypeDefinitionContentProvider(treeViewer));
		treeViewer.setLabelProvider(new DefinitionLabelProvider());
		
		treeViewer.setComparator(new DefinitionComparator());
		
		treeViewer.getTree().setHeaderVisible(true);
		treeViewer.getTree().setLinesVisible(true);
		
		treeViewer.getTree().setToolTipText(""); //$NON-NLS-1$
		
		setInput(null, null);
	}
	
	/**
	 * @see InstanceViewer#setInput(TypeDefinition, Iterable)
	 */
	@Override
	public void setInput(TypeDefinition type, Iterable<Instance> instances) {
		// remove old columns
		TreeColumn[] columns = treeViewer.getTree().getColumns();
		if (columns != null) {
			for (TreeColumn column : columns) {
				column.dispose();
			}
		}
		
			// create row defs for metadata
//			if (features != null) {
//				boolean displayLineage = false;
//				int lineageLength = 0;
//				int featuresSize = 0;
//				for (Feature f : features) {
//					featuresSize++;
//					Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
//					if (l != null && l.getProcessSteps().size() > 0) {
//						displayLineage = true;
//						if (lineageLength < l.getProcessSteps().size()) {
//							lineageLength = l.getProcessSteps().size();
//						}
//					}
//				}
//				
//				if (displayLineage) {
//					Object[][] processStepsText = new Object[lineageLength][featuresSize + 1];
//					int featureIndex = 0;
//					for (Feature f : features) {
//						Lineage l = (Lineage) f.getUserData().get("METADATA_LINEAGE"); //$NON-NLS-1$
//						if (l != null && l.getProcessSteps().size() > 0) {
//							int psIndex = 0;
//							for (ProcessStep ps : l.getProcessSteps()) {
//								processStepsText[psIndex][featureIndex + 1] = ps.getDescription().toString();
//								psIndex++;
//							}
//						}
//						featureIndex++;
//					}
//					
//					DefaultTreeNode lineage = new DefaultTreeNode(Messages.DefinitionFeatureTreeViewer_5); //$NON-NLS-1$
//					metadata.addChild(lineage);
//					for (int i = 0; i < lineageLength; i++) {
//						processStepsText[i][0] = Messages.DefinitionFeatureTreeViewer_6 + (i + 1); //$NON-NLS-1$
//						DefaultTreeNode processStep = new DefaultTreeNode(processStepsText[i]);
//						lineage.addChild(processStep);
//					}
//				}
			
		// set input
		treeViewer.setInput(type);
		
		Layout layout = treeViewer.getTree().getParent().getLayout();
		
		// add type column
		if (type != null) {
			TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
			column.getColumn().setText(type.getDisplayName());
			column.setLabelProvider(new TreeColumnViewerLabelProvider(
					new DefinitionLabelProvider()));
			if (layout instanceof TreeColumnLayout) {
				((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
			}
		}
		
		// add columns for features
		int index = 1;
		if (instances != null) {
//			// sort features
//			List<Feature> sortedFeatures = new ArrayList<Feature>();
//			for (Feature f : features) {
//				sortedFeatures.add(f);
//			}
//			Collections.sort(sortedFeatures, new Comparator<Feature>() {
//
//				@Override
//				public int compare(Feature o1, Feature o2) {
//					FeatureId id1 = FeatureBuilder.getSourceID(o1);
//					if (id1 == null) {
//						id1 = o1.getIdentifier();
//					}
//					
//					FeatureId id2 = FeatureBuilder.getSourceID(o2);
//					if (id2 == null) {
//						id2 = o2.getIdentifier();
//					}
//					
//					return id1.getID().compareTo(id2.getID());
//				}
//				
//			});
			
			for (Instance instance : instances) { //sortedFeatures) {
				TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.LEFT);
//				FeatureId id = FeatureBuilder.getSourceID(feature);
//				if (id == null) {
//					id = feature.getIdentifier();
//				}
//				column.getColumn().setText(id.toString());
				column.getColumn().setText(String.valueOf(index)); //XXX identifier?
				column.setLabelProvider(new DefinitionInstanceLabelProvider(instance));
				if (layout instanceof TreeColumnLayout) {
					((TreeColumnLayout) layout).setColumnData(column.getColumn(), new ColumnWeightData(1));
				}
				
				// add tool tip
//				new ColumnBrowserTip(treeViewer, 400, 300, true, index, null);
				
				index++;
			}
		}
		
		treeViewer.refresh();
		treeViewer.getTree().getParent().layout(true, true);
		
		selectionProvider.updateSelection(instances);
		
		// auto-expand attributes/metadata
//		treeViewer.expandToLevel(2);
	}

	/**
	 * @see InstanceViewer#getViewer()
	 */
	@Override
	public TreeViewer getViewer() {
		return treeViewer;
	}

	/**
	 * @see InstanceViewer#getControl()
	 */
	@Override
	public Control getControl() {
		return main;
	}

	/**
	 * @see InstanceViewer#getInstanceSelectionProvider()
	 */
	@Override
	public ISelectionProvider getInstanceSelectionProvider() {
		return selectionProvider;
	}

}
