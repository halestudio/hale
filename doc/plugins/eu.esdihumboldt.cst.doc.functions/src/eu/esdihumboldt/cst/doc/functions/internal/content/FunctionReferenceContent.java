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

package eu.esdihumboldt.cst.doc.functions.internal.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.help.IHelpContentProducer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;
import eu.esdihumboldt.hale.common.align.extension.category.Category;
import eu.esdihumboldt.hale.common.align.extension.category.CategoryExtension;
import eu.esdihumboldt.hale.common.align.extension.function.AbstractFunction;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.common.graph.content.SourceTargetContentProvider;
import eu.esdihumboldt.hale.ui.common.graph.labels.FunctionGraphLabelProvider;
import eu.esdihumboldt.hale.ui.util.DisplayThread;
import eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph;

/**
 * Provides content for function documentation.
 * 
 * @author Simon Templer
 */
public class FunctionReferenceContent implements IHelpContentProducer,
		FunctionReferenceConstants {

	private static final ALogger log = ALoggerFactory
			.getLogger(FunctionReferenceContent.class);

	private VelocityEngine ve;
	private File tempDir;

	private File file_template;

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href,
			Locale locale) {
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
			} else {
				try {
					return getFunctionContent(func_id);
				} catch (Exception e) {
					log.error("Error creating help content", e);
				}
			}
		}

		return null;
	}

	private InputStream getFunctionContent(String func_id) throws Exception {
		// maps "function" to the real function ID (used by the template)
		AbstractFunction<?> function = FunctionUtil.getFunction(func_id);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		Template template = null;

		init();

		// creates the template file into the temporary directory
		// if it doesn't already exist
		File functionFile = new File(tempDir, func_id + ".html");
		if (!functionFile.exists()) {

			VelocityContext context = new VelocityContext();

			context.put("function", function);
			
			if (function.getCategoryId() != null) {
				String categoryId = function.getCategoryId();
				
				Category category = (CategoryExtension.getInstance().get(categoryId));
				
//				String category = categoryId.substring(categoryId
//						.lastIndexOf(".") + 1);
//
//				category = capitalize(category);
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

			template = ve.getTemplate(file_template.getName(), "UTF-8");

			FileWriter fw = new FileWriter(functionFile);

			template.merge(context, fw);

			fw.close();

		}

		return new FileInputStream(functionFile);
	}

	/**
	 * Initialize temporary directory and template engine.
	 * 
	 * @throws Exception
	 *             if an error occurs during the initialization
	 */
	private void init() throws Exception {
		synchronized (this) {
			if (ve == null) {
				ve = new VelocityEngine();
				// create a temporary directory
				tempDir = Files.createTempDir();

				file_template = new File(tempDir, "template.vm");
				URL templatePath = this.getClass().getResource("template.html");
				FileOutputStream fos = new FileOutputStream(file_template);
				InputStream stream = templatePath.openStream();

				// copys the InputStream into FileOutputStream
				IOUtils.copy(stream, fos);

				stream.close();
				fos.close();

				ve.setProperty("file.resource.loader.path",
						tempDir.getAbsolutePath());
				// initialize VelocityEngine
				ve.init();
			}
		}
	}

	private InputStream getImageContent(String func_id) throws Exception {

		final AbstractFunction<?> function = FunctionUtil.getFunction(func_id);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		init();

		final File _functionFile = new File(tempDir, func_id + ".png");
		if (!_functionFile.exists()) {
			Display display;
			if (Display.getCurrent() != null) {
				// use the current display if available
				display = Display.getCurrent();
			} else {
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
					OffscreenGraph off_graph = new OffscreenGraph(200, 100) {

						@Override
						protected void configureViewer(GraphViewer viewer) {
							LayoutAlgorithm algo = new TreeLayoutAlgorithm(
									TreeLayoutAlgorithm.LEFT_RIGHT);

							SourceTargetContentProvider stcp = new SourceTargetContentProvider();
							FunctionGraphLabelProvider fglp = new FunctionGraphLabelProvider(false);
							viewer.setContentProvider(stcp);
							viewer.setLabelProvider(fglp);
							viewer.setInput(function);
							viewer.setLayoutAlgorithm(algo);
						}

					};

					Graph graph = off_graph.getGraph();
					@SuppressWarnings("unchecked")
					List<GraphNode> list = graph.getNodes();
					int height = 0;
					int width = 0;
					if (list.size() >= 3) {
						for (GraphNode gn : list) {
							Rectangle rec = gn.getFigure().getBounds();
							height = height + rec.height + 5;
						}
						for (int i = 0; i < 3; i++) {
							Rectangle rec = list.get(i).getFigure().getBounds();
							width = width + rec.width + 5;
						}
					} else {
						// used fix size for amount of figures <= 2
						width = 230;
						height = 100;
					}

					// else {
					// // FIXME: doesn't work correctly; see function "assign".
					// // maybe use fix size ?
					// for (GraphNode gn : list) {
					// Rectangle rec = gn.getFigure().getBounds();
					// height = height + rec.height + 5;
					// width = width + rec.width + 5;
					// }
					// }
					// System.out.println("height = " + height);
					// System.out.println("width = " + width);
					// resizes the off-screen graph to the computed size
					off_graph.resize(width, height);

					try {
						off_graph.saveImage(
								new FileOutputStream(_functionFile), null);
					} catch (IOException e) {
						log.warn("Conversion from Graph to Image failed!");
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
		AbstractFunction<?> function = FunctionUtil.getFunction(func_id);

		URL url = function.getIconURL();
		
		try {
			return url.openStream();
		} catch (IOException e) {
			log.warn("Icon loading failed.");
			e.printStackTrace();
		}
		return null;
	}

}
