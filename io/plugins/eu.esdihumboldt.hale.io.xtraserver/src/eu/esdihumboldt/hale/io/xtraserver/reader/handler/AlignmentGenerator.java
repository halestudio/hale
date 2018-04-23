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

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Transforms a {@link XtraServerMapping} to a {@link MutableAlignment}
 * 
 * @author zahnen
 */
public class AlignmentGenerator {

	private final TransformationContext transformationContext;
	private final TypeTransformationHandlerFactory typeHandlerFactory;
	private final PropertyTransformationHandlerFactory propertyHandlerFactory;
	private final XtraServerMapping xtraServerMapping;

	/**
	 * @param sourceTypes source types
	 * @param targetTypes target types
	 * @param entityResolver entity resolver
	 * @param progress progress indicator
	 * @param reporter reporter
	 * @param xtraServerMapping XtraServer Mapping
	 * 
	 */
	public AlignmentGenerator(final TypeIndex sourceTypes, final TypeIndex targetTypes,
			final EntityResolver entityResolver, final ProgressIndicator progress,
			final IOReporter reporter, final XtraServerMapping xtraServerMapping) {
		this.transformationContext = new TransformationContext(sourceTypes, targetTypes,
				entityResolver, progress, reporter);
		this.typeHandlerFactory = TypeTransformationHandler.createFactory(transformationContext);
		this.propertyHandlerFactory = PropertyTransformationHandler
				.createFactory(transformationContext);
		this.xtraServerMapping = xtraServerMapping;
	}

	/**
	 * @return the generated alignment
	 */
	public MutableAlignment generate() {
		final MutableAlignment alignment = new DefaultAlignment();

		xtraServerMapping.getFeatureTypeMappings().stream().flatMap(generateFeatureTypeCellStream())
				.forEach(cell -> alignment.addCell(cell));

		return alignment;
	}

	private Function<FeatureTypeMapping, Stream<MutableCell>> generateFeatureTypeCellStream() {
		return featureTypeMapping -> featureTypeMapping.getPrimaryTables().stream()
				.flatMap(generateTableCellStream(featureTypeMapping, false));
	}

	private Function<MappingTable, Stream<MutableCell>> generateTableCellStream(
			final FeatureTypeMapping featureTypeMapping, final boolean isJoined) {
		return mappingTable -> {

			final Stream<MutableCell> propertyCellStream = mappingTable.getValues().stream()
					.peek(mappingValue -> System.out.println(mappingValue.getTargetPath()))
					.map(generatePropertyCell(featureTypeMapping.getName(), mappingTable.getName()))
					.filter(Optional::isPresent).map(Optional::get);

			// recurse
			final Stream<MutableCell> joinedPropertyCellStream = mappingTable.getJoiningTables()
					.stream().flatMap(generateTableCellStream(featureTypeMapping, true));

			final Stream<MutableCell> cellStream = Stream.concat(propertyCellStream,
					joinedPropertyCellStream);

			if (isJoined) {
				return cellStream;
			}
			else {
				final Optional<TypeTransformationHandler> typeHandler = typeHandlerFactory
						.create(featureTypeMapping, mappingTable.getName());

				if (typeHandler.isPresent()) {
					final MutableCell typeCell = typeHandler.get().handle(featureTypeMapping,
							mappingTable.getName());

					return Stream.concat(Stream.of(typeCell), cellStream);
				}
				else {
					transformationContext.getReporter().warn(
							"Mapping for feature type \"{0}\" could not be imported",
							featureTypeMapping.getName());
				}

				return Stream.empty();
			}
		};
	}

	private Function<MappingValue, Optional<MutableCell>> generatePropertyCell(
			final String featureTypeName, final String tableName) {
		return mappingValue -> {
			final Optional<PropertyTransformationHandler> propertyHandler = propertyHandlerFactory
					.create(mappingValue);

			if (propertyHandler.isPresent()) {
				return Optional.ofNullable(propertyHandler.get().handle(mappingValue, tableName));

			}
			else {
				transformationContext.getReporter().warn(
						"Mapping for property \"{0}\" of feature type \"{1}\" could not be imported",
						mappingValue.getTargetPath(), featureTypeName);
			}

			return Optional.empty();
		};
	}
}
