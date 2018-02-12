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

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;

/**
 * Factory for creating Type Transformation Handlers
 * 
 * @author zahnen
 */
public class TypeTransformationHandlerFactory {

	private final TransformationContext transformationContext;

	/**
	 * @param transformationContext the transformation context
	 */
	public TypeTransformationHandlerFactory(TransformationContext transformationContext) {
		this.transformationContext = transformationContext;
	}

	/**
	 * Create a new Transformation Type Handler
	 * 
	 * @param featureTypeMapping the mapping that should be transformed
	 * 
	 * @return new TypeHandler
	 */
	public Optional<TypeTransformationHandler> create(final FeatureTypeMapping featureTypeMapping) {
		if (featureTypeMapping.getTables().stream()
				.allMatch(mappingTable -> mappingTable.isPrimary())) {
			return Optional.of(new RetypeHandler(transformationContext));
		}
		else if (!featureTypeMapping.getTables().isEmpty()) {
			return Optional.of(new JoinHandler(transformationContext));
		}

		return Optional.empty();
	}

}
