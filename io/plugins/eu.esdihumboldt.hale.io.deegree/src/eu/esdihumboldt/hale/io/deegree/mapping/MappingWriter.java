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
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.commons.xml.stax.IndentingXMLStreamWriter;
import org.deegree.feature.persistence.sql.MappedAppSchema;
import org.deegree.feature.persistence.sql.config.SQLFeatureStoreConfigWriter;
import org.deegree.feature.persistence.sql.ddl.DDLCreator;
import org.deegree.feature.types.AppSchema;

import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.schema.model.Schema;

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
				getMappedSchema());

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
			mappedSchema = mapApplicationSchema(appSchema, config);

			// TODO adapt according to alignment?
		}

		return mappedSchema;
	}

}
