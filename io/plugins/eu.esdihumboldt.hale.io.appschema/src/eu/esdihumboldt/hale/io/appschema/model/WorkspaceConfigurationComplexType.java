/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     GeoSolutions <https://www.geo-solutions.it>
 */

package eu.esdihumboldt.hale.io.appschema.model;

import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_NAMESPACE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_PREFIX;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.getFirstElementByTagName;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.common.core.io.ComplexValueType;

/**
 * Complex value for {@link WorkspaceConfiguration}.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class WorkspaceConfigurationComplexType
		implements ComplexValueType<WorkspaceConfiguration, Void> {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#fromDOM(org.w3c.dom.Element,
	 *      java.lang.Object)
	 */
	@Override
	public WorkspaceConfiguration fromDOM(Element fragment, Void context) {
		WorkspaceConfiguration value = new WorkspaceConfiguration();

		if (fragment != null) {
			NodeList workspaceElements = fragment.getElementsByTagNameNS(APP_SCHEMA_NAMESPACE,
					"workspace");
			for (int i = 0; i < workspaceElements.getLength(); i++) {
				Element workspaceEl = (Element) workspaceElements.item(i);

				Element defaultNameEl = getFirstElementByTagName(workspaceEl, "defaultName",
						APP_SCHEMA_NAMESPACE);
				if (defaultNameEl == null) {
					throw new IllegalArgumentException(
							"Found \"workspace\" element with no child \"defaultName\" element");
				}
				Element nameEl = getFirstElementByTagName(workspaceEl, "name",
						APP_SCHEMA_NAMESPACE);
				if (nameEl == null) {
					throw new IllegalArgumentException(
							"Found \"workspace\" element with no child \"name\" element");
				}

				Element namespaceEl = getFirstElementByTagName(workspaceEl, "namespace",
						APP_SCHEMA_NAMESPACE);
				if (namespaceEl == null) {
					throw new IllegalArgumentException(
							"Found \"workspace\" element with no child \"namespace\" element");
				}

				WorkspaceMetadata workspace = new WorkspaceMetadata(
						defaultNameEl.getTextContent().trim(), namespaceEl.getTextContent().trim());
				workspace.setName(nameEl.getTextContent().trim());

				Element isolatedEl = getFirstElementByTagName(workspaceEl, "isolated",
						APP_SCHEMA_NAMESPACE);
				if (isolatedEl != null) {
					if ("true".equalsIgnoreCase(isolatedEl.getTextContent().trim())) {
						workspace.setIsolated(true);
					}
				}

				NodeList featureTypeElements = workspaceEl
						.getElementsByTagNameNS(APP_SCHEMA_NAMESPACE, "featureType");
				for (int j = 0; j < featureTypeElements.getLength(); j++) {
					String featureType = featureTypeElements.item(j).getTextContent().trim();
					if (!featureType.isEmpty()) {
						workspace.getFeatureTypes().add(featureType);
					}
				}

				value.addWorkspace(workspace);
			}
		}

		return value;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#toDOM(java.lang.Object)
	 */
	@Override
	public Element toDOM(WorkspaceConfiguration value) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element workspaceConfEl = doc.createElementNS(APP_SCHEMA_NAMESPACE,
					"WorkspaceConfiguration");
			workspaceConfEl.setPrefix(APP_SCHEMA_PREFIX);

			if (value != null) {
				for (WorkspaceMetadata workspace : value.getWorkspaces()) {
					Element workspaceEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "workspace");

					Element defaultNameEl = doc.createElementNS(APP_SCHEMA_NAMESPACE,
							"defaultName");
					defaultNameEl.setTextContent(workspace.getDefaultName());
					workspaceEl.appendChild(defaultNameEl);

					Element nameEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "name");
					nameEl.setTextContent(workspace.getName());
					workspaceEl.appendChild(nameEl);

					Element namespaceEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "namespace");
					namespaceEl.setTextContent(workspace.getNamespaceUri());
					workspaceEl.appendChild(namespaceEl);

					Element isolatedEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "isolated");
					isolatedEl.setTextContent(Boolean.toString(workspace.isIsolated()));
					workspaceEl.appendChild(isolatedEl);

					for (String featureType : workspace.getFeatureTypes()) {
						Element featureTypeEl = doc.createElementNS(APP_SCHEMA_NAMESPACE,
								"featureType");
						featureTypeEl.setTextContent(featureType);
						workspaceEl.appendChild(featureTypeEl);
					}

					workspaceConfEl.appendChild(workspaceEl);
				}
			}

			return workspaceConfEl;
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#getContextType()
	 */
	@Override
	public Class<? extends Void> getContextType() {
		return Void.class;
	}

}
