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

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.deegree.mapping.config.MappingConfiguration;
import eu.esdihumboldt.hale.io.deegree.mapping.model.AppSchemaDecorator;
import eu.esdihumboldt.hale.io.deegree.mapping.model.MappedAppSchemaCopy;
import eu.esdihumboldt.hale.io.deegree.mapping.model.ModelHelper;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

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

	/**
	 * Create a new mapping writer.
	 * 
	 * @param targetSchema the target schema
	 * @param alignment the alignment, may be <code>null</code>
	 * @param config the configuration
	 */
	public MappingWriter(Schema targetSchema, @Nullable Alignment alignment,
			MappingConfiguration config) {
		super();
		this.targetSchema = targetSchema;
		this.alignment = alignment;
		this.config = config;
	}

	/**
	 * Save the SQLFeatureStore configuration.
	 * 
	 * @param out the output stream to save the configuration to
	 * @throws Exception if an error occurs saving the configuration
	 */
	public void saveConfig(OutputStream out) throws Exception {
		// XXX also takes properties with href primitive mappings
		SQLFeatureStoreConfigWriter configWriter = new SQLFeatureStoreConfigWriter(
				getMappedSchema(), getPropertiesWithPrimitiveHref());

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
	 * Note that deegree seems to only support first level properties to be
	 * named here, that have an XLink attribute group. Also, it is not possible
	 * to specify the relation to a feature type, which means they are used for
	 * any feature type.
	 * 
	 * @return the list of qualified names of properties
	 */
	private List<QName> getPropertiesWithPrimitiveHref() {
		// TODO Auto-generated method stub

		return null;
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
		});
	}

	/**
	 * Adapt the given schema based on the alignment.
	 * 
	 * @param appSchema the application schema to adapt
	 * @return the adapted schema
	 */
	protected AppSchema adaptSchema(AppSchema appSchema) {
		if (alignment != null) {
			// determine types that are mapped to
			Set<TypeDefinition> targetTypes = alignment.getTypeCells().stream().flatMap(c -> {
				return c.getTarget().values().stream();
			}).map(e -> e.getDefinition().getType()).collect(Collectors.toSet());
			// the feature type name corresponds to the element name
			Set<QName> targetElements = targetTypes.stream()
					.<XmlElement> flatMap(
							t -> t.getConstraint(XmlElements.class).getElements().stream())
					.map(e -> e.getName()).collect(Collectors.toSet());

			return new AppSchemaDecorator(appSchema) {

				@Override
				public List<FeatureType> getFeatureTypes(String namespace,
						boolean includeCollections, boolean includeAbstracts) {
					List<FeatureType> fts = super.getFeatureTypes(namespace, includeCollections,
							includeAbstracts);

					return fts.stream().filter(ft -> {
						// only accept types that are part of the mapped-to
						// types
						return targetElements.contains(ft.getName());
					}).collect(Collectors.toList());
				}
			};
		}
		else {
			return appSchema;
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
