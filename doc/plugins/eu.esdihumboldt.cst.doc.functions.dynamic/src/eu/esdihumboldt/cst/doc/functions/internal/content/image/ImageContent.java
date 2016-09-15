/*
 * Copyright (c) 2016 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.doc.functions.internal.content.image;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.common.graph.content.FunctionGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.FunctionGraphLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.layout.FunctionTreeLayoutAlgorithm;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;

/**
 * Function image content provider for the function help.
 * 
 * @author Simon Templer
 */
public class ImageContent {

	private static final ALogger log = ALoggerFactory.getLogger(ImageContent.class);

	/**
	 * Get the function image for the function with the given identifier.
	 * 
	 * @param func_id the function identifier
	 * @param tempDir the temporary directory where the function image may be
	 *            stored
	 * @return the function image input stream or <code>null</code>
	 * @throws Exception if an error occurs generating the image
	 */
	public static InputStream getImageContent(String func_id, File tempDir) throws Exception {

		final FunctionDefinition<?> function = FunctionUtil.getFunction(func_id, null);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		final File _functionFile = new File(tempDir, func_id + ".png");
		if (!_functionFile.exists()) {
			Display display;
			if (Display.getCurrent() != null) {
				// use the current display if available
				display = Display.getCurrent();
			}
			else {
				try {
					// use workbench display if available
					display = PlatformUI.getWorkbench().getDisplay();
				} catch (Throwable e) {
					// use a dedicated display thread if no workbench is
					// available
					display = DisplayThread.getInstance().getDisplay();
				}
			}
			display.syncExec(new Runnable() {

				@Override
				public void run() {

					// create an initial off-screen graph with fixed values;
					// resize the graph after computing the figures width and
					// height
					OffscreenGraph off_graph = new OffscreenGraph(300, 200) {

						@Override
						protected void configureViewer(GraphViewer viewer) {
							LayoutAlgorithm algo = new FunctionTreeLayoutAlgorithm();

							FunctionGraphContentProvider stcp = new FunctionGraphContentProvider();
							// XXX no service provider given
							FunctionGraphLabelProvider fglp = new FunctionGraphLabelProvider(null,
									false);
							viewer.setContentProvider(stcp);
							viewer.setLabelProvider(fglp);
							viewer.setInput(function);
							viewer.setLayoutAlgorithm(algo);
						}

					};

					Graph graph = off_graph.getGraph();
					Dimension dim = computeSize(graph);
					int width;
					if (dim.width > 450) {
						width = dim.width;
					}
					else {
						// minimum width = 450
						width = 450;
					}
					int height = dim.height;
					off_graph.resize(width, height);

					try {
						off_graph.saveImage(
								new BufferedOutputStream(new FileOutputStream(_functionFile)),
								null);
					} catch (IOException e) {
						log.warn("Conversion from Graph to Image failed!");
					} finally {
						off_graph.dispose();
					}
				}
			});
		}

		if (_functionFile.exists()) {
			return new FileInputStream(_functionFile);
		}

		return null;
	}

	private static Dimension computeSize(Graph graph) {
		@SuppressWarnings("unchecked")
		List<GraphNode> list = graph.getNodes();
		int height = 0;
		int width = 0;
		List<GraphNode> tempSourceList = new ArrayList<GraphNode>();
		List<GraphNode> tempTargetList = new ArrayList<GraphNode>();
		for (GraphNode gn : list) {
			int sourceCons = gn.getSourceConnections().size();
			int targetCons = gn.getTargetConnections().size();
			if (sourceCons == 0 && targetCons == 1) {
				tempSourceList.add(gn);
			}
			else if (sourceCons >= 1 && targetCons >= 1) {
				width = width + gn.getFigure().getBounds().width + 10;
				height = height + gn.getFigure().getBounds().height;
			}
			else {
				tempTargetList.add(gn);
			}
		}
		int accuSourceWidth = 0;
		int accuSourceHeight = 0;
		int accuHeight = 0;
		for (GraphNode node : tempSourceList) {
			Rectangle rec = node.getFigure().getBounds();
			int sourceWidth = rec.width;
			int sourceHeight = rec.height;

			accuSourceHeight = accuSourceHeight + sourceHeight;

			if (accuSourceWidth < sourceWidth) {
				accuSourceWidth = sourceWidth;
			}
			if (accuHeight < accuSourceHeight) {
				accuHeight = accuSourceHeight;
			}

		}

		int accuTargetWidth = 0;
		int accuTargetHeight = 0;
		for (GraphNode node : tempTargetList) {
			Rectangle rec = node.getFigure().getBounds();
			int targetWidth = rec.width;
			int targetHeight = rec.height;

			accuTargetHeight = accuTargetHeight + targetHeight;

			if (accuTargetWidth < targetWidth) {
				accuTargetWidth = targetWidth;
			}
			if (accuHeight < accuTargetHeight) {
				accuHeight = accuTargetHeight;
			}
		}
		width = width + accuSourceWidth + accuTargetWidth + 30;

		if (height < accuHeight) {
			height = accuHeight + 35;
		}
		height += 20;

		Dimension d = new Dimension();
		d.setSize(width, height);

		return d;
	}

}
