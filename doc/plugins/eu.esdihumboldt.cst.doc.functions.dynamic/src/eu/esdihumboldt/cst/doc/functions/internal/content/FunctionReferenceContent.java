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

package eu.esdihumboldt.cst.doc.functions.internal.content;

import java.awt.Dimension;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.help.IHelpContentProducer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.w3c.dom.Element;

import com.google.common.io.Files;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.category.CategoryExtension;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent;
import eu.esdihumboldt.hale.ui.common.graph.content.FunctionGraphContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.FunctionGraphLabelProvider;
import eu.esdihumboldt.hale.ui.common.graph.layout.FunctionTreeLayoutAlgorithm;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;
import eu.esdihumboldt.util.xml.XmlUtil;

/**
 * Provides content for function documentation.
 * 
 * @author Simon Templer
 */
public class FunctionReferenceContent extends AbstractVelocityContent implements
		FunctionReferenceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(FunctionReferenceContent.class);

	/**
	 * Directory for storing the generated images.
	 */
	private File tempDir;

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			String func_id = href.substring(FUNCTION_TOPIC_PATH.length());
			// strip everything after a ?
			int ind = func_id.indexOf('?');
			if (ind >= 0) {
				func_id = func_id.substring(0, ind);
			}
			// strip the .*htm? ending
			if (func_id.endsWith("html") || func_id.endsWith("htm")) {
				func_id = func_id.substring(0, func_id.lastIndexOf('.'));
			}
			// .png ending
			if (func_id.endsWith(".png")) {
				func_id = func_id.substring(0, func_id.lastIndexOf('.'));
				try {
					return getImageContent(func_id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// /icon ending
			if (func_id.endsWith("/icon")) {
				func_id = func_id.substring(0, func_id.lastIndexOf('/'));
				try {
					return getIconContent(func_id);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					return getFunctionContent(func_id);
				} catch (Exception e) {
					log.error("Error creating help content", e);
				}
			}
		}

		return null;
	}

	/**
	 * @see AbstractVelocityContent#getTemplate(String)
	 */
	@Override
	protected InputStream getTemplate(String templateId) throws Exception {
		return FunctionReferenceContent.class.getResource("template.html").openStream();
	}

	private InputStream getFunctionContent(String func_id) throws Exception {
		// maps "function" to the real function ID (used by the template)
		final FunctionDefinition function = FunctionUtil.getFunction(func_id, null);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		Callable<VelocityContext> contextFactory = new Callable<VelocityContext>() {

			@Override
			public VelocityContext call() throws Exception {
				VelocityContext context = new VelocityContext();

				context.put("function", function);

				// Map<paramDisplayName, sampleDataStringRepresentation>
				Map<String, String> parameterDocu = new HashMap<String, String>();
				for (FunctionParameterDefinition param : function.getDefinedParameters()) {
					if (param.getValueDescriptor() != null
							&& param.getValueDescriptor().getSampleData() != null) {
						Value sample = param.getValueDescriptor().getSampleData();
						if (sample.isRepresentedAsDOM()) {
							// get DOM Element as String
							Element ele = sample.getDOMRepresentation();
							StringWriter writer = new StringWriter();
							StreamResult formattedXmlString = new StreamResult(writer);
							XmlUtil.prettyPrint(new DOMSource(ele), formattedXmlString);
							// escape special chars to display xml code on html
							String xmlString = formattedXmlString.getWriter().toString();
							xmlString = StringEscapeUtils.escapeXml(xmlString);

							parameterDocu.put(param.getDisplayName(), xmlString);
						}
						else {
							parameterDocu.put(param.getDisplayName(),
									sample.getStringRepresentation());
						}
					}
				}
				context.put("parameterDocu", parameterDocu);

				if (function.getCategoryId() != null) {
					String categoryId = function.getCategoryId();

					Category category = (CategoryExtension.getInstance().get(categoryId));

					// String category = categoryId.substring(categoryId
					// .lastIndexOf(".") + 1);
					//
					// category = capitalize(category);
					context.put("category", category);
				}

				// creating path for the file to be included
				URL help_url = function.getHelpURL();
				if (help_url != null) {
					String help_path = help_url.getPath();
					String bundle = function.getDefiningBundle();

					StringBuffer sb_include = new StringBuffer();
					sb_include.append(bundle);
					sb_include.append(help_path);
					sb_include.append("/help");

					String final_help_url = sb_include.toString();

					context.put("include", final_help_url);
				}

				return context;
			}
		};

		return getContentFromTemplate(func_id, "function", contextFactory);
	}

	private InputStream getImageContent(String func_id) throws Exception {

		final FunctionDefinition function = FunctionUtil.getFunction(func_id, null);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		if (tempDir == null) {
			tempDir = Files.createTempDir();
			tempDir.deleteOnExit();
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
						off_graph.saveImage(new BufferedOutputStream(new FileOutputStream(
								_functionFile)), null);
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

	private InputStream getIconContent(String func_id) {
		FunctionDefinition function = FunctionUtil.getFunction(func_id, null);

		URL url = function.getIconURL();

		try {
			return url.openStream();
		} catch (IOException e) {
			log.warn("Icon loading failed.");
			e.printStackTrace();
		}
		return null;
	}

	private Dimension computeSize(Graph graph) {
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
