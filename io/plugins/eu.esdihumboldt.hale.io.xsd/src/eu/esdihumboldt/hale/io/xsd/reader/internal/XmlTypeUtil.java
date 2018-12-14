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

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import java.math.BigDecimal;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaFacet;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinExclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaWhiteSpaceFacet;
import org.geotools.feature.NameImpl;
import org.geotools.feature.type.AttributeTypeImpl;
import org.geotools.xs.XSSchema;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AbstractFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ElementType;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Enumeration;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.ValidationConstraint;
import eu.esdihumboldt.hale.io.gml.geometry.Geometries;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.SkipGeometryValidation;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.UnionBinding;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.UnionEnumeration;
import eu.esdihumboldt.hale.io.xsd.reader.internal.constraint.UnionValidationConstraint;
import eu.esdihumboldt.util.validator.AndValidator;
import eu.esdihumboldt.util.validator.DigitCountValidator;
import eu.esdihumboldt.util.validator.EnumerationValidator;
import eu.esdihumboldt.util.validator.LengthValidator;
import eu.esdihumboldt.util.validator.NumberValidator;
import eu.esdihumboldt.util.validator.OrValidator;
import eu.esdihumboldt.util.validator.PatternValidator;
import eu.esdihumboldt.util.validator.Validator;

/**
 * Utility methods regarding type resolving
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class XmlTypeUtil {

//	private static final ALogger log = ALoggerFactory.getLogger(TypeUtil.class);
//	
//	private static final AGroup TYPE_RESOLVE = AGroupFactory.getGroup(Messages.getString("TypeUtil.0")); //$NON-NLS-1$

	/**
	 * The XML simple types schema
	 */
	protected static final XSSchema xsSchema = new XSSchema();

	/**
	 * GML 3.2 namespace
	 */
	private static final String NAMESPACE_GML32 = "http://www.opengis.net/gml/3.2"; //$NON-NLS-1$

	/**
	 * GML up to 3.1.x namespace
	 */
	private static final String NAMESPACE_GML = "http://www.opengis.net/gml"; //$NON-NLS-1$

	/**
	 * Qualified name of the anyType schema type
	 */
	public static final QName NAME_ANY_TYPE = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI,
			"anyType");

	/**
	 * Set of XML schema types that should get a String binding but don't get
	 * one through the Geotools bindings
	 * 
	 * @see "http://www.w3schools.com/Schema/schema_dtypes_string.asp"
	 */
	private static final Set<String> XS_STRING_TYPES = new HashSet<String>();
	static {
		XS_STRING_TYPES.add("ID"); //$NON-NLS-1$
		XS_STRING_TYPES.add("IDREF"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NCName"); //$NON-NLS-1$
		XS_STRING_TYPES.add("token"); //$NON-NLS-1$
		XS_STRING_TYPES.add("Name"); //$NON-NLS-1$
		XS_STRING_TYPES.add("language"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITY"); //$NON-NLS-1$
		XS_STRING_TYPES.add("ENTITIES"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKEN"); //$NON-NLS-1$
		XS_STRING_TYPES.add("NMTOKENS"); //$NON-NLS-1$
		XS_STRING_TYPES.add("normalizedString"); //$NON-NLS-1$
		XS_STRING_TYPES.add("QName"); //$NON-NLS-1$
	}

	private static final Set<QName> GML_GEOMETRY_TYPES = new HashSet<QName>();
	static {
		GML_GEOMETRY_TYPES.add(new QName(NAMESPACE_GML, "AbstractGeometryType"));
		GML_GEOMETRY_TYPES.add(new QName(NAMESPACE_GML32, "AbstractGeometryType"));
	}

//	/**
//	 * Get the attribute type for an GML type
//	 * 
//	 * @param typeName the type name
//	 * 
//	 * @return the attribute type or <code>null</code>
//	 */
//	public static AttributeType getGMLAttributeType(Name typeName) {
//		AttributeType gmlType = gml3Schema.get(typeName);
//		if (gmlType == null && typeName.getNamespaceURI().equals(NAMESPACE_GML3_2)) {
//			// try again with GML2/3 namespace
//			gmlType = gml3Schema.get(new NameImpl(NAMESPACE_GML, typeName.getLocalPart()));
//			//FIXME replicate type with correct namespace?
//		}
//		return gmlType;
//	}
//	
//	/**
//	 * Get the predefined attribute type (GML or XS) with the given type name
//	 * 
//	 * @param typeName the type name
//	 * 
//	 * @return the attribute type or <code>null</code>
//	 */
//	public static AttributeType getPredefinedAttributeType(Name typeName) {
//		AttributeType result = xsSchema.get(typeName);
//		
//		if (result == null) {
//			result = getGMLAttributeType(typeName);
//		}
//		
//		return result;
//	}

	/**
	 * Configure a type with defaults if possible, e.g. for simple types
	 * 
	 * @param type the type to configure
	 */
	public static void configureType(XmlTypeDefinition type) {
		// XSD simple types
		if (configureXsdSimpleType(type)) {
			return;
		}

		// TODO more configuration options?
		// TODO e.g. GML?
	}

	/**
	 * Configure the given type as XML schema simple type if possible
	 * 
	 * @param type the type to configure
	 * @return if the type could be configured as XSD simple type
	 */
	@SuppressWarnings("unchecked")
	private static boolean configureXsdSimpleType(XmlTypeDefinition type) {
		Name typeName = new NameImpl(type.getName().getNamespaceURI(),
				type.getName().getLocalPart());

		AttributeType ty = xsSchema.get(typeName);

		// special case: ID etc. - assure String binding
		if (ty != null && XS_STRING_TYPES.contains(typeName.getLocalPart())) {
			ty = new AttributeTypeImpl(typeName, java.lang.String.class, false, false,
					Collections.EMPTY_LIST, ty.getSuper(), null);
		}

		// only enable hasValue if the type is not anyType
		// anyType has special treatment in
		// XmlSchemaReader.setMetadataAndConstraints(TypeDefinition, ...)
		boolean hasValue = !typeName.getLocalPart().equals("anyType");

		if (ty != null) {
			// configure type

			// set binding
			type.setConstraint(Binding.get(ty.getBinding()));
			// simple type flag
			if (hasValue) {
				type.setConstraint(HasValueFlag.ENABLED);
			}
			// not abstract
			type.setConstraint(AbstractFlag.DISABLED);
			// not mappable
			type.setConstraint(MappingRelevantFlag.DISABLED);
			type.setConstraint(MappableFlag.DISABLED);

			type.setLocation(URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI));
			if (ty.getDescription() != null) {
				type.setDescription(ty.getDescription().toString());
			}

			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Configure a type definition for a simple type based on the
	 * {@link XmlSchemaSimpleType}.
	 * 
	 * @param type the type definition
	 * @param simpleType the schema simple type
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	public static void configureSimpleType(XmlTypeDefinition type, XmlSchemaSimpleType simpleType,
			XmlIndex index, IOReporter reporter) {
		XmlSchemaSimpleTypeContent content = simpleType.getContent();

		// it's a simple type
		type.setConstraint(HasValueFlag.ENABLED);

		if (content instanceof XmlSchemaSimpleTypeUnion) {
			// simple type union
			configureSimpleTypeUnion(type, (XmlSchemaSimpleTypeUnion) content, index, reporter);
		}
		else if (content instanceof XmlSchemaSimpleTypeList) {
			// simple type list
			configureSimpleTypeList(type, (XmlSchemaSimpleTypeList) content, index, reporter);
		}
		else if (content instanceof XmlSchemaSimpleTypeRestriction) {
			// simple type restriction
			configureSimpleTypeRestriction(type, (XmlSchemaSimpleTypeRestriction) content, index,
					reporter);
		}
		else {
			reporter.error(new IOMessageImpl(
					MessageFormat.format("Unrecognized simple type {0}", type.getName()), null,
					simpleType.getLineNumber(), simpleType.getLinePosition()));
		}
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * restriction.
	 * 
	 * @param type the type definition
	 * @param restriction the simple type restriction
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeRestriction(XmlTypeDefinition type,
			XmlSchemaSimpleTypeRestriction restriction, XmlIndex index, IOReporter reporter) {
		QName baseTypeName = restriction.getBaseTypeName();

		XmlTypeDefinition baseTypeDef;
		if (baseTypeName != null) {
			// resolve super type
			baseTypeDef = index.getOrCreateType(baseTypeName);
		}
		else if (restriction.getBaseType() != null) {
			// simple schema type
			XmlSchemaSimpleType simpleType = restriction.getBaseType();

			// create an anonymous type
			QName anonymousName = new QName(
					type.getName().getNamespaceURI() + "/" + type.getName().getLocalPart(),
					"AnonymousSuperType"); //$NON-NLS-1$

			baseTypeDef = new AnonymousXmlType(anonymousName);

			XmlTypeUtil.configureSimpleType(type, simpleType, index, reporter);

			// no schema location available at this point
			final String schemaLoc = null;

			// set metadata
			XmlSchemaReader.setMetadata(type, simpleType, schemaLoc, index);

		}
		else {
			reporter.error(new IOMessageImpl(
					"Simple type restriction without base type, skipping type configuration.", null,
					restriction.getLineNumber(), restriction.getLinePosition()));
			return;
		}

		// set super type
		type.setSuperType(baseTypeDef);
		// mark as restriction
		type.setConstraint(RestrictionFlag.ENABLED);
		// assign no binding, inherit from super type

		// The following code expects schema to be valid.

		List<Validator> validators = new LinkedList<Validator>();

		// patterns and enumerations in one step are ORed together!
		List<String> values = new LinkedList<String>();
		List<Validator> patternValidators = new LinkedList<Validator>();

		// TODO different handling for date/time/g.../duration in
		// (min|max)(In|Ex)clusive
		// XXX only for date, time, duration, dateTime, gMonthDay, gYearMonth?
		// no also for some cases of gYear, gMonth, gDay (they can have a
		// timezone!)
		// but only need to handle cases where isDecimal() is false...

		XmlSchemaObjectCollection facets = restriction.getFacets();
		for (int i = 0; i < facets.getCount(); i++) {
			XmlSchemaFacet facet = (XmlSchemaFacet) facets.getItem(i);
			if (facet instanceof XmlSchemaEnumerationFacet) {
				values.add(facet.getValue().toString());
			}
			else if (facet instanceof XmlSchemaFractionDigitsFacet) {
				validators.add(new DigitCountValidator(DigitCountValidator.Type.FRACTIONDIGITS,
						Integer.parseInt(facet.getValue().toString())));
			}
			else if (facet instanceof XmlSchemaLengthFacet) {
				validators.add(new LengthValidator(LengthValidator.Type.EXACT,
						Integer.parseInt(facet.getValue().toString())));
			}
			else if (facet instanceof XmlSchemaMaxExclusiveFacet) {
				if (isDecimal(facet.getValue().toString())) // number or date?
					validators.add(new NumberValidator(NumberValidator.Type.MAXEXCLUSIVE,
							new BigDecimal(facet.getValue().toString())));
				else
					reporter.warn(new IOMessageImpl(
							"(min|max)(In|Ex)clusive not supported for non-number types", null,
							facet.getLineNumber(), facet.getLinePosition()));
			}
			else if (facet instanceof XmlSchemaMaxInclusiveFacet) {
				if (isDecimal(facet.getValue().toString())) // number or date?
					validators.add(new NumberValidator(NumberValidator.Type.MAXINCLUSIVE,
							new BigDecimal(facet.getValue().toString())));
				else
					reporter.warn(new IOMessageImpl(
							"(min|max)(In|Ex)clusive not supported for non-number types", null,
							facet.getLineNumber(), facet.getLinePosition()));
			}
			else if (facet instanceof XmlSchemaMaxLengthFacet) {
				validators.add(new LengthValidator(LengthValidator.Type.MAXIMUM,
						Integer.parseInt(facet.getValue().toString())));
			}
			else if (facet instanceof XmlSchemaMinLengthFacet) {
				validators.add(new LengthValidator(LengthValidator.Type.MINIMUM,
						Integer.parseInt(facet.getValue().toString())));
			}
			else if (facet instanceof XmlSchemaMinExclusiveFacet) {
				if (isDecimal(facet.getValue().toString())) // number or date?
					validators.add(new NumberValidator(NumberValidator.Type.MINEXCLUSIVE,
							new BigDecimal(facet.getValue().toString())));
				else
					reporter.warn(new IOMessageImpl(
							"(min|max)(In|Ex)clusive not supported for non-number types", null,
							facet.getLineNumber(), facet.getLinePosition()));
			}
			else if (facet instanceof XmlSchemaMinInclusiveFacet) {
				if (isDecimal(facet.getValue().toString())) // number or date?
					validators.add(new NumberValidator(NumberValidator.Type.MININCLUSIVE,
							new BigDecimal(facet.getValue().toString())));
				else
					reporter.warn(new IOMessageImpl(
							"(min|max)(In|Ex)clusive not supported for non-number types", null,
							facet.getLineNumber(), facet.getLinePosition()));
			}
			else if (facet instanceof XmlSchemaPatternFacet) {
				patternValidators.add(new PatternValidator(facet.getValue().toString()));
			}
			else if (facet instanceof XmlSchemaTotalDigitsFacet) {
				validators.add(new DigitCountValidator(DigitCountValidator.Type.TOTALDIGITS,
						Integer.parseInt(facet.getValue().toString())));
			}
			else if (facet instanceof XmlSchemaWhiteSpaceFacet) {
				reporter.info(new IOMessageImpl("White space facet not supported", null,
						facet.getLineNumber(), facet.getLinePosition()));
				// Nothing to validate according to w3.
				// Values should be processed according to rule?
			}
			else {
				reporter.error(
						new IOMessageImpl("Unrecognized facet: " + facet.getClass().getSimpleName(),
								null, facet.getLineNumber(), facet.getLinePosition()));
			}
		}

		if (!patternValidators.isEmpty())
			validators.add(new OrValidator(patternValidators));

		if (!values.isEmpty()) {
			// set enumeration constraint
			// no check of which values are okay, they must be validated
			// somewhere else.
			// XXX conversion to be done?
			type.setConstraint(new Enumeration<String>(values, false));
			validators.add(new EnumerationValidator(values));
		}

		if (!validators.isEmpty())
			type.setConstraint(new ValidationConstraint(new AndValidator(validators), type));
	}

	/**
	 * Checks whether the given string can be converted to a decimal.
	 * 
	 * @param s the string to check
	 * @return true, iff the sting can be converted to a decimal
	 */
	private static boolean isDecimal(String s) {
		try {
			new BigDecimal(s);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * list.
	 * 
	 * @param type the type definition
	 * @param list the simple type list
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeList(XmlTypeDefinition type,
			XmlSchemaSimpleTypeList list, XmlIndex index, IOReporter reporter) {
		XmlTypeDefinition elementType = null;
		if (list.getItemType() != null) {
			XmlSchemaSimpleType simpleType = list.getItemType();
			if (simpleType.getQName() != null) {
				// named type
				elementType = index.getOrCreateType(simpleType.getQName());
			}
			else {
				// anonymous type
				QName baseName = new QName(
						type.getName().getNamespaceURI() + "/" + type.getName().getLocalPart(), //$NON-NLS-1$
						"AnonymousType"); //$NON-NLS-1$

				elementType = new AnonymousXmlType(baseName);
			}

			configureSimpleType(elementType, simpleType, index, reporter);
		}
		else if (list.getItemTypeName() != null) {
			// named type
			elementType = index.getOrCreateType(list.getItemTypeName());
		}

		if (elementType != null) {
			// set constraints on type

			// element type
			type.setConstraint(ElementType.createFromType(elementType));
			// list binding
			type.setConstraint(Binding.get(List.class));
		}
		else {
			reporter.error(new IOMessageImpl("Unrecognized base type for simple type list", null,
					list.getLineNumber(), list.getLinePosition()));
		}
	}

	/**
	 * Configure a type definition for a simple type based on a simple type
	 * union.
	 * 
	 * @param type the type definition
	 * @param union the simple type union
	 * @param index the XML index for resolving type definitions
	 * @param reporter the report
	 */
	private static void configureSimpleTypeUnion(XmlTypeDefinition type,
			XmlSchemaSimpleTypeUnion union, XmlIndex index, IOReporter reporter) {
		XmlSchemaObjectCollection baseTypes = union.getBaseTypes();

		// collect type definitions
		Set<TypeDefinition> unionTypes = new HashSet<TypeDefinition>();

		if (union.getMemberTypesQNames() != null) {
			for (QName unionMember : union.getMemberTypesQNames())
				unionTypes.add(index.getOrCreateType(unionMember));
		}

		// base type definitions
		if (baseTypes != null && baseTypes.getCount() > 0) {
			for (int i = 0; i < baseTypes.getCount(); i++) {
				XmlSchemaObject baseType = baseTypes.getItem(i);
				if (baseType instanceof XmlSchemaSimpleType) {
					XmlSchemaSimpleType simpleType = (XmlSchemaSimpleType) baseType;

					// Here it is a xs:localSimpleTypes, name attribute is
					// prohibited!
					// So it always is a anonymous type.
					QName baseName = new QName(
							type.getName().getNamespaceURI() + "/" + type.getName().getLocalPart(), //$NON-NLS-1$
							"AnonymousType" + i); //$NON-NLS-1$
					XmlTypeDefinition baseDef = new AnonymousXmlType(baseName);

					configureSimpleType(baseDef, simpleType, index, reporter);
					unionTypes.add(baseDef);
				}
				else {
					reporter.error(new IOMessageImpl("Unrecognized base type for simple type union",
							null, union.getLineNumber(), union.getLinePosition()));
				}
			}
		}

		// binding constraint
		type.setConstraint(new UnionBinding(unionTypes));
		// enumeration constraint
		type.setConstraint(new UnionEnumeration(unionTypes));
		// validation constraint
		type.setConstraint(new UnionValidationConstraint(unionTypes));
	}

	/**
	 * Determine if there is a special binding available for a type (apart from
	 * explicit definition in the schema)
	 * 
	 * @param type the type definition
	 * @return the special binding or <code>null</code>
	 */
	public static boolean setSpecialBinding(XmlTypeDefinition type) {
		// determine special bindings

		// geometry bindings
		Geometries geoms = Geometries.getInstance();

		try {
			Iterable<TypeConstraint> constraints = geoms.getTypeConstraints(type);
			if (constraints != null) {
				// set the geometry related constraints (Binding, ElementType,
				// GeometryType)
				for (TypeConstraint constraint : constraints) {
					type.setConstraint(constraint);
				}
			}

			// enable augmented value, as the derived geometry will be stored as
			// the value
			// XXX should this be done in handler?!
			type.setConstraint(AugmentedValueFlag.ENABLED);
			type.setConstraint(SkipGeometryValidation.getInstance());
		} catch (GeometryNotSupportedException e) {
			// ignore - is no geometry or is not recognized
		}

		// XXX the old way
//		if (GML_GEOMETRY_TYPES.contains(type.getName())) {
//			//XXX just assign GeometryProperty binding for now
//			//FIXME concept of binding constraint and geometry property must be adapted to include built-in support for multiple geometries (with possible different CRS)
//			type.setConstraint(Binding.get(GeometryProperty.class));
//			//TODO set geometry type?
////			type.setConstraint(...); 
//			
//			// enable augmented value, as the derived geometry will be stored as the value
//			type.setConstraint(AugmentedValueFlag.ENABLED); 
//			return true;
//		}

		// otherwise the super type binding will be used
		return false;
	}

}
