/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.wfs.ui.describefeature;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Splitter;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.io.wfs.ui.AbstractWFSSource;
import eu.esdihumboldt.hale.io.wfs.ui.KVPUtil;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.hale.ui.util.io.URIFieldEditor;
import eu.esdihumboldt.hale.ui.util.wizard.HaleWizardDialog;

/**
 * Source for loading a schema from a WFS.
 * 
 * @author Simon Templer
 */
public class WFSDescribeFeatureSource extends AbstractWFSSource<ImportProvider> {

	@Override
	protected void determineSource(URIFieldEditor sourceURL) {
		WFSDescribeFeatureConfig config = new WFSDescribeFeatureConfig();
		WFSDescribeFeatureWizard wizard = new WFSDescribeFeatureWizard(config);
		HaleWizardDialog dialog = new HaleWizardDialog(Display.getCurrent().getActiveShell(),
				wizard);

		if (dialog.open() == WizardDialog.OK) {
			WFSDescribeFeatureConfig result = wizard.getConfiguration();

			// create URL
			URIBuilder builder = new URIBuilder(result.getDescribeFeatureUri());
			// add fixed parameters
			builder.addParameter("SERVICE", "WFS");
			builder.addParameter("VERSION", result.getVersion().toString());
			builder.addParameter("REQUEST", "DescribeFeatureType");
			// specify type names
			if (!result.getTypeNames().isEmpty()) {
				KVPUtil.addTypeNameParameter(builder, result.getTypeNames(), result.getVersion());
			}

			try {
				sourceURL.setStringValue(builder.build().toASCIIString());
				getPage().setErrorMessage(null);
			} catch (URISyntaxException e) {
				getPage().setErrorMessage(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public boolean updateConfiguration(ImportProvider provider) {
		boolean success = super.updateConfiguration(provider);
		if (success && provider instanceof XmlSchemaReader) {
			// analyze URI and determine relevant elements

			URI loc = provider.getSource().getLocation();
			if (loc != null) {
				URIBuilder b = new URIBuilder(loc);
				String namespace = null;
				String typename = null;
				for (NameValuePair param : b.getQueryParams()) {
					switch (param.getName().toLowerCase()) {
					case "namespace":
					case "namespaces":
						namespace = param.getValue();
						break;
					case "typename":
					case "typenames":
						typename = param.getValue();
						break;
					}
				}

				if (typename != null && !typename.isEmpty()) {
					// parse namespaces
					Map<String, String> prefixToNamespace = new HashMap<>();
					if (namespace != null && !namespace.isEmpty()) {
						Pattern ex = Pattern.compile("xmlns\\((([\\w\\d]+)(=|,))?(.+)\\)");
						Matcher matcher = ex.matcher(namespace);

						while (matcher.find()) {
							String prefix = matcher.group(2);
							if (prefix == null) {
								prefix = XMLConstants.DEFAULT_NS_PREFIX;
							}
							String ns = matcher.group(4);
							prefixToNamespace.put(prefix, ns);
						}

						// previously used implementation below does not support
						// comma separator inside xmlns(...)
//						for (String xmlns : Splitter.on(',').omitEmptyStrings().trimResults()
//								.split(namespace)) {
//							if (xmlns.startsWith("xmlns(") && xmlns.endsWith(")")) {
//								String mapping = xmlns.substring("xmlns(".length(),
//										xmlns.length() - 1);
//								List<String> mp = Splitter.on('=').limit(2).trimResults()
//										.splitToList(mapping);
//								if (mp.size() == 2) {
//									prefixToNamespace.put(mp.get(0), mp.get(1));
//								}
//								else {
//									// mapping for default namespace
//									prefixToNamespace.put(XMLConstants.DEFAULT_NS_PREFIX, mp.get(0));
//								}
//							}
//						}
					}

					Set<QName> elements = new HashSet<>();
					// parse type names
					for (String type : Splitter.on(',').omitEmptyStrings().trimResults()
							.split(typename)) {
						List<String> nameParts = Splitter.on(':').limit(2).trimResults()
								.splitToList(type);
						QName name;
						if (nameParts.size() == 2) {
							// prefix and name
							String prefix = nameParts.get(0);
							String ns = prefixToNamespace.get(prefix);
							if (ns != null) {
								name = new QName(ns, nameParts.get(1), prefix);
							}
							else {
								// namespace unknown - fall back to local name
								// only
								name = new QName(nameParts.get(1));
							}
						}
						else {
							// only local name
							name = new QName(nameParts.get(0));
						}

						elements.add(name);
					}

					((XmlSchemaReader) provider).setRelevantElements(elements);
				}
			}
		}
		return success;
	}

	@Override
	protected String getCaption() {
		return "WFS DescribeFeatureType KVP request";
	}

}
