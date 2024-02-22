/*
 * Copyright (c) 2023 wetransform GmbH
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

package eu.esdihumboldt.hale.io.json.internal.schema;

import java.math.BigDecimal;

/**
 * Types of primitive values encountered in Json.
 * 
 * @author Simon Templer
 */
public enum JsonValueType {

	/**
	 * String value type
	 */
	STRING("StringValueType", String.class),

	/**
	 * Number value type
	 */
	NUMBER("NumberValueType", BigDecimal.class),

	/**
	 * Boolean value type
	 */
	BOOLEAN("BooleanValueType", Boolean.class);

	final String typeName;

	final Class<?> binding;

	/**
	 * @param typeName the local name of the respective type
	 * @param binding the binding to use
	 */
	private JsonValueType(String typeName, Class<?> binding) {
		this.typeName = typeName;
		this.binding = binding;
	}

}
