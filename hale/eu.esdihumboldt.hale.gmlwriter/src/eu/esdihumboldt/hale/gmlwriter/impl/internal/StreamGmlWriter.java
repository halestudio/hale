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

package eu.esdihumboldt.hale.gmlwriter.impl.internal;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.FeatureCollection;
import org.geotools.gml3.GML;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.tools.FeatureInspector;

/**
 * Writes GML using an {@link XMLStreamWriter}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class StreamGmlWriter {
	
	private static final ALogger log = ALoggerFactory.getLogger(StreamGmlWriter.class);

	/**
	 * The XML stream writer
	 */
	private final XMLStreamWriter writer;
	
	/**
	 * The target schema
	 */
	private final Schema targetSchema;

	/**
	 * The GML namespace
	 */
	private final String gmlNs;
	
	/**
	 * The type index
	 */
	private final TypeIndex types;

	/**
	 * Constructor
	 * 
	 * @param targetSchema the target schema
	 * @param out the output stream
	 * @throws XMLStreamException if setting up the stream writer fails
	 */
	public StreamGmlWriter(Schema targetSchema, OutputStream out) throws XMLStreamException {
		this.targetSchema = targetSchema;
		
		// create and set-up a writer
		
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// will set namespaces if these not set explicitly
		outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces",
				new Boolean(true));
		// create XML stream writer with UTF-8 encoding
		XMLStreamWriter tmpWriter = outputFactory.createXMLStreamWriter(out, "UTF-8");

		String defNamespace = null;
		
		// read the namespaces from the map containing namespaces
		if (targetSchema.getPrefixes() != null) {
			for (Entry<String, String> entry : targetSchema.getPrefixes().entrySet()) {
				if (entry.getValue().isEmpty()) {
					defNamespace = entry.getKey();
				}
				else if (!entry.getKey().equals(targetSchema.getNamespace())) {
					tmpWriter.setPrefix(entry.getValue(), entry.getKey());
				}
			}
		}
		
		if (defNamespace == null) {
			defNamespace = targetSchema.getNamespace();
		}
		
		tmpWriter.setDefaultNamespace(defNamespace);
		
		writer = new IndentingXMLStreamWriter(tmpWriter);
		
		// determine GML namespace from target schema
		String gml = null;
		if (targetSchema.getPrefixes() != null) {
			for (String ns : targetSchema.getPrefixes().keySet()) {
				if (ns.startsWith("http://www.opengis.net/gml")) {
					gml = ns;
					break;
				}
			}
		}
		
		if (gml == null) {
			// default to GML 2/3 namespace
			gml = GML.NAMESPACE;
		}
		
		gmlNs = gml;
		if (log.isDebugEnabled()) {
			log.debug("GML namespace is " + gmlNs);
		}
		
		// fill type index with root types
		types = new TypeIndex();
		for (SchemaElement element : targetSchema.getElements().values()) {
			types.addType(element.getType());
		}
	}

	/**
	 * Write the given features
	 * 
	 * @param features the feature collection
	 * @throws XMLStreamException if writing the feature collection fails 
	 */
	public void write(FeatureCollection<FeatureType, Feature> features) throws XMLStreamException {
		writer.writeStartDocument();
		
		writer.writeStartElement("gml", "FeatureCollection", gmlNs);
		
		Iterator<Feature> it = features.iterator();
		try {
			Feature feature = it.next();
			
			TypeDefinition type = types.getType(feature.getType());
			
			// write the feature
            writer.writeStartElement( gmlNs, "featureMember" );
            writeMember(feature, type);
            writer.writeEndElement(); // featureMember
		} finally {
			features.close(it);
		}
        
        writer.writeEndElement(); // FeatureCollection
        
        writer.writeEndDocument();
	}

	/**
	 * Write a given feature
	 * 
	 * @param feature the feature to write
	 * @param type the feature type definition
	 * @throws XMLStreamException if writing the feature fails 
	 */
	protected void writeMember(Feature feature, TypeDefinition type) throws XMLStreamException {
		Name elementName = getElementName(type);
		writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
		
		// feature id
		FeatureId id = feature.getIdentifier();
		
		if (id != null) {
			AttributeDefinition idDef = type.getAttribute("fid");
			if (idDef != null && idDef.isAttribute()) {
				// fid attribute present in type
				Object idProp = FeatureInspector.getPropertyValue(feature, Arrays.asList("fid"), null);
				if (idProp == null) {
					// set fid attribute on feature if not set
					FeatureInspector.setPropertyValue(feature, Arrays.asList("fid"), id);
				}
			}
			else {
				// manually add fid attribute
				writer.writeAttribute(gmlNs, "fid", id.toString());
			}
		}
		
		writeProperties(feature, type);
		
		writer.writeEndElement(); // type element name
	}

	/**
	 * Get the element name from a type definition
	 * 
	 * @param type the type definition
	 * @return the element name
	 */
	private Name getElementName(TypeDefinition type) {
		Set<SchemaElement> elements = type.getDeclaringElements();
		if (elements == null || elements.isEmpty()) {
			log.warn("No schema element for type " + type.getDisplayName() + 
					" found, using type name instead");
			return type.getName();
		}
		else {
			Name elementName = elements.iterator().next().getElementName();
			if (elements.size() > 0) {
				log.warn("Multiple element definitions for type " + 
						type.getDisplayName() + " found, using element " + 
						elementName.getLocalPart());
			}
			return elementName;
		}
	}

	/**
	 * Write the given feature's properties
	 * 
	 * @param feature the feature
	 * @param type the feature type
	 * @throws XMLStreamException if writing the properties fails
	 */
	private void writeProperties(ComplexAttribute feature, TypeDefinition type) throws XMLStreamException {
		// writing the feature is controlled by the type definition
		Collection<AttributeDefinition> attributes = type.getAttributes();
		for (AttributeDefinition attDef : attributes) {
			// attributes must be handled first
			if (attDef.isAttribute()) {
				Property property = FeatureInspector.getProperty(feature, Arrays.asList(attDef.getName()), false);
				Object value = (property == null) ? (null) : (property.getValue());
				
				writeAttribute(value, attDef);
			}
		}
		
		for (AttributeDefinition attDef : attributes) {
			// elements must be handled after attributes
			if (!attDef.isAttribute()) {
				Property property = FeatureInspector.getProperty(feature, Arrays.asList(attDef.getName()), false);
				Object value = (property == null) ? (null) : (property.getValue());
				
				writeElement(value, attDef);
			}
		}
	}

	/**
	 * Write a property element
	 * 
	 * @param value the element value
	 * @param attDef the attribute definition
	 * @throws XMLStreamException if writing the element fails
	 */
	private void writeElement(Object value, AttributeDefinition attDef) throws XMLStreamException {
		// for collections call this method for each item
		if (value instanceof Collection<?>) {
			for (Object item : ((Collection<?>) value)) {
				writeElement(item, attDef);
			}
			return;
		}
		
		// single objects
		
		if (value == null) {
			// null value
			if (attDef.getMinOccurs() > 0) {
				// write empty element
				writer.writeEmptyElement(attDef.getNamespace(), attDef.getName());
				
				if (!attDef.isNillable()) {
					log.warn("Non-nillable element " + attDef.getName() + " is null");
				}
			}
			// otherwise just skip it
		}
		else {
			// value is set
			
			writer.writeStartElement(attDef.getNamespace(), attDef.getName());
			
			if (value instanceof ComplexAttribute) {
				// write properties
				writeProperties((ComplexAttribute) value, attDef.getAttributeType());
			}
			else {
				// write value as content
				writer.writeCharacters(value.toString()); //TODO convert mechanism
			}
			
			writer.writeEndElement();
		}
	}

	/**
	 * Write a property attribute
	 * 
	 * @param value the attribute value, may be <code>null</code>
	 * @param attDef the attribute definition
	 * @throws XMLStreamException if writing the attribute fails 
	 */
	private void writeAttribute(Object value, AttributeDefinition attDef) throws XMLStreamException {
		if (value == null) {
			if (!attDef.isNillable()) {
				log.warn("Non-nillable attribute " + attDef.getName() + " is null");
			}
		}
		else {
			//TODO correct conversion of value
			String ns = attDef.getNamespace();
			if (ns != null && !ns.isEmpty()) {
				writer.writeAttribute(attDef.getNamespace(), attDef.getName(), value.toString());
			}
			else {
				// get namespace from declaring type XXX is this the correct solution? or should the namespace just be omitted
				ns = attDef.getDeclaringType().getName().getNamespaceURI();
				writer.writeAttribute(ns, attDef.getName(), value.toString());
			}
		}
	}

}
