package eu.esdihumboldt.hale.ui.ttreeexporter;

import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.ui.common.graph.content.TransformationTreeContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.TransformationTreeLabelProvider;
import eu.esdihumboldt.hale.ui.util.graph.ExportGraphAction;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;

public class TTreeExporter {
	public static synchronized void exportTTree(final TransformationTree tree) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				final TransformationTreeContentProvider contentProvider = new TransformationTreeContentProvider();
				final TransformationTreeLabelProvider labelProvider = new TransformationTreeLabelProvider();
				final TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm(TreeLayoutAlgorithm.RIGHT_LEFT);
				OffscreenGraph offscreenGraph = new OffscreenGraph(1024, 768) {
					@Override
					protected void configureViewer(GraphViewer viewer) {
						viewer.setContentProvider(contentProvider);
						viewer.setLabelProvider(labelProvider);
						viewer.setLayoutAlgorithm(layoutAlgorithm);
						viewer.setInput(tree);
					}
				};
				GraphViewer viewer = new GraphViewer(offscreenGraph.getGraph());
				new ExportGraphAction(viewer).run();
				offscreenGraph.dispose();
			}
		});
	}
}
