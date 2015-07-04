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

package eu.esdihumboldt.hale.common.schema.io.util;

import eu.esdihumboldt.hale.common.core.io.util.ImportProviderDecorator;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;
import eu.esdihumboldt.hale.common.schema.io.SchemaReader;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.TypeIndex;

/**
 * Decorator for {@link SchemaReader}s.
 * 
 * @param <T> the provider type
 * @author Stefano Costa, GeoSolutions
 */
public abstract class SchemaReaderDecorator<T extends SchemaReader> extends
		ImportProviderDecorator<T> implements SchemaReader {

	/**
	 * @see ImportProviderDecorator#ImportProviderDecorator(eu.esdihumboldt.hale.common.core.io.ImportProvider)
	 */
	public SchemaReaderDecorator(T internalProvider) {
		super(internalProvider);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.io.SchemaReader#setSharedTypes(eu.esdihumboldt.hale.common.schema.model.TypeIndex)
	 */
	@Override
	public void setSharedTypes(TypeIndex sharedTypes) {
		internalProvider.setSharedTypes(sharedTypes);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.io.SchemaReader#setSchemaSpace(eu.esdihumboldt.hale.common.schema.SchemaSpaceID)
	 */
	@Override
	public void setSchemaSpace(SchemaSpaceID schemaSpace) {
		internalProvider.setSchemaSpace(schemaSpace);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.io.SchemaReader#getSchema()
	 */
	@Override
	public Schema getSchema() {
		return internalProvider.getSchema();
	}

}
