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

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.io.gml.geometry.GMLConstants;
import eu.esdihumboldt.hale.io.gml.internal.simpletype.SimpleTypeUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Utility methods used for the GML writer
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class GmlWriterUtil implements GMLConstants {

	private static final ALogger log = ALoggerFactory.getLogger(GmlWriterUtil.class);

	/**
	 * Get the element name from a type definition
	 * 
	 * @param type the type definition
	 * @return the element name
	 */
	public static QName getElementName(TypeDefinition type) {
		Collection<? extends XmlElement> elements = type.getConstraint(XmlElements.class)
				.getElements();
		if (elements == null || elements.isEmpty()) {
			log.debug("No schema element for type " + type.getDisplayName() + //$NON-NLS-1$
					" found, using type name instead"); //$NON-NLS-1$
			return type.getName();
		}
		else {
			QName elementName = elements.iterator().next().getName();
			if (elements.size() > 1) {
				log.warn("Multiple element definitions for type " + //$NON-NLS-1$
						type.getDisplayName() + " found, using element " + //$NON-NLS-1$
						elementName.getLocalPart());
			}
			return elementName;
		}
	}

	/**
	 * Add a namespace to the given XML stream writer
	 * 
	 * @param writer the XML stream writer
	 * @param namespace the namespace to add
	 * @param preferredPrefix the preferred prefix
	 * @throws XMLStreamException if setting a prefix for the namespace fails
	 */
	public static void addNamespace(PrefixAwareStreamWriter writer, String namespace,
			String preferredPrefix) throws XMLStreamException {
		if (!writer.hasPrefix(namespace)) {
			// no prefix for namespace set

			String prefix = preferredPrefix;
			String ns = writer.getNamespace(prefix);
			if (ns == null) {
				// add namespace
				writer.setPrefix(prefix, namespace);
			}
			else {
				int i = 0;
				while (ns != null) {
					ns = writer.getNamespace(prefix + "-" + (++i)); //$NON-NLS-1$
				}

				writer.setPrefix(prefix + "-" + i, namespace); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Determines if the given type represents a XML ID
	 * 
	 * @param type the type definition
	 * @return if the type represents an ID
	 */
	public static boolean isID(TypeDefinition type) {
		if (type.getName().equals(new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "ID"))) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}

		if (type.getSuperType() != null) {
			return isID(type.getSuperType());
		}
		else {
			return false;
		}
	}

	/**
	 * Determines if the given type is an INSPIRE type
	 * 
	 * @param type the type definition
	 * @return if the type is an INSPIRE type
	 */
	public static boolean isINSPIREtype(TypeDefinition type) {
		if (type.getName().getNamespaceURI().startsWith("http://inspire.ec.europa.eu/schemas/")
				|| type.getName().getNamespaceURI()
						.startsWith("https://inspire.ec.europa.eu/schemas/")) {
			return true;
		}
		return false;
	}

	/**
	 * Determine if a given type is a feature type.
	 * 
	 * @param type the type definition
	 * @return if the type represents a feature type
	 */
	public static boolean isFeatureType(TypeDefinition type) {
		if ("AbstractFeatureType".equals(type.getName().getLocalPart())
				&& type.getName().getNamespaceURI().startsWith(GML_NAMESPACE_CORE)) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isFeatureType(type.getSuperType());
		}

		return false;
	}

	/**
	 * Write a property attribute
	 * 
	 * @param writer the XML stream writer
	 * @param value the attribute value, may be <code>null</code>
	 * @param propDef the attribute definition
	 * @throws XMLStreamException if writing the attribute fails
	 */
	public static void writeAttribute(XMLStreamWriter writer, Object value,
			PropertyDefinition propDef) throws XMLStreamException {
		if (value == null) {
			long min = propDef.getConstraint(Cardinality.class).getMinOccurs();
			if (min > 0) {
				if (!propDef.getConstraint(NillableFlag.class).isEnabled()) {
					log.warn("Non-nillable attribute " + propDef.getName() + " is null"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					// XXX write null attribute?!
					writeAtt(writer, null, propDef);
				}
			}
		}
		else {
			writeAtt(writer, SimpleTypeUtil.convertToXml(value, propDef.getPropertyType()),
					propDef);
		}
	}

	private static void writeAtt(XMLStreamWriter writer, String value, PropertyDefinition propDef)
			throws XMLStreamException {
		String ns = propDef.getName().getNamespaceURI();
		if (ns != null && !ns.isEmpty()) {
			writer.writeAttribute(ns, propDef.getName().getLocalPart(),
					(value != null) ? (value) : (null));
		}
		else {
			// no namespace
			writer.writeAttribute(propDef.getName().getLocalPart(),
					(value != null) ? (value) : (null));
		}
	}

	/**
	 * Write any required ID attribute, generating a random ID if needed.
	 * 
	 * @param writer the XML stream writer
	 * @param type the type definition
	 * @param parent the parent object, may be <code>null</code>. If it is set
	 *            the value for the ID will be tried to be retrieved from the
	 *            parent object, otherwise a random ID will be generated
	 * @param onlyIfNotSet if the ID shall only be written if no value is set in
	 *            the parent object
	 * @throws XMLStreamException if an error occurs writing the ID
	 */
	public static void writeRequiredID(XMLStreamWriter writer, DefinitionGroup type, Group parent,
			boolean onlyIfNotSet) throws XMLStreamException {
		writeID(writer, type, parent, onlyIfNotSet, null);
	}

	/**
	 * Write any required ID attribute, generating a random ID if needed. If a
	 * desired ID is given, write it even if the attribute is not required.
	 * 
	 * @param writer the XML stream writer
	 * @param type the type definition
	 * @param parent the parent object, may be <code>null</code>. If it is set
	 *            the value for the ID will be tried to be retrieved from the
	 *            parent object, otherwise a random ID will be generated
	 * @param onlyIfNotSet if the ID shall only be written if no value is set in
	 *            the parent object
	 * @param desiredId a desired identifier or <code>null</code>
	 * @throws XMLStreamException if an error occurs writing the ID
	 */
	public static void writeID(XMLStreamWriter writer, DefinitionGroup type, Group parent,
			boolean onlyIfNotSet, @Nullable String desiredId) throws XMLStreamException {
		// find ID attribute
		PropertyDefinition idProp = null;
		for (PropertyDefinition prop : collectProperties(DefinitionUtil.getAllChildren(type))) {
			if (prop.getConstraint(XmlAttributeFlag.class).isEnabled()
					&& (desiredId != null
							|| prop.getConstraint(Cardinality.class).getMinOccurs() > 0)
					&& isID(prop.getPropertyType())) {
				idProp = prop;
				break; // we assume there is only one ID attribute
			}
		}

		if (idProp == null) {
			// no ID attribute found
			return;
		}

		Object value = null;
		if (parent != null) {
			Object[] values = parent.getProperty(idProp.getName());
			if (values != null && values.length > 0) {
				value = values[0];
			}

			if (value != null && onlyIfNotSet) {
				// don't write the ID
				return;
			}
		}

		if (value == null) {
			value = desiredId;
		}

		if (value != null) {
			writeAttribute(writer, value, idProp);
		}
		else {
			UUID genID = UUID.randomUUID();
			writeAttribute(writer, "_" + genID.toString(), idProp); //$NON-NLS-1$
		}
	}

	/**
	 * Write the opening element of a {@link PathElement} to the given stream
	 * writer
	 * 
	 * @param writer the stream writer
	 * @param step the path element
	 * @param generateRequiredID if required IDs shall be generated for the path
	 *            element
	 * @throws XMLStreamException if writing to the stream writer fails
	 */
	public static void writeStartPathElement(XMLStreamWriter writer, PathElement step,
			boolean generateRequiredID) throws XMLStreamException {
		QName name = step.getName();
		if (!step.isTransient()) {
			writeStartElement(writer, name);

			// write eventual required ID
			if (generateRequiredID) {
				GmlWriterUtil.writeRequiredID(writer, step.getType(), null, false);
			}

			// write additional attributes/elements according to the path
			// element
			step.prepareWrite(writer);
		}
	}

	/**
	 * Collect all property definitions defined by the given child definitions,
	 * i.e. returns a flattened version of the children.
	 * 
	 * @param children the child definitions
	 * @return the property definitions
	 */
	private static Collection<PropertyDefinition> collectProperties(
			Iterable<? extends ChildDefinition<?>> children) {
		List<PropertyDefinition> result = new ArrayList<PropertyDefinition>();
		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null) {
				result.add(child.asProperty());
			}
			else if (child.asGroup() != null) {
				result.addAll(collectProperties(child.asGroup().getDeclaredChildren()));
			}
		}
		return result;
	}

	/**
	 * Collect all the paths to all child properties, even those contained in
	 * groups.
	 * 
	 * @param children the children
	 * @param basePath the base path
	 * @param elementsOnly if only properties representing an XML element should
	 *            be considered
	 * @return the child paths, each ending with a property element
	 */
	public static Collection<DefinitionPath> collectPropertyPaths(
			Iterable<? extends ChildDefinition<?>> children, DefinitionPath basePath,
			boolean elementsOnly) {
		List<DefinitionPath> result = new ArrayList<DefinitionPath>();
		for (ChildDefinition<?> child : children) {
			if (child.asProperty() != null) {
				if (!elementsOnly
						|| !child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
					DefinitionPath path = new DefinitionPath(basePath);
					path.addProperty(child.asProperty());
					result.add(path);
				}
			}
			else if (child.asGroup() != null) {
				DefinitionPath path = new DefinitionPath(basePath);
				path.addGroup(child.asGroup());
				result.addAll(collectPropertyPaths(child.asGroup().getDeclaredChildren(), path,
						elementsOnly));
			}
		}
		return result;
	}

	/**
	 * Write a start element.
	 * 
	 * @param writer the writer
	 * @param name the element name
	 * @throws XMLStreamException if an error occurs writing the start element
	 */
	public static void writeStartElement(XMLStreamWriter writer, QName name)
			throws XMLStreamException {
		String ns = name.getNamespaceURI();
		if (ns != null && !ns.isEmpty()) {
			writer.writeStartElement(name.getNamespaceURI(), name.getLocalPart());
		}
		else {
			writer.writeStartElement(name.getLocalPart());
		}
	}

	/**
	 * Write an empty element.
	 * 
	 * @param writer the writer
	 * @param name the element name
	 * @throws XMLStreamException if an error occurs writing the empty element
	 */
	public static void writeEmptyElement(XMLStreamWriter writer, QName name)
			throws XMLStreamException {
		String ns = name.getNamespaceURI();
		if (ns != null && !ns.isEmpty()) {
			writer.writeEmptyElement(name.getNamespaceURI(), name.getLocalPart());
		}
		else {
			writer.writeEmptyElement(name.getLocalPart());
		}
	}

}
