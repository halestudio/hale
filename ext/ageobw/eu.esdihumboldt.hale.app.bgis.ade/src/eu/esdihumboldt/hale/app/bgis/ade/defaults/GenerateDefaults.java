/*
 * Copyright (c) 2013 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.app.bgis.ade.defaults;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultSchemaSpace;

/**
 * Generates default value mappings for BGIS CityGML ADE.
 * 
 * @author Simon Templer
 */
public class GenerateDefaults {

	/**
	 * ADE namespace.
	 */
	public static final String ADE_NS = "http://www.bund.de/AGeoBw";

	/**
	 * Identifier of the alignment content type.
	 */
	public static final String ALIGNMENT_CONTENT_TYPE = "eu.esdihumboldt.hale.io.align.26";

	private Schema schema;

	private Alignment alignment;

	private GenerateDefaultsContext context;

	/**
	 * Generate the default value mapping based on the given configuration.
	 * 
	 * @param context the configuration for the mapping generation
	 * @throws Exception if an unrecoverable error occurs during the generation
	 */
	public void generate(GenerateDefaultsContext context) throws Exception {
		this.context = context;

		// load schema
		loadSchema();

		// TODO load config

		// generate mapping
		generateMapping();

		// write alignment
		writeAlignment();
	}

	private void loadSchema() throws IOProviderConfigurationException, IOException {
		System.out.println("Loading schema...");

		LocatableInputSupplier<? extends InputStream> schemaIn = new DefaultInputSupplier(
				context.getSchema());
		SchemaReader schemaReader = HaleIO.findIOProvider(SchemaReader.class, schemaIn, context
				.getSchema().getPath());
		schemaReader.setSource(schemaIn);
		IOReport report = schemaReader.execute(new NullProgressIndicator());
		if (!report.isSuccess() || !report.getErrors().isEmpty()) {
			throw new IllegalStateException("Failed to load schema");
		}
		schema = schemaReader.getSchema();
	}

	private void generateMapping() {
		System.out.println("Generating default value mapping cells for");

		// collect all ADE feature types
		List<TypeDefinition> featureTypes = new ArrayList<TypeDefinition>();
		for (TypeDefinition type : schema.getTypes()) {
			if (ADE_NS.equals(type.getName().getNamespaceURI()) && isFeatureType(type)) {
				featureTypes.add(type);
			}
		}

		// visit ADE properties and create cells
		DefaultsVisitor defs = new DefaultsVisitor();
		for (TypeDefinition type : featureTypes) {
			System.out.println(type.getDisplayName() + "...");
			defs.accept(new TypeEntityDefinition(type, SchemaSpaceID.TARGET, null));
		}

		if (defs.getCells().isEmpty()) {
			System.out.println("WARNING: no cells were created");
		}
		else {
			System.out.println(defs.getCells().size() + " cells were created.");
		}

		// create alignment
		MutableAlignment align = new DefaultAlignment();
		for (Cell cell : defs.getCells()) {
			align.addCell(cell);
		}

		this.alignment = align;
	}

	private void writeAlignment() throws Exception {
		System.out.println("Writing alignment to " + context.getOut().getAbsolutePath());

		// create alignment writer
		IContentType contentType = Platform.getContentTypeManager().getContentType(
				ALIGNMENT_CONTENT_TYPE);
		IOProviderDescriptor factory = HaleIO.findIOProviderFactory(AlignmentWriter.class,
				contentType, null);
		AlignmentWriter writer = (AlignmentWriter) factory.createExtensionObject();

		// configure alignment writer
		writer.setTargetSchema(new DefaultSchemaSpace().addSchema(schema));
		writer.setTarget(new FileIOSupplier(context.getOut()));
		writer.setAlignment(alignment);

		IOReport report = writer.execute(new NullProgressIndicator());
		if (!report.isSuccess() || !report.getErrors().isEmpty()) {
			throw new IllegalStateException("Errors while writing the alignment.");
		}
		else {
			System.out.println("Completed successfully.");
		}
	}

	/**
	 * Determine if a given type is a feature type.
	 * 
	 * @param type the type definition
	 * @return if the type represents a feature type
	 */
	private boolean isFeatureType(TypeDefinition type) {
		if ("AbstractFeatureType".equals(type.getName().getLocalPart())
				&& type.getName().getNamespaceURI().startsWith("http://www.opengis.net/gml")) {
			return true;
		}

		if (type.getSuperType() != null) {
			return isFeatureType(type.getSuperType());
		}

		return false;
	}
}
