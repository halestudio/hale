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

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import java.util.Optional;

import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.MappingValueBuilder;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.io.xtraserver.writer.XtraServerMappingUtils;

/**
 * Transforms the custom function 'custom:alignment:adv.inspire.id.default' to a
 * {@link MappingValue}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class CustomFunctionAdvToLocalId extends FormattedStringHandler {

	public final static String FUNCTION_ID = "custom:alignment:adv.inspire.id.default";

	CustomFunctionAdvToLocalId(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.AbstractPropertyTransformationHandler#doHandle(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.common.align.model.Property)
	 */
	@Override
	public Optional<MappingValue> doHandle(final Cell propertyCell, final Property targetProperty) {

		final String propertyName = propertyName(XtraServerMappingUtils
				.getSourceProperty(propertyCell).getDefinition().getPropertyPath());

		final MappingValue mappingValue = new MappingValueBuilder().expression()
				.qualifiedTargetPath(buildPath(targetProperty.getDefinition().getPropertyPath()))
				.value("'" + mappingContext.getFeatureTypeName() + "_' || $T$." + propertyName)
				.build();

		return Optional.of(mappingValue);
	}

}
