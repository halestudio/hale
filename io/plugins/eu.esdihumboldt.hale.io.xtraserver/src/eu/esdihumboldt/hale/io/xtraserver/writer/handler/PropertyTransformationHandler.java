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

import de.interactive_instruments.xtraserver.config.api.MappingValue;

/**
 * Handler for Property transformations
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
@FunctionalInterface
public interface PropertyTransformationHandler extends TransformationHandler<MappingValue> {

	/**
	 * Create a new {@link PropertyTransformationHandlerFactory} object
	 * 
	 * @param mappingContext Mapping Context
	 * @return new Property Transformation Handler Factory object
	 */
	public static PropertyTransformationHandlerFactory createFactory(
			final MappingContext mappingContext) {
		return new PropertyTransformationHandlerFactory(mappingContext);
	}

	/**
	 * Check if a transformation is supported by the
	 * {@link PropertyTransformationHandlerFactory}
	 * 
	 * @param typeTransformationIdentifier the hale identifier of the transformation
	 * @return true if the transformation is supported, false otherwise
	 */
	public static boolean isTransformationSupported(final String typeTransformationIdentifier) {
		return PropertyTransformationHandlerFactory.SUPPORTED_TYPES
				.contains(typeTransformationIdentifier);
	}
}
