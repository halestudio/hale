/*
 * Copyright (c) 2017 wetransform GmbH
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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;

/**
 * Type resolver backed by a map.
 * 
 * @author Simon Templer
 */
public class MapTypeProvider implements TypeProvider {

	private final Map<Value, DefaultTypeDefinition> map;

	/**
	 * Create a new type provider based on the given map.
	 * 
	 * @param map the map with references mapped to the respective type
	 *            definitions
	 */
	public MapTypeProvider(Map<Value, DefaultTypeDefinition> map) {
		super();
		this.map = map;
	}

	@Override
	public Optional<TypeDefinition> resolve(Value reference) {
		return Optional.ofNullable(map.get(reference));
	}

	@Override
	public DefaultTypeDefinition getOrCreateType(QName typeName, Value id) {
		DefaultTypeDefinition type = map.get(id);
		if (type == null) {
			type = new DefaultTypeDefinition(typeName);
			map.put(id, type);
		}
		return type;
	}

}
