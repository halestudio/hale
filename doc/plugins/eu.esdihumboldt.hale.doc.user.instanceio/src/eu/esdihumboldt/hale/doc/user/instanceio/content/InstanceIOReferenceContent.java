/*
 * Copyright (c) 2014 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.doc.user.instanceio.content;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.runtime.content.IContentType;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.DefaultValue;
import eu.esdihumboldt.hale.common.core.parameter.InstanceProviderParameter;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.doc.user.instanceio.InstanceIOReferenceConstants;
import eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent;

/**
 * Content Producer to dynamically produce Instance Reader and Writer
 * documentation content.
 * 
 * @author Yasmina Kammeyer
 */
public class InstanceIOReferenceContent extends AbstractVelocityContent implements
		InstanceIOReferenceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(InstanceIOReferenceContent.class);

	/**
	 * The template File for overview
	 */
	public static final String TEMPLATE_OVERVIEW = "instanceIO";

	/**
	 * The template file for one reader or writer
	 */
	public static final String TEMPLATE_READERWRITER = "readerwriter";

	/**
	 * @see org.eclipse.help.IHelpContentProducer#getInputStream(java.lang.String,
	 *      java.lang.String, java.util.Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		// it is an instance io overiew
		if (href.contains(INSTANCEIO_OVERVIEW_PATH))
			try {
				return getContentFromTemplate("instanceIO", TEMPLATE_OVERVIEW,
						new Callable<VelocityContext>() {

							@Override
							public VelocityContext call() throws Exception {
								VelocityContext context = new VelocityContext();
								// getProviderFactorries returns
								// Collection<IOProviderDescriptor>
								Collection<IOProviderDescriptor> writer = HaleIO
										.getProviderFactories(InstanceWriter.class);
								context.put("writers", writer);
								Collection<IOProviderDescriptor> reader = HaleIO
										.getProviderFactories(InstanceReader.class);
								context.put("readers", reader);

								return context;
							}
						});
			} catch (Exception e) {
				log.error("Error creating instance io overview", e);
				return null;
			}
		// it is an instance reader or writer
		if (href.startsWith(INSTANCEIO_TOPIC_PATH)) {
			String providerId = href.substring(INSTANCEIO_TOPIC_PATH.length());
			// strip everything after a ?
			int ind = providerId.indexOf('?');
			if (ind >= 0) {
				providerId = providerId.substring(0, ind);
			}
			// strip the .*htm? ending
			if (providerId.endsWith("html") || providerId.endsWith("htm")) {
				providerId = providerId.substring(0, providerId.lastIndexOf('.'));
			}
			try {
				return getInstanceIOContent(providerId);
			} catch (Exception e) {
				log.error("Error creating instance io info page.", e);
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param providerId The id of the provider
	 * @return The in input stream or null, if the given provider does not
	 *         exist.
	 * @throws Exception - if an error occurs
	 */
	private InputStream getInstanceIOContent(String providerId) throws Exception {
		final String id = providerId;
		final String direction;
		// Try to get instance reader
		IOProviderDescriptor readerwriter = HaleIO.findIOProviderFactory(InstanceReader.class,
				null, id);
		// If not successful try to get instance writer
		if (readerwriter == null) {
			readerwriter = HaleIO.findIOProviderFactory(InstanceWriter.class, null, id);
			direction = "writer";
		}
		else
			direction = "reader";

		if (readerwriter == null) {
			log.warn("Instance Provider: " + id + " does not exist.");
			return null;
		}

		final IOProviderDescriptor io = readerwriter;

		return getContentFromTemplate(providerId, TEMPLATE_READERWRITER,
				new Callable<VelocityContext>() {

					@Override
					public VelocityContext call() throws Exception {
						VelocityContext context = new VelocityContext();
						// getProviderFactorries returns
						context.put("io", io);
						// put direction (input/reader - output/writer)
						context.put("direction", direction);
						// Read all possible file extensions of Content Types
						Collection<String> fileextensions = new ArrayList<String>();
						String[] extension;
						// Supported Types
						for (IContentType ct : io.getSupportedTypes()) {
							extension = ct.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
							for (String format : extension) {
								// add file format extension
								fileextensions.add("*." + format);
							}
						}
						context.put("fileformat",
								fileextensions.toArray(new String[fileextensions.size()]));
						// collect all optional parameter
						Collection<InstanceProviderParameter> parameter = new ArrayList<InstanceProviderParameter>();
						Map<String, String> example = new HashMap<String, String>();
						String providerName = "";
						for (InstanceProviderParameter param : io.getProviderParameter()) {
							parameter.add(param);
							providerName = param.getName();
							// parse if parameter is optional
//							if (param.isOptional()) {
//								parameter.add(providerName);
//							}
//							else
//								parameter.add(providerName += "*");

							// get example use of parameter
							if (param.getDefaultValue() != null) {
								// parameter.add(param.getDefaultValue().getSampleData());
								// example.put(providerName,
								// param.getDefaultValue().getSampleData());
								for (DefaultValue value : param.getDefaultValue()) {
									example.put(providerName, value.getSampleData());
								}
							}
						}

						context.put("parameter",
								parameter.toArray(new InstanceProviderParameter[parameter.size()]));
						context.put("example", example);
						return context;
					}
				});
	}

	/**
	 * @see eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent#getTemplate(java.lang.String)
	 */
	@Override
	protected InputStream getTemplate(String templateId) throws Exception {
		if (templateId.equals(TEMPLATE_READERWRITER)) {
			return InstanceIOReferenceContent.class.getResourceAsStream("readerwriter.html");
		}
		else {
			return InstanceIOReferenceContent.class.getResourceAsStream("instanceIO.html");
		}
	}

}
