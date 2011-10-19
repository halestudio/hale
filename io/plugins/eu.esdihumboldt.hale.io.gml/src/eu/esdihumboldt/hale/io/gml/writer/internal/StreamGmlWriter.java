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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.feature.ComplexAttributeImpl;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.NameImpl;
import org.geotools.gml3.GML;
import org.geotools.gml3.GMLSchema;
import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.commons.tools.AttributeProperty;
import eu.esdihumboldt.commons.tools.FeatureInspector;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.StreamGeometryWriter;
import eu.esdihumboldt.hale.schemaprovider.Schema;
import eu.esdihumboldt.hale.schemaprovider.model.AttributeDefinition;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.DefinitionUtil;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;
import eu.esdihumboldt.hale.schemaprovider.provider.ApacheSchemaProvider;

/**
 * Writes GML/XML using a {@link XMLStreamWriter}
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class StreamGmlWriter extends AbstractInstanceWriter {
	
	/**
	 * Schema instance namespace (for specifying schema locations)
	 */
	public static final String SCHEMA_INSTANCE_NS = XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI; //$NON-NLS-1$
	
	private static final ALogger log = ALoggerFactory.getLogger(StreamGmlWriter.class);

	/**
	 * The parameter name for the XML root element name
	 */
	public static final String PARAM_ROOT_ELEMENT_NAME = "xml.rootElement.name";

	/**
	 * The parameter name for the XML root element namespace
	 */
	public static final String PARAM_ROOT_ELEMENT_NAMESPACE = "xml.rootElement.namespace";

	/**
	 * The XML stream writer
	 */
	private XMLStreamWriter writer;
	
	/**
	 * The GML namespace
	 */
	private String gmlNs;
	
	/**
	 * The type index
	 */
	private TypeIndex types;
	
	/**
	 * The geometry writer
	 */
	private StreamGeometryWriter geometryWriter;
	
	/**
	 * Additional schemas included in the document
	 */
	private final List<Schema> additionalSchemas = new ArrayList<Schema>();
	
	/**
	 * States if a feature collection shall be used
	 */
	private final boolean useFeatureCollection;
	
	/**
	 * Create a GML writer
	 * 
	 * @param useFeatureCollection if a GML feature collection shall be used to 
	 *   store the instances (if possible)
	 */
	public StreamGmlWriter(boolean useFeatureCollection) {
		super();
		this.useFeatureCollection = useFeatureCollection;
		
		addSupportedParameter(PARAM_ROOT_ELEMENT_NAMESPACE);
		addSupportedParameter(PARAM_ROOT_ELEMENT_NAME);
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		try {
			init();
		} catch (XMLStreamException e) {
			throw new IOException("Creating the XML stream writer failed", e);
		}
		
		try {
			write(getInstances(), progress, reporter);
			reporter.setSuccess(true);
		} catch (XMLStreamException e) {
			reporter.error(new IOMessageImpl(e.getLocalizedMessage(), e));
			reporter.setSuccess(false);
		} finally {
			progress.end();
		}
		
		return reporter;
	}

	/**
	 * @see AbstractInstanceWriter#getValidationSchemas()
	 */
	@Override
	public List<Schema> getValidationSchemas() {
		List<Schema> result = new ArrayList<Schema>(super.getValidationSchemas());
		result.addAll(additionalSchemas);
		return result;
	}

	/**
	 * Create and setup the stream writer, the type index and the GML namespace
	 * (Initializes {@link #writer}, {@link #gmlNs} and {@link #types},
	 * resets {@link #geometryWriter} and {@link #additionalSchemas}).
	 * 
	 * @throws XMLStreamException if creating the {@link XMLStreamWriter} fails
	 * @throws IOException if creating the output stream fails
	 */
	private void init() throws XMLStreamException, IOException {
		// reset geometry writer
		geometryWriter = null;
		// reset additional schemas
		additionalSchemas.clear();
		
		// create and set-up a writer
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// will set namespaces if these not set explicitly
		outputFactory.setProperty("javax.xml.stream.isRepairingNamespaces", //$NON-NLS-1$
				Boolean.valueOf(true));
		// create XML stream writer with UTF-8 encoding
		XMLStreamWriter tmpWriter = outputFactory.createXMLStreamWriter(getTarget().getOutput(), "UTF-8"); //$NON-NLS-1$

		String defNamespace = null;
		
		// read the namespaces from the map containing namespaces
		if (getTargetSchema().getPrefixes() != null) {
			for (Entry<String, String> entry : getTargetSchema().getPrefixes().entrySet()) {
				if (entry.getValue().isEmpty()) {
					defNamespace = entry.getKey();
				}
				else {
					tmpWriter.setPrefix(entry.getValue(), entry.getKey());
				}
			}
		}
		
		GmlWriterUtil.addNamespace(tmpWriter, SCHEMA_INSTANCE_NS, "xsi"); //$NON-NLS-1$
		
		if (defNamespace == null) {
			defNamespace = getTargetSchema().getNamespace();
			
			//TODO remove prefix for target schema namespace?
		}
		
		tmpWriter.setDefaultNamespace(defNamespace);
		
		writer = new IndentingXMLStreamWriter(tmpWriter);
		
		// determine GML namespace from target schema
		String gml = null;
		if (getTargetSchema().getPrefixes() != null) {
			for (String ns : getTargetSchema().getPrefixes().keySet()) {
				if (ns.startsWith("http://www.opengis.net/gml")) { //$NON-NLS-1$
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
			log.debug("GML namespace is " + gmlNs); //$NON-NLS-1$
		}
		
		// fill type index with root types
		types = new TypeIndex();
//		for (SchemaElement element : getTargetSchema().getElements().values()) {
//			types.addType(element.getType());
//		}
		for (Definition def : getTargetSchema().getTypes().keySet()) {
			types.addType(DefinitionUtil.getType(def));
		}
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "GML/XML";
	}

	/**
	 * Write the given instances
	 * 
	 * @param features the feature collection
	 * @param reporter the reporter
	 * @param progress the progress
	 * @throws XMLStreamException if writing the feature collection fails 
	 */
	public void write(FeatureCollection<FeatureType, Feature> features, 
			ProgressIndicator progress, IOReporter reporter) throws XMLStreamException {
		progress.begin("Generating " + getTypeName(), features.size());
		
		TypeDefinition containerDefinition = null;
		Name containerName = null;
		
		if (useFeatureCollection) {
			// try to find FeatureCollection element
			Iterator<SchemaElement> it = getTargetSchema().getAllElements().values().iterator();
			Collection<SchemaElement> fcElements = new HashSet<SchemaElement>();
			while (it.hasNext()) {
				SchemaElement el = it.next();
				if (isFeatureCollection(el)) {
					fcElements.add(el);
				}
			}
			
			if (fcElements.isEmpty() && gmlNs.equals("http://www.opengis.net/gml")) { //$NON-NLS-1$
				// no FeatureCollection defined and "old" namespace -> GML 2
				// include WFS 1.0.0 for the FeatureCollection element
				try {
					URI location = getClass().getResource("/schemas/wfs/1.0.0/WFS-basic.xsd").toURI(); //$NON-NLS-1$
					Schema wfsSchema = new ApacheSchemaProvider().loadSchema(location, null);
					// replace location for verification
					wfsSchema = new Schema(wfsSchema.getElements(), wfsSchema.getNamespace(), 
							new URL("http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd"),  //$NON-NLS-1$
							wfsSchema.getPrefixes());
					// add as additional schema
					additionalSchemas.add(wfsSchema);
					// add namespace
					GmlWriterUtil.addNamespace(writer, wfsSchema.getNamespace(), "wfs"); //$NON-NLS-1$
					for (SchemaElement el : wfsSchema.getElements().values()) {
						if (isFeatureCollection(el)) {
							fcElements.add(el);
						}
					}
				} catch (Exception e) {
					log.warn("Using WFS schema for the FeatureCollection definition failed", e); //$NON-NLS-1$
				}
			}
			
			if (fcElements.isEmpty()) {
				reporter.warn(new IOMessageImpl(
						"No element describing a FeatureCollection found", null)); //$NON-NLS-1$
			}
			else {
				// select fc element TODO priorized selection (root element parameters)
				SchemaElement fcElement = fcElements.iterator().next();
				containerDefinition = fcElement.getType();
				containerName = fcElement.getElementName();
				
				log.info("Found " + fcElements.size() + " possible FeatureCollection elements" + //$NON-NLS-1$ //$NON-NLS-2$
						", using element " + fcElement.getElementName()); //$NON-NLS-1$
			}
		}
		
		if (containerDefinition == null) {
			// no container defined, try to use a custom root element
			String namespace = getParameter(PARAM_ROOT_ELEMENT_NAMESPACE);
			if (namespace == null) {
				// default to target namespace
				namespace = getTargetSchema().getNamespace();
			}
			String elementName = getParameter(PARAM_ROOT_ELEMENT_NAME);
			
			if (elementName != null) {
				Iterator<SchemaElement> it = getTargetSchema().getAllElements().values().iterator();
				while (it.hasNext() && containerDefinition == null) {
					SchemaElement el = it.next();
					if (el.getElementName().getNamespaceURI().equals(namespace) &&
							el.getElementName().getLocalPart().equals(elementName)) {
						containerDefinition = el.getType();
						containerName = new NameImpl(namespace, elementName);
					}
				}
			}
		}

		if (containerDefinition == null || containerName == null) {
			throw new IllegalStateException("No root element/container found");
		}
		
		writer.writeStartDocument();
		writer.writeStartElement(containerName.getNamespaceURI(), 
				containerName.getLocalPart());
		
		// generate mandatory id attribute (for feature collection)
		GmlWriterUtil.writeRequiredID(writer, containerDefinition, null, false);
		
		// write schema locations
		StringBuffer locations = new StringBuffer();
		locations.append(getTargetSchema().getNamespace());
		locations.append(" "); //$NON-NLS-1$
		locations.append(getTargetSchema().getLocation().toString());
		for (Schema schema : additionalSchemas) {
			locations.append(" "); //$NON-NLS-1$
			locations.append(schema.getNamespace());
			locations.append(" "); //$NON-NLS-1$
			locations.append(schema.getLocation().toString());
		}
		writer.writeAttribute(SCHEMA_INSTANCE_NS, "schemaLocation", locations.toString()); //$NON-NLS-1$
		
		// boundedBy is needed for GML 2 FeatureCollections
		AttributeDefinition boundedBy = containerDefinition.getAttribute("boundedBy"); //$NON-NLS-1$
		if (boundedBy != null && boundedBy.getMinOccurs() > 0) {
			writer.writeStartElement(boundedBy.getNamespace(), boundedBy.getName());
			writer.writeStartElement(gmlNs, "null"); //$NON-NLS-1$
			writer.writeCharacters("missing"); //$NON-NLS-1$
			writer.writeEndElement();
			writer.writeEndElement();
		}
		
		// write the instances
		Iterator<Feature> itFeature = features.iterator();
		try {
			Map<TypeDefinition, DefinitionPath> paths = new HashMap<TypeDefinition, DefinitionPath>();
			
			Descent lastDescent = null;
			while (itFeature.hasNext() && !progress.isCanceled()) {
				Feature feature = itFeature.next();
				
				TypeDefinition type = types.getType(feature.getType());
				
				// get stored definition path for the type
				DefinitionPath defPath;
				if (paths.containsKey(type)) {
					defPath = paths.get(type); // get the stored path, may be null
				}
				else {
					// determine a valid definition path in the container
					//TODO specify a maximum descent level? (else searching the container for matches might take _very_ long)
					defPath = findMemberAttribute(
							containerDefinition, containerName, type);
					// store path (may be null)
					paths.put(type, defPath);
				}
				if (defPath != null) {
					// write the feature
					lastDescent = Descent.descend(writer, defPath, lastDescent, false);
		            writeMember(feature, type);
				}
				else {
					reporter.warn(new IOMessageImpl(MessageFormat.format(
							"No compatible member attribute for type {0} found in root element {1}, one instance was skipped", 
							type.getDisplayName(), containerName.getLocalPart()), null));
				}
	            
	            progress.advance(1);
			}
			if (lastDescent != null) {
				lastDescent.close();
			}
		} finally {
			features.close(itFeature);
		}
        
        writer.writeEndElement(); // FeatureCollection
        
        writer.writeEndDocument();
	}

	/**
	 * Find a matching attribute for the given member type in the given 
	 * container type
	 * 
	 * @param container the container type
	 * @param containerName the container element name
	 * @param memberType the member type
	 * 
	 * @return the attribute definition or <code>null</code>
	 */
	protected DefinitionPath findMemberAttribute(
			TypeDefinition container, Name containerName, 
			final TypeDefinition memberType) {
		for (AttributeDefinition attribute : container.getAttributes()) {
			// direct match
			if (attribute.getAttributeType() != null && attribute.getAttributeType().equals(memberType)) {
				return new DefinitionPath(
						attribute.getAttributeType(), 
						new NameImpl(attribute.getNamespace(), attribute.getName()),
						attribute.getMaxOccurs() <= 1);
			}
		}
		
		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {
			
			@Override
			protected DefinitionPath matchPath(TypeDefinition type,
					TypeDefinition matchParam, DefinitionPath path) {
				if (type.equals(memberType)) {
					return path;
				}
				
				//XXX special case: FeatureCollection from foreign schema
				Set<SchemaElement> elements = matchParam.getDeclaringElements();
				if (!elements.isEmpty() && !type.getDeclaringElements().isEmpty()) {
					TypeDefinition parent = matchParam.getSuperType();
					while (parent != null) {
						if (parent.equals(type)) {
							return new DefinitionPath(path).addSubstitution(elements.iterator().next());
						}
						
						parent = parent.getSuperType();
					}
				}
				
				return null;
			}
		};
		
		// candidate match
		List<DefinitionPath> candidates = matcher.findCandidates(container, 
				containerName, true, memberType);
		if (candidates != null && !candidates.isEmpty()) {
			return candidates.get(0); //TODO notification? FIXME will this work? possible problem: attribute is selected even though better candidate is in other attribute
		}
		
		return null;
	}

	private boolean isFeatureCollection(SchemaElement el) {
		//TODO improve condition?
		return el.getElementName().getLocalPart().contains("FeatureCollection") && //$NON-NLS-1$
			!el.getType().isAbstract() &&
			el.getType().getAttribute("featureMember") != null; //$NON-NLS-1$
	}

	/**
	 * Write a given feature
	 * 
	 * @param feature the feature to write
	 * @param type the feature type definition
	 * @throws XMLStreamException if writing the feature fails 
	 */
	protected void writeMember(Feature feature, TypeDefinition type) throws XMLStreamException {
//		Name elementName = GmlWriterUtil.getElementName(type);
//		writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
		
		// feature id
		FeatureId id = feature.getIdentifier();
		
		if (id != null) {
			String idAttribute = "fid"; //XXX GML 2/3 ? //$NON-NLS-1$
			AttributeDefinition idDef = type.getAttribute(idAttribute);
			if (idDef == null) {
				idAttribute = "id"; // GML 3.2 //$NON-NLS-1$
				idDef = type.getAttribute(idAttribute);
			}
			if (idDef != null && idDef.isAttribute()) {
				// id attribute present in type
				Object idProp = FeatureInspector.getPropertyValue(feature, Arrays.asList(idAttribute), null);
				if (idProp == null) {
					// set id attribute on feature if not set
					FeatureInspector.setPropertyValue(feature, Arrays.asList(idAttribute), id);
				}
			}
			else {
				// manually add id attribute
				//XXX writer.writeAttribute(gmlNs, idAttribute, id.toString());
			}
		}
		
		writeProperties(feature, type, true);
		
//		writer.writeEndElement(); // type element name
	}

	/**
	 * Write the given feature's properties
	 * 
	 * @param feature the feature
	 * @param type the feature type
	 * @param allowElements if element properties may be written
	 * @throws XMLStreamException if writing the properties fails
	 */
	private void writeProperties(ComplexAttribute feature, TypeDefinition type, boolean allowElements) throws XMLStreamException {
		// eventually generate mandatory ID that is not set
		GmlWriterUtil.writeRequiredID(writer, type, feature, true);
		
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
		
		if (allowElements) {
			for (AttributeDefinition attDef : attributes) {
				// elements must be handled after attributes
				if (!attDef.isAttribute()) {
					Property property = FeatureInspector.getProperty(feature, Arrays.asList(attDef.getName()), false);
					Object value = (property == null) ? (null) : (property.getValue());
					
					writeElement(value, property, attDef);
				}
			}
		}
	}

	/**
	 * Write a property element
	 * 
	 * @param value the element value
	 * @param property the property that contained the value (needed for 
	 * attribute values for simple types)
	 * @param attDef the attribute definition
	 * @throws XMLStreamException if writing the element fails
	 */
	private void writeElement(Object value, Property property, AttributeDefinition attDef) throws XMLStreamException {
		// for collections call this method for each item
		if (value instanceof Collection<?>) {
			for (Object item : ((Collection<?>) value)) {
				writeElement(item, null, attDef);
			}
			return;
		}
		
		// single objects
		
		if (value == null) {
			// null value
			if (attDef.getMinOccurs() > 0) {
				// write empty element
				writer.writeEmptyElement(attDef.getNamespace(), attDef.getName());
				
				// but may have attributes
				writeSimpleTypeAttributes(property, attDef);
				
				if (!attDef.isNillable()) {
					log.warn("Non-nillable element " + attDef.getName() + " is null"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else {
					// nillable -> we may mark it as nil
					writer.writeAttribute(SCHEMA_INSTANCE_NS, "nil", "true"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			// otherwise just skip it
		}
		else {
			// value is set
			
			writer.writeStartElement(attDef.getNamespace(), attDef.getName());
			
			if (value instanceof ComplexAttribute) {
				// write properties
				writeProperties((ComplexAttribute) value, attDef.getAttributeType(), true);
			}
			else if (value instanceof Geometry) {
				// write geometry
				writeGeometry(((Geometry) value), attDef, getCommonSRSName());
			}
			else {
				// write any attributes
				writeSimpleTypeAttributes(property, attDef);
				
				// write value as content
				writer.writeCharacters(value.toString()/*FIXME SimpleTypeUtil.convertToXml(value, attDef.getAttributeType())*/);
			}
			
			writer.writeEndElement();
		}
	}

	/**
	 * Write any attributes for simple type elements
	 * 
	 * @param property the property of the simple type element
	 * @param attDef the attribute definition of the simple type element
	 * @throws XMLStreamException if an error occurs writing the attributes
	 */
	private void writeSimpleTypeAttributes(Property property,
			AttributeDefinition attDef) throws XMLStreamException {
		if (property != null && attDef.isElement() // only elements may have properties
				&& !(property instanceof AttributeProperty)) { //XXX this is a dirty hack - find a better solution
			Collection<Property> properties = FeatureInspector.getProperties(property);
			if (properties != null && !properties.isEmpty()) {
				//XXX create dummy attribute for writeProperties TODO better: FeatureInspector must support Property
				ComplexAttribute ca = new ComplexAttributeImpl(properties, GMLSchema.ABSTRACTSTYLETYPE_TYPE, null);
				writeProperties(ca, attDef.getAttributeType(), false);
			}
		}
	}

	/**
	 * Write a geometry
	 * 
	 * @param geometry the geometry
	 * @param property the geometry property
	 * @param srsName the common SRS name, may be <code>null</code> 
	 * @throws XMLStreamException if an error occurs writing the geometry  
	 */
	private void writeGeometry(Geometry geometry, AttributeDefinition property, 
			String srsName) throws XMLStreamException {
		getGeometryWriter().write(writer, geometry, property, srsName);
	}

	/**
	 * Get the geometry writer
	 * 
	 * @return the geometry writer instance to use 
	 */
	protected StreamGeometryWriter getGeometryWriter() {
		if (geometryWriter == null) {
			geometryWriter = StreamGeometryWriter.getDefaultInstance(gmlNs);
		}
		
		return geometryWriter;
	}
	
	/**
	 * Write a property attribute
	 * 
	 * @param value the attribute value, may be <code>null</code>
	 * @param attDef the attribute definition
	 * @throws XMLStreamException if writing the attribute fails 
	 */
	private void writeAttribute(Object value, 
			AttributeDefinition attDef) throws XMLStreamException {
		GmlWriterUtil.writeAttribute(writer, value, attDef);
	}

}
