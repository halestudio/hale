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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.geotools.gml3.GML;

import com.vividsolutions.jts.geom.Geometry;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.instance.io.impl.AbstractInstanceWriter;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.io.gml.internal.simpletype.SimpleTypeUtil;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.AbstractTypeMatcher;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.DefinitionPath;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.Descent;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.StreamGeometryWriter;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

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
	 * The parameter name for the flag specifying if a geometry should be
	 * simplified before writing it, if possible. Defaults to true.
	 */
	public static final String PARAM_SIMPLIFY_GEOMETRY = "gml.geometry.simplify";

	/**
	 * The XML stream writer
	 */
	private XMLStreamWriter writer;
	
	/**
	 * The GML namespace
	 */
	private String gmlNs;
	
//	/**
//	 * The type index
//	 */
//	private TypeIndex types;
	
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

	private XmlIndex targetIndex;
	
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
	
	//FIXME
//	/**
//	 * @see AbstractInstanceWriter#getValidationSchemas()
//	 */
//	@Override
//	public List<Schema> getValidationSchemas() {
//		List<Schema> result = new ArrayList<Schema>(super.getValidationSchemas());
//		result.addAll(additionalSchemas);
//		return result;
//	}
	
	/**
	 * @see AbstractInstanceWriter#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		if (getXMLIndex() == null) {
			fail("No XML target schema");
		}
	}

	/**
	 * Get the XML type index.
	 * @return the target type index
	 */
	protected XmlIndex getXMLIndex() {
		if (targetIndex == null) {
			targetIndex = getXMLIndex(getTargetSchema());
		}
		return targetIndex;
	}
	
	/**
	 * Get the XML index from the given schema space
	 * @param schemas the schema space
	 * @return the XML index or <code>null</code>
	 */
	public static XmlIndex getXMLIndex(SchemaSpace schemas) {
		//XXX respect a container, types?
		for (Schema schema : schemas.getSchemas()) {
			if (schema instanceof XmlIndex) {
				//TODO respect root element for schema selection?
				return (XmlIndex) schema;
			}
		}
		
		return null;
	}

	/**
	 * Create and setup the stream writer, the type index and the GML namespace
	 * (Initializes {@link #writer}, {@link #gmlNs} and {@link #targetIndex},
	 * resets {@link #geometryWriter} and {@link #additionalSchemas}).
	 * 
	 * @throws XMLStreamException if creating the {@link XMLStreamWriter} fails
	 * @throws IOException if creating the output stream fails
	 */
	private void init() throws XMLStreamException, IOException {
		// reset target index
		targetIndex = null;
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
		
		XmlIndex index = getXMLIndex();
		// read the namespaces from the map containing namespaces
		if (index.getPrefixes() != null) {
			for (Entry<String, String> entry : index.getPrefixes().entrySet()) {
				if (entry.getValue().isEmpty()) {
					//XXX don't use a default namespace, as this results in problems with schemas w/o elementFormQualified=true
					//defNamespace = entry.getKey();
				}
				else {
					tmpWriter.setPrefix(entry.getValue(), entry.getKey());
				}
			}
		}
		
		GmlWriterUtil.addNamespace(tmpWriter, SCHEMA_INSTANCE_NS, "xsi"); //$NON-NLS-1$
		
		// determine default namespace
		if (defNamespace == null) {
			//XXX don't use a default namespace, as this results in problems with schemas w/o elementFormQualified=true
			//defNamespace = index.getNamespace();
			
			//TODO remove prefix for target schema namespace?
		}
		
		tmpWriter.setDefaultNamespace(defNamespace);
		
		writer = new IndentingXMLStreamWriter(tmpWriter);
		
		// determine GML namespace from target schema
		String gml = null;
		if (index.getPrefixes() != null) {
			for (String ns : index.getPrefixes().keySet()) {
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
		
		//FIXME fill type index with root types
//		types = new TypeIndex();
//		for (SchemaElement element : getTargetSchema().getElements().values()) {
//			types.addType(element.getType());
//		}
//		for (Definition def : getTargetSchema().getTypes().keySet()) {
//			types.addType(DefinitionUtil.getType(def));
//		}
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return true;
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "GML/XML";
	}

	/**
	 * Write the given instances.
	 * @param instances the instance collection
	 * @param reporter the reporter
	 * @param progress the progress
	 * @throws XMLStreamException if writing the feature collection fails 
	 */
	public void write(InstanceCollection instances, 
			ProgressIndicator progress, IOReporter reporter) throws XMLStreamException {
		progress.begin("Generating " + getTypeName(), instances.size());
		
		TypeDefinition containerDefinition = null;
		QName containerName = null;
		
		if (useFeatureCollection) {
			// try to find FeatureCollection element
			Iterator<XmlElement> it = targetIndex.getElements().values().iterator();
			Collection<XmlElement> fcElements = new HashSet<XmlElement>();
			while (it.hasNext()) {
				XmlElement el = it.next();
				if (isFeatureCollection(el)) {
					fcElements.add(el);
				}
			}
			
			
			if (fcElements.isEmpty() && gmlNs.equals("http://www.opengis.net/gml")) { //$NON-NLS-1$
				// no FeatureCollection defined and "old" namespace -> GML 2
				// include WFS 1.0.0 for the FeatureCollection element
				try {
					URI location = StreamGmlWriter.class.getResource("/schemas/wfs/1.0.0/WFS-basic.xsd").toURI(); //$NON-NLS-1$
					XmlSchemaReader schemaReader = new XmlSchemaReader();
					schemaReader.setSource(new DefaultInputSupplier(location));
					//FIXME to work with the extra schema it must be integrated with the main schema
//					schemaReader.setSharedTypes(sharedTypes);
					
					IOReport report = schemaReader.execute(null);
					
					if (report.isSuccess()) {
						XmlIndex wfsSchema = schemaReader.getSchema();
						
						// look for FeatureCollection element
						for (XmlElement el : wfsSchema.getElements().values()) {
							if (isFeatureCollection(el)) {
								fcElements.add(el);
							}
						}
						
						// add as additional schema, replace location for verification
						additionalSchemas.add(new SchemaDecorator(wfsSchema) {
							@Override
							public URI getLocation() {
								return URI.create("http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd");
							}
						});
						
						// add namespace
						GmlWriterUtil.addNamespace(writer, wfsSchema.getNamespace(), "wfs"); //$NON-NLS-1$
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
				XmlElement fcElement = fcElements.iterator().next();
				containerDefinition = fcElement.getType();
				containerName = fcElement.getName();
				
				log.info("Found " + fcElements.size() + " possible FeatureCollection elements" + //$NON-NLS-1$ //$NON-NLS-2$
						", using element " + fcElement.getName()); //$NON-NLS-1$
			}
		}
		
		if (containerDefinition == null) {
			// no container defined, try to use a custom root element
			String namespace = getParameter(PARAM_ROOT_ELEMENT_NAMESPACE);
			// determine target namespace
			if (namespace == null) {
				// default to target namespace
				namespace = targetIndex.getNamespace();
			}
			String elementName = getParameter(PARAM_ROOT_ELEMENT_NAME);
			
			// find root element
			if (elementName != null) {
				Iterator<XmlElement> it = targetIndex.getElements().values().iterator();
				while (it.hasNext() && containerDefinition == null) {
					XmlElement el = it.next();
					if (el.getName().getNamespaceURI().equals(namespace) &&
							el.getName().getLocalPart().equals(elementName)) {
						containerDefinition = el.getType();
						containerName = el.getName();
					}
				}
			}
		}

		if (containerDefinition == null || containerName == null) {
			throw new IllegalStateException("No root element/container found");
		}
		
		writer.writeStartDocument();
		GmlWriterUtil.writeStartElement(writer, containerName);
		
		// generate mandatory id attribute (for feature collection)
		GmlWriterUtil.writeRequiredID(writer, containerDefinition, null, false);
		
		// write schema locations
		StringBuffer locations = new StringBuffer();
		locations.append(targetIndex.getNamespace());
		locations.append(" "); //$NON-NLS-1$
		locations.append(targetIndex.getLocation().toString());
		for (Schema schema : additionalSchemas) {
			locations.append(" "); //$NON-NLS-1$
			locations.append(schema.getNamespace());
			locations.append(" "); //$NON-NLS-1$
			locations.append(schema.getLocation().toString());
		}
		writer.writeAttribute(SCHEMA_INSTANCE_NS, "schemaLocation", locations.toString()); //$NON-NLS-1$
		
		// boundedBy is needed for GML 2 FeatureCollections
		//XXX working like this - getting the child with only a local name?
		ChildDefinition<?> boundedBy = containerDefinition.getChild(new QName("boundedBy")); //$NON-NLS-1$
		if (boundedBy != null && boundedBy.asProperty() != null 
				&& boundedBy.asProperty().getConstraint(Cardinality.class).getMinOccurs() > 0) {
			writer.writeStartElement(
					boundedBy.getName().getNamespaceURI(), 
					boundedBy.getName().getLocalPart());
			writer.writeStartElement(gmlNs, "null"); //$NON-NLS-1$
			writer.writeCharacters("missing"); //$NON-NLS-1$
			writer.writeEndElement();
			writer.writeEndElement();
		}
		
		//FIXME write the instances
		ResourceIterator<Instance> itInstance = instances.iterator();
		try {
			Map<TypeDefinition, DefinitionPath> paths = new HashMap<TypeDefinition, DefinitionPath>();
			
			Descent lastDescent = null;
			while (itInstance.hasNext() && !progress.isCanceled()) {
				Instance instance = itInstance.next();
				
				TypeDefinition type = instance.getDefinition();
				
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
		            writeMember(instance, type);
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
			itInstance.close();
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
			TypeDefinition container, QName containerName, 
			final TypeDefinition memberType) {
		//XXX not working if property is no substitution of the property type - use matching instead
//		for (PropertyDefinition property : GmlWriterUtil.collectProperties(container.getChildren())) {
//			// direct match - 
//			if (property.getPropertyType().equals(memberType)) {
//				long max = property.getConstraint(Cardinality.class).getMaxOccurs();
//				return new DefinitionPath(
//						property.getPropertyType(), 
//						property.getName(),
//						max != Cardinality.UNBOUNDED && max <= 1);
//			}
//		}
		
		AbstractTypeMatcher<TypeDefinition> matcher = new AbstractTypeMatcher<TypeDefinition>() {
			
			@Override
			protected DefinitionPath matchPath(TypeDefinition type,
					TypeDefinition matchParam, DefinitionPath path) {
				if (type.equals(memberType)) {
					return path;
				}
				
				//XXX special case: FeatureCollection from foreign schema
				Collection<? extends XmlElement> elements = matchParam.getConstraint(XmlElements.class).getElements();
				Collection<? extends XmlElement> containerElements = type.getConstraint(XmlElements.class).getElements();
				if (!elements.isEmpty() && !containerElements.isEmpty()) {
					TypeDefinition parent = matchParam.getSuperType();
					while (parent != null) {
						if (parent.equals(type)) {
							//FIXME will not work with separately loaded schemas because e.g. the choice allowing the specific type is missing
							//FIXME add to path
//							return new DefinitionPath(path).addSubstitution(elements.iterator().next());
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

	private boolean isFeatureCollection(XmlElement el) {
		//TODO improve condition?
		//FIXME working like this?!
		return el.getName().getLocalPart().contains("FeatureCollection") && //$NON-NLS-1$
			!el.getType().getConstraint(AbstractFlag.class).isEnabled() &&
			hasChild(el.getType(), "featureMember"); //$NON-NLS-1$
	}

	private boolean hasChild(TypeDefinition type, String localName) {
		for (ChildDefinition<?> child : type.getChildren()) {
			if (localName.equals(child.getName().getLocalPart())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Write a given instance
	 * 
	 * @param instance the instance to writer
	 * @param type the feature type definition
	 * @throws XMLStreamException if writing the feature fails 
	 */
	protected void writeMember(Instance instance, TypeDefinition type) throws XMLStreamException {
//		Name elementName = GmlWriterUtil.getElementName(type);
//		writer.writeStartElement(elementName.getNamespaceURI(), elementName.getLocalPart());
		
		writeProperties(instance, type, true);
		
//		writer.writeEndElement(); // type element name
	}

	/**
	 * Write the given feature's properties
	 * 
	 * @param group the feature
	 * @param definition the feature type
	 * @param allowElements if element properties may be written
	 * @throws XMLStreamException if writing the properties fails
	 */
	private void writeProperties(Group group, DefinitionGroup definition, 
			boolean allowElements) throws XMLStreamException {
		// eventually generate mandatory ID that is not set
		GmlWriterUtil.writeRequiredID(writer, definition, group, true);
		
		// writing the feature is controlled by the type definition
		// so retrieving values from instance must happen based on actual 
		// structure! (e.g. including groups)
		
		// write the attributes, as they must be handled first
		writeProperties(group, DefinitionUtil.getAllChildren(definition), true);
		
		if (allowElements) {
			// write the elements
			writeProperties(group, DefinitionUtil.getAllChildren(definition), false);
		}
	}

	/**
	 * Write attribute or element properties.
	 * @param parent the parent group
	 * @param children the child definitions
	 * @param attributes <code>true</code> if attribute properties shall be 
	 *   written, <code>false</code> if element properties shall be written
	 * @throws XMLStreamException if writing the attributes/elements fails
	 */
	private void writeProperties(Group parent,
			Collection<? extends ChildDefinition<?>> children,
			boolean attributes) throws XMLStreamException {
		if (parent == null) {
			return;
		}
		
		for (ChildDefinition<?> child : children) {
			Object[] values = parent.getProperty(child.getName());
			if (values == null || values.length <= 0) {
				continue;
			}
			
			if (child.asProperty() != null) {
				PropertyDefinition propDef = child.asProperty();
				boolean isAttribute = propDef.getConstraint(XmlAttributeFlag.class).isEnabled();
				
				if (attributes && isAttribute) {
					// write attribute
					writeAttribute(values[0], propDef);
					
					if (values.length > 1) {
						//TODO warning?!
					}
				}
				else if (!attributes && !isAttribute) {
					// write element
					for (Object value : values) {
						writeElement(value, propDef);
					}
				}
			}
			else if (child.asGroup() != null) {
				// handle to child groups
				for (Object value : values) {
					if (value instanceof Group) {
						writeProperties((Group) value, 
								DefinitionUtil.getAllChildren(child.asGroup()), 
								attributes);
					}
					else {
						//TODO warning/error?
					}
				}
			}
		}
	}

	/**
	 * Write a property element.
	 * @param value the element value
	 * @param propDef the property definition
	 * @throws XMLStreamException if writing the element fails
	 */
	private void writeElement(Object value, PropertyDefinition propDef) throws XMLStreamException {
		Group group = null;
		if (value instanceof Group) {
			group = (Group) value;
			if (value instanceof Instance) {
				// extract value from instance
				value = ((Instance) value).getValue();
			}
		}
		
		if (group == null) {
			// just a value
			
			if (value == null) {
				// null value
				if (propDef.getConstraint(Cardinality.class).getMinOccurs() > 0) {
					// write empty element
					GmlWriterUtil.writeEmptyElement(writer, propDef.getName());
					
					// mark as nil
					writeElementValue(null, propDef);
				}
				// otherwise just skip it
			}
			else {
				GmlWriterUtil.writeStartElement(writer, propDef.getName());
				
				if (value instanceof GeometryProperty<?> || value instanceof Geometry) {
					//XXX other check, e.g. for constraints?
					//FIXME currently duplicate of code some lines below!!!!
					
					String srsName;
					Geometry geom;
					if (value instanceof Geometry) {
						geom = (Geometry) value;
						srsName = null;
					}
					else {
						geom = ((GeometryProperty<?>) value).getGeometry();
						srsName = null; //TODO
//						CRSDefinition def = ((GeometryProperty<?>) value).getCRSDefinition();
//						if (def != null) {
//							srsName = def.getCRS().getName().toString();
//						}
					}
					
					// write geometry
					writeGeometry(geom, propDef, srsName); //FIXME getCommonSRSName());
				}
				else {
					// simple element with value
					// write value as content
					writeElementValue(value, propDef);
				}
				
				writer.writeEndElement();
			}
		}
		else {
			// children and maybe a value
			
			GmlWriterUtil.writeStartElement(writer, propDef.getName());
			
			boolean hasValue = propDef.getPropertyType().getConstraint(
					HasValueFlag.class).isEnabled();
			
			// handle about annotated geometries
			if (!hasValue && (value instanceof Geometry || value instanceof GeometryProperty<?>)) {
				//XXX what about collections of geometries?
				//XXX other check, e.g. for constraints?
				
				String srsName;
				Geometry geom;
				if (value instanceof Geometry) {
					geom = (Geometry) value;
					srsName = null;
				}
				else {
					geom = ((GeometryProperty<?>) value).getGeometry();
					srsName = null; //TODO
//					CRSDefinition def = ((GeometryProperty<?>) value).getCRSDefinition();
//					if (def != null) {
//						srsName = def.getCRS().getName().toString();
//					}
				}
				
				// write geometry
				writeGeometry(geom, propDef, srsName); //FIXME getCommonSRSName());
			}
			else {
				// write all children (no elements if there is a value)
				writeProperties(group, group.getDefinition(), !hasValue);
				
				// write value
				if (hasValue) {
					writeElementValue(value, propDef);
				}
			}
			
			writer.writeEndElement();
		}
	}

	/**
	 * Write an element value, either as element content or as <code>nil</code>. 
	 * @param value the element value
	 * @param propDef the property definition the value is associated to
	 * @throws XMLStreamException if an error occurs writing the value
	 */
	private void writeElementValue(Object value, PropertyDefinition propDef) throws XMLStreamException {
		if (value == null) {
			// null value
			if (!propDef.getConstraint(NillableFlag.class).isEnabled()) {
				log.warn("Non-nillable element " + propDef.getName() + " is null"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			else {
				// nillable -> we may mark it as nil
				writer.writeAttribute(SCHEMA_INSTANCE_NS, "nil", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		else {
			// write value as content
			writer.writeCharacters(SimpleTypeUtil.convertToXml(value, 
					propDef.getPropertyType()));
		}
	}

//	/**
//	 * Write any attributes for simple type elements
//	 * 
//	 * @param property the property of the simple type element
//	 * @param propDef the property definition of the simple type element
//	 * @throws XMLStreamException if an error occurs writing the attributes
//	 */
//	private void writeSimpleTypeAttributes(Property property,
//			PropertyDefinition propDef) throws XMLStreamException {
//		if (property != null && propDef.isElement() // only elements may have properties
//				&& !(property instanceof AttributeProperty)) { //XXX this is a dirty hack - find a better solution
//			Collection<Property> properties = FeatureInspector.getProperties(property);
//			if (properties != null && !properties.isEmpty()) {
//				//XXX create dummy attribute for writeProperties TODO better: FeatureInspector must support Property
//				ComplexAttribute ca = new ComplexAttributeImpl(properties, GMLSchema.ABSTRACTSTYLETYPE_TYPE, null);
//				writeProperties(ca, propDef.getAttributeType(), false);
//			}
//		}
//	}

	/**
	 * Write a geometry
	 * 
	 * @param geometry the geometry
	 * @param property the geometry property
	 * @param srsName the common SRS name, may be <code>null</code> 
	 * @throws XMLStreamException if an error occurs writing the geometry  
	 */
	private void writeGeometry(Geometry geometry, PropertyDefinition property, 
			String srsName) throws XMLStreamException {
		// write geometries
		getGeometryWriter().write(writer, geometry, property, srsName);
	}

	/**
	 * Get the geometry writer
	 * 
	 * @return the geometry writer instance to use 
	 */
	protected StreamGeometryWriter getGeometryWriter() {
		if (geometryWriter == null) {
			boolean simplifyGeometry;
			try {
				simplifyGeometry = Boolean.parseBoolean(getParameter(PARAM_SIMPLIFY_GEOMETRY));
			} catch (Throwable e) {
				// default to true
				simplifyGeometry = true;
			}
			
			geometryWriter = StreamGeometryWriter.getDefaultInstance(gmlNs,
					simplifyGeometry);
		}
		
		return geometryWriter;
	}
	
	/**
	 * Write a property attribute
	 * 
	 * @param value the attribute value, may be <code>null</code>
	 * @param propDef the associated property definition
	 * @throws XMLStreamException if writing the attribute fails 
	 */
	private void writeAttribute(Object value, 
			PropertyDefinition propDef) throws XMLStreamException {
		GmlWriterUtil.writeAttribute(writer, value, propDef);
	}

}
