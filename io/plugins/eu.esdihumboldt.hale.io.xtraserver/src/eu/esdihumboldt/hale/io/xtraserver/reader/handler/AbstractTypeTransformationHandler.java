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

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import eu.esdihumboldt.hale.common.align.model.MutableCell;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultCell;

/**
 * Abstract Type Transformation Handler
 * 
 * @author zahnen
 */
abstract class AbstractTypeTransformationHandler implements TypeTransformationHandler {

	protected final TransformationContext transformationContext;

	protected AbstractTypeTransformationHandler(final TransformationContext transformationContext) {
		this.transformationContext = transformationContext;
	}

	@Override
	public final MutableCell handle(final FeatureTypeMapping featureTypeMapping,
			final String tableName) {

		transformationContext.nextTypeTransformation(tableName, featureTypeMapping);

		final String transformationIdentifier = doHandle(featureTypeMapping, tableName);

		final MutableCell typeCell = new DefaultCell();

		typeCell.setTransformationIdentifier(transformationIdentifier);

		typeCell.setSource(transformationContext.getCurrentSourceTypeEntities());
		typeCell.setTarget(transformationContext.getCurrentTargetTypeEntities());

		typeCell.setTransformationParameters(transformationContext.getCurrentTypeParameters());

		return typeCell;
	}

	public abstract String doHandle(final FeatureTypeMapping featureTypeMapping,
			final String primaryTableName);

}
