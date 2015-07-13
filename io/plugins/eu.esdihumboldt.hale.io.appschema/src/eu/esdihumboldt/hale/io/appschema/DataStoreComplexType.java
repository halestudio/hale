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

package eu.esdihumboldt.hale.io.appschema;

import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_NAMESPACE;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.APP_SCHEMA_PREFIX;
import static eu.esdihumboldt.hale.io.appschema.AppSchemaIO.getFirstElementByTagName;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.esdihumboldt.hale.common.core.io.ComplexValueType;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.SourceDataStoresPropertyType.DataStore.Parameters.Parameter;

/**
 * Complex value for DataStore configuration.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class DataStoreComplexType implements ComplexValueType<DataStore, Void> {

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#fromDOM(org.w3c.dom.Element,
	 *      java.lang.Object)
	 */
	@Override
	public DataStore fromDOM(Element fragment, Void context) {
		DataStore dataStore = new DataStore();

		if (fragment != null) {
			Element idEl = getFirstElementByTagName(fragment, "id", APP_SCHEMA_NAMESPACE);
			if (idEl != null) {
				dataStore.setId(idEl.getTextContent());
			}

			NodeList paramElements = fragment.getElementsByTagNameNS(APP_SCHEMA_NAMESPACE,
					"Parameter");
			if (paramElements != null && paramElements.getLength() > 0) {
				dataStore.setParameters(new Parameters());
				for (int i = 0; i < paramElements.getLength(); i++) {
					Element paramEl = (Element) paramElements.item(i);
					Element paramNameEl = getFirstElementByTagName(paramEl, "name",
							APP_SCHEMA_NAMESPACE);
					Element paramValueEl = getFirstElementByTagName(paramEl, "value",
							APP_SCHEMA_NAMESPACE);
					if (paramNameEl != null && paramValueEl != null) {
						Parameter param = new Parameter();
						param.setName(paramNameEl.getTextContent());
						param.setValue(paramValueEl.getTextContent());

						dataStore.getParameters().getParameter().add(param);
					}
				}
			}
		}
		return dataStore;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.ComplexValueType#toDOM(java.lang.Object)
	 */
	@Override
	public Element toDOM(DataStore dataStore) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Element dataStoreEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "DataStore");
			dataStoreEl.setPrefix(APP_SCHEMA_PREFIX);

			if (dataStore != null) {
				if (dataStore.getId() != null && !dataStore.getId().trim().isEmpty()) {
					Element idEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "id");
					idEl.setPrefix(APP_SCHEMA_PREFIX);
					dataStoreEl.appendChild(idEl);
				}
				else {
					// TODO: generate unique id
				}
				if (dataStore.getParameters() != null) {
					List<Parameter> parameters = dataStore.getParameters().getParameter();
					if (parameters.size() > 0) {
						Element paramsEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "parameters");
						for (Parameter param : parameters) {
							Element paramEl = doc
									.createElementNS(APP_SCHEMA_NAMESPACE, "Parameter");
							Element paramNameEl = doc.createElementNS(APP_SCHEMA_NAMESPACE, "name");
							paramNameEl.setTextContent(param.getName());
							Element paramValueEl = doc.createElementNS(APP_SCHEMA_NAMESPACE,
									"value");
							paramValueEl.setTextContent(param.getValue());
							paramEl.appendChild(paramNameEl);
							paramEl.appendChild(paramValueEl);
							paramsEl.appendChild(paramEl);
						}
						dataStoreEl.appendChild(paramsEl);
					}
				}
			}

			return dataStoreEl;
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
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
