/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.align.io.impl.dummy;

import eu.esdihumboldt.hale.common.align.io.impl.DefaultEntityResolver;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.ClassType;
import eu.esdihumboldt.hale.common.align.io.impl.internal.generated.PropertyType;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultProperty;
import eu.esdihumboldt.hale.common.align.model.impl.DefaultType;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Entity resolver that returns dummy entities for not resolvable entities.
 * 
 * @author Simon Templer
 */
public class DummyEntityResolver extends DefaultEntityResolver {

	@Override
	public Property resolveProperty(final PropertyType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveProperty(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			return new DefaultProperty(EntityToDef.toDummyDef(entity, schemaSpace));
		}
	}

	@Override
	public Type resolveType(final ClassType entity, final TypeIndex schema,
			final SchemaSpaceID schemaSpace) {
		try {
			return super.resolveType(entity, schema, schemaSpace);
		} catch (RuntimeException e) {
			return new DefaultType(EntityToDef.toDummyDef(entity, schemaSpace));
		}
	}

}
