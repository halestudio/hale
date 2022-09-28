/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.core.io.project;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.model.IOConfiguration;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.JaxbProjectIO;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.JaxbToProject;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.ProjectToJaxb;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.IOConfigurationType;
import eu.esdihumboldt.hale.common.core.io.project.model.internal.generated.ObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.helpers.DefaultValidationEventHandler;

/**
 * Helper class for converting {@link IOConfiguration} to DOM (and back) using
 * the JAXB project model.
 * 
 * @author Kai Schwierczek
 * @author Simon Templer
 */
public class DOMProjectHelper {

	private static final ALogger log = ALoggerFactory.getLogger(DOMProjectHelper.class);

	private DOMProjectHelper() {
	}

	/**
	 * Converts the given element to a type entity definition. If any exception
	 * occurs <code>null</code> is returned.
	 * 
	 * @param fragment the fragment to convert
	 * @return the I/O configuration or <code>null</code>
	 */
	public static IOConfiguration configurationFromDOM(Element fragment) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbProjectIO.PROJECT_CONTEXT,
					IOConfigurationType.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarshalling
			u.setEventHandler(new DefaultValidationEventHandler());

			JAXBElement<IOConfigurationType> root = u.unmarshal(fragment,
					IOConfigurationType.class);

			return JaxbToProject.toIOConfiguration(root.getValue());
		} catch (Exception e) {
			log.error("Failed to create object representation from DOM", e);
			return null;
		}
	}

	/**
	 * Converts the given I/O configuration to an element.
	 * 
	 * @param config the I/O configuration
	 * @return the created element or <code>null</code> in case of an exception
	 */
	public static Element configurationToDOM(IOConfiguration config) {
		return jaxbElementToDOM(new ObjectFactory()
				.createConfiguration(ProjectToJaxb.toIOConfigurationType(config)));
	}

	private static Element jaxbElementToDOM(Object jaxbElement) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbProjectIO.PROJECT_CONTEXT,
					ObjectFactory.class.getClassLoader());
			Marshaller m = jc.createMarshaller();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			m.marshal(jaxbElement, doc);

			return (Element) doc.getFirstChild();
		} catch (Exception e) {
			log.error("Failed to serialize element to DOM", e);
			return null;
		}
	}
}
