/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.ttreeexporter;

import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.cst.extension.hooks.TransformationTreeHook;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.ui.HaleUI;
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
				final TransformationTreeLabelProvider labelProvider = new TransformationTreeLabelProvider(
						null, HaleUI.getServiceProvider());
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
					GraphViewer viewer = new GraphViewer(offscreenGraph.getGraph(), SWT.NONE);
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
