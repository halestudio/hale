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

import de.interactive_instruments.xtraserver.config.api.MappingValue;

/**
 * Handler for Property transformations
 * 
 * @author zahnen
 */
@FunctionalInterface
public interface PropertyTransformationHandler extends TransformationHandler<MappingValue> {

	/**
	 * Create a new {@link PropertyTransformationHandlerFactory} object
	 * 
	 * @param transformationContext Transformation Context
	 * @return new Property Transformation Handler Factory object
	 */
	public static PropertyTransformationHandlerFactory createFactory(
			final TransformationContext transformationContext) {
		return new PropertyTransformationHandlerFactory(transformationContext);
	}
}
