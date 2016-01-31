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

package eu.esdihumboldt.hale.app.bgis.ade.duplicate;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.content.IContentType;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppConstants;
import eu.esdihumboldt.hale.app.bgis.ade.common.BGISAppUtil;
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
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Generates an extended mappings for BGIS CityGML ADE based on an example
 * mapping.
 * 
 * @author Simon Templer
 */
public class GenerateDuplicates implements BGISAppConstants {

	private SchemaSpace sourceSchema;

	private SchemaSpace targetSchema;

	private Alignment examples;

	private Alignment alignment;

	private GenerateDuplicatesContext context;

	/**
	 * Generate the default value mapping based on the given configuration.
	 * 
	 * @param context the configuration for the mapping generation
	 * @throws Exception if an unrecoverable error occurs during the generation
	 */
	public void generate(GenerateDuplicatesContext context) throws Exception {
		this.context = context;

		// load project
		if (loadProject()) {
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

	private void generateMapping() {
		System.out.println("Indexing example cells...");

		// index all cells based on the target property name
		SetMultimap<String, Cell> exampleCells = HashMultimap.create();
		for (Cell cell : examples.getCells()) {
			if (cell.getTarget().size() == 1) {
				// only supports cells with one target
				EntityDefinition entityDef = CellUtil.getFirstEntity(cell.getTarget())
						.getDefinition();
				if (entityDef.getDefinition() instanceof PropertyDefinition) {
					if (ADE_NS.equals(entityDef.getDefinition().getName().getNamespaceURI())) {
						exampleCells.put(entityDef.getDefinition().getName().getLocalPart(), cell);
					}
					else
						System.out.println("WARNING: ignoring cell with non-ADE target property");
				}
				else
					System.out.println("WARNING: ignoring type cell");
			}
			else
				System.out.println("WARNING: ignoring cell with multiple or no targets");
		}

		// collect all ADE feature types
		List<TypeDefinition> featureTypes = BGISAppUtil.getADEFeatureTypes(targetSchema);

		// visit ADE properties and create cells
		System.out.println("Generating mapping from example cells for");
		String cellNote = MessageFormat.format(
				"Generated through duplication of example cells on BGIS ADE feature types.\n"
						+ "{0,date,medium}", new Date());
		DuplicateVisitor visitor = new DuplicateVisitor(exampleCells, cellNote);
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
