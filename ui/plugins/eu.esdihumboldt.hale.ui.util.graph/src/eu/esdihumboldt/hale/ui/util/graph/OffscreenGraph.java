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

package eu.esdihumboldt.hale.ui.util.graph;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.render.awt.internal.svg.export.GraphicsSVG;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.internal.dot.DotExport;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.ui.util.swing.SwingRcpUtilities;

/**
 * Renders a graph to an image.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class OffscreenGraph {

	private final Graph graph;
	private Shell shell;
	private Composite composite;

	/**
	 * @return the graph
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Create an off-screen graph.
	 * 
	 * @param width the graph width
	 * @param height the graph height
	 */
	public OffscreenGraph(int width, int height) {
		shell = new Shell();
		shell.setSize(width, height);
		shell.setLayout(new FillLayout());

		composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setVisible(true);

		// Workaround to draw in background -->
		graph = new Graph(composite, SWT.NONE);
		GraphViewer viewer = new GraphViewer(graph);

		configureViewer(viewer);

		if (graph.getLayoutAlgorithm() == null) {
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(TreeLayoutAlgorithm.LEFT_RIGHT), true);
		}
		graph.setBounds(0, 0, width, height);
		graph.getViewport().setBounds(new Rectangle(0, 0, width, height));
		shell.setVisible(false);

		Object input = viewer.getInput();
		// re-setting the input seems to be needed for the tree layout to place
		// the nodes correctly
		viewer.setInput(input);

		graph.applyLayoutNow();

		IFigure root = graph.getRootLayer();
		root.getUpdateManager().performUpdate();
	}

	/**
	 * Resize the off-screen graph.
	 * 
	 * @param width the graph width
	 * @param height the graph height
	 */
	public void resize(int width, int height) {
		shell.setSize(width, height);

		graph.setBounds(0, 0, width, height);
		graph.getViewport().setBounds(new Rectangle(0, 0, width, height));

		graph.applyLayoutNow();

		IFigure root = graph.getRootLayer();
		root.getUpdateManager().performUpdate();
	}

	/**
	 * Dispose the off-screen graph shell.
	 * 
	 * @see Shell#dispose()
	 */
	public void dispose() {
		if (shell != null) {
			shell.dispose();
		}
	}

	/**
	 * Configure the viewer.
	 * 
	 * @param viewer the graph viewer
	 */
	protected abstract void configureViewer(GraphViewer viewer);

	/**
	 * Save the graph as image to an output stream.
	 * 
	 * @param out the output stream to write the image to
	 * @param format the informal name of the image format, if <code>null</code>
	 *            defaults to <code>png</code>
	 * @throws IOException if writing the image fails
	 * 
	 * @see ImageIO#write(java.awt.image.RenderedImage, String, OutputStream)
	 */
	public void saveImage(OutputStream out, String format) throws IOException {
		saveImage(graph.getRootLayer(), out, format);
	}

	/**
	 * Save the graph as Scalable Vector Graphics to an output stream.
	 * 
	 * @param out the output stream to write the SVG DOM to
	 * @throws IOException if writing to the output stream fails
	 * @throws TransformerFactoryConfigurationError if creating the transformer
	 *             fails
	 * @throws TransformerException if writing the document fails
	 */
	public void saveSVG(OutputStream out) throws IOException, TransformerFactoryConfigurationError,
			TransformerException {
		saveSVG(graph.getRootLayer(), out);
	}

	/**
	 * Save a figure as image to an output stream.
	 * 
	 * @param root the figure to draw
	 * @param out the output stream to write the image to
	 * @param format the informal name of the image format, if <code>null</code>
	 *            defaults to <code>png</code>
	 * @throws IOException if writing the image fails
	 * 
	 * @see ImageIO#write(java.awt.image.RenderedImage, String, OutputStream)
	 */
	public static void saveImage(IFigure root, OutputStream out, String format) throws IOException {
		if (format == null) {
			format = "png";
		}

		Image drawImage = new Image(Display.getCurrent(), root.getSize().width,
				root.getSize().height);
		final GC gc = new GC(drawImage);
		SWTGraphics graphics = new SWTGraphics(gc);
		try {
			gc.setAntialias(SWT.ON);
			gc.setInterpolation(SWT.HIGH);

			// paint the graph to an image
			root.paint(graphics);
			BufferedImage bufferedImage = SwingRcpUtilities.convertToAWT(drawImage.getImageData());
			ImageIO.write(bufferedImage, format, out);
		} finally {
			gc.dispose();
			drawImage.dispose();
			out.close();
		}
	}

	/**
	 * Save a figure as Scalable Vector Graphics to an output stream.
	 * 
	 * @param root the figure to draw
	 * @param out the output stream to write the SVG DOM to
	 * @throws IOException if writing to the output stream fails
	 * @throws TransformerFactoryConfigurationError if creating the transformer
	 *             fails
	 * @throws TransformerException if writing the document fails
	 */
	public static void saveSVG(IFigure root, OutputStream out) throws IOException,
			TransformerFactoryConfigurationError, TransformerException {
		Rectangle viewBox = root.getBounds().getCopy();
		GraphicsSVG graphics = GraphicsSVG.getInstance(viewBox);

		// paint figure
		try {
			root.paint(graphics);

			Element svgRoot = graphics.getRoot();

			// Define the view box
			svgRoot.setAttributeNS(null, "viewBox", String.valueOf(viewBox.x) + " " + //$NON-NLS-1$ //$NON-NLS-2$
					String.valueOf(viewBox.y) + " " + //$NON-NLS-1$
					String.valueOf(viewBox.width) + " " + //$NON-NLS-1$
					String.valueOf(viewBox.height));

			// Write the document to the stream
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

			DOMSource source = new DOMSource(svgRoot);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
		} finally {
			graphics.dispose();
			out.close();
		}
	}

	/**
	 * Save a graph in the dot format to an output stream.
	 * 
	 * @param graph the graph
	 * @param out the output stream
	 * @throws IOException if writing to the output stream fails
	 */
	public static void saveDot(Graph graph, OutputStream out) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(out);
			writer.write(new DotExport(graph).toDotString());
		} finally {
			if (writer != null)
				writer.close();
			out.close();
		}
	}

}
