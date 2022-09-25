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

package eu.esdihumboldt.hale.common.align.io.impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.dummy.DummyEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.EntityDefinitionToJaxb;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ObjectFactory;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * Helper class for converting EntityDefinitions to DOM (and back) using the
 * JAXB alignment model.
 * 
 * @author Kai Schwierczek
 */
public class DOMEntityDefinitionHelper {

	private DOMEntityDefinitionHelper() {
	}

	private static final EntityResolver resolver = new DummyEntityResolver();

	/**
	 * Converts the given element to a type entity definition. If any exception
	 * occurs <code>null</code> is returned.
	 * 
	 * @param fragment the fragment to convert
	 * @param types the type index to use for unmarshalling
	 * @param ssid the schema space to use for unmarshalling
	 * @return the type entity definition or <code>null</code>
	 */
	public static TypeEntityDefinition typeFromDOM(Element fragment, TypeIndex types,
			SchemaSpaceID ssid) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
					ClassType.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarshalling
			u.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());

			JAXBElement<ClassType> root = u.unmarshal(fragment, ClassType.class);

			return resolver.resolveType(root.getValue(), types, ssid).getDefinition();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the given element to a property entity definition. If any
	 * exception occurs <code>null</code> is returned.
	 * 
	 * @param fragment the fragment to convert
	 * @param types the type index to use for unmarshalling
	 * @param ssid the schema space to use for unmarshalling
	 * @return the type entity definition or <code>null</code>
	 */
	public static PropertyEntityDefinition propertyFromDOM(Element fragment, TypeIndex types,
			SchemaSpaceID ssid) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
					PropertyType.class.getClassLoader());
			Unmarshaller u = jc.createUnmarshaller();

			// it will debug problems while unmarshalling
			u.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());

			JAXBElement<PropertyType> root = u.unmarshal(fragment, PropertyType.class);

			return resolver.resolveProperty(root.getValue(), types, ssid).getDefinition();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts the given type entity definition to an element.
	 * 
	 * @param type the type entity definition to convert
	 * @return the created element or <code>null</code> in case of an exception
	 */
	public static Element typeToDOM(TypeEntityDefinition type) {
		return jaxbElementToDOM(EntityDefinitionToJaxb.convert(type));
	}

	/**
	 * Converts the given type entity definition to an element.
	 * 
	 * @param type the type entity definition to convert
	 * @return the created element or <code>null</code> in case of an exception
	 */
	public static Element typeToDOM(TypeDefinition type) {
		TypeEntityDefinition entity = new TypeEntityDefinition(type, null, null);
		return typeToDOM(entity);
	}

	/**
	 * Converts the given property entity definition to an element.
	 * 
	 * @param property the property entity definition to convert
	 * @return the created element or <code>null</code> in case of an exception
	 */
	public static Element propertyToDOM(PropertyEntityDefinition property) {
		return jaxbElementToDOM(EntityDefinitionToJaxb.convert(property));
	}

	private static Element jaxbElementToDOM(Object jaxbElement) {
		try {
			JAXBContext jc = JAXBContext.newInstance(JaxbAlignmentIO.ALIGNMENT_CONTEXT,
					ObjectFactory.class.getClassLoader());
			Marshaller m = jc.createMarshaller();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			m.marshal(jaxbElement, doc);

			return (Element) doc.getFirstChild();
		} catch (Exception e) {
			return null;
		}
	}
}
