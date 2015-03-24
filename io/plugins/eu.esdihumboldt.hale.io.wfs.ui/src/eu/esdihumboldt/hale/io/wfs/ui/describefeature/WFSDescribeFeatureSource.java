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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Maps.EntryTransformer;

import eu.esdihumboldt.hale.common.core.io.ImportProvider;
import eu.esdihumboldt.hale.io.wfs.ui.AbstractWFSSource;
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
				// namespaces mapped to prefixes
				Map<String, String> namespaces = new HashMap<>();
				// type names with updated prefix
				Set<QName> typeNames = new HashSet<>();

				for (QName type : result.getTypeNames()) {
					String prefix;
					if (type.getNamespaceURI() != null && !type.getNamespaceURI().isEmpty()) {
						prefix = namespaces.get(type.getNamespaceURI());
						if (prefix == null) {
							// no mapping yet for namespace
							String candidate = type.getPrefix();
							prefix = addPrefix(candidate, type.getNamespaceURI(), namespaces);
						}
					}
					else {
						// default namespace
						prefix = XMLConstants.DEFAULT_NS_PREFIX;
					}

					// add updated type
					typeNames.add(new QName(type.getNamespaceURI(), type.getLocalPart(), prefix));
				}

				// add namespace prefix definitions
				if (!namespaces.isEmpty()) {
					builder.addParameter(
							"NAMESPACE",
							Joiner.on(',').join(
									Maps.transformEntries(namespaces,
											new EntryTransformer<String, String, String>() {

												@Override
												public String transformEntry(String namespace,
														String prefix) {
													StringBuilder sb = new StringBuilder();
													sb.append("xmlns(");
													sb.append(prefix);
													sb.append("=");
													sb.append(namespace);
													sb.append(")");
													return sb.toString();
												}

											}).values()));
				}
				// add type names
				if (!typeNames.isEmpty()) {
					builder.addParameter(
							"TYPENAME",
							Joiner.on(',').join(
									Iterables.transform(typeNames, new Function<QName, String>() {

										@Override
										public String apply(QName typeName) {
											String prefix = typeName.getPrefix();
											if (prefix == null || prefix.isEmpty()) {
												return typeName.getLocalPart();
											}
											return prefix + ":" + typeName.getLocalPart();
										}
									})));
				}
			}

			try {
				sourceURL.setStringValue(builder.build().toASCIIString());
				getPage().setErrorMessage(null);
			} catch (URISyntaxException e) {
				getPage().setErrorMessage(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Add a new prefix.
	 * 
	 * @param candidate the prefix candidate
	 * @param namespace the namespace to be associated to the prefix
	 * @param namespaces the current namespaces mapped to prefixes
	 * @return the prefix to use
	 */
	private String addPrefix(String candidate, final String namespace,
			final Map<String, String> namespaces) {
		int num = 1;
		while (namespaces.values().contains(candidate)) {
			candidate = "ns" + num;
			num++;
		}
		namespaces.put(namespace, candidate);
		return candidate;
	}

	@Override
	protected String getCaption() {
		return "WFS DescribeFeatureType request";
	}

}
