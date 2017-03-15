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

import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Type resolver backed by a map.
 * 
 * @author Simon Templer
 */
public class MapTypeResolver implements TypeResolver {

	private final Map<Value, TypeDefinition> map;

	/**
	 * Create a new type resolver based on the given map.
	 * 
	 * @param map the map with references mapped to the respective type
	 *            definitions
	 */
	public MapTypeResolver(Map<Value, TypeDefinition> map) {
		super();
		this.map = map;
	}

	@Override
	public Optional<TypeDefinition> resolve(Value reference) {
		return Optional.ofNullable(map.get(reference));
	}

}
