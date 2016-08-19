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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.eclipse.help.IHelpContentProducer;
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
import eu.esdihumboldt.util.xml.XmlUtil;

/**
 * Provides content for function documentation.
 * 
 * @author Simon Templer
 */
public class FunctionReferenceContent extends AbstractVelocityContent
		implements FunctionReferenceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(FunctionReferenceContent.class);

	/**
	 * Directory for storing the generated images.
	 */
	private File tempDir;

	private static final Method imageContentMethod = loadImageContentMethod();

	private static Method loadImageContentMethod() {
		try {
			FunctionReferenceContent.class.getClassLoader()
					.loadClass("eu.esdihumboldt.hale.ui.util.graph.OffscreenGraph");

			Class<?> clazz = FunctionReferenceContent.class.getClassLoader().loadClass(
					"eu.esdihumboldt.cst.doc.functions.internal.content.image.ImageContent");
			return clazz.getMethod("getImageContent", String.class, File.class);
		} catch (Throwable e) {
			log.warn("Could not load image content method for function help", e);
			return null;
		}
	}

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

	private InputStream getImageContent(String func_id) {
		if (imageContentMethod == null) {
			return null;
		}

		if (tempDir == null) {
			tempDir = Files.createTempDir();
			tempDir.deleteOnExit();
		}

		try {
			return (InputStream) imageContentMethod.invoke(null, func_id, tempDir);
		} catch (Exception e) {
			log.error("Error getting image content for function " + func_id);
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
		final FunctionDefinition<?> function = FunctionUtil.getFunction(func_id, null);

		if (function == null) {
			log.warn("Unknown function " + func_id);
			return null;
		}

		Callable<VelocityContext> contextFactory = new Callable<VelocityContext>() {

			@Override
			public VelocityContext call() throws Exception {
				VelocityContext context = new VelocityContext();

				context.put("showImage", imageContentMethod != null);

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

	private InputStream getIconContent(String func_id) {
		FunctionDefinition<?> function = FunctionUtil.getFunction(func_id, null);

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
