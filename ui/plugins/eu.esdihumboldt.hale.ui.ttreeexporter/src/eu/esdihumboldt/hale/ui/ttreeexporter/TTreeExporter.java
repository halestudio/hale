/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.ui.ttreeexporter;

import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.ui.common.graph.content.TransformationTreeContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.TransformationTreeLabelProvider;
import eu.esdihumboldt.hale.ui.util.graph.ExportGraphAction;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;

/**
 * Exports a transformation tree to a file, requiring user interaction.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class TTreeExporter implements TransformationTreeHook {

	/**
	 * @see TransformationTreeHook#processTransformationTree(TransformationTree,
	 *      TreeState, MutableInstance)
	 */
	@Override
	public void processTransformationTree(final TransformationTree tree, TreeState state,
			MutableInstance target) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				final TransformationTreeContentProvider contentProvider = new TransformationTreeContentProvider();
				final TransformationTreeLabelProvider labelProvider = new TransformationTreeLabelProvider();
				final TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm(
						TreeLayoutAlgorithm.RIGHT_LEFT);
				OffscreenGraph offscreenGraph = new OffscreenGraph(1024, 768) {

					@Override
					protected void configureViewer(GraphViewer viewer) {
						viewer.setContentProvider(contentProvider);
						viewer.setLabelProvider(labelProvider);
						viewer.setLayoutAlgorithm(layoutAlgorithm);
						viewer.setInput(tree);
					}
				};
				try {
					GraphViewer viewer = new GraphViewer(offscreenGraph.getGraph());
					// XXX if called during transformation, the active shell may
					// be null and run will fail!
					new ExportGraphAction(viewer).run();
				} finally {
					offscreenGraph.dispose();
				}
			}
		});
	}
}
