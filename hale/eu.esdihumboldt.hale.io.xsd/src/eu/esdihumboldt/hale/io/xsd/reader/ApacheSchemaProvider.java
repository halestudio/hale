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
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
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
import org.apache.ws.commons.schema.XmlSchemaUse;
import org.apache.ws.commons.schema.resolver.DefaultURIResolver;
import org.apache.ws.commons.schema.resolver.URIResolver;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;

import de.cs3d.util.logging.AGroup;
import de.cs3d.util.logging.AGroupFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.core.io.ContentType;
import eu.esdihumboldt.hale.core.io.IOProvider;
import eu.esdihumboldt.hale.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.core.io.impl.LogProgressIndicator;
import eu.esdihumboldt.hale.core.io.report.IOReport;
import eu.esdihumboldt.hale.core.io.report.IOReporter;
import eu.esdihumboldt.hale.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.io.xsd.XMLSchemaIO;
import eu.esdihumboldt.hale.io.xsd.internal.Messages;
import eu.esdihumboldt.hale.io.xsd.reader.internal.HumboldtURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.ProgressURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.SchemaElement;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.schema.io.SchemaReader;
import eu.esdihumboldt.hale.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.schema.model.Schema;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.constraints.type.AbstractFlag;
import eu.esdihumboldt.hale.schema.model.constraints.type.BindingConstraint;
import eu.esdihumboldt.hale.schema.model.impl.DefaultSchema;
import eu.esdihumboldt.hale.schema.model.impl.DefaultTypeDefinition;

/**
 * The main functionality of this class is to load an XML schema file (XSD)
 * and create a FeatureType collection. This implementation is based on the
 * Apache XmlSchema library (http://ws.apache.org/commons/XmlSchema/). It is
 * necessary use this library instead of the GeoTools Xml schema loader, because
 * the GeoTools version cannot handle GML 3.2 based files.
 * 
 * @author Bernd Schneiders, Logica; Thorsten Reitz, Fraunhofer IGD;
 *   Simon Templer, Fraunhofer IGD
 */
public class ApacheSchemaProvider 
	extends AbstractSchemaReader {
	
	/**
	 * The log
	 */
	private static ALogger _log = ALoggerFactory.getLogger(ApacheSchemaProvider.class);
	
	private static final AGroup NO_DEFINITION = AGroupFactory.getGroup(Messages.getString("ApacheSchemaProvider.0"));  //$NON-NLS-1$
	
	private static final AGroup MISSING_ATTRIBUTE_REF = AGroupFactory.getGroup(Messages.getString("ApacheSchemaProvider.1")); //$NON-NLS-1$
	
	private XmlIndex index;
	
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
		// TODO Auto-generated method stub
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
		
		// use XML Schema to load schema with all its subschema to the memory
		
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
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			//XXX !this doesn't really work well if there is really no namespace!
			namespace = "http://www.opengis.net/gml"; //$NON-NLS-1$
		}
		
		xmlSchema.setSourceURI(location.toString());
		NamespacePrefixList namespaces = xmlSchema.getNamespaceContext();
		Map<String, String> prefixes = new HashMap<String, String>();
		for (String prefix : namespaces.getDeclaredPrefixes()) {
			prefixes.put(namespaces.getNamespaceURI(prefix), prefix);
		}
		
		//FIXME continue from here
		
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
	 * @return the map of feature type names and types
	 */
	protected void loadSchema(String schemaLocation, XmlSchema xmlSchema, 
			Set<String> imports, ProgressIndicator progress) {
		String namespace = xmlSchema.getTargetNamespace();
		if (namespace == null || namespace.isEmpty()) {
			// default to gml schema
			//FIXME
			namespace = "http://www.opengis.net/gml"; //$NON-NLS-1$
		}
	
		// type names for type definitions where is no element
//		Set<String> schemaTypeNames = new HashSet<String>();
		
		// the schema items
		XmlSchemaObjectCollection items = xmlSchema.getItems();
		
//		Map<XmlSchemaElement, Name> anonymousTypes = new HashMap<XmlSchemaElement, Name>();
		
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
							schemaLocation);
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
					SchemaElement schemaElement = new SchemaElement(elementName, 
							elementType, subGroup);
					
					// set metadata
					String description = XMLSchemaIO.getDescription(element);
					schemaElement.setDescription(description);
					schemaElement.setLocation(createLocationURI(schemaLocation, item));
					
					//TODO set constraints? (e.g. Mappable)
					
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
				createType((XmlSchemaType) item, null, schemaLocation);
			}
			else if (item instanceof XmlSchemaAttribute) {
				// schema attribute that might be referenced somewhere
				XmlSchemaAttribute att = (XmlSchemaAttribute) item;
				if (att.getQName() != null) {
					index.getAttributes().put(att.getQName(), att);
				}
				else {
					reporter.warn(new IOMessageImpl(MessageFormat.format(
							"Attribute could not be processed: {0}", att.getName()), 
							null, att.getLineNumber(), att.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaAttributeGroup) {
				// schema attribute group that might be referenced somewhere
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) item;
				if (group.getName() != null) {
					index.getAttributeGroups().put(group.getName(), group);
				}
				else {
					reporter.warn(new IOMessageImpl(
							"Attribute group could not be processed", 
							null, group.getLineNumber(), group.getLinePosition()));
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
		
		// 3rd pass: create feature types 
//		for (Name typeName : typeNames.getItems()) {
//			XmlSchemaObject item = typeDefinitions.get(typeName);
//	
//			if (item == null) {
//				_log.error("No definition for " + typeName.toString()); //$NON-NLS-1$
//			}
//			
//		}
	}

	/**
	 * Create a type definition from the given schema type and add it to the
	 * index or enhance an existing type definition if it is already in the
	 * index.
	 * 
	 * @param schemaType the schema type
	 * @param typeName the type name to use for the type, <code>null</code>
	 *   if the name of the schema type shall be used
	 * @return the created type definition
	 */
	private TypeDefinition createType(XmlSchemaType schemaType, QName typeName,
			String schemaLocation) {
		if (typeName == null) {
			typeName = schemaType.getQName();
		}
		
		// try to get type definition from index
		XmlTypeDefinition type = (XmlTypeDefinition) index.getType(typeName);
		
		// create new type if necessary
		if (type == null) {
			type = new XmlTypeDefinition(typeName);
			index.addType(type);
		}
		
		if (schemaType instanceof XmlSchemaSimpleType) {
			// attribute type from simple schema types
//XXX		TypeDefinition simpleType = TypeUtil.resolveSimpleType(
//XXX				typeName, (XmlSchemaSimpleType) item, typeResolver);
			configureSimpleType(type, (XmlSchemaSimpleType) schemaType);
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
			
			type.setConstraint(BindingConstraint.getBinding(Instance.class)); //XXX instead object binding?
			
			// set metadata
			type.setLocation(createLocationURI(schemaLocation, schemaType));
			type.setDescription(XMLSchemaIO.getDescription(complexType));
			
			// determine the defined properties and add them to the declaring type
			createProperties(type, complexType);
			
			// set additional properties
			type.setConstraint((complexType.isAbstract())?
					(AbstractFlag.ENABLED):(AbstractFlag.DISABLED));
		}
		else {
			reporter.warn(new IOMessageImpl("Unrecognized schema type", null,
					schemaType.getLineNumber(), schemaType.getLinePosition()));
		}
	}

	private URI createLocationURI(String schemaLocation,
			XmlSchemaObject schemaObject) {
		//XXX improve
		try {
			return new URI(schemaLocation);
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
		return XMLSchemaIO.XSD_CT;
	}
	
	/**
	 * Extracts attribute definitions from a {@link XmlSchemaParticle}.
	 * 
	 * @param typeDef the definition of the declaring type
	 * @param particle the particle
	 * @param schemaTypes the schema types 
	 * @param referenceResolver the reference resolver
	 * 
	 * @return the list of attribute definitions
	 */
	private List<AttributeDefinition> getAttributesFromParticle(TypeDefinition typeDef, XmlSchemaParticle particle, 
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver) {
		List<AttributeDefinition> attributeResults = new ArrayList<AttributeDefinition>();
		
		// particle:
		if (particle instanceof XmlSchemaSequence) {
			// <sequence>
			XmlSchemaSequence sequence = (XmlSchemaSequence)particle;
			for (int j = 0; j < sequence.getItems().getCount(); j++) {
				XmlSchemaObject object = sequence.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					AbstractElementAttribute attribute = getAttributeFromElement(
							(XmlSchemaElement) object, typeDef, schemaTypes, 
							referenceResolver);
					if (attribute != null) {
						attributeResults.add(attribute);
					}
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice TODO wrap those attributes?
					attributeResults.addAll(getAttributesFromParticle(typeDef, 
							(XmlSchemaParticle) object, schemaTypes, 
							referenceResolver));
				}
			}
			// </sequence>
		}
		else if (particle instanceof XmlSchemaChoice) {
			//FIXME how to correctly deal with this? for now we add all choices
			// <choice>
			XmlSchemaChoice choice = (XmlSchemaChoice) particle;
			for (int j = 0; j < choice.getItems().getCount(); j++) {
				XmlSchemaObject object = choice.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					AbstractElementAttribute attribute = getAttributeFromElement(
							(XmlSchemaElement) object, typeDef, schemaTypes, 
							referenceResolver);
					if (attribute != null) {
						attribute.setMinOccurs(0); //XXX set minOccurs to zero because its a choice
						attributeResults.add(attribute);
					}
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice TODO wrap those attributes?
					attributeResults.addAll(getAttributesFromParticle(typeDef, 
							(XmlSchemaParticle) object, schemaTypes, 
							referenceResolver));
				}
			}
			// </choice>
		}
		
		return attributeResults;
	}

	/**
	 * Get an attribute from an element
	 * 
	 * @param element the schema element
	 * @param declaringType the definition of the declaring type
	 * @param schemaTypes the schema types
	 * @param referenceResolver the reference resolver
	 * 
	 * @return an attribute definition or <code>null</code>
	 */
	private AbstractElementAttribute getAttributeFromElement(
			XmlSchemaElement element, TypeDefinition declaringType,
			SchemaTypeResolver schemaTypes, SchemaReferenceResolver referenceResolver) {
		if (element.getSchemaTypeName() != null) {
			// element referencing a type
			// <element name="ELEMENT_NAME" type="SCHEMA_TYPE_NAME" />
			return new SchemaAttribute(
					declaringType,
					element.getName(), 
					new NameImpl(element.getSchemaTypeName().getNamespaceURI(), 
							element.getSchemaTypeName().getLocalPart()), 
					element,
					schemaTypes);
		}
		else if (element.getRefName() != null) {
			// references another element
			// <element ref="REF_NAME" />
			Name elementName = new NameImpl(
					element.getRefName().getNamespaceURI(),
					element.getRefName().getLocalPart());
			
			// local element definition
			SchemaElement reference = referenceResolver.getSchemaElement(elementName);
			if (reference == null) {
				_log.warn("Reference to element " + element.getRefName().getNamespaceURI() + "/" + element.getRefName().getLocalPart() +" not found"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				return null;
			}
			else {
				return new ElementReferenceAttribute(
						declaringType, 
						element.getName(), 
						reference.getTypeName(), 
						element, 
						reference);
			}
		}
		else if (element.getSchemaType() != null) {
			// element w/o type or ref
			if (element.getSchemaType() instanceof XmlSchemaComplexType) {
				// <element ...>
				//   <complexType>
				XmlSchemaComplexType complexType = (XmlSchemaComplexType) element.getSchemaType();
				XmlSchemaContentModel model = complexType.getContentModel();
				XmlSchemaParticle particle = complexType.getParticle();
				if (model != null) {
					XmlSchemaContent content = model.getContent();
					
					QName qname = null;
					if (content instanceof XmlSchemaComplexContentExtension || 
							content instanceof XmlSchemaComplexContentRestriction) {
						// <complexContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaComplexContentExtension) {
							qname = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							qname = ((XmlSchemaComplexContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (declaringType != null) {
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							TypeDefinition anonymousType = new AnonymousType(anonymousName, null, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
							
							// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
							// it's enough for it to be added to the imported types map
							if (schemaTypes != null) {
								schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
							}
							
							// create an attribute with the anonymous type
							SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
							return result;
						}
						
						//   </extension> / </restriction>
						// </complexContent>
					} else if (content instanceof XmlSchemaSimpleContentExtension
							|| content instanceof XmlSchemaSimpleContentRestriction) { 
						// <simpleContent>
						//   <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaSimpleContentExtension) {
							qname = ((XmlSchemaSimpleContentExtension) content).getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							qname = ((XmlSchemaSimpleContentRestriction) content).getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}
						
						if (declaringType != null) {
							// create an anonymous type that extends the type referenced by qname
							// with additional attributes
							Name superTypeName = new NameImpl(qname.getNamespaceURI(), qname.getLocalPart());
							
							// try to get the type definition of the super type
							TypeDefinition superType = TypeUtil.resolveAttributeType(superTypeName, schemaTypes);
							if (superType == null) {
								_log.error("Couldn't resolve super type: " + superTypeName.getNamespaceURI() + "/" + superTypeName.getLocalPart()); //$NON-NLS-1$ //$NON-NLS-2$
							}
							
							// create an anonymous type that extends the super type
							Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), superTypeName.getLocalPart() + nameExt); //$NON-NLS-1$
							// for now use the super attribute type, because attributes aren't added as attribute descriptors
							AttributeType attributeType = superType.getType(null); 
							TypeDefinition anonymousType = new AnonymousType(anonymousName, attributeType, superType, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
							
							// add attributes to the anonymous type
							// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
							getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
							
							// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
							// it's enough for it to be added to the imported types map
							if (schemaTypes != null) {
								schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
							}
							
							// create an attribute with the anonymous type
							SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
							return result;
						}
						
						//   </extension>
						// </simpleContent>
					}
					
					if (qname != null) {
						// return base type for dependency resolution
						return new SchemaAttribute(
								declaringType,
								element.getName(), 
								new NameImpl(qname.getNamespaceURI(), qname.getLocalPart()), 
								element,
								schemaTypes);
					}
					else {
						return null;
					}
				} else if (particle != null) {
					// this where we get when there is an anonymous complex type as property type
					if (declaringType == null) {
						// called only to get the type name for dependency resolution - not needed for anonymous types
						return null;
					}
					else {
						// create an anonymous type
						Name anonymousName = new NameImpl(declaringType.getIdentifier() + "/" + element.getName(), "AnonymousType"); //$NON-NLS-1$ //$NON-NLS-2$
						TypeDefinition anonymousType = new AnonymousType(anonymousName, null, null, (schemaTypes != null)?(schemaTypes.getSchemaLocation()):(null));
						
						// add attributes to the anonymous type
						// adding the attributes will happen automatically when the AbstractSchemaAttribute is created
						getAttributes(anonymousType, complexType, schemaTypes, referenceResolver);
						
						// add the anonymous type to the type map - needed for type resolution in SchemaAttribute
						// it's enough for it to be added to the imported types map
						if (schemaTypes != null) {
							schemaTypes.getImportedTypes().put(anonymousName, anonymousType);
						}
						
						// create an attribute with the anonymous type
						SchemaAttribute result = new SchemaAttribute(declaringType, element.getName(), anonymousName, element, schemaTypes);
						return result;
					}
				}
				//   </complexType>
				// </element>
			}
			else if (element.getSchemaType() instanceof XmlSchemaSimpleType) {
				// simple schema type
				TypeDefinition type = TypeUtil.resolveSimpleType(null, (XmlSchemaSimpleType) element.getSchemaType(), schemaTypes);
				if (type != null) {
					return new SchemaTypeAttribute(
							declaringType,
							element.getName(), 
							element,
							type);
				}
				else {
					_log.error("Could not resolve type for element " + element.getName()); //$NON-NLS-1$
				}
			}
		}
		
		return null;
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
	
	private boolean referencesType(Map<Name, SchemaElement> elements,
			Name dependency) {
		//elements.containsValue(dependency)
		//TODO
		//XXX for now, return false
		return false;
	}

	/**
	 * Create the properties for the given complex type
	 * 
	 * @param typeDef the definition of the declaring type 
	 * @param item the complex type item
	 */
	private void createProperties(TypeDefinition typeDef, 
			XmlSchemaComplexType item) {
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
					createPropertiesFromParticle(typeDef, particle);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					createPropertiesFromCollection(attributeCollection, typeDef, null);
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
					createPropertiesFromParticle(typeDef, particle);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					createPropertiesFromCollection(attributeCollection, typeDef, null);
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
					createPropertiesFromCollection(attributeCollection, typeDef, null);
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
					createPropertiesFromCollection(attributeCollection, typeDef, null);
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
				createPropertiesFromParticle(typeDef, particle);
			}
			// attributes
			XmlSchemaObjectCollection attributeCollection = complexType.getAttributes();
			if (attributeCollection != null) {
				createPropertiesFromCollection(attributeCollection, typeDef, null);
			}
		}
		
		// </complexType>
	}
	
	private void createPropertiesFromCollection(
			XmlSchemaObjectCollection attributeCollection, TypeDefinition declaringType,
			String indexPrefix) {
		if (indexPrefix == null) {
			indexPrefix = ""; //$NON-NLS-1$
		}
		
		for (int index = 0; index < attributeCollection.getCount(); index++) {
			XmlSchemaObject object = attributeCollection.getItem(index);
			if (object instanceof XmlSchemaAttribute) {
				// <attribute ... />
				XmlSchemaAttribute attribute = (XmlSchemaAttribute) object;
				
				createAttribute(attribute, declaringType, indexPrefix + index, null);
			}
			else if (object instanceof XmlSchemaAttributeGroup) {
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) object;
				
				createAttributes(group, declaringType, indexPrefix + index);
			}
			else if (object instanceof XmlSchemaAttributeGroupRef) {
				XmlSchemaAttributeGroupRef groupRef = (XmlSchemaAttributeGroupRef) object;
				
				//XXX reference properties? how? ensure that all groups are in the index before creating the properties?
				//XXX could a constraint be used? maybe, order doesn't matter with attributes
//				if (groupRef.getRefName() != null) {
//					XmlSchemaAttributeGroup group = referenceResolver.getSchemaAttributeGroup(new NameImpl(
//							groupRef.getRefName().getNamespaceURI(), 
//							groupRef.getRefName().getLocalPart()));
//					
//					if (group != null) {
//						attributeResults.addAll(createAttributes(group, 
//								declaringType, schemaTypes, referenceResolver, indexPrefix + index));
//					}
//					else {
//						_log.warn("Reference to attribute group " + groupRef.getRefName() + " could not be resolved"); //$NON-NLS-1$ //$NON-NLS-2$
//					}
//				}
			}
		}
	}

	private void createAttributes(XmlSchemaAttributeGroup group, 
			TypeDefinition declaringType, String index) {
		createPropertiesFromCollection(group.getAttributes(), 
				declaringType, index + "_"); //$NON-NLS-1$
	}

	private void createAttribute(XmlSchemaAttribute attribute, 
			TypeDefinition declaringType, String index, 
			XmlSchemaUse useOverride) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			return new DefaultResolveAttribute(
					declaringType, 
					new NameImpl(typeName.getNamespaceURI(), typeName.getLocalPart()), 
					attribute, 
					schemaTypes,
					(useOverride != null)?(useOverride):(attribute.getUse()));
		}
		else if (attribute.getSchemaType() != null) {
			if (declaringType != null) {
				QName name = attribute.getSchemaType().getQName();
				Name attributeTypeName = (name != null)?
						(new NameImpl(name.getNamespaceURI(), name.getLocalPart())):
						(new NameImpl(declaringType.getName().getNamespaceURI() + "/" + declaringType.getName().getLocalPart(), "AnonymousAttribute" + index)); //$NON-NLS-1$ //$NON-NLS-2$
				TypeDefinition attributeType = TypeUtil.resolveSimpleType(
						attributeTypeName, 
						attribute.getSchemaType(), 
						schemaTypes);
				
				return new DefaultAttribute(declaringType, attributeTypeName, 
						attribute, attributeType, 
						(useOverride != null)?(useOverride):(attribute.getUse()));
			}
		}
		else if (attribute.getRefName() != null) {
			// <attribute ref="REF_NAME" />
			XmlSchemaAttribute referencedAtt = referenceResolver.getSchemaAttribute(new NameImpl(
					attribute.getRefName().getNamespaceURI(), 
					attribute.getRefName().getLocalPart()));
			
			if (referencedAtt != null) {
				return createAttribute(referencedAtt, declaringType, schemaTypes, referenceResolver, index, attribute.getUse());
			}
			else {
				_log.warn(MISSING_ATTRIBUTE_REF, "Reference to attribute " + attribute.getRefName() + " could not be resolved"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		return null;
	}

//	/**
//	 * Get the attributes type names for the given item
//	 * 
//	 * @param item the complex type item
//	 * @param referenceResolver the reference resolver
//	 * @return the attribute type names
//	 */
//	private Set<Name> getAttributeTypeNames(XmlSchemaComplexType item, 
//			SchemaReferenceResolver referenceResolver) {
//		List<AttributeDefinition> attributes = getAttributes(null, item, null, 
//				referenceResolver);
//		
//		Set<Name> typeNames = new HashSet<Name>();
//		
//		for (AttributeDefinition def : attributes) {
//			typeNames.add(def.getTypeName());
//		}
//		
//		return typeNames;
//	}

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



