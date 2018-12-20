/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer;

import java.net.URI;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.CellParentWrapper;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.MappingContext;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.PropertyTransformationHandler;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.PropertyTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandlerFactory;
import eu.esdihumboldt.hale.io.xtraserver.writer.handler.UnsupportedTransformationException;

/**
 * Translates an Alignment to a XtraServer Mapping.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class XtraServerMappingGenerator {

	private final Alignment alignment;
	private final TypeTransformationHandlerFactory typeHandlerFactory;
	private final PropertyTransformationHandlerFactory propertyHandlerFactory;
	private final ProgressIndicator progress;
	private final MappingContext mappingContext;

	/**
	 * Constructor
	 * 
	 * @param alignment the Alignment with all cells
	 * @param targetSchemaSpace the target schema
	 * @param progress Progress indicator
	 * @param projectProperties project transformation properties
	 * @param projectInfo project info
	 * @param projectLocation project file
	 * @param reporter reporter
	 */
	public XtraServerMappingGenerator(final Alignment alignment,
			final SchemaSpace targetSchemaSpace, final ProgressIndicator progress,
			final Map<String, Value> projectProperties, final ProjectInfo projectInfo,
			final URI projectLocation, final IOReporter reporter) {
		this.alignment = alignment;
		mappingContext = new MappingContext(alignment, targetSchemaSpace, projectProperties,
				projectInfo, projectLocation, reporter);
		this.typeHandlerFactory = TypeTransformationHandler.createFactory(mappingContext);
		this.propertyHandlerFactory = PropertyTransformationHandler.createFactory(mappingContext);
		// Calculate the total work units for the progress indicator (+1 for
		// writing the
		// file)
		int c = 1;
		for (final Cell typeCell : this.alignment.getActiveTypeCells()) {
			c += this.alignment.getPropertyCells(typeCell).size() + 1;
		}
		progress.begin("Translating hale alignment to XtraServer Mapping file", c);
		this.progress = progress;
	}

	/**
	 * Generates the Mapping object
	 * 
	 * @param reporter status reporter
	 * @return the generated XtraServer Mapping
	 * 
	 * @throws UnsupportedTransformationException if the transformation of types
	 *             or properties is not supported
	 */
	public XtraServerMapping generate(final IOReporter reporter)
			throws UnsupportedTransformationException {

		for (final Cell typeCell : this.alignment.getActiveTypeCells()) {
			final String typeTransformationIdentifier = typeCell.getTransformationIdentifier();
			// Create FeatureTypeMapping from the type cells. The Mapping tables
			// are created
			// and added by the Type Handlers
			this.progress.setCurrentTask("Transforming type");
			final TypeTransformationHandler typeHandler = typeHandlerFactory
					.create(typeTransformationIdentifier);
			if (typeHandler != null) {
				typeHandler.handle(typeCell);
				this.progress.setCurrentTask(
						"Mapping values for Feature Type " + mappingContext.getFeatureTypeName());
				// Add MappingValues from the type cell's property cells
				for (final Cell propertyCell : this.alignment.getPropertyCells(typeCell).stream()
						.sorted(new Comparator<Cell>() {

							@Override
							public int compare(Cell c1, Cell c2) {
								return c1.getPriority().compareTo(c2.getPriority());
							}
						}).collect(Collectors.toList())) {
					final String propertyTransformationIdentifier = propertyCell
							.getTransformationIdentifier();
					final PropertyTransformationHandler propertyHandler = propertyHandlerFactory
							.create(propertyTransformationIdentifier);
					if (propertyHandler != null) {
						propertyHandler.handle(new CellParentWrapper(typeCell, propertyCell));
					}
					this.progress.advance(1);
				}
			}
			else {
				this.progress.advance(this.alignment.getPropertyCells(typeCell).size());
			}
		}
		return mappingContext.getMapping();
	}

	/**
	 * Return all property paths for which no association target could be found
	 * in the schema.
	 * 
	 * @return list of properties with missing association targets
	 */
	public Set<String> getMissingAssociationTargets() {
		return this.mappingContext.getMissingAssociationTargets();
	}

}
