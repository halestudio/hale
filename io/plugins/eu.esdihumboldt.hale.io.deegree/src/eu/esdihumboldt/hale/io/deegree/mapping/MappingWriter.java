/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.deegree.mapping;

import static eu.esdihumboldt.hale.io.deegree.mapping.MappingHelper.mapApplicationSchema;
import static eu.esdihumboldt.hale.io.deegree.mapping.MappingHelper.readApplicationSchema;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.commons.xml.stax.IndentingXMLStreamWriter;
import org.deegree.feature.persistence.sql.FeatureTypeMapping;
import org.deegree.feature.persistence.sql.MappedAppSchema;
import org.deegree.feature.persistence.sql.config.SQLFeatureStoreConfigWriter;
import org.deegree.feature.persistence.sql.ddl.DDLCreator;
import org.deegree.feature.types.AppSchema;
import org.deegree.feature.types.FeatureType;

import eu.esdihumboldt.hale.common.align.helper.EntityFinder;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.codelist.config.CodeListAssociations;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.schema.Classification;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Reference;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.ReferenceProperty;
import eu.esdihumboldt.hale.io.deegree.mapping.config.MappingConfiguration;
import eu.esdihumboldt.hale.io.deegree.mapping.config.PrimitiveLinkMode;
import eu.esdihumboldt.hale.io.deegree.mapping.model.AppSchemaDecorator;
import eu.esdihumboldt.hale.io.deegree.mapping.model.MappedAppSchemaCopy;
import eu.esdihumboldt.hale.io.deegree.mapping.model.ModelHelper;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.reader.XmlSchemaReader;

/**
 * Writer for deegree feature mappings based on hale schema and alignment.
 * 
 * @author Simon Templer
 */
public class MappingWriter {

	/**
	 * The target schema.
	 */
	protected final Schema targetSchema;

	/**
	 * The alignment.
	 */
	protected final Alignment alignment;

	private MappedAppSchema mappedSchema;

	private final MappingConfiguration config;

	private final SimpleLog log;

	private final ProjectInfo projectInfo;

	/**
	 * Create a new mapping writer.
	 * 
	 * @param targetSchema the target schema
	 * @param alignment the alignment, may be <code>null</code>
	 * @param projectInfo the project information, may be <code>null</code>
	 * @param config the configuration
	 * @param log the process log
	 */
	public MappingWriter(Schema targetSchema, @Nullable Alignment alignment,
			@Nullable ProjectInfo projectInfo, MappingConfiguration config, SimpleLog log) {
		super();
		this.targetSchema = targetSchema;
		this.alignment = alignment;
		this.projectInfo = projectInfo;
		this.config = config;
		this.log = log;
	}

	/**
	 * Save the SQLFeatureStore configuration.
	 * 
	 * @param out the output stream to save the configuration to
	 * @throws Exception if an error occurs saving the configuration
	 */
	public void saveConfig(OutputStream out) throws Exception {
		Set<QName> propertiesWithPrimitiveHref = getPropertiesWithPrimitiveHref();
		if (propertiesWithPrimitiveHref.isEmpty()) {
			log.info("Identified no properties with primitive link");
		}
		else {
			log.info("Identified the following properties with primitive links: "
					+ propertiesWithPrimitiveHref.stream().map(QName::toString)
							.collect(Collectors.joining(", ")));
		}

		SQLFeatureStoreConfigWriter configWriter = new SQLFeatureStoreConfigWriter(
				getMappedSchema(), new ArrayList<>(propertiesWithPrimitiveHref));

		List<String> schemaUrls = new ArrayList<>();

		// TODO configurable?
		schemaUrls.add(targetSchema.getLocation().toASCIIString());

		XMLStreamWriter xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
		xmlWriter = new IndentingXMLStreamWriter(xmlWriter);
		try {
			configWriter.writeConfig(xmlWriter, config.getJDBCConnectionId(), schemaUrls);
		} finally {
			xmlWriter.close();
		}
	}

	/**
	 * Get the properties where a primitive mapping should be used for XLinks.
	 * 
	 * Note that deegree seems to only support properties to be named here, that
	 * have an XLink attribute group. Also, it is not possible to specify the
	 * relation to a feature type, or the nesting within a type, which means
	 * they apply for any feature type and at any level.
	 * 
	 * @return the list of qualified names of properties
	 */
	protected Set<QName> getPropertiesWithPrimitiveHref() {
		switch (config.getPrimitiveLinkMode()) {
		case targetElement: // fall through
		case inspire:
			EntityFinder finder = new EntityFinder(e -> {
				if (e instanceof PropertyEntityDefinition) {
					// is a property
					PropertyDefinition prop = ((PropertyEntityDefinition) e).getDefinition();
					Reference ref = prop.getConstraint(Reference.class);
					if ("href".equals(prop.getName().getLocalPart()) && ref.isReference()) {
						// by default here we treat everything as primitive that
						// does not have types associated
						boolean primitive = true;
						// -> check referenced types
						if (ref.getReferencedTypes() != null
								&& !ref.getReferencedTypes().isEmpty()) {
							// concrete types referenced
							primitive = false;
						}
						// -> check referenced types in a parent
						// ReferenceProperty (e.g. gml:ReferenceType)
						if (primitive) {
							EntityDefinition parent = AlignmentUtil.getParent(e);
							if (parent instanceof PropertyEntityDefinition) {
								PropertyDefinition parentProp = ((PropertyEntityDefinition) parent)
										.getDefinition();
								ReferenceProperty parentRef = parentProp
										.getConstraint(ReferenceProperty.class);
								if (parentRef.isReference()
										&& parentRef.getReferencedTypes() != null
										&& !parentRef.getReferencedTypes().isEmpty()) {
									// concrete types referenced
									primitive = false;
								}
								else if (config.getPrimitiveLinkMode()
										.equals(PrimitiveLinkMode.inspire)) {
									// in INSPIRE mode, only mark inspire
									// properties as primitive
									boolean inspireProp = parentProp.getName().getNamespaceURI()
											.contains("inspire.ec.europa.eu");
									if (!inspireProp) {
										primitive = false;
									}
								}
							}
						}

						return primitive;
					}
				}

				return false;
			}, 5);

			List<EntityDefinition> entities = finder.find(getTargetTypes().stream()
					.map(t -> new TypeEntityDefinition(t, SchemaSpaceID.TARGET, null))
					.collect(Collectors.toList()));

			return entities.stream().map(e -> AlignmentUtil.getParent(e))
					.map(e -> e.getDefinition().getName()).collect(Collectors.toSet());

		case codeListAssociation: // code list associations as primitive links
			if (projectInfo != null) {
				CodeListAssociations associations = projectInfo
						.getSetting(CodeListAssociations.KEY_ASSOCIATIONS)
						.as(CodeListAssociations.class);

				return associations.getAssociations().keySet().stream().map(e -> {
					List<QName> names = e.getNames();
					if (names.size() > 1) {
						QName candidate = names.get(names.size() - 1);
						if (!candidate.equals(XmlSchemaReader.NAME_XLINK_REF)) {
							// in case there is an association directly at the
							// property
							// (though that should not be the case for
							// gml:ReferenceTypes)
							return candidate;
						}
						else {
							if (names.size() > 2) {
								return names.get(names.size() - 2);
							}
							else {
								// can't return parent
								return null;
							}
						}
					}
					else {
						// can't use type name
						return null;
					}
				}).filter(e -> e != null).collect(Collectors.toSet());
			}
			else {
				log.error(
						"Can't get information on code list associations for primitive links: No project information available");
				return new HashSet<>();
			}

		case none: // no primitive links
		default:
			return new HashSet<>();
		}
	}

	/**
	 * Save the DDL.
	 * 
	 * @param out the output stream to save the DDL to
	 * @throws Exception if an error occurs saving the DDL
	 */
	public void saveDDL(OutputStream out) throws Exception {
		List<String> ddl = new ArrayList<>(Arrays.asList(
				DDLCreator.newInstance(getMappedSchema(), config.getSQLDialect()).getDDL()));
		ddl.add("");
		String joined = ddl.stream().collect(Collectors.joining(";\n"));
		try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
			writer.write(joined);
		}
	}

	/**
	 * Get the mapped application schema.
	 * 
	 * @return the mapped application schema
	 * @throws Exception if an error occurs configuring the mapped schema
	 */
	public MappedAppSchema getMappedSchema() throws Exception {
		if (mappedSchema == null) {
			AppSchema appSchema = readApplicationSchema(targetSchema.getLocation().toString());
			appSchema = adaptSchema(appSchema);
			MappedAppSchema tmpMapping = mapApplicationSchema(appSchema, config);

			// adapt according to alignment / configuration
			this.mappedSchema = adaptMapping(tmpMapping, config);
		}

		return mappedSchema;
	}

	/**
	 * Adapt the given mapping based on the mapping configuration.
	 * 
	 * @param mapping the original mapping
	 * @param config the mapping configuration
	 * @return the adapted mapping
	 */
	private MappedAppSchema adaptMapping(MappedAppSchema mapping, MappingConfiguration config) {
		Function<FeatureTypeMapping, String> prefixGenerator;
		switch (config.getIDPrefixMode()) {
		case element:
			// use element local name plus underscore
			prefixGenerator = (ftm) -> ftm.getFeatureType().getLocalPart() + '_';
			break;
		case deegree:
		default:
			// return unchanged
			return mapping;
		}

		return new MappedAppSchemaCopy(mapping, fts -> {
			return fts.map(ftm -> {
				return ModelHelper.withFIDMapping(
						ModelHelper.withPrefix(prefixGenerator.apply(ftm), ftm.getFidMapping()),
						ftm);
			});
		}, config.includeFeatureCollections(), config.includeAbstractTypes());
	}

	/**
	 * Adapt the given schema based on the alignment.
	 * 
	 * @param appSchema the application schema to adapt
	 * @return the adapted schema
	 */
	protected AppSchema adaptSchema(AppSchema appSchema) {
		Set<TypeDefinition> targetTypes = getTargetTypes();
		// the feature type name corresponds to the element name
		Set<QName> targetElements = targetTypes.stream()
				.<XmlElement> flatMap(
						t -> t.getConstraint(XmlElements.class).getElements().stream())
				.map(e -> e.getName()).collect(Collectors.toSet());

		return new AppSchemaDecorator(appSchema) {

			@Override
			public List<FeatureType> getFeatureTypes(String namespace, boolean includeCollections,
					boolean includeAbstracts) {
				List<FeatureType> fts = super.getFeatureTypes(namespace,
						config.includeFeatureCollections(), config.includeAbstractTypes());

				return fts.stream().filter(ft -> {
					// only accept types that are part of the mapped-to
					// types
					return targetElements.contains(ft.getName());
				}).collect(Collectors.toList());
			}
		};
	}

	/**
	 * Get the target feature types.
	 * 
	 * @return the set of types
	 */
	protected Set<TypeDefinition> getTargetTypes() {
		if (alignment != null) {
			// default behavior when alignment is present
			// determine feature types that are mapped to
			return alignment.getTypeCells().stream().flatMap(c -> {
				return c.getTarget().values().stream();
			}).map(e -> e.getDefinition().getType()).filter(
					t -> Classification.getClassification(t).equals(Classification.CONCRETE_FT))
					.collect(Collectors.toSet());
		}
		else {
			// w/o alignment, default to mapping relevant feature types
			Set<TypeDefinition> types = targetSchema.getMappingRelevantTypes().stream().filter(
					t -> Classification.getClassification(t).equals(Classification.CONCRETE_FT))
					.collect(Collectors.toSet());
			if (types.isEmpty()) {
				// if there are no mapping relevant feature types, use all
				// feature types
				return targetSchema.getTypes().stream().filter(
						t -> Classification.getClassification(t).equals(Classification.CONCRETE_FT))
						.collect(Collectors.toSet());
			}
			else {
				return types;
			}
		}
	}

	/**
	 * Determine if a hale type definition and a deegree feature type represent
	 * the same type.
	 * 
	 * @param t1 the hale type definition
	 * @param t2 the deegree feature type
	 * @return <code>true</code> if the definitions refer to the same type,
	 *         <code>false</code> otherwise
	 */
	protected boolean typeMatches(TypeDefinition t1, FeatureType t2) {
		QName ftName = t2.getName();

		// the feature type name corresponds to the element name
		Collection<? extends XmlElement> elements = t1.getConstraint(XmlElements.class)
				.getElements();
		return elements.stream().anyMatch(element -> element.getName().equals(ftName));
	}

}
