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

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.util.Optional;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.io.EntityResolver;
import eu.esdihumboldt.hale.common.align.model.MutableAlignment;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * TODO Type description
 * 
 * @author zahnen
 */
public class HaleAlignmentGenerator {

	private final TransformationContext transformationContext;
	private final TypeTransformationHandlerFactory typeHandlerFactory;
	private final PropertyTransformationHandlerFactory propertyHandlerFactory;
	private final XtraServerMapping xtraServerMapping;

	/**
	 * @param sourceTypes
	 * @param targetTypes
	 * @param entityResolver
	 * @param progress
	 * @param reporter
	 * @param xtraServerMapping
	 * 
	 */
	public HaleAlignmentGenerator(final TypeIndex sourceTypes, final TypeIndex targetTypes,
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

		for (String featureTypeName : xtraServerMapping.getFeatureTypeNames(false)) {
			final FeatureTypeMapping featureTypeMapping = xtraServerMapping
					.getFeatureTypeMapping(featureTypeName, true).get();

			final Optional<TypeTransformationHandler> typeHandler = typeHandlerFactory
					.create(featureTypeMapping);

			if (typeHandler.isPresent()) {

				for (String primaryTableName : featureTypeMapping.getPrimaryTableNames()) {

					final MutableCell typeCell = typeHandler.get().handle(featureTypeMapping,
							primaryTableName);
					alignment.addCell(typeCell);

					// TODO: only add values for primary table and its join tables
					for (MappingValue mappingValue : featureTypeMapping.getValues()) {

						final Optional<PropertyTransformationHandler> propertyHandler = propertyHandlerFactory
								.create(mappingValue);

						if (propertyHandler.isPresent()) {
							final MutableCell propertyCell = propertyHandler.get()
									.handle(mappingValue, primaryTableName);
							alignment.addCell(propertyCell);

						}
						else {
							transformationContext.getReporter().warn(
									"Mapping for property \"{0}\" of feature type \"{1}\" could not be imported",
									mappingValue.getTarget(), featureTypeMapping.getName());
						}

					}

				}
			}
			else {
				transformationContext.getReporter().warn(
						"Mapping for feature type \"{0}\" could not be imported",
						featureTypeMapping.getName());
			}

		}

		return alignment;
	}
}
