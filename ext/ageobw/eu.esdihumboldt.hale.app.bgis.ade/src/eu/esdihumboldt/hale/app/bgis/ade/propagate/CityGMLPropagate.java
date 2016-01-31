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

package eu.esdihumboldt.hale.app.bgis.ade.propagate;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.content.IContentType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppConstants;
import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppUtil;
import eu.esdihumboldt.hale.app.bgis.ade.propagate.config.ExcelFeatureMap;
import eu.esdihumboldt.hale.app.bgis.ade.propagate.config.FeatureMap;
import eu.esdihumboldt.hale.common.align.io.AlignmentWriter;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.core.io.HaleIO;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.extension.IOProviderDescriptor;
import eu.esdihumboldt.hale.common.core.io.impl.NullProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.FileIOSupplier;
import eu.esdihumboldt.hale.common.core.io.supplier.LocatableInputSupplier;
import eu.esdihumboldt.hale.common.core.report.Report;
import eu.esdihumboldt.hale.common.core.report.ReportHandler;
import eu.esdihumboldt.hale.common.headless.impl.ProjectTransformationEnvironment;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.gml.CityGMLConstants;

/**
 * Generates an extended mapping for BGIS CityGML ADE based on an example
 * mapping.
 * 
 * @author Simon Templer
 */
public class CityGMLPropagate implements BGISAppConstants, CityGMLConstants {

	private SchemaSpace sourceSchema;

	private SchemaSpace targetSchema;

	private Schema cityGMLSource;

	private Alignment examples;

	private Alignment alignment;

	private FeatureMap config;

	private CityGMLPropagateContext context;

	/**
	 * Generate the default value mapping based on the given configuration.
	 * 
	 * @param context the configuration for the mapping generation
	 * @throws Exception if an unrecoverable error occurs during the generation
	 */
	public void generate(CityGMLPropagateContext context) throws Exception {
		this.context = context;

		// load project
		if (loadProject()) {
			// load CityGML schema
			loadCityGML();

			// load the feature map
			loadConfig();

			// generate mapping
			generateMapping();

			// write alignment
			writeAlignment();
		}
	}

	private boolean loadProject() throws IOException {
		final AtomicBoolean success = new AtomicBoolean(true);

		LocatableInputSupplier<? extends InputStream> projectIn = new DefaultInputSupplier(
				context.getProject());
		ProjectTransformationEnvironment env = new ProjectTransformationEnvironment("sample",
				projectIn, new ReportHandler() {

					@Override
					public void publishReport(Report<?> report) {
						if (report.isSuccess() && report.getErrors().isEmpty()) {
							System.out.println(report.getSummary());
						}
						else {
							System.err.println("Error loading project: " + report.getSummary());
							success.set(false);
						}
					}
				});

		if (success.get()) {
			this.sourceSchema = env.getSourceSchema();
			this.targetSchema = env.getTargetSchema();
			this.examples = env.getAlignment();

			return true;
		}

		return false;
	}

	private void loadCityGML() throws IOProviderConfigurationException, IOException {
		System.out.println("Loading schema...");

		LocatableInputSupplier<? extends InputStream> schemaIn = new DefaultInputSupplier(
				context.getSourceSchema());
		SchemaReader schemaReader = HaleIO.findIOProvider(SchemaReader.class, schemaIn, context
				.getSourceSchema().getPath());
		schemaReader.setSource(schemaIn);
		IOReport report = schemaReader.execute(new NullProgressIndicator());
		if (!report.isSuccess() || !report.getErrors().isEmpty()) {
			throw new IllegalStateException("Failed to load schema");
		}
		cityGMLSource = schemaReader.getSchema();
	}

	private void loadConfig() {
		System.out.println("Loading feature map...");

		try {
			config = new ExcelFeatureMap(context.getConfig());
		} catch (Exception e) {
			throw new IllegalStateException("Failed to load feature map configuration", e);
		}
	}

	private void generateMapping() {
		System.out.println("Indexing example cells...");

		// index all cells based on the target property name
		SetMultimap<String, Cell> bgisExamples = HashMultimap.create();
		SetMultimap<QName, Cell> cityGMLExamples = HashMultimap.create();

		for (Cell cell : examples.getCells()) {
			if (cell.getTarget().size() == 1) {
				// only supports cells with one target
				EntityDefinition entityDef = CellUtil.getFirstEntity(cell.getTarget())
						.getDefinition();
				// XXX check source?!
				if (entityDef.getDefinition() instanceof PropertyDefinition) {
					QName name = entityDef.getDefinition().getName();
					if (ADE_NS.equals(name.getNamespaceURI())) {
						bgisExamples.put(name.getLocalPart(), cell);
					}
					else if (name.getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)) {
						// XXX only support level 1 properties?
						cityGMLExamples.put(name, cell);
					}
					else
						System.out
								.println("WARNING: ignoring cell with target property neither from CityGML nor from BGIS ADE");
				}
				else
					System.out.println("WARNING: ignoring type cell");
			}
			else
				System.out.println("WARNING: ignoring cell with multiple or no targets");
		}

		// collect all ADE feature types
		List<TypeDefinition> featureTypes = BGISAppUtil.getADEFeatureTypes(targetSchema);

		// collect ADE display names
		Set<String> adeTypeNames = new HashSet<String>();
		for (TypeDefinition type : featureTypes) {
			adeTypeNames.add(type.getDisplayName());
		}

		// collect possibly relevant target CityGML feature types
		for (TypeDefinition type : targetSchema.getTypes()) {
			if (type.getName().getNamespaceURI().startsWith(CITYGML_NAMESPACE_CORE)
					&& BGISAppUtil.isFeatureType(type)) {
				if (!adeTypeNames.contains(type.getDisplayName())) {
					/*
					 * But ensure to only add those that do not share the
					 * display name with an ADE type, as in the feature map the
					 * type identification is only done on based on the display
					 * name, and ADE types take precedent.
					 */
					featureTypes.add(type);
				}
			}
		}

		// visit ADE properties and create cells
		System.out.println("Generating mapping from example cells for");
		String cellNote = MessageFormat.format(
				"Generated through propagation of example cells on CityGML and BGIS ADE feature types.\n"
						+ "{0,date,medium}", new Date());
		CityGMLPropagateVisitor visitor = new CityGMLPropagateVisitor(cityGMLSource, bgisExamples,
				cityGMLExamples, config, cellNote);
		for (TypeDefinition type : featureTypes) {
			System.out.println(type.getDisplayName() + "...");
			visitor.accept(new TypeEntityDefinition(type, SchemaSpaceID.TARGET, null));
		}

		if (visitor.getCells().isEmpty()) {
			System.out.println("WARNING: no cells were created");
		}
		else {
			System.out.println(visitor.getCells().size() + " cells were created.");
		}

		// create alignment
		MutableAlignment align = new DefaultAlignment();
		for (MutableCell cell : visitor.getCells()) {
			align.addCell(cell);
		}

		this.alignment = align;
	}

	private void writeAlignment() throws Exception {
		System.out.println("Writing alignment to " + context.getOut().getAbsolutePath());

		// create alignment writer
		IContentType contentType = HalePlatform.getContentTypeManager().getContentType(
				ALIGNMENT_CONTENT_TYPE);
		IOProviderDescriptor factory = HaleIO.findIOProviderFactory(AlignmentWriter.class,
				contentType, null);
		AlignmentWriter writer = (AlignmentWriter) factory.createExtensionObject();

		// configure alignment writer
		writer.setSourceSchema(sourceSchema);
		writer.setTargetSchema(targetSchema);
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

}
