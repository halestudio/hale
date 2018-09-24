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
package eu.esdihumboldt.hale.io.xsd.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
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
import org.apache.ws.commons.schema.XmlSchemaForm;
import org.apache.ws.commons.schema.XmlSchemaGroup;
import org.apache.ws.commons.schema.XmlSchemaGroupRef;
import org.apache.ws.commons.schema.XmlSchemaImport;
import org.apache.ws.commons.schema.XmlSchemaInclude;
import org.apache.ws.commons.schema.XmlSchemaNotation;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentExtension;
import org.apache.ws.commons.schema.XmlSchemaSimpleContentRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.apache.ws.commons.schema.utils.NamespacePrefixList;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableSet;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.ValueList;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.io.impl.AbstractSchemaReader;
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ChoiceFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultGroupPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.XMLSchemaIO;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentConfiguration;
import eu.esdihumboldt.hale.io.xsd.anytype.CustomTypeContentHelper;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAppInfo;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlIdUnique;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlMixedFlag;
import eu.esdihumboldt.hale.io.xsd.internal.Messages;
import eu.esdihumboldt.hale.io.xsd.model.ComplexContentHasValue;
import eu.esdihumboldt.hale.io.xsd.model.HasNotInheritableValue;
import eu.esdihumboldt.hale.io.xsd.model.XmlAttribute;
import eu.esdihumboldt.hale.io.xsd.model.XmlAttributeGroup;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlGroup;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.internal.AnonymousXmlType;
import eu.esdihumboldt.hale.io.xsd.reader.internal.HumboldtURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.ProgressURIResolver;
import eu.esdihumboldt.hale.io.xsd.reader.internal.SubstitutionGroupProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttributeGroupReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlAttributeReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlElementReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlGroupReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeUtil;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.ElementName;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.ElementReferenceProperty;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.MappableUsingXsiType;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.MappingRelevantIfFeatureType;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.XLinkReference;
import eu.esdihumboldt.util.Identifiers;
import eu.esdihumboldt.util.io.InputSupplier;
import eu.esdihumboldt.util.resource.Resources;
import gnu.trove.TObjectIntHashMap;

/**
 * The main functionality of this class is to load an XML schema file (XSD) and
 * create a schema with {@link TypeDefinition}s. This implementation is based on
 * the Apache XmlSchema library (
 * {@link "http://ws.apache.org/commons/XmlSchema/"}).
 * 
 * It is necessary use this library instead of the GeoTools XML schema loader,
 * because the GeoTools version cannot handle GML 3.2 based files.
 * 
 * @author Simon Templer
 * @author Bernd Schneiders
 * @author Thorsten Reitz
 */
public class XmlSchemaReader extends AbstractSchemaReader {

	/**
	 * Namespace prefix generator.
	 */
	private static final class NamespaceIdentifiers extends Identifiers<String> {

		/**
		 * @see Identifiers#Identifiers(String, boolean, int)
		 */
		private NamespaceIdentifiers(String prefix, boolean useEquals, int startCounter) {
			super(prefix, useEquals, startCounter);
		}

		/**
		 * Add existing identifiers (to avoid conflicts).
		 * 
		 * @param objectsAndIdentifiers objects (namespaces) mapped to
		 *            identifiers (prefixes)
		 */
		public void addIdentifiers(Map<String, String> objectsAndIdentifiers) {
			for (Entry<String, String> entry : objectsAndIdentifiers.entrySet()) {
				putObjectIdentifier(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Name of the parameter specifying the elements that represent mapping
	 * relevant types.
	 */
	public static final String PARAM_RELEVANT_ELEMENTS = "relevantElements";

	/**
	 * Name of the parameter specifying if only those types are deemed mappable,
	 * that have an associated global element definition.
	 */
	public static final String PARAM_ONLY_ELEMENTS_MAPPABLE = "onlyElementsMappable";

	/**
	 * Name of the parameter specifying which mode to use to determine the
	 * default mapping relevant types.
	 * 
	 * Valid values currently are {@value #MAPPING_RELEVANT_MODE_MAIN_SCHEMA}
	 * and {@value #MAPPING_RELEVANT_MODE_FEATURE_TYPES}.
	 */
	public static final String PARAM_MAPPING_RELEVANT_MODE = "relevantMode";

	/**
	 * Mapping relevant type mode that marks types from the main schema as
	 * relevant.
	 */
	public static final String MAPPING_RELEVANT_MODE_MAIN_SCHEMA = "mainSchema";

	/**
	 * Mapping relevant type mode that marks GML feature types as relevant.
	 */
	public static final String MAPPING_RELEVANT_MODE_FEATURE_TYPES = "featureTypes";

	/**
	 * Name of the parameter specifying custom type content configuration.
	 */
	public static final String PARAM_CUSTOM_TYPE_CONTENT = "customTypeContent";

	/**
	 * The display name constraint for choices
	 */
	private static final DisplayName DISPLAYNAME_CHOICE = new DisplayName("choice");

	/**
	 * The log
	 */
	private static final ALogger _log = ALoggerFactory.getLogger(XmlSchemaReader.class);

	/**
	 * Qualified name of the XLink reference attribute.
	 */
	private static final QName NAME_XLINK_REF = new QName("http://www.w3.org/1999/xlink", "href");

	/**
	 * Name for virtual INSPIRE NilReason type with adapted enumeration.
	 */
	private static final QName INSPIRE_NILREASON_TYPENAME = new QName(
			"http://www.esdi-humboldt.eu/hale/inspire/ext", "NilReasonType");

	/**
	 * Values for the virtual INSPIRE NilReason type.
	 */
	private static final Collection<? extends String> INSPIRE_NILREASON_VALUES = ImmutableSet
			.of("unknown", "other:unpopulated", "withheld");

	/**
	 * The XML definition index
	 */
	private XmlIndex index;

	/**
	 * Holds the number of created groups for a parent. The parent identifier is
	 * mapped to the number of groups.
	 */
	private TObjectIntHashMap<String> groupCounter;

	/**
	 * The current reporter
	 */
	private IOReporter reporter;

	/**
	 * The generated namespace prefixes
	 */
	private final NamespaceIdentifiers namespaceGeneratedPrefixes = new NamespaceIdentifiers("ns",
			true, 1);

	/**
	 * @see SchemaReader#getSchema()
	 */
	@Override
	public XmlIndex getSchema() {
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
	 * @see AbstractImportProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		super.validate();

		if (getSharedTypes() != null) {
			for (TypeDefinition type : getSharedTypes().getTypes()) {
				if (type instanceof XmlTypeDefinition) {
					fail("Loading multiple XML schemas not supported, please create a combined XML schema instead.");
				}
			}
		}
	}

	/**
	 * @see AbstractIOProvider#execute(ProgressIndicator, IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {
		progress.begin(Messages.getString("ApacheSchemaProvider.21"), ProgressIndicator.UNKNOWN); //$NON-NLS-1$
		this.reporter = reporter;

		XmlSchema xmlSchema = null;
		XmlSchemaCollection schemaCol = new XmlSchemaCollection();
		// Check if the file is located on web
		URI location = getSource().getLocation();
		if (location.getHost() == null) {
			schemaCol.setSchemaResolver(
					new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location));
		}
		else if (location.getScheme().equals("bundleresource")) { //$NON-NLS-1$
			schemaCol.setSchemaResolver(
					new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location) + "/"); //$NON-NLS-1$
		}
		else {
			schemaCol.setSchemaResolver(
					new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(findBaseUri(location) + "/"); //$NON-NLS-1$
		}

		InputStream is = null;
		// try resolving using (local) Resources
		InputSupplier<? extends InputStream> input = Resources.tryResolve(location,
				Resources.RESOURCE_TYPE_XML_SCHEMA);
		if (input != null) {
			try {
				is = input.getInput();
			} catch (Exception e) {
				// ignore
			}
		}

		if (is == null) {
			is = getSource().getInput();
		}
		StreamSource ss = new StreamSource(is);
		ss.setSystemId(location.toString());
		xmlSchema = schemaCol.read(ss, null);
		is.close();

		String namespace = xmlSchema.getTargetNamespace();
		if (namespace == null) {
			namespace = XMLConstants.NULL_NS_URI;
		}

		xmlSchema.setSourceURI(location.toString());

		// create index
		index = new XmlIndex(namespace, location);

		// create group counter
		groupCounter = new TObjectIntHashMap<String>();

		Set<String> imports = new HashSet<String>();
		imports.add(location.toString());

		// load XML Schema schema (for base type definitions)
		try {
			is = XmlSchemaReader.class.getResourceAsStream("/schemas/XMLSchema.xsd");
			ss = new StreamSource(is);
			schemaCol.setSchemaResolver(
					new ProgressURIResolver(new HumboldtURIResolver(), progress));
			schemaCol.setBaseUri(
					findBaseUri(XmlSchemaReader.class.getResource("/schemas/XMLSchema.xsd").toURI())
							+ "/");
			XmlSchema xsSchema = schemaCol.read(ss, null);
			is.close();
			xsSchema.setSourceURI("http://www.w3.org/2001/XMLSchema.xsd");
			XmlSchemaImport xmlSchemaImport = new XmlSchemaImport();
			xmlSchemaImport.setSchema(xsSchema);

			// add it to includes as XmlSchemaImport (not XmlSchemaInclude!)
			xmlSchema.getIncludes().add(xmlSchemaImport);
		} catch (Exception e) {
			_log.error("Exception while loading XML Schema schema", e);
		}

		loadSchema(location.toString(), xmlSchema, imports, progress, true);

		groupCounter.clear();

		// post processing
		applyRelevantElements(index);
		applyCustomTypeContent(index);

		reporter.setSuccess(true);
		return reporter;
	}

	/**
	 * Apply custom type content configuration.
	 * 
	 * @param index the XML index
	 */
	private void applyCustomTypeContent(XmlIndex index) {
		CustomTypeContentConfiguration config = getParameter(PARAM_CUSTOM_TYPE_CONTENT)
				.as(CustomTypeContentConfiguration.class);
		if (config != null) {
			CustomTypeContentHelper.applyConfigurations(index, config);
		}
	}

	/**
	 * Apply the relevant elements setting to the given XML index.
	 * 
	 * @param index the XML index
	 */
	private void applyRelevantElements(XmlIndex index) {
		Set<? extends QName> names = getRelevantElements();

		if (names != null && !names.isEmpty()) {
			// only apply if any elements are given

			// get all currently marked relevant types
			Set<TypeDefinition> toggleTypes = new HashSet<>(index.getMappingRelevantTypes());

			boolean foundAny = false;
			for (QName name : names) {
				XmlElement elm = index.getElements().get(name);
				if (elm != null) {
					foundAny = true;

					TypeDefinition type = elm.getType();
					if (toggleTypes.contains(type)) {
						// do not toggle -> stay relevant
						toggleTypes.remove(type);
					}
					else {
						// toggle -> become relevant
						toggleTypes.add(type);
					}
				}
			}

			if (foundAny) {
				// only apply if one of the given elements was actually found in
				// the schema
				index.toggleMappingRelevant(toggleTypes);
			}
		}
	}

	/**
	 * Set the element names of mapping relevant types.
	 * 
	 * @param elementNames the element names
	 */
	public void setRelevantElements(Collection<? extends QName> elementNames) {
		ValueList elementList = new ValueList();
		for (QName name : elementNames) {
			elementList.add(Value.of(name));
		}
		setParameter(PARAM_RELEVANT_ELEMENTS, elementList.toValue());
	}

	/**
	 * @return the names of the elements configured as relevant
	 */
	public Set<? extends QName> getRelevantElements() {
		Set<QName> result = new HashSet<>();
		ValueList elementList = getParameter(PARAM_RELEVANT_ELEMENTS).as(ValueList.class);
		if (elementList != null) {
			for (Value val : elementList) {
				QName name = val.as(QName.class);
				if (name != null) {
					result.add(name);
				}
			}
		}
		return result;
	}

	/**
	 * Set the mode to use for determining mapping relevant types.
	 * 
	 * @param mode the mode name
	 */
	public void setMappingRelevantMode(String mode) {
		setParameter(PARAM_MAPPING_RELEVANT_MODE, Value.of(mode));
	}

	/**
	 * @return the mode to use for determining mapping relevant types
	 */
	public String getMappingRelevantMode() {
		return getParameter(PARAM_MAPPING_RELEVANT_MODE).as(String.class,
				MAPPING_RELEVANT_MODE_MAIN_SCHEMA);
	}

	/**
	 * Set if only elements should be mappable. Otherwise all types with a
	 * global type definition are mappable.
	 * 
	 * @param onlyElements if only elements should be mappable
	 */
	public void setOnlyElementsMappable(boolean onlyElements) {
		setParameter(PARAM_ONLY_ELEMENTS_MAPPABLE, Value.of(onlyElements));
	}

	/**
	 * @return states if only types with associated global elements are
	 *         classified as mappable types
	 */
	public boolean isOnlyElementsMappable() {
		return getParameter(PARAM_ONLY_ELEMENTS_MAPPABLE).as(Boolean.class, true);
	}

	/**
	 * Load the feature types defined by the given schema
	 * 
	 * @param schemaLocation the schema location
	 * @param xmlSchema the schema
	 * @param imports the imports/includes that were already loaded or where
	 *            loading has been started
	 * @param progress the progress indicator
	 * @param mainSchema states if this is a main schema and therefore elements
	 *            declared here should be flagged mappable
	 */
	protected void loadSchema(String schemaLocation, XmlSchema xmlSchema, Set<String> imports,
			ProgressIndicator progress, boolean mainSchema) {
		String namespace = xmlSchema.getTargetNamespace();
		if (namespace == null) {
			namespace = XMLConstants.NULL_NS_URI;
		}

		// add namespace prefixes
		NamespacePrefixList namespaces = xmlSchema.getNamespaceContext();
		addPrefixes(namespaces, namespace, mainSchema);

		// the schema items
		XmlSchemaObjectCollection items = xmlSchema.getItems();

		// go through all schema items
		for (int i = 0; i < items.getCount(); i++) {
			XmlSchemaObject item = items.getItem(i);

			if (item instanceof XmlSchemaElement) {
				// global element declaration
				XmlSchemaElement element = (XmlSchemaElement) item;
				// determine type
				XmlTypeDefinition elementType = null;

				if (element.getSchemaTypeName() != null) {
					// reference to type
					elementType = index.getOrCreateType(element.getSchemaTypeName());
				}
				else if (element.getSchemaType() != null) {
					// element has internal type definition, generate anonymous
					// type name
					QName typeName = new QName(element.getQName().getNamespaceURI(),
							element.getQName().getLocalPart() + "_AnonymousType"); //$NON-NLS-1$
					// create type
					elementType = createType(element.getSchemaType(), typeName, schemaLocation,
							namespace, mainSchema);
				}
				else if (element.getQName() != null) {
					// element with no type
					elementType = index.getOrCreateType(XmlTypeUtil.NAME_ANY_TYPE);
				}
				// XXX what about element.getRefName()?

				if (elementType != null) {
					// the element name
					// XXX use element QName instead?
					QName elementName = new QName(namespace, element.getName());
					// the substitution group
					QName subGroup = element.getSubstitutionGroup();
					// TODO do we also need an index for substitutions?

					// create schema element
					XmlElement schemaElement = new XmlElement(elementName, elementType, subGroup);

					// set metadata
					setMetadata(schemaElement, element, schemaLocation);

					// extend XmlElements constraint
					XmlElements xmlElements = elementType.getConstraint(XmlElements.class);
					xmlElements.addElement(schemaElement);
					// set custom display name
					elementType.setConstraint(new ElementName(xmlElements));

					// set Mappable constraint (e.g. Mappable)
					// for types with an associated element it can be determined
					// on the spot if it is mappable
					configureMappingRelevant(elementType, mainSchema);
					// XXX needed? may result in conflicts when defining
					// mappable types manually XXX the element is also marked
					// with the Mappable constraint, to help with cases where
					// multiple elements are defined for one
//					schemaElement.setConstraint(MappableFlag.get(mainSchema));

					// store element in index
					index.getElements().put(elementName, schemaElement);
				}
				else {
					reporter.error(new IOMessageImpl(
							MessageFormat.format("No type for element {0} found.",
									element.getName()),
							null, element.getLineNumber(), element.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaType) {
				// complex or simple type
				createType((XmlSchemaType) item, null, schemaLocation, namespace, mainSchema);
			}
			else if (item instanceof XmlSchemaAttribute) {
				// schema attribute that might be referenced somewhere
				XmlSchemaAttribute att = (XmlSchemaAttribute) item;
				if (att.getQName() != null) {
					XmlTypeDefinition type = getAttributeType(att, null, schemaLocation);
					if (type == null) {
						// XXX if this occurs we might need a attribute
						// referencing attribute
						reporter.error(new IOMessageImpl("Could not determine attribute type", null,
								att.getLineNumber(), att.getLinePosition()));
					}
					else {
						XmlAttribute attribute = new XmlAttribute(att.getQName(), type);

						index.getAttributes().put(attribute.getName(), attribute);
					}
				}
				else {
					reporter.warn(new IOMessageImpl(
							MessageFormat.format("Attribute could not be processed: {0}",
									att.getName()),
							null, att.getLineNumber(), att.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaAttributeGroup) {
				// schema attribute group that might be referenced somewhere
				XmlSchemaAttributeGroup attributeGroup = (XmlSchemaAttributeGroup) item;
				if (attributeGroup.getName() != null) {
					String groupIdent = attributeGroup.getName().getNamespaceURI() + "/"
							+ attributeGroup.getName().getLocalPart();
					XmlAttributeGroup attGroup = new XmlAttributeGroup(groupIdent, true);
					createAttributes(attributeGroup, attGroup, "", schemaLocation, namespace);
					index.getAttributeGroups().put(attributeGroup.getName(), attGroup);
				}
				else {
					reporter.warn(new IOMessageImpl("Attribute group could not be processed", null,
							attributeGroup.getLineNumber(), attributeGroup.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaGroup) {
				// group that might be referenced somewhere
				XmlSchemaGroup schemaGroup = (XmlSchemaGroup) item;
				if (schemaGroup.getName() != null) {
					String groupIdent = schemaGroup.getName().getNamespaceURI() + "/"
							+ schemaGroup.getName().getLocalPart();
					XmlGroup group = new XmlGroup(groupIdent, true);
					createPropertiesFromParticle(group, schemaGroup.getParticle(), schemaLocation,
							namespace, false);
					index.getGroups().put(schemaGroup.getName(), group);
				}
				else {
					reporter.warn(new IOMessageImpl("Group could not be processed", null,
							schemaGroup.getLineNumber(), schemaGroup.getLinePosition()));
				}
			}
			else if (item instanceof XmlSchemaImport || item instanceof XmlSchemaInclude) {
				// ignore, is treated separately
			}
			else if (item instanceof XmlSchemaNotation) {
				// notations are ignored
			}
			else {
				reporter.error(new IOMessageImpl(
						"Unrecognized global definition: " + item.getClass().getSimpleName(), null,
						item.getLineNumber(), item.getLinePosition()));
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
				if (!(imports.contains(location))) { // only add schemas that
														// were not already
														// added
					imports.add(location); // place a marker in the map to
											// prevent loading the location in
											// the call to loadSchema
					loadSchema(location, importedSchema, imports, progress,
							mainSchema && imp instanceof XmlSchemaInclude);
					// is part of main schema if it's a main schema include
				}
				if (imp instanceof XmlSchemaInclude) {
					includes.add(location);
				}
			} catch (Throwable e) {
				reporter.error(new IOMessageImpl(
						"Error adding imported schema from " + schemaLocation, e)); // $NON-NLS-1$
			}
		}

		_log.info("Creating types for schema at " + schemaLocation); //$NON-NLS-1$

		progress.setCurrentTask(
				MessageFormat.format(Messages.getString("ApacheSchemaProvider.33"), namespace)); //$NON-NLS-1$
	}

	/**
	 * Configure the mapping relevant flag for a type.
	 * 
	 * @param type the type to configure
	 * @param mainSchema if the type belongs to the main schema
	 */
	private void configureMappingRelevant(XmlTypeDefinition type, boolean mainSchema) {
		MappingRelevantFlag mappingRelevant = null;
		switch (getMappingRelevantMode()) {
		case MAPPING_RELEVANT_MODE_FEATURE_TYPES:
			mappingRelevant = new MappingRelevantIfFeatureType(type);
			break;
		case MAPPING_RELEVANT_MODE_MAIN_SCHEMA:
		default:
			mappingRelevant = mainSchema ? MappingRelevantFlag.get(true) : null;
			break;
		}

		if (mappingRelevant != null) {
			type.setConstraint(mappingRelevant);
		}
		else {
			// do not override with false, e.g. when a schema is
			// loaded multiple times (e.g. because of different
			// import locations)
			type.setConstraintIfNotSet(MappingRelevantFlag.get(false));
		}
	}

	/**
	 * Add namespace prefixes from a schema to the XmlIndex.
	 * 
	 * @param namespaces the namespace prefixes defined in the (single) schema
	 * @param defaultNamespace the default namespace of the schema
	 * @param mainSchema specifies if the schema is the main schema
	 */
	private void addPrefixes(NamespacePrefixList namespaces, String defaultNamespace,
			boolean mainSchema) {
		Map<String, String> prefixes = index.getPrefixes(); // namespaces mapped
															// to prefixes
		Set<String> orphanedNamespaces = new HashSet<String>();
		for (String prefix : namespaces.getDeclaredPrefixes()) {
			String ns = namespaces.getNamespaceURI(prefix);

			if (!prefixes.containsKey(ns)) {
				// prefix for namespace is not yet included
				if (prefixes.containsValue(prefix)) {
					// prefix already there, may not override
					orphanedNamespaces.add(ns);
				}
				else {
					// ok to use prefix
					prefixes.put(ns, prefix);
				}
			}
		}

		// update namespace identifiers with current prefixes
		namespaceGeneratedPrefixes.addIdentifiers(prefixes);

		// handle orphaned namespaces
		for (String ns : orphanedNamespaces) {
			if (!XMLConstants.XML_NS_URI.equals(ns)) {
				// exclude XML namespace, its prefix is fixed

				String prefix = namespaceGeneratedPrefixes.getId(ns);
				prefixes.put(ns, prefix);
			}
		}
		// special handling of default namespace (add it if not known)
		if (!mainSchema && !prefixes.containsKey(defaultNamespace)
				&& !XMLConstants.XML_NS_URI.equals(defaultNamespace)) {
			// exclude XML namespace, its prefix is fixed

			// generate a namespace prefix for imported schemas that might have
			// none
			String prefix = namespaceGeneratedPrefixes.getId(defaultNamespace);
			prefixes.put(defaultNamespace, prefix);
		}
	}

	/**
	 * Create a type definition from the given schema type and add it to the
	 * index or enhance an existing type definition if it is already in the
	 * index.
	 * 
	 * @param schemaType the schema type
	 * @param typeName the type name to use for the type, <code>null</code> if
	 *            the name of the schema type shall be used
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the schema namespace
	 * @param mainSchema if the type definition is a global definition in a main
	 *            schema
	 * @return the created type
	 */
	private XmlTypeDefinition createType(XmlSchemaType schemaType, QName typeName,
			String schemaLocation, String schemaNamespace, boolean mainSchema) {
		if (typeName == null) {
			typeName = schemaType.getQName();
		}

		// get type definition from index
		XmlTypeDefinition type = index.getOrCreateType(typeName);

		if (schemaType instanceof XmlSchemaSimpleType) {
			// attribute type from simple schema types
			configureSimpleType(type, (XmlSchemaSimpleType) schemaType, schemaLocation);
		}
		else if (schemaType instanceof XmlSchemaComplexType) {
			XmlSchemaComplexType complexType = (XmlSchemaComplexType) schemaType;

			// determine the super type name
			QName superTypeName = getSuperTypeName(complexType);

			// determine if the super type relation is a restriction
			boolean isRestriction = isRestriction(complexType);
			type.setConstraint(
					(isRestriction) ? (RestrictionFlag.ENABLED) : (RestrictionFlag.DISABLED));

			if (superTypeName != null) {
				// get super type from index
				XmlTypeDefinition superType = index.getOrCreateType(superTypeName);
				type.setSuperType(superType);

				// XXX reuse the super type's attribute type where appropriate?
			}

			// set mappable constraint
			// don't override mappable if explicitly set to false
			if (isOnlyElementsMappable()) {
				// only types with a global element definition (or with a super
				// type that matches this condition)
				type.setConstraintIfNotSet(new MappableUsingXsiType(type));
			}
			else {
				// all global complex type definitions should be mappable
				type.setConstraintIfNotSet(MappableFlag.ENABLED);
				// and mapping relevant depending on the mode

				configureMappingRelevant(type, mainSchema);
			}

			// set type metadata and constraints
			setMetadataAndConstraints(type, complexType, schemaLocation);

			// determine the defined properties and add them to the declaring
			// type
			createProperties(type, complexType, schemaLocation, schemaNamespace);
		}
		else {
			reporter.error(new IOMessageImpl(
					"Unrecognized schema type: " + schemaType.getClass().getSimpleName(), null,
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
	private void configureSimpleType(XmlTypeDefinition type, XmlSchemaSimpleType schemaType,
			String schemaLocation) {
		XmlTypeUtil.configureSimpleType(type, schemaType, index, reporter);

		// set metadata
		setMetadata(type, schemaType, schemaLocation);
	}

	private static URI createLocationURI(String schemaLocation, XmlSchemaObject schemaObject) {
		if (schemaLocation == null) {
			return null;
		}

		// XXX improve
		try {
			return new URI(schemaLocation + "#" + schemaObject.getLineNumber() + ":"
					+ schemaObject.getLinePosition());
		} catch (URISyntaxException e) {
			// ignore
			return null;
		}
	}

	/**
	 * @see AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "XML schema";
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
	private void createPropertiesFromParticle(DefinitionGroup declaringGroup,
			XmlSchemaParticle particle, String schemaLocation, String schemaNamespace,
			boolean forceGroup) {
		// particle:
		if (particle instanceof XmlSchemaSequence) {
			// <sequence>
			XmlSchemaSequence sequence = (XmlSchemaSequence) particle;

			// create group only if necessary (sequences that appear exactly
			// once will result in no group if not forced)
			if (forceGroup || sequence.getMinOccurs() != 1 || sequence.getMaxOccurs() != 1) {
				// create a sequence group
				QName sequenceName = createGroupName(declaringGroup, "sequence");
				DefaultGroupPropertyDefinition sequenceGroup = new DefaultGroupPropertyDefinition(
						sequenceName, declaringGroup, false);
				// set cardinality
				long max = (sequence.getMaxOccurs() == Long.MAX_VALUE) ? (Cardinality.UNBOUNDED)
						: (sequence.getMaxOccurs());
				sequenceGroup.setConstraint(Cardinality.get(sequence.getMinOccurs(), max));
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
					createPropertyFromElement((XmlSchemaElement) object, declaringGroup,
							schemaLocation, schemaNamespace);
					// </element>
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice
					// content doesn't need to be grouped, it can be decided in
					// the method
					createPropertiesFromParticle(declaringGroup, (XmlSchemaParticle) object,
							schemaLocation, schemaNamespace, false);
				}
			}
			// </sequence>
		}
		else if (particle instanceof XmlSchemaChoice) {
			// <choice>
			XmlSchemaChoice choice = (XmlSchemaChoice) particle;

			// create a choice group
			QName choiceName = createGroupName(declaringGroup, "choice");
			DefaultGroupPropertyDefinition choiceGroup = new DefaultGroupPropertyDefinition(
					choiceName, declaringGroup, false); // no flatten allowed
														// because of choice
			// set custom display name
			choiceGroup.setConstraint(DISPLAYNAME_CHOICE);
			// set cardinality
			long max = (choice.getMaxOccurs() == Long.MAX_VALUE) ? (Cardinality.UNBOUNDED)
					: (choice.getMaxOccurs());
			choiceGroup.setConstraint(Cardinality.get(choice.getMinOccurs(), max));
			// set choice constraint
			choiceGroup.setConstraint(ChoiceFlag.ENABLED);
			// set metadata
			setMetadata(choiceGroup, choice, schemaLocation);

			// create properties with choiceGroup as parent
			for (int j = 0; j < choice.getItems().getCount(); j++) {
				XmlSchemaObject object = choice.getItems().getItem(j);
				if (object instanceof XmlSchemaElement) {
					// <element>
					createPropertyFromElement((XmlSchemaElement) object, choiceGroup,
							schemaLocation, schemaNamespace);
				}
				else if (object instanceof XmlSchemaParticle) {
					// contained particles, e.g. a choice or sequence
					// inside a choice they must form a group
					createPropertiesFromParticle(choiceGroup, (XmlSchemaParticle) object,
							schemaLocation, schemaNamespace, true);
				}
			}
			// </choice>
		}
		else if (particle instanceof XmlSchemaGroupRef) {
			// <group ref="..." />
			XmlSchemaGroupRef groupRef = (XmlSchemaGroupRef) particle;

			QName groupName = groupRef.getRefName();

			long max = (groupRef.getMaxOccurs() == Long.MAX_VALUE) ? (Cardinality.UNBOUNDED)
					: (groupRef.getMaxOccurs());
			long min = groupRef.getMinOccurs();

			/*
			 * Only allow flatten if group is not forced and appears exactly
			 * once
			 */
			XmlGroupReferenceProperty property = new XmlGroupReferenceProperty(groupName,
					declaringGroup, index, groupName, !forceGroup && min == 1 && max == 1);

			// set cardinality constraint
			property.setConstraint(Cardinality.get(min, max));

			// set metadata
			setMetadata(property, groupRef, schemaLocation);
		}
		else if (particle instanceof XmlSchemaAny) {
			// XXX ignore for now
			reporter.info(new IOMessageImpl("Particle that allows any element is not supported.",
					null, particle.getLineNumber(), particle.getLinePosition()));
		}
		else {
			reporter.error(new IOMessageImpl(
					"Unrecognized particle: " + particle.getClass().getSimpleName(), null,
					particle.getLineNumber(), particle.getLinePosition()));
		}
	}

	/**
	 * Create a name for a group.
	 * 
	 * @param declaringGroup the declaring group
	 * @param groupType the group type
	 * @return the group name
	 */
	private QName createGroupName(DefinitionGroup declaringGroup, String groupType) {
		int groupNumber;
		synchronized (groupCounter) {
			groupNumber = groupCounter.get(declaringGroup.getIdentifier()) + 1;
			groupCounter.put(declaringGroup.getIdentifier(), groupNumber);
		}
		return new QName(declaringGroup.getIdentifier(), groupType + "_" + groupNumber);
	}

	/**
	 * Create a property from an element
	 * 
	 * @param element the schema element
	 * @param declaringGroup the definition of the declaring group
	 * @param schemaLocation the schema location
	 * @param schemaNamespace the schema namespace
	 */
	private void createPropertyFromElement(XmlSchemaElement element, DefinitionGroup declaringGroup,
			String schemaLocation, String schemaNamespace) {
		if (element.getSchemaTypeName() != null) {
			// element referencing a type
			// <element name="ELEMENT_NAME" type="SCHEMA_TYPE_NAME" />
			QName elementName = element.getQName();

			SubstitutionGroupProperty substitutionGroup = new SubstitutionGroupProperty(
					new QName(elementName.getNamespaceURI() + "/" + elementName.getLocalPart(),
							"choice"), // TODO
										// improve
										// naming?
					declaringGroup);

			DefaultPropertyDefinition property = new DefaultPropertyDefinition(elementName,
					substitutionGroup, index.getOrCreateType(element.getSchemaTypeName()));

			// set metadata and constraints
			setMetadataAndConstraints(property, element, schemaLocation);

			substitutionGroup.setProperty(property);
		}
		else if (element.getRefName() != null) {
			// references another element
			// <element ref="REF_NAME" />
			QName elementName = element.getRefName();

			SubstitutionGroupProperty substitutionGroup = new SubstitutionGroupProperty(
					new QName(elementName.getNamespaceURI() + "/" + elementName.getLocalPart(),
							"choice"), // TODO
										// improve
										// naming?
					declaringGroup);

			XmlElementReferenceProperty property = new XmlElementReferenceProperty(elementName,
					substitutionGroup, index, elementName);

			// set metadata and constraints FIXME can the constraints be set at
			// this point? or must the property determine them from the
			// SchemaElement?
			setMetadataAndConstraints(property, element, schemaLocation);

			substitutionGroup.setProperty(property);
		}
		else if (element.getSchemaType() != null) {
			// element w/o type name or reference but an internal type
			// definition
			if (element.getSchemaType() instanceof XmlSchemaComplexType) {
				// <element ...>
				// <complexType>
				XmlSchemaComplexType complexType = (XmlSchemaComplexType) element.getSchemaType();
				XmlSchemaContentModel model = complexType.getContentModel();
				if (model != null) {
					XmlSchemaContent content = model.getContent();

					QName superTypeName = null;
					if (content instanceof XmlSchemaComplexContentExtension
							|| content instanceof XmlSchemaComplexContentRestriction) {
						// <complexContent>
						// <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaComplexContentExtension) {
							superTypeName = ((XmlSchemaComplexContentExtension) content)
									.getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							superTypeName = ((XmlSchemaComplexContentRestriction) content)
									.getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}

						if (superTypeName != null) {
							// try to get the type definition of the super type
							XmlTypeDefinition superType = index.getOrCreateType(superTypeName);

							// create an anonymous type that extends the super
							// type
							QName anonymousName = new QName(
									getTypeIdentifier(declaringGroup) + "/" + element.getName(),
									superTypeName.getLocalPart() + nameExt); // $NON-NLS-1$

							AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
							anonymousType.setSuperType(superType);

							// set metadata and constraints
							setMetadataAndConstraints(anonymousType, complexType, schemaLocation);

							// add properties to the anonymous type
							createProperties(anonymousType, complexType, schemaLocation,
									schemaNamespace);

							// create a property with the anonymous type
							DefaultPropertyDefinition property = new DefaultPropertyDefinition(
									element.getQName(), declaringGroup, anonymousType);

							// set metadata and constraints
							setMetadataAndConstraints(property, element, schemaLocation);
						}
						else {
							reporter.error(new IOMessageImpl(
									"Could not determine super type for complex content", null,
									content.getLineNumber(), content.getLinePosition()));
						}

						// </extension> / </restriction>
						// </complexContent>
					}
					else if (content instanceof XmlSchemaSimpleContentExtension
							|| content instanceof XmlSchemaSimpleContentRestriction) {
						// <simpleContent>
						// <extension base="..."> / <restriction ...>
						String nameExt;
						if (content instanceof XmlSchemaSimpleContentExtension) {
							superTypeName = ((XmlSchemaSimpleContentExtension) content)
									.getBaseTypeName();
							nameExt = "Extension"; //$NON-NLS-1$
						}
						else {
							superTypeName = ((XmlSchemaSimpleContentRestriction) content)
									.getBaseTypeName();
							nameExt = "Restriction"; //$NON-NLS-1$
						}

						if (superTypeName != null) {
							// try to get the type definition of the super type
							XmlTypeDefinition superType = index.getOrCreateType(superTypeName);

							// create an anonymous type that extends the super
							// type
							QName anonymousName = new QName(
									getTypeIdentifier(declaringGroup) + "/" + element.getName(),
									superTypeName.getLocalPart() + nameExt); // $NON-NLS-1$

							AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);
							anonymousType.setSuperType(superType);

							// set metadata and constraints
							setMetadata(anonymousType, complexType, schemaLocation);
							anonymousType.setConstraint(HasValueFlag.ENABLED);
							// set no binding, inherit it from the super type
							// XXX is this ok?

							// add properties to the anonymous type
							createProperties(anonymousType, complexType, schemaLocation,
									schemaNamespace);

							// create a property with the anonymous type
							DefaultPropertyDefinition property = new DefaultPropertyDefinition(
									element.getQName(), declaringGroup, anonymousType);

							// set metadata and constraints
							setMetadataAndConstraints(property, element, schemaLocation);
						}
						else {
							reporter.error(new IOMessageImpl(
									"Could not determine super type for simple content", null,
									content.getLineNumber(), content.getLinePosition()));
						}

						// </extension>
						// </simpleContent>
					}
				}
				else {
					// this where we get when there is an anonymous complex type
					// as property type
					// create an anonymous type
					QName anonymousName = new QName(
							getTypeIdentifier(declaringGroup) + "/" + element.getName(),
							"AnonymousType");

					// create anonymous type with no super type
					AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);

					// set metadata and constraints
					setMetadataAndConstraints(anonymousType, complexType, schemaLocation);

					// add properties to the anonymous type
					createProperties(anonymousType, complexType, schemaLocation, schemaNamespace);

					// create a property with the anonymous type
					DefaultPropertyDefinition property = new DefaultPropertyDefinition(
							element.getQName(), declaringGroup, anonymousType);

					// set metadata and constraints
					setMetadataAndConstraints(property, element, schemaLocation);
				}
				// </complexType>
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

				// create a property with the anonymous type
				DefaultPropertyDefinition property = new DefaultPropertyDefinition(
						element.getQName(), declaringGroup, anonymousType);

				// set metadata and constraints
				setMetadataAndConstraints(property, element, schemaLocation);
			}
		}
		else {
			// <element name="..." />

			// no type defined
			reporter.warn(new IOMessageImpl("Element definition without an associated type: {0}",
					null, element.getLineNumber(), element.getLinePosition(), element.getQName()));

			// assuming xsd:anyType as default type
			QName elementName = element.getQName();

			SubstitutionGroupProperty substitutionGroup = new SubstitutionGroupProperty(
					new QName(elementName.getNamespaceURI() + "/" + elementName.getLocalPart(),
							"choice"), // TODO
										// improve
										// naming?
					declaringGroup);

			DefaultPropertyDefinition property = new DefaultPropertyDefinition(elementName,
					substitutionGroup, index.getOrCreateType(XmlTypeUtil.NAME_ANY_TYPE));

			// set metadata and constraints
			setMetadataAndConstraints(property, element, schemaLocation);

			substitutionGroup.setProperty(property);
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
	 *             child definition
	 */
	private static String getTypeIdentifier(DefinitionGroup group) throws IllegalArgumentException {
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
	private void setMetadataAndConstraints(XmlTypeDefinition type, XmlSchemaComplexType complexType,
			String schemaLocation) {
		type.setConstraint(AbstractFlag.get(complexType.isAbstract()));

		/*
		 * HasValue and Binding and all other inheritable constraints from super
		 * type, override constraints for special types
		 */

		// special bindings (geometries)
		XmlTypeUtil.setSpecialBinding(type);

		// mixed types
		if (complexType.isMixed()) {
			if (type.getName().equals(XmlTypeUtil.NAME_ANY_TYPE)) {
				// prevent enabling HasValueFlag on anyType
//				type.setConstraint(HasValueFlag.DISABLED);

				// anyType may have a simple value like a string etc.
				type.setConstraint(HasNotInheritableValue.INSTANCE);
			}
			else {
				// XXX how to treat mixed type?
				// XXX for now represent as a string value
				/*
				 * Types inheriting from a mixed don't necessarily are mixed
				 * themselves.
				 */
				type.setConstraint(HasNotInheritableValue.INSTANCE);
			}
			/*
			 * XXX String binding is a problem as it is inherited to non-mixed
			 * types
			 */
//			type.setConstraint(Binding.get(String.class));
			// mark as mixed type
			type.setConstraint(XmlMixedFlag.ENABLED);
		}

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
	public void setMetadata(AbstractDefinition<?> definition, XmlSchemaAnnotated annotated,
			String schemaLocation) {
		setMetadata(definition, annotated, schemaLocation, index);
	}

	/**
	 * Set the metadata for a definition
	 * 
	 * @param definition the definition
	 * @param annotated the XML annotated object
	 * @param schemaLocation the schema location
	 * @param index the XML index
	 */
	public static void setMetadata(AbstractDefinition<?> definition, XmlSchemaAnnotated annotated,
			String schemaLocation, XmlIndex index) {
		definition.setDescription(XMLSchemaIO.getDescription(annotated));

		List<XmlSchemaAppInfo> appInfo = XMLSchemaIO.getAppInfo(annotated);
		if (appInfo != null) {
			XmlAppInfo constraint = new XmlAppInfo(appInfo);
			if (definition instanceof DefaultPropertyDefinition) {
				((DefaultPropertyDefinition) definition).setConstraint(constraint);
			}
			else if (definition instanceof DefaultGroupPropertyDefinition) {
				((DefaultGroupPropertyDefinition) definition).setConstraint(constraint);
			}
			else if (definition instanceof DefaultTypeDefinition) {
				((DefaultTypeDefinition) definition).setConstraint(constraint);
			}

			if (definition instanceof ChildDefinition<?>) {
				handleAppInfoTargetElement(appInfo, (ChildDefinition<?>) definition, index);
			}
		}

		definition.setLocation(createLocationURI(schemaLocation, annotated));
	}

	/**
	 * Handle reference information in XML Schema AppInfo.
	 * 
	 * @param appInfos the list of AppInfos
	 * @param definition the property the infos are associated to
	 * @param index the XML index
	 */
	private static void handleAppInfoTargetElement(List<XmlSchemaAppInfo> appInfos,
			ChildDefinition<?> definition, XmlIndex index) {
		Set<QName> elementNames = null;

		for (final XmlSchemaAppInfo appInfo : appInfos) {
			for (int i = 0; i < appInfo.getMarkup().getLength(); i++) {
				final Node item = appInfo.getMarkup().item(i);
				if ("targetElement".equals(item.getNodeName())) {
					// TODO also check for GML namespace?

					final String target = item.getTextContent();
					String[] parts = target.split(":");
					QName elementName = null;
					if (parts.length == 1) {
						elementName = new QName(parts[0]);
					}
					else if (parts.length == 2) {
						Map<String, String> namespaces = index.getPrefixes().inverse();
						String ns = namespaces.get(parts[0]);
						elementName = new QName(ns, parts[1]);
					}
					if (elementName != null) {
						if (elementNames == null) {
							elementNames = new HashSet<>();
						}

						elementNames.add(elementName);
					}
				}
			}
		}

		if (elementNames != null) {
			List<QName> valuePath = null; // TODO take from existing reference
											// property, if any?
			ElementReferenceProperty constraint = new ElementReferenceProperty(index, valuePath,
					elementNames);
			if (definition instanceof DefaultPropertyDefinition) {
				((DefaultPropertyDefinition) definition).setConstraint(constraint);
			}
			else if (definition instanceof DefaultGroupPropertyDefinition) {
				((DefaultPropertyDefinition) definition).setConstraint(constraint);
			}
		}

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
		property.setConstraint(new XmlIdUnique(property));

		// set constraints
		property.setConstraint(NillableFlag.get(element.isNillable()));
		long max = (element.getMaxOccurs() == Long.MAX_VALUE) ? (Cardinality.UNBOUNDED)
				: (element.getMaxOccurs());
		property.setConstraint(Cardinality.get(element.getMinOccurs(), max));

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
		if (model != null) {
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
		if (model != null) {
			XmlSchemaContent content = model.getContent();
			if (content instanceof XmlSchemaComplexContentRestriction
					|| content instanceof XmlSchemaSimpleContentRestriction) {
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
	private void createProperties(XmlTypeDefinition typeDef, XmlSchemaComplexType item,
			String schemaLocation, String schemaNamespace) {
		// item:
		// <complexType ...>
		XmlSchemaContentModel model = item.getContentModel();
		if (model != null) {
			XmlSchemaContent content = model.getContent();

			if (content instanceof XmlSchemaComplexContentExtension) {
				// <complexContent>
				// <extension base="...">
				XmlSchemaComplexContentExtension extension = (XmlSchemaComplexContentExtension) content;
				// particle (e.g. sequence)
				if (extension.getParticle() != null) {
					XmlSchemaParticle particle = extension.getParticle();
					createPropertiesFromParticle(typeDef, particle, schemaLocation, schemaNamespace,
							false);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, null,
							schemaLocation, schemaNamespace);
				}
				// complex content may have a value in certain cases
				// (if it is mixed it definitely has, which will override this
				// setting)
				typeDef.setConstraintIfNotSet(new ComplexContentHasValue(typeDef));
				// </extension>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaComplexContentRestriction) {
				// <complexContent>
				// <restriction base="...">
				XmlSchemaComplexContentRestriction restriction = (XmlSchemaComplexContentRestriction) content;
				// particle (e.g. sequence)
				if (restriction.getParticle() != null) {
					XmlSchemaParticle particle = restriction.getParticle();
					createPropertiesFromParticle(typeDef, particle, schemaLocation, schemaNamespace,
							false);
				}
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, null,
							schemaLocation, schemaNamespace);
				}
				// complex content does not have a value
				// (only if it is mixed, which can override this setting)
				typeDef.setConstraintIfNotSet(HasValueFlag.DISABLED);
				// </restriction>
				// </complexContent>
			}
			else if (content instanceof XmlSchemaSimpleContentExtension) {
				// <simpleContent>
				// <extension base="...">
				XmlSchemaSimpleContentExtension extension = (XmlSchemaSimpleContentExtension) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = extension.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, null,
							schemaLocation, schemaNamespace);
				}
				// </extension>
				// </simpleContent>
			}
			else if (content instanceof XmlSchemaSimpleContentRestriction) {
				// <simpleContent>
				// <restriction base="...">
				XmlSchemaSimpleContentRestriction restriction = (XmlSchemaSimpleContentRestriction) content;
				// attributes
				XmlSchemaObjectCollection attributeCollection = restriction.getAttributes();
				if (attributeCollection != null) {
					createAttributesFromCollection(attributeCollection, typeDef, null,
							schemaLocation, schemaNamespace);
				}
				// </restriction>
				// </simpleContent>
			}
		}
		else {
			// no complex content (instead e.g. <sequence>)
			XmlSchemaComplexType complexType = item;
			// particle (e.g. sequence)
			if (item.getParticle() != null) {
				XmlSchemaParticle particle = complexType.getParticle();
				createPropertiesFromParticle(typeDef, particle, schemaLocation, schemaNamespace,
						false);
			}
			// attributes
			XmlSchemaObjectCollection attributeCollection = complexType.getAttributes();
			if (attributeCollection != null) {
				createAttributesFromCollection(attributeCollection, typeDef, null, schemaLocation,
						schemaNamespace);
			}
		}

		// </complexType>
	}

	private void createAttributesFromCollection(XmlSchemaObjectCollection attributeCollection,
			DefinitionGroup declaringType, String indexPrefix, String schemaLocation,
			String schemaNamespace) {
		if (indexPrefix == null) {
			indexPrefix = ""; //$NON-NLS-1$
		}

		for (int index = 0; index < attributeCollection.getCount(); index++) {
			XmlSchemaObject object = attributeCollection.getItem(index);
			if (object instanceof XmlSchemaAttribute) {
				// <attribute ... />
				XmlSchemaAttribute attribute = (XmlSchemaAttribute) object;

				createAttribute(attribute, declaringType, schemaLocation, schemaNamespace);
			}
			else if (object instanceof XmlSchemaAttributeGroup) {
				XmlSchemaAttributeGroup group = (XmlSchemaAttributeGroup) object;

				createAttributes(group, declaringType, indexPrefix + index, schemaLocation,
						schemaNamespace);
			}
			else if (object instanceof XmlSchemaAttributeGroupRef) {
				XmlSchemaAttributeGroupRef groupRef = (XmlSchemaAttributeGroupRef) object;

				if (groupRef.getRefName() != null) {
					QName groupName = groupRef.getRefName();
					// XXX extend group name with namespace?
					XmlAttributeGroupReferenceProperty property = new XmlAttributeGroupReferenceProperty(
							groupName, declaringType, this.index, groupName, true);
					// TODO add constraints?

					// set metadata
					setMetadata(property, groupRef, schemaLocation);
				}
				else {
					reporter.error(new IOMessageImpl("Unrecognized attribute group reference", null,
							object.getLineNumber(), object.getLinePosition()));
				}
			}
		}
	}

	private void createAttributes(XmlSchemaAttributeGroup group, DefinitionGroup declaringType,
			String index, String schemaLocation, String schemaNamespace) {
		createAttributesFromCollection(group.getAttributes(), declaringType, index + "_", //$NON-NLS-1$
				schemaLocation, schemaNamespace);
	}

	private void createAttribute(XmlSchemaAttribute attribute, DefinitionGroup declaringGroup,
			String schemaLocation, String schemaNamespace) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			QName attributeName = determineAttributeName(attribute, schemaNamespace);

			// resolve type by name
			XmlTypeDefinition type = getTypeForAttribute(typeName, declaringGroup, attributeName);

			// create property
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(attributeName,
					declaringGroup, type);

			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		else if (attribute.getSchemaType() != null) {
			XmlSchemaSimpleType simpleType = attribute.getSchemaType();

			// create an anonymous type
			QName anonymousName = new QName(
					getTypeIdentifier(declaringGroup) + "/" + attribute.getName(), "AnonymousType"); //$NON-NLS-2$

			AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);

			configureSimpleType(anonymousType, simpleType, schemaLocation);

			// create property
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(
					determineAttributeName(attribute, schemaNamespace), declaringGroup,
					anonymousType);

			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		else if (attribute.getRefName() != null) {
			// <attribute ref="REF_NAME" />
			// reference to a named attribute
			QName attName = attribute.getRefName();

			XmlAttributeReferenceProperty property = new XmlAttributeReferenceProperty(attName,
					declaringGroup, this.index, attName);

			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}
		else {
			/*
			 * Attribute with no type given has anySimpleType, see
			 * "http://www.w3.org/TR/2001/REC-xmlschema-1-20010502/#cAttribute_Declarations"
			 */
			// resolve type by name
			XmlTypeDefinition type = index.getOrCreateType(
					new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anySimpleType"));

			// create property
			DefaultPropertyDefinition property = new DefaultPropertyDefinition(
					determineAttributeName(attribute, schemaNamespace), declaringGroup, type);

			// set metadata and constraints
			setMetadataAndConstraints(property, attribute, schemaLocation);
		}

	}

	/**
	 * Get the named type to use for a specific XML attribute.
	 * 
	 * @param typeName the name of the referenced type
	 * @param declaringGroup the declaring group of the attribute
	 * @param attributeName the attribute name
	 * @return the type definition that should be used as the attribute type
	 */
	private XmlTypeDefinition getTypeForAttribute(QName typeName, DefinitionGroup declaringGroup,
			QName attributeName) {
		// special case handling

		// XXX INSPIRE nilReason hack
		// detect nilReason attribute TODO check GML namespace?
		if (attributeName.getLocalPart().equals("nilReason")
				&& "NilReasonType".equals(typeName.getLocalPart())
				&& declaringGroup instanceof Definition<?>) {
			Definition<?> parentDef = (Definition<?>) declaringGroup;
			// determine if parent is defined in INSPIRE
			if (parentDef.getName().getNamespaceURI() != null && parentDef.getName()
					.getNamespaceURI().startsWith("http://inspire.ec.europa.eu/schemas")) {
				// get or create custom INSPIRE NilReason type
				XmlTypeDefinition customType = (XmlTypeDefinition) this.index
						.getType(INSPIRE_NILREASON_TYPENAME);
				if (customType == null) {
					// not yet created, configure now
					customType = this.index.getOrCreateType(INSPIRE_NILREASON_TYPENAME);

					// use the original type as super type
					customType.setSuperType(this.index.getOrCreateType(typeName));

					// description with documentation of the values
					customType.setDescription(
							"Virtual type representing the GML NilReasonType adapted for the valid values specified by INSPIRE:\n\n"
									+ "unknown:\nThe correct value for the specific spatial object is not known to, and not computable by, the data provider. However, a correct value may exist.\n"
									+ "NOTE 'unknown' is applied on an object-by-object basis in a spatial data set.\n\n"
									+ "unpopulated:\nThe characteristic is not part of the dataset maintained by the data provider. However, the characteristic may exist in the real world.\n"
									+ "NOTE The characteristic receives this value for all objects in the spatial data set.\n\n"
									+ "withheld:\nThe characteristic may exist, but is confidential and not divulged by the data provider.");

					// define a custom enumeration based on valid INSPIRE void
					// reasons
					customType
							.setConstraint(new Enumeration<String>(INSPIRE_NILREASON_VALUES, true));
				}
				return customType;
			}
		}

		// default case
		return this.index.getOrCreateType(typeName);
	}

	/**
	 * Determine the qualified attribute name for a XML Schema attribute.
	 * 
	 * @param attribute the XML Schema attribute
	 * @param schemaNamespace the schema namespace
	 * @return the qualified name of the attribute
	 */
	private QName determineAttributeName(XmlSchemaAttribute attribute, String schemaNamespace) {
		if (attribute.getForm().getValue().equals(XmlSchemaForm.QUALIFIED)
				&& (attribute.getQName().getNamespaceURI() == null || attribute.getQName()
						.getNamespaceURI().equals(XMLConstants.NULL_NS_URI))) {
			/*
			 * It seems in this case the namespace is not included in
			 * attribute.getQName(). Is this a bug in the schema parser? As a
			 * workaround we provide the namespace.
			 */
			return new QName(schemaNamespace, attribute.getQName().getLocalPart());
		}

		return attribute.getQName();
	}

	/**
	 * Create the type definition for an attribute, if possible
	 * 
	 * @param attribute the XML attribute
	 * @param index the name index string
	 * @param schemaLocation the schema location
	 * @return the type definition or <code>null</code>
	 */
	private XmlTypeDefinition getAttributeType(XmlSchemaAttribute attribute, String index,
			String schemaLocation) {
		// create attributes
		QName typeName = attribute.getSchemaTypeName();
		if (typeName != null) {
			// resolve type by name
			return this.index.getOrCreateType(typeName);
		}
		else if (attribute.getSchemaType() != null) {
			XmlSchemaSimpleType simpleType = attribute.getSchemaType();

			// create an anonymous type
			QName anonymousName = new QName(
					attribute.getQName().getNamespaceURI() + "/" + attribute.getName(), //$NON-NLS-1$
					"AnonymousType");

			AnonymousXmlType anonymousType = new AnonymousXmlType(anonymousName);

			configureSimpleType(anonymousType, simpleType, schemaLocation);

			return anonymousType;
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
		property.setConstraint(new XmlIdUnique(property));

		// set constraints
		property.setConstraint(XmlAttributeFlag.ENABLED);

		if (attribute.getUse() != null) {
			long maxOccurs = (attribute.getUse().getValue()
					.equals(Constants.BlockConstants.PROHIBITED)) ? (0) : (1);
			long minOccurs = (attribute.getUse().getValue()
					.equals(Constants.BlockConstants.REQUIRED)) ? (1) : (0);
			property.setConstraint(Cardinality.get(minOccurs, maxOccurs));
		}

		property.setConstraint(NillableFlag.DISABLED);

		// special handling for XLink references (which are always XML
		// attributes)
		if (NAME_XLINK_REF.equals(property.getName())) {
			property.setConstraint(new XLinkReference());
		}

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
