/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Component    : HALE
 * Created on   : Jun 3, 2009 -- 4:50:10 PM
 */
package eu.esdihumboldt.hale.io.xsd.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroup;
import org.apache.ws.commons.schema.XmlSchemaAttributeGroupRef;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexContentExtension;
import org.apache.ws.commons.schema.XmlSchemaComplexContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaContent;
import org.apache.ws.commons.schema.XmlSchemaContentModel;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaExternal;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.io.xsd.XmlSchemaIO;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.SuperTypeBinding;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.internal.Messages;
import eu.esdihumboldt.hale.io.xsd.reader.internal.AnonymousXmlType;
import eu.esdihumboldt.hale.io.xsd.reader.internal.HumboldtURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.ProgressURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.TypeUtil;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttribute;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttributeGroup;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttributeGroupReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttributeReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlElement;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlElementReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.schema.model.Group;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.property.CardinalityConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.property.ChoiceFlag;
import eu.esdihumboldt.hale.schema.model.constraints.property.NillableFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.AbstractFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.BindingConstraint;
import eu.esdihumboldt.hale.schema.model.constraints.type.SimpleFlag;
import eu.esdihumboldt.hale.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.schema.model.impl.DefaultPropertyDefinition;

/**
 * The main functionality of this class is to load an XML schema file (XSD)
 * and create a {@link Schema} with {@link TypeDefinition}s. This implementation
 * is based on the Apache XmlSchema library 
 * ({@link "http://ws.apache.org/commons/XmlSchema/"}).
 * 
 * It is necessary use this library instead of the GeoTools XML schema loader, 
 * because the GeoTools version cannot handle GML 3.2 based files.
 * 
 * @author Simon Templer
 * @author Bernd Schneiders
 * @author Thorsten Reitz
 */
public class XmlSchemaReader 
	extends AbstractSchemaReader {
	
	/**
	 * The log
	 */
	private static ALogger _log = ALoggerFactory.getLogger(XmlSchemaReader.class);
	
	/**
	 * The XML definition index
	 */
	private XmlIndex index;
	
	/**
	 * The current reporter
	 */
	private IOReporter reporter;
	
	/**
	 * @see SchemaReader#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return index;
	}

	/**
	 * @see IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin(Messages.getString("ApacheSchemaProvider.21"), ProgressIndicator.UNKNOWN); //$NON-NLS-1$
		this.reporter = reporter;
		
		//XXX cache should be used through supplier mechanism
//		try {
//			is = Request.getInstance().get(location);
//		} catch (Exception e) {
//			is = locationURL.openStream();
//		}
		
		XmlSchema xmlSchema = null;
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		// Check if the file is located on web
		URI location = getSource().getLocation();
		if (location.getHost() == null) {
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location));
		} else if (location.getScheme().equals("bundleresource")) { //$NON-NLS-1$
			schemaCol.setSchemaResolver(new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location) + "/"); //$NON-NLS-1$
		}
		else {
			URIResolver resolver = schemaCol.getSchemaResolver();
			schemaCol.setSchemaResolver(new ProgressURIResolver((resolver == null)?(new DefaultURIResolver()):(resolver), progress));
		}
		
		InputStream is = getSource().getInput();
		xmlSchema = schemaCol.read(new StreamSource(is), null);
		is.close();

		String namespace = xmlSchema.getTargetNamespace();
		if (namespace == null) {
			namespace = XMLConstants.NULL_NS_URI;
		}
		
		xmlSchema.setSourceURI(location.toString());
		NamespacePrefixList namespaces = xmlSchema.getNamespaceContext();
		Map<String, String> prefixes = new HashMap<String, String>();
		for (String prefix : namespaces.getDeclaredPrefixes()) {
			prefixes.put(namespaces.getNamespaceURI(prefix), prefix);
		}
		
		// create index
		index = new XmlIndex(namespace, location);
		
		Set<String> imports = new HashSet<String>();
		imports.add(location.toString());

		loadSchema(location.toString(), xmlSchema, imports, progress);

//		Map<String, SchemaElement> elements = new HashMap<String, SchemaElement>();
//		for (SchemaElement element : schemaResult.getElements().values()) {
//			if (element.getType() != null) {
//				if (element.getType().isComplexType()) {
//					elements.put(element.getIdentifier(), element);
//				}
//			}
//			else {
//				_log.warn(NO_DEFINITION, "No type definition for element " + element.getElementName().getLocalPart()); //$NON-NLS-1$
//			}
//		}
//
//		Schema result = new Schema(elements, namespace, locationURL, prefixes);
//		
//		Map<Name, SchemaElement> allElements = new HashMap<Name, SchemaElement>();
//		Map<Name, TypeDefinition> allTypes = new HashMap<Name, TypeDefinition>();
//		
//		allElements.putAll(schemaResult.getElements());
//		allTypes.putAll(schemaResult.getTypes());
//		
//		for (SchemaResult sr : imports.values()) {
//			allElements.putAll(sr.getElements());
//			allTypes.putAll(sr.getTypes());
//		}
//		
//		result.setAllElements(allElements);
//		result.setAllTypes(allTypes);
		
		return reporter;
	}
	
	/**
	 * Load the feature types defined by the given schema
	 * 
	 * @param schemaLocation the schema location 
	 * @param xmlSchema the schema
	 * @param imports the imports/includes that were already
	 *   loaded or where loading has been started
	 * @param progress the progress indicator
	 */
	protected void loadSchema(String schemaLocation, XmlSchema xmlSchema, 
			Set<String> imports, ProgressIndicator progress) {
		String namespace = xmlSchema.getTargetNamespace();
		if (namespace == null) {
			namespace = XMLConstants.NULL_NS_URI;
		}
	
		// the schema items
		XmlSchemaObjectCollection items = xmlSchema.getItems();
		
		// go through all schema items
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);
			
			if (item instanceof XmlSchemaElement) {
				XmlSchemaElement element = (XmlSchemaElement) item;
				// determine type
				TypeDefinition elementType = null;
				
				if (element.getSchemaTypeName() != null) {
					// reference to type
					elementType = index.getType(element.getSchemaTypeName());
				}
				else if (element.getSchemaType() != null) {
					// element has internal type definition, generate anonymous type name
					QName typeName = new QName(element.getQName().getNamespaceURI(),
							element.getQName().getLocalPart() + "_AnonymousType"); //$NON-NLS-1$
					// create type
					elementType = createType(element.getSchemaType(), typeName,
							schemaLocation, namespace);
				}
				else if (element.getQName() != null) {
					// reference to type
					elementType = index.getType(element.getQName()); //XXX this really a type???
				}
				//XXX what about element.getRefName()? 
				
				if (elementType != null) {
					// the element name
					QName elementName = new QName(namespace, element.getName()); //XXX use element QName instead?
					// the substitution group
					QName subGroup = element.getSubstitutionGroup();
					
					// create schema element
					XmlElement schemaElement = new XmlElement(elementName, 
							elementType, subGroup);
					
					// set metadata
					setMetadata(schemaElement, element, schemaLocation);
					
					//TODO set constraints? (e.g. Mappable)
					//TODO extend SchemaElement constraint
					
					// store element in index
					index.getElements().put(elementName, schemaElement);
				} else {
					reporter.error(new IOMessageImpl(MessageFormat.format(
							"No type for element {0} found.", element.getName()), 
							null, element.getLineNumber(), element.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaType) {
				// complex or simple type
				createType((XmlSchemaType) item, null, schemaLocation,
						namespace);
			}
			else if (item instanceof XmlSchemaAttribute) {
				// schema attribute that might be referenced somewhere
				XmlSchemaAttribute att = (XmlSchemaAttribute) item;
				if (att.getQName() != null) {
					XmlTypeDefinition type = getAttributeType(att, null, schemaLocation);
					if (type == null) {
						//XXX if this occurs we might need a attribute referencing attribute
						throw new IllegalStateException("Could not determine attribute type");
					}
					XmlAttribute attribute = new XmlAttribute(att.getQName(), type);
					
					index.getAttributes().put(attribute.getName(), attribute);
				}
				else {
					reporter.warn(new IOMessageImpl(MessageFormat.format(
							"Attribute could not be processed: {0}", att.getName()), 
							null, att.getLineNumber(), att.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaAttributeGroup) {
				// schema attribute group that might be referenced somewhere
				XmlSchemaAttributeGroup attributeGroup = (XmlSchemaAttributeGroup) item;
				if (attributeGroup.getName() != null) {
					XmlAttributeGroup attGroup = new XmlAttributeGroup();
					createAttributes(attributeGroup, attGroup, "", schemaLocation, namespace);
					index.getAttributeGroups().put(attributeGroup.getName(), attGroup);
				}
				else {
					reporter.warn(new IOMessageImpl(
							"Attribute group could not be processed", 
							null, attributeGroup.getLineNumber(), attributeGroup.getLinePosition()));
				}
			}
		}
		
		// Set of include locations
		Set<String> includes = new HashSet<String>();
		
		// handle imports
		XmlSchemaObjectCollection externalItems = xmlSchema.getIncludes();
		if (externalItems.getCount() > 0) {
			_log.info("Loading includes and imports for schema at " + schemaLocation); //$NON-NLS-1$
		}
		
		for (int i = 0; i < externalItems.getCount(); i++) {
			try {
				XmlSchemaExternal imp = (XmlSchemaExternal) externalItems.getItem(i);
				XmlSchema importedSchema = imp.getSchema();
				String location = importedSchema.getSourceURI();
				if (!(imports.contains(location))) { // only add schemas that were not already added
					imports.add(location); // place a marker in the map to prevent loading the location in the call to loadSchema 
					loadSchema(location, importedSchema, imports, progress);
				}
				if (imp instanceof XmlSchemaInclude) {
					includes.add(location);
				}
			} catch (Throwable e) {
				_log.error("Error adding imported schema", e); //$NON-NLS-1$
			}
		}
		
		_log.info("Creating types for schema at " + schemaLocation); //$NON-NLS-1$
		
		progress.setCurrentTask(MessageFormat.format(
				Messages.getString("ApacheSchemaProvider.33"), namespace)); //$NON-NLS-1$
	}

	/**
	 * Create a type definition from the given schema type and add it to the
	 * index or enhance an existing type definition if it is already in the
	 * index.
	 * 
	 * @param schemaType the schema type
	 * @param typeName the type name to use for the type, <code>null</code>
	 *   if the name of the schema type shall be used
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the schema namespace
	 * @return the created type
	 */
	private XmlTypeDefinition createType(XmlSchemaType schemaType, QName typeName,
			String schemaLocation, String schemaNamespace) {
		if (typeName == null) {
			typeName = schemaType.getQName();
		}
		
		// get type definition from index
		XmlTypeDefinition type = index.getType(typeName);
		
		if (schemaType instanceof XmlSchemaSimpleType) {
			// attribute type from simple schema types
			configureSimpleType(type, (XmlSchemaSimpleType) schemaType,
					schemaLocation);
		}
		else if (schemaType instanceof XmlSchemaComplexType) {
			XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaType;
			
			// determine the super type name
			QName superTypeName = getSuperTypeName(complexType);
			
			// determine if the super type relation is a restriction
			boolean isRestriction = isRestriction(complexType);
			type.setConstraint((isRestriction)?(RestrictionFlag.ENABLED):(RestrictionFlag.DISABLED));
			
			if (superTypeName != null) {
				// get super type from index
				XmlTypeDefinition superType = index.getType(superTypeName);
				type.setSuperType(superType);
				
				//XXX reuse the super type's attribute type where appropriate?
			}
			
			// set type metadata and constraints
			setMetadataAndConstraints(type, complexType, schemaLocation);
			
			// determine the defined properties and add them to the declaring type
			createProperties(type, complexType, schemaLocation, schemaNamespace);
		}
		else {
			reporter.warn(new IOMessageImpl("Unrecognized schema type", null,
					schemaType.getLineNumber(), schemaType.getLinePosition()));
		}
		
		return type;
	}

	/**
	 * Configure a type definition for a simple type 
	 * 
	 * @param type the type definition
	 * @param schemaType the schema simple type
	 * @param schemaLocation the schema location
	 */
	private void configureSimpleType(XmlTypeDefinition type,
			XmlSchemaSimpleType schemaType, String schemaLocation) {
		TypeUtil.configureSimpleType(type, schemaType, index, reporter);
		
		// set metadata
		setMetadata(type, schemaType, schemaLocation);
	}

	private URI createLocationURI(String schemaLocation,
			XmlSchemaObject schemaObject) {
		//XXX improve
		try {
			return new URI(schemaLocation + "#" + schemaObject.getLineNumber() + 
					":" + schemaObject.getLinePosition());
		} catch (URISyntaxException e) {
			// ignore
			return null;
		}
	}

	/**
	 * @see AbstractIOProvider#getDefaultContentType()
	 */
	@Override
	protected ContentType getDefaultContentType() {
		return XmlSchemaIO.XSD_CT;
	}
	
	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param declaringGroup the definition of the declaring group
	 * @param particle the particle
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the schema namespace
	 * @param forceGroup force creating a group (e.g. if the parent is a choice)
	 */
	private void createPropertiesFromParticle(Group declaringGroup, 
			XmlSchemaParticle particle, String schemaLocation, 
			String schemaNamespace, boolean forceGroup) {
		// particle:
		if (particle instanceof XmlSchemaSequence) {
			// <sequence>
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			
			// create group only if necessary (sequences that appear exactly once will result in no group if not forced)
			if (forceGroup || sequence.getMinOccurs() != 1 || sequence.getMaxOccurs() != 1) {
				// create a sequence group
				String sequenceName = sequence.getId();
				if (sequenceName == null || sequenceName.isEmpty()) {
					sequenceName = UUID.randomUUID().toString(); //TODO improve name
				}
				DefaultGroupPropertyDefinition sequenceGroup = new DefaultGroupPropertyDefinition(
						new QName(sequenceName), declaringGroup);
				// set cardinality
				long max = (sequence.getMaxOccurs() == Long.MAX_VALUE)?(CardinalityConstraint.UNBOUNDED):(sequence.getMaxOccurs());
				sequenceGroup.setConstraint(CardinalityConstraint.getCardinality(
						sequence.getMinOccurs(), max ));
				// set choice constraint (no choice)
				sequenceGroup.setConstraint(ChoiceFlag.DISABLED);
				// set metadata
				setMetadata(sequenceGroup, sequence, schemaLocation);
				
				// use group as parent
				declaringGroup = sequenceGroup;
			}
			
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					createPropertyFromElement((XmlSchemaElement) object, 
							declaringGroup, schemaLocation, schemaNamespace);
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice
					// content doesn't need to be grouped, it can be decided in the method
					createPropertiesFromParticle(declaringGroup, 
							(XmlSchemaParticle) object, schemaLocation,
							schemaNamespace, false);
				}
			}
			// </sequence>
		}
		else if (particle instanceof XmlSchemaChoice) {
			// <choice>
			XmlSchemaChoice choice = (XmlSchemaChoice) particle;
			
			// create a choice group
			String choiceName = choice.getId();
			if (choiceName == null || choiceName.isEmpty()) {
				choiceName = UUID.randomUUID().toString(); //TODO improve name
			}
			DefaultGroupPropertyDefinition choiceGroup = new DefaultGroupPropertyDefinition(
					new QName(choiceName), declaringGroup);
			// set cardinality
			long max = (choice.getMaxOccurs() == Long.MAX_VALUE)?(CardinalityConstraint.UNBOUNDED):(choice.getMaxOccurs());
			choiceGroup.setConstraint(CardinalityConstraint.getCardinality(
					choice.getMinOccurs(), max ));
			// set choice constraint
			choiceGroup.setConstraint(ChoiceFlag.ENABLED);
			// set metadata
			setMetadata(choiceGroup, choice, schemaLocation);
			
			// create properties with choiceGroup as parent
			for (int j = 0; j < choice.getItems().getCount(); j++) {
				XmlSchemaObject object = choice.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					createPropertyFromElement((XmlSchemaElement) object, 
							choiceGroup, schemaLocation, schemaNamespace);
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice or sequence
					// inside a choice they must form a group
					createPropertiesFromParticle(choiceGroup, 
							(XmlSchemaParticle) object, schemaLocation,
							schemaNamespace, true);
				}
			}
			// </choice>
		}
	}

	/**
	 * Create a property from an element
	 * 
	 * @param element the schema element
	 * @param declaringGroup the definition of the declaring group
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the schema namespace
	 */
	private void createPropertyFromElement(XmlSchemaElement element, 
			Group declaringGroup, String schemaLocation, String schemaNamespace) {
		if (element.getSchemaTypeName() != null) {
			// element referencing a type
			// <element name="ELEMENT_NAME" type="SCHEMA_TYPE_NAME" />
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(
					element.getQName(), declaringGroup, 
					index.getType(element.getSchemaTypeName()));
			
			// set metadata and constraints
			setMetadataAndConstraints(property, element, schemaLocation);
		}
		else if (element.getRefName() != null) {
			// references another element
			// <element ref="REF_NAME" />
			QName elementName = element.getRefName();
			if (elementName.getNamespaceURI() == null || elementName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
				// if no namespace is defined use the schema namespace for the element reference
				// the schema namespace may be the empty namespace but it is OK in that case
				//XXX is this also ok if elementFormDefault="unqualified" ?
				elementName = new QName(schemaNamespace, elementName.getLocalPart());
			}
			
			XmlElementReferenceProperty property = new XmlElementReferenceProperty(
					elementName, declaringGroup, index, elementName);
			
			// set metadata and constraints FIXME can the constraints be set at this point? or must the property determine them from the SchemaElement?
			setMetadataAndConstraints(property, element, schemaLocation);
		}
		else if (element.getSchemaType() != null) {
			// element w/o type name or reference but an internal type definition
			if (element.getSchemaType() instanceof XmlSchemaComplexType) {
				// <element ...>
				//   <complexType>
				XmlSchemaComplexType complexType = (XmlSchemaComplexType) element.getSchemaType();
				XmlSchemaContentModel model = complexType.getContentModel();
				XmlSchemaParticle particle = complexType.getParticle();
				if (model != null) {
					XmlSchemaContent content = model.getContent();
					
					QName superTypeName = null;
					if (content instanceof XmlSchemaComplexContentExtension || 
							content instanceof XmlSchemaComplexContentRestriction) {
						// <complexContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaComplexContentExtension) {
							superTypeName = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							superTypeName = ((XmlSchemaComplexContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (superTypeName != null) {
							// try to get the type definition of the super type
							XmlTypeDefinition superType = index.getType(superTypeName);
							
							// create an anonymous type that extends the super type
							QName anonymousName = new QName(
									getTypeIdentifier(declaringGroup) + "/" + element.getName(), 
									superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							
							AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
							anonymousType.setSuperType(superType);
							
							// set metadata and constraints
							setMetadataAndConstraints(anonymousType, complexType, schemaLocation);
							
							// add properties to the anonymous type
							createProperties(anonymousType, complexType,
									schemaLocation, schemaNamespace);
							
							// create a property with the anonymous type
							DefaultPropertyDefinition property = new DefaultPropertyDefinition(
									element.getQName(), declaringGroup, anonymousType);
							
							// set metadata and constraints
							setMetadataAndConstraints(property, element, schemaLocation);
						}
						else {
							reporter.error(new IOMessageImpl(
									"Could not determine super type for complex content", 
									null, content.getLineNumber(), content.getLinePosition()));
						}
						
						//   </extension> / </restriction>
						// </complexContent>
					} else if (content instanceof XmlSchemaSimpleContentExtension
							|| content instanceof XmlSchemaSimpleContentRestriction) { 
						// <simpleContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaSimpleContentExtension) {
							superTypeName = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							superTypeName = ((XmlSchemaSimpleContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (superTypeName != null) {
							// try to get the type definition of the super type
							XmlTypeDefinition superType = index.getType(superTypeName);
							
							// create an anonymous type that extends the super type
							QName anonymousName = new QName(
									getTypeIdentifier(declaringGroup) + "/" + element.getName(), 
									superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							
							AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
							anonymousType.setSuperType(superType);
							
							// set metadata and constraints
							setMetadata(anonymousType, complexType, schemaLocation);
							anonymousType.setConstraint(SimpleFlag.ENABLED);
							// set super type binding
							//XXX is this ok? 
							anonymousType.setConstraint(new SuperTypeBinding(anonymousType));
							
							// add properties to the anonymous type
							createProperties(anonymousType, complexType,
									schemaLocation, schemaNamespace);
							
							// create a property with the anonymous type
							DefaultPropertyDefinition property = new DefaultPropertyDefinition(
									element.getQName(), declaringGroup, anonymousType);
							
							// set metadata and constraints
							setMetadataAndConstraints(property, element, schemaLocation);
						}
						else {
							reporter.error(new IOMessageImpl(
									"Could not determine super type for simple content", 
									null, content.getLineNumber(), content.getLinePosition()));
						}
						
						//   </extension>
						// </simpleContent>
					}
				} else if (particle != null) {
					// this where we get when there is an anonymous complex type as property type
					// create an anonymous type
					QName anonymousName = new QName(
							getTypeIdentifier(declaringGroup) + "/" + element.getName(), 
							"AnonymousType");
					
					// create anonymous type with no super type
					AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
					
					// set metadata and constraints
					setMetadataAndConstraints(anonymousType, complexType, schemaLocation);
					
					// add properties to the anonymous type
					createProperties(anonymousType, complexType,
							schemaLocation, schemaNamespace);
					
					// create a property with the anonymous type
					DefaultPropertyDefinition property = new DefaultPropertyDefinition(
							element.getQName(), declaringGroup, anonymousType);
					
					// set metadata and constraints
					setMetadataAndConstraints(property, element, schemaLocation);
				}
				//   </complexType>
				// </element>
			}
			else if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
				// simple schema type
				XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) element.getSchemaType();
				
				// create an anonymous type
				QName anonymousName = new QName(
						getTypeIdentifier(declaringGroup) + "/" + element.getName(), 
						"AnonymousType"); //$NON-NLS-1$
				
				AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
				
				configureSimpleType(anonymousType, simpleType, schemaLocation);
			}
		}
	}

	/**
	 * Get a type identifier from the given group. If the group is a type
	 * definition its identifier will be returned, if it is a child definition
	 * the identifier of the parent type will be returned.
	 * 
	 * @param group the group
	 * @return the type identifier
	 * @throws IllegalArgumentException if the group is neither a type nor a
	 *   child definition
	 */
	private static String getTypeIdentifier(Group group) throws IllegalArgumentException {
		if (group instanceof TypeDefinition) {
			return ((TypeDefinition) group).getIdentifier();
		}
		else if (group instanceof ChildDefinition<?>) {
			return ((ChildDefinition<?>) group).getParentType().getIdentifier();
		}
		
		return null;
	}

	/**
	 * Set metadata and constraints for a complex type
	 * 
	 * @param type the type definition
	 * @param complexType the complex type definition
	 * @param schemaLocation the schema location
	 */
	private void setMetadataAndConstraints(XmlTypeDefinition type,
			XmlSchemaComplexType complexType, String schemaLocation) {
		//TODO type constraints!
		type.setConstraint(BindingConstraint.getBinding(Instance.class)); //XXX instead object binding?
		type.setConstraint(AbstractFlag.get(complexType.isAbstract()));
		type.setConstraint(SimpleFlag.DISABLED);
		
		// set metadata
		setMetadata(type, complexType, schemaLocation);
	}

	/**
	 * Set the metadata for a definition
	 * 
	 * @param definition the definition
	 * @param annotated the XML annotated object
	 * @param schemaLocation the schema location
	 */
	private void setMetadata(AbstractDefinition<?> definition,
			XmlSchemaAnnotated annotated, String schemaLocation) {
		definition.setDescription(XmlSchemaIO.getDescription(annotated));
		definition.setLocation(createLocationURI(schemaLocation, annotated));
	}

	/**
	 * Set metadata and constraints for a property based on a XML element
	 * 
	 * @param property the property
	 * @param element the XML element
	 * @param schemaLocation the schema location
	 */
	private void setMetadataAndConstraints(DefaultPropertyDefinition property,
			XmlSchemaElement element, String schemaLocation) {
		// set constraints
		property.setConstraint(NillableFlag.get(element.isNillable()));
		long max = (element.getMaxOccurs() == Long.MAX_VALUE)?(CardinalityConstraint.UNBOUNDED):(element.getMaxOccurs());
		property.setConstraint(CardinalityConstraint.getCardinality(
				element.getMinOccurs(), max ));
		
		// set metadata
		setMetadata(property, element, schemaLocation);
	}

	/**
	 * Find a super type name based on a complex type
	 * 
	 * @param item the complex type defining a super type
	 * @return the name of the super type or <code>null</code>
	 */
	private QName getSuperTypeName(XmlSchemaComplexType item) {
		QName qname = null;
		
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();
			
			if (content instanceof XmlSchemaComplexContentExtension) {
				qname = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaComplexContentRestriction) { // restriction
				qname = ((XmlSchemaComplexContentRestriction) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				qname = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
			}
			else if (content instanceof XmlSchemaSimpleContentRestriction) { // restriction
				qname = ((XmlSchemaSimpleContentRestriction) content).getBaseTypeName();
			}
		}
		
		return qname;
	}
	
	/**
	 * Determines if the super type relation of the given item is a restriction
	 * 
	 * @param item the complex type defining a super type
	 * 
	 * @return if the super type relation of the given item is a restriction
	 */
	private boolean isRestriction(XmlSchemaComplexType item) {
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();
			if (content instanceof XmlSchemaComplexContentRestriction || content instanceof XmlSchemaSimpleContentRestriction) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Create the properties for the given complex type
	 * 
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the scheme namspace
	 */
	private void createProperties(XmlTypeDefinition typeDef, 
			XmlSchemaComplexType item, String schemaLocation, String schemaNamespace) {
		// item:
		// <complexType ...>
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null ) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				// <complexContent>
				//   <extension base="...">
				XmlSchemaComplexContentExtension extension = (XmlSchemaComplexContentExtension) content;
				// particle (e.g. sequence)
				if (extension.getParticle() != null) {
					XmlSchemaParticle particle = extension.getParticle();
					createPropertiesFromParticle(typeDef, particle, 
							schemaLocation, schemaNamespace, false);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, 
							null, schemaLocation, schemaNamespace);
				}
				//   </extension>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaComplexContentRestriction) {
				// <complexContent>
				//   <restriction base="...">
				XmlSchemaComplexContentRestriction restriction = (XmlSchemaComplexContentRestriction) content;
				// particle (e.g. sequence)
				if (restriction.getParticle() != null) {
					XmlSchemaParticle particle = restriction.getParticle();
					createPropertiesFromParticle(typeDef, particle, 
							schemaLocation, schemaNamespace, false);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, 
							null, schemaLocation, schemaNamespace);
				}
				//   </restriction>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				// <simpleContent>
				//   <extension base="...">
				XmlSchemaSimpleContentExtension extension = (XmlSchemaSimpleContentExtension) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, 
							null, schemaLocation, schemaNamespace);
				}
				//   </extension>
				// </simpleContent>
			}
			else if (content instanceof XmlSchemaSimpleContentRestriction) {
				// <simpleContent>
				//   <restriction base="...">
				XmlSchemaSimpleContentRestriction restriction = (XmlSchemaSimpleContentRestriction) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, 
							null, schemaLocation, schemaNamespace);
				}
				//   </restriction>
				// </simpleContent>
			}
		}
		else {
			// no complex content (instead e.g. <sequence>)
			XmlSchemaComplexType complexType = item;
			// particle (e.g. sequence)
			if (item.getParticle() != null) {
				XmlSchemaParticle particle = complexType.getParticle();
				createPropertiesFromParticle(typeDef, particle, schemaLocation,
						schemaNamespace, false);
			}
			// attributes
			XmlSchemaObjectCollection attributeCollection = complexType.getAttributes();
			if (attributeCollection != null) {
				createAttributesFromCollection(attributeCollection, typeDef, 
						null, schemaLocation, schemaNamespace);
			}
		}
		
		// </complexType>
	}
	
	private void createAttributesFromCollection(
			XmlSchemaObjectCollection attributeCollection, Group declaringType,
			String indexPrefix, String schemaLocation, String schemaNamespace) {
		if (indexPrefix == null) {
			indexPrefix = ""; //$NON-NLS-1$
		}
		
		for (int index = 0; index < attributeCollection.getCount(); index++) {
			XmlSchemaObject object = attributeCollection.getItem(index);
			if (object instanceof XmlSchemaAttribute) {
				// <attribute ... />
				XmlSchemaAttribute attribute = (XmlSchemaAttribute) object;
				
				createAttribute(attribute, declaringType, schemaLocation, 
						schemaNamespace);
			}
			else if (object instanceof XmlSchemaAttributeGroup) {
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) object;
				
				createAttributes(group, declaringType, indexPrefix + index,
						schemaLocation, schemaNamespace);
			}
			else if (object instanceof XmlSchemaAttributeGroupRef) {
				XmlSchemaAttributeGroupRef groupRef = (XmlSchemaAttributeGroupRef) object;
				
				if (groupRef.getRefName() != null) {
					QName groupName = groupRef.getRefName();
					//XXX extend group name with namespace?
					XmlAttributeGroupReferenceProperty property = new XmlAttributeGroupReferenceProperty(
							groupName, declaringType, this.index, groupName);
					//TODO add constraints?
					
					// set metadata
					setMetadata(property, groupRef, schemaLocation);
				}
				else {
					reporter.error(new IOMessageImpl(
							"Unrecognized attribute group reference", null,
							object.getLineNumber(), object.getLinePosition()));
				}
			}
		}
	}

	private void createAttributes(XmlSchemaAttributeGroup group, 
			Group declaringType, String index, String schemaLocation, 
			String schemaNamespace) {
		createAttributesFromCollection(group.getAttributes(), 
				declaringType, index + "_", schemaLocation, schemaNamespace); //$NON-NLS-1$
	}

	private void createAttribute(XmlSchemaAttribute attribute, 
			Group declaringType, String schemaLocation, 
			String schemaNamespace) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			// resolve type by name
			XmlTypeDefinition type = this.index.getType(typeName);
			
			// create property
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(
					attribute.getQName(), declaringType, type);
			
			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		else if (attribute.getSchemaType() != null) {
			QName name = attribute.getSchemaType().getQName();
			XmlTypeDefinition attType = this.index.getType(name);
			
			// attribute type from simple schema types
			configureSimpleType(attType, attribute.getSchemaType(),
					schemaLocation);
			
			// create property
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(
					attribute.getQName(), declaringType, attType);
			
			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		else if (attribute.getRefName() != null) {
			// <attribute ref="REF_NAME" />
			// reference to a named attribute
			QName attName = attribute.getRefName();
			if (attName.getNamespaceURI() == null || attName.getNamespaceURI().equals(XMLConstants.NULL_NS_URI)) {
				// if no namespace is defined use the schema namespace for the attribute reference
				// the schema namespace may be the empty namespace but it is OK in that case
				//XXX is this also ok if attributeFormDefault="unqualified" ? XXX seems to be as it is also needed in that case
				attName = new QName(schemaNamespace, attName.getLocalPart());
			}
			
			XmlAttributeReferenceProperty property = new XmlAttributeReferenceProperty(
					attName, declaringType, this.index, 
					attName);
			
			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		
	}
	
	/**
	 * Create the type definition for an attribute, if possible
	 * 
	 * @param attribute the XML attribute
	 * @param index the name index string
	 * @param schemaLocation the schema location
	 * @return the type definition or <code>null</code>
	 */
	private XmlTypeDefinition getAttributeType(XmlSchemaAttribute attribute, 
			String index, String schemaLocation) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			// resolve type by name
			return this.index.getType(typeName);
		}
		else if (attribute.getSchemaType() != null) {
			QName name = attribute.getSchemaType().getQName();
			XmlTypeDefinition attType = this.index.getType(name);
			
			// attribute type from simple schema types
			configureSimpleType(attType, attribute.getSchemaType(),
					schemaLocation);
		}
		else if (attribute.getRefName() != null) {
			// <attribute ref="REF_NAME" />
			// reference to a named attribute
			// can't create type
			return null;
		}
		
		return null;
	}

	/**
	 * Set metadata and constraints for a property based on a XML attribute
	 * 
	 * @param property the property
	 * @param attribute the XML attribute
	 * @param schemaLocation the schema location
	 */
	private void setMetadataAndConstraints(DefaultPropertyDefinition property,
			XmlSchemaAttribute attribute, String schemaLocation) {
		// set constraints
		property.setConstraint(XmlAttributeFlag.ENABLED);
		
		if (attribute.getUse() != null) {
			long maxOccurs = (attribute.getUse().getValue().equals(Constants.BlockConstants.PROHIBITED))?(0):(1);
			long minOccurs = (attribute.getUse().getValue().equals(Constants.BlockConstants.REQUIRED))?(1):(0);
			property.setConstraint(CardinalityConstraint.getCardinality(minOccurs, maxOccurs));
		}
		
		property.setConstraint(NillableFlag.DISABLED);
		
		// set metadata
		setMetadata(property, attribute, schemaLocation);
	}

	/**
	 * Get the base URI for the given URI
	 * 
	 * @param uri the URI
	 * 
	 * @return the base URI as string
	 */
	private String findBaseUri(URI uri) {
		String baseUri = ""; //$NON-NLS-1$
		baseUri = uri.toString();
		if (baseUri.matches("^.*?\\/.+")) { //$NON-NLS-1$
			baseUri = baseUri.substring(0, baseUri.lastIndexOf("/")); //$NON-NLS-1$
		}
		_log.info("Base URI for schemas to be used: " + baseUri); //$NON-NLS-1$
		return baseUri;
	}
	
}



