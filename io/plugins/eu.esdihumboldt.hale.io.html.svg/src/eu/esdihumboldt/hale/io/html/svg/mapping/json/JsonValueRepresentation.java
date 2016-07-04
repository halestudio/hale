/*
 * Copyright (c) 2016 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.html.svg.mapping.json;

import java.io.StringReader;
import java.io.StringWriter;

import org.w3c.dom.Element;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueDefinition;
import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.util.xml.XmlUtil;
import groovy.json.JsonSlurper;

/**
 * Value representation as JSON.
 * 
 * When making changes, make sure it is consistent with AlignmentFormat.
 */
public class JsonValueRepresentation implements ValueRepresentation {

	private static final ALogger log = ALoggerFactory.getLogger(JsonValueRepresentation.class);

	@Override
	public Object getValueRepresentation(Value value) {
		if (value == null) {
			return null;
		}

		if (value.isRepresentedAsDOM()) {
			// try conversion using complex value mechanism
			Object intern = value.getValue();
			if (intern == null) {
				return null;
			}
			ComplexValueDefinition cdv = ComplexValueExtension.getInstance()
					.getDefinition(intern.getClass());
			if (cdv != null && cdv.getJsonConverter() != null) {
				return cdv.getJsonConverter().toJson(intern);
			}

			// fall-back to generic XML-JSON conversion
			Element element = value.getDOMRepresentation();
			if (element == null) {
				return null;
			}
			else {
				String json = null;
				try {
					String xmlString = XmlUtil.serialize(element, false);
					try (StringReader xmlReader = new StringReader(xmlString);
							StringWriter jsonWriter = new StringWriter()) {
						JsonXML.toJson(xmlReader, jsonWriter);
						json = jsonWriter.toString();
					}
				} catch (Exception e) {
					log.error("Failed to created JSON representation of value", e);
					return null;
				}

				Object res = new JsonSlurper().parseText(json);
				return res;
			}
		}
		else {
			return value.getStringRepresentation();
		}
	}

}
