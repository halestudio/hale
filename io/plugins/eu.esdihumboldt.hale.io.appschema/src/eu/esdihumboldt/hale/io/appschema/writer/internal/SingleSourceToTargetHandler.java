/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.appschema.writer.internal;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.appschema.impl.internal.generated.app_schema.TypeMappingsPropertyType.FeatureTypeMapping;
import eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext;

/**
 * Base class for type transformation handlers converting a single source entity
 * to a single target entity.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public abstract class SingleSourceToTargetHandler implements TypeTransformationHandler {

	/**
	 * @see eu.esdihumboldt.hale.io.appschema.writer.internal.TypeTransformationHandler#handleTypeTransformation(eu.esdihumboldt.hale.common.align.model.Cell,
	 *      eu.esdihumboldt.hale.io.appschema.writer.internal.mapping.AppSchemaMappingContext)
	 */
	@Override
	public FeatureTypeMapping handleTypeTransformation(Cell typeCell,
			AppSchemaMappingContext context) {
		ListMultimap<String, ? extends Entity> sourceEntities = typeCell.getSource();
		if (sourceEntities == null || sourceEntities.size() == 0) {
			throw new IllegalStateException("No source type has been specified.");
		}
		ListMultimap<String, ? extends Entity> targetEntities = typeCell.getTarget();
		if (targetEntities == null || targetEntities.size() == 0) {
			throw new IllegalStateException("No target type has been specified.");
		}

		// Maps 1 source to 1 target, so it is safe to pick the first entity in
		// the list
		Entity sourceType = sourceEntities.values().iterator().next();
		Entity targetType = targetEntities.values().iterator().next();
		TypeDefinition targetTypeDef = targetType.getDefinition().getType();

		FeatureTypeMapping ftMapping = context.getOrCreateFeatureTypeMapping(targetTypeDef);
		ftMapping.setSourceType(sourceType.getDefinition().getType().getName().getLocalPart());

		return ftMapping;
	}

}
