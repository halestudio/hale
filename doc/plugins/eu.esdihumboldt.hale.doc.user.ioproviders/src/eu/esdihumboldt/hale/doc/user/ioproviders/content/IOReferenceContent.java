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

package eu.esdihumboldt.hale.doc.user.ioproviders.content;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.w3c.dom.Element;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.parameter.IOProviderParameter;
import eu.esdihumboldt.hale.common.instance.io.InstanceReader;
import eu.esdihumboldt.hale.common.instance.io.InstanceWriter;
import eu.esdihumboldt.hale.doc.user.ioproviders.IOReferenceConstants;
import eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent;
import eu.esdihumboldt.util.xml.XmlUtil;

/**
 * Content Producer to dynamically produce Instance Reader and Writer
 * documentation content.
 * 
 * @author Yasmina Kammeyer
 */
public class IOReferenceContent extends AbstractVelocityContent implements IOReferenceConstants {

	private static final ALogger log = ALoggerFactory.getLogger(IOReferenceContent.class);

	/**
	 * The template File for overview
	 */
	public static final String TEMPLATE_OVERVIEW = "overview";

	/**
	 * The template file for one reader or writer
	 */
	public static final String TEMPLATE_PROVIDER = "provider";

	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		// it is an I/O provider reference
		if (href.startsWith(IO_PROVIDERS_TOPIC_PATH)) {
			String providerId = href.substring(IO_PROVIDERS_TOPIC_PATH.length());
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
				return getIOProviderContent(providerId);
			} catch (Exception e) {
				log.error("Error creating instance io info page.", e);
				return null;
			}
		}
		// should be an I/O provider overview by type
		else if (href.startsWith(OVERVIEW_TOPIC_PATH)) {
			// extract provider type name
			String providerType = href.substring(OVERVIEW_TOPIC_PATH.length());
			// strip everything after a ?
			int ind = providerType.indexOf('?');
			if (ind >= 0) {
				providerType = providerType.substring(0, ind);
			}
			// strip the .*htm? ending
			if (providerType.endsWith("html") || providerType.endsWith("htm")) {
				providerType = providerType.substring(0, providerType.lastIndexOf('.'));
			}

			Class<? extends IOProvider> providerClass = null;

			switch (providerType) {
			case "InstanceReader":
				providerClass = InstanceReader.class;
				break;
			case "InstanceWriter":
				providerClass = InstanceWriter.class;
				break;
			}

			if (providerClass != null) {
				final Class<? extends IOProvider> provider = providerClass;

				try {
					return getContentFromTemplate("overview." + providerType, TEMPLATE_OVERVIEW,
							new Callable<VelocityContext>() {

								@Override
								public VelocityContext call() throws Exception {
									VelocityContext context = new VelocityContext();
									// getProviderFactorries returns
									// Collection<IOProviderDescriptor>
									Collection<IOProviderDescriptor> writer = HaleIO
											.getProviderFactories(provider);
									context.put("providers", writer);
									context.put("providerType", provider.getSimpleName());

									return context;
								}
							});
				} catch (Exception e) {
					log.error("Error creating provider overview", e);
					return null;
				}
			}

			return null;
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
	private InputStream getIOProviderContent(String providerId) throws Exception {
		final String id = providerId;
		// try to get I/O provider
		IOProviderDescriptor providerDescriptor = HaleIO.findIOProviderFactory(IOProvider.class,
				null, providerId);

		if (providerDescriptor == null) {
			log.warn("I/O provider " + id + " does not exist.");
			return null;
		}

		final IOProviderDescriptor descriptor = providerDescriptor;

		return getContentFromTemplate(providerId, TEMPLATE_PROVIDER,
				new Callable<VelocityContext>() {

					@Override
					public VelocityContext call() throws Exception {
						VelocityContext context = new VelocityContext();

						// I/O provider descriptor
						context.put("descriptor", descriptor);

						// I/O provider type
						String type = (ImportProvider.class.isAssignableFrom(descriptor
								.getProviderType())) ? ("reader") : ("writer");
						context.put("providerType", type);

						// collect all optional parameter
						Collection<IOProviderParameter> parameter = new ArrayList<IOProviderParameter>();
						Map<String, String> example = new HashMap<String, String>();
						Map<String, String> defaults = new HashMap<String, String>();
						for (IOProviderParameter param : descriptor.getProviderParameter()) {
							parameter.add(param);

							// get example use of parameter
							if (param.getValueDescriptor() != null
									&& param.getValueDescriptor().getSampleData() != null) {
								// store sample value
								Value sample = param.getValueDescriptor().getSampleData();
								String displayValue = toDisplayValue(sample);
								if (displayValue != null) {
									example.put(param.getName(), displayValue);
								}

								// store default value
								Value defaultValue = param.getValueDescriptor().getDefaultValue();
								displayValue = toDisplayValue(defaultValue);
								if (displayValue != null) {
									defaults.put(param.getName(), displayValue);
								}
							}
						}

						context.put("parameter", parameter);
						context.put("example", example);
						context.put("defaults", defaults);
						return context;
					}
				});
	}

	/**
	 * Convert a given {@link Value} for displaying it as part of a help page.
	 * 
	 * @param value the value
	 * @return the formatted value or <code>null</code>
	 */
	protected String toDisplayValue(Value value) {
		if (value == null || value.equals(Value.NULL)) {
			return null;
		}

		if (value.isRepresentedAsDOM()) {
			// get DOM Element as String
			Element ele = value.getDOMRepresentation();
			StringWriter writer = new StringWriter();
			StreamResult formattedXmlString = new StreamResult(writer);
			try {
				XmlUtil.prettyPrint(new DOMSource(ele), formattedXmlString);
			} catch (TransformerException e) {
				log.error("Could not format parameter value for help", e);
				return null;
			}
			String xmlString = writer.toString();
			xmlString = StringEscapeUtils.escapeHtml(xmlString);

			return "<pre><code>" + xmlString + "</code></pre>";
		}
		else
			return "<code>" + value.getStringRepresentation() + "</code>";
	}

	/**
	 * @see eu.esdihumboldt.hale.doc.util.content.AbstractVelocityContent#getTemplate(java.lang.String)
	 */
	@Override
	protected InputStream getTemplate(String templateId) throws Exception {
		if (templateId.equals(TEMPLATE_PROVIDER)) {
			return IOReferenceContent.class.getResourceAsStream("provider.html");
		}
		else {
			return IOReferenceContent.class.getResourceAsStream("overview.html");
		}
	}

}
