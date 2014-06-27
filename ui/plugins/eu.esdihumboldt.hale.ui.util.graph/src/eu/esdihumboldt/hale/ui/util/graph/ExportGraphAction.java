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

package eu.esdihumboldt.hale.ui.util.graph;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.ui.util.graph.internal.GraphUIUtilBundle;

/**
 * Renders a graph and saves it to a file.
 * 
 * @author Simon Templer
 */
public class ExportGraphAction extends Action {

	private static final ALogger log = ALoggerFactory.getLogger(ExportGraphAction.class);

	private final GraphViewer viewer;

	/**
	 * Create an action that renders and saves a graph to a file.
	 * 
	 * @param viewer the graph viewer
	 */
	public ExportGraphAction(GraphViewer viewer) {
		this.viewer = viewer;

		setText("Export to file");
		setToolTipText("Export the graph to an image or dot file");
		setImageDescriptor(GraphUIUtilBundle.getImageDescriptor("icons/export.gif"));
	}

	/**
	 * @see Action#run()
	 */
	@Override
	public void run() {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		// XXX if called from TTreeExporter during transformation, the active
		// shell may be null!

		dialog.setOverwrite(true);
		dialog.setText("Export graph to file");

		String[] imageExtensions = ImageIO.getWriterFileSuffixes();

		StringBuffer extensions = new StringBuffer("*.svg;*.gv;*.dot");
		for (String imageExt : imageExtensions) {
			extensions.append(";*.");
			extensions.append(imageExt);
		}

		dialog.setFilterExtensions(new String[] { extensions.toString() });

		dialog.setFilterNames(new String[] { "Image, SVG or dot file (" + extensions + ")" });

		String fileName = dialog.open();
		if (fileName != null) {
			final File file = new File(fileName);

//			//XXX use an off-screen graph (testing)
//			OffscreenGraph graph = new OffscreenGraph(1000, 1000) {
//				
//				@Override
//				protected void configureViewer(GraphViewer viewer) {
//					viewer.setContentProvider(RenderAction.this.viewer.getContentProvider());
//					viewer.setLabelProvider(RenderAction.this.viewer.getLabelProvider());
//					viewer.setInput(RenderAction.this.viewer.getInput());
//					viewer.setLayoutAlgorithm(new TreeLayoutAlgorithm(TreeLayoutAlgorithm.LEFT_RIGHT), false);
//				}
//			};

			// get the graph
			final Graph graph = viewer.getGraphControl();

			final String ext = FilenameUtils.getExtension(file.getAbsolutePath());
			final IFigure root = graph.getRootLayer();

			ProgressMonitorDialog progress = new ProgressMonitorDialog(Display.getCurrent()
					.getActiveShell());
			try {
				progress.run(false, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException {
						try {
							OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

							if (ext.equalsIgnoreCase("gv") || ext.equalsIgnoreCase("dot")) {
								OffscreenGraph.saveDot(graph, out);
							}
//							else if (ext.equalsIgnoreCase("svg")) {
//								OffscreenGraph.saveSVG(root, out);
//							}
							else {
								OffscreenGraph.saveImage(root, out, ext);
							}
						} catch (Throwable e) {
							log.userError("Error saving graph to file", e);
						}
					}

				});
			} catch (Throwable e) {
				log.error("Error launching graph export", e);
			}
		}
	}

}
