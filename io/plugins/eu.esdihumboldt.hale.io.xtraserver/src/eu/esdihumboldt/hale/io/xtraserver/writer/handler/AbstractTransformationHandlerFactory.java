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

import java.util.Map;
import java.util.Objects;

/**
 * Abstract Factory for creating Type and Property Transformation Handlers
 * 
 * @param <T> TransformationHandler
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractTransformationHandlerFactory<T extends TransformationHandler<?>> {

	private final static boolean ignoreUnknownTransformations = true;

	private final Map<String, T> handlers;
	private final MappingContext mappingContext;

	AbstractTransformationHandlerFactory(final MappingContext mappingContext,
			final Map<String, T> handlers) {
		this.mappingContext = mappingContext;
		this.handlers = handlers;
	}

	/**
	 * Create a new Transformation Type Handler
	 * 
	 * @param typeTransformationIdentifier type function identifier
	 * @return new TypeHandler
	 * @throws UnsupportedTransformationException if the transformation is not
	 *             supported
	 */
	public T create(final String typeTransformationIdentifier)
			throws UnsupportedTransformationException {
		final T handler = handlers.get(Objects.requireNonNull(typeTransformationIdentifier,
				"Type transformation identifier is null"));
		if (handler == null) {
			if (ignoreUnknownTransformations) {
				mappingContext.getReporter().warn("Transformation not supported: {0}",
						typeTransformationIdentifier);
			}
			else {
				throw new UnsupportedTransformationException(typeTransformationIdentifier);
			}
		}
		return handler;
	}
}