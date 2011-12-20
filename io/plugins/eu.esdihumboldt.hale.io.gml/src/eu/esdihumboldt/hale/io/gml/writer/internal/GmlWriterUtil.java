/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.NameImpl;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.Name;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.PathElement;
import eu.esdihumboldt.specification.modelrepository.abstractfc.SchemaElement;

/**
 * Utility methods used for the GML writer
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class GmlWriterUtil {
	
	private static final ALogger log = ALoggerFactory.getLogger(GmlWriterUtil.class);
	
	/**
	 * Get the element name from a type definition
	 * 
	 * @param type the type definition
	 * @return the element name
	 */
	public static Name getElementName(TypeDefinition type) {
		Set<SchemaElement> elements = type.getDeclaringElements();
		if (elements == null || elements.isEmpty()) {
			log.debug("No schema element for type " + type.getDisplayName() +  //$NON-NLS-1$
					" found, using type name instead"); //$NON-NLS-1$
			return type.getName();
		}
		else {
			Name elementName = elements.iterator().next().getElementName();
			if (elements.size() > 1) {
				log.warn("Multiple element definitions for type " +  //$NON-NLS-1$
						type.getDisplayName() + " found, using element " +  //$NON-NLS-1$
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
	public static void addNamespace(XMLStreamWriter writer,
			String namespace, String preferredPrefix) throws XMLStreamException {
		if (writer.getPrefix(namespace) == null) {
			// no prefix for schema instance namespace
			
			String prefix = preferredPrefix;
			String ns = writer.getNamespaceContext().getNamespaceURI(prefix);
			if (ns == null) {
				// add xsi namespace
				writer.setPrefix(prefix, namespace);
			}
			else {
				int i = 0;
				while (ns != null) {
					ns = writer.getNamespaceContext().getNamespaceURI(prefix + "-" + (++i)); //$NON-NLS-1$
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
		if (type.getName().equals(new NameImpl("http://www.w3.org/2001/XMLSchema", "ID"))) { //$NON-NLS-1$ //$NON-NLS-2$
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
	 * Write a property attribute
	 * 
	 * @param writer the XML stream writer 
	 * @param value the attribute value, may be <code>null</code>
	 * @param attDef the attribute definition
	 * @throws XMLStreamException if writing the attribute fails 
	 */
	public static void writeAttribute(XMLStreamWriter writer, Object value, 
			AttributeDefinition attDef) throws XMLStreamException {
		if (value == null) {
			if (attDef.getMinOccurs() > 0) {
				if (!attDef.isNillable()) {
					log.warn("Non-nillable attribute " + attDef.getName() + " is null"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					//XXX write null attribute?!
					writeAtt(writer, null, attDef);
				}
			}
		}
		else {
			writeAtt(writer, value.toString() /*FIXME SimpleTypeUtil.convertToXml(value, attDef.getAttributeType())*/, attDef);
		}
	}

	private static void writeAtt(XMLStreamWriter writer, String value, 
			AttributeDefinition attDef) throws XMLStreamException {
		String ns = attDef.getNamespace();
		if (ns != null && !ns.isEmpty()) {
			writer.writeAttribute(attDef.getNamespace(), attDef.getName(), 
					(value != null)?(value):(null));
		}
		else {
			// no namespace
			writer.writeAttribute(attDef.getName(), 
					(value != null)?(value):(null));
		}
	}
	
	/**
	 * Write any required ID attribute, generating a random ID if needed
	 * 
	 * @param writer the XML stream writer
	 * @param type the type definition
	 * @param parent the parent object, may be <code>null</code>. If it is set
	 *   the value for the ID will be tried to be retrieved from the parent
	 *   object, otherwise a random ID will be generated
	 * @param onlyIfNotSet if the ID shall only be written if no value is set
	 *   in the parent object
	 * @throws XMLStreamException if an error occurs writing the ID
	 */
	public static void writeRequiredID(XMLStreamWriter writer,
			TypeDefinition type, ComplexAttribute parent, boolean onlyIfNotSet) throws XMLStreamException {
		// find ID attribute
		AttributeDefinition idAtt = null;
		for (AttributeDefinition att : type.getAttributes()) {
			if (att.isAttribute() && att.getMinOccurs() > 0 && isID(att.getAttributeType())) {
				idAtt = att;
				break; // we assume there is only one ID attribute
			}
		}
		
		if (idAtt == null) {
			// no ID attribute found
			return;
		}
		
		Object value = null;
		if (parent != null) {
			Property prop = FeatureInspector.getProperty(parent, Arrays.asList(idAtt.getName()), false);
			if (prop != null) {
				value = prop.getValue();
			}
			
			if (value != null && onlyIfNotSet) {
				// don't write the ID
				return;
			}
		}
		
		if (value != null) {
			writeAttribute(writer, value, idAtt);
		}
		else {
			UUID genID = UUID.randomUUID();
			writeAttribute(writer, "_" + genID.toString(), idAtt); //$NON-NLS-1$
		}
	}

	/**
	 * Write the opening element of a {@link PathElement} to the given stream 
	 * writer
	 * 
	 * @param writer the stream writer
	 * @param step the path element
	 * @param generateRequiredID if required IDs shall be generated for the 
	 *   path element
	 * @throws XMLStreamException if writing to the stream writer fails 
	 */
	public static void writeStartPathElement(XMLStreamWriter writer, PathElement step, 
			boolean generateRequiredID) throws XMLStreamException {
		Name name = step.getName();
		writer.writeStartElement(name.getNamespaceURI(), name.getLocalPart());
		if (step.isDowncast()) {
			// add xsi:type
			writer.writeAttribute(StreamGmlWriter.SCHEMA_INSTANCE_NS, "type", 
					step.getType().getName().getLocalPart()); //XXX namespace needed for the attribute value?
		}
		// write eventual required ID
		if (generateRequiredID) {
			GmlWriterUtil.writeRequiredID(writer, step.getType(), null, false);
		}
	}

}
