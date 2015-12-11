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

package eu.esdihumboldt.hale.io.jdbc.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Constraint with information on an SQL array type.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
@Immutable
public class SQLArray implements TypeConstraint {

	/**
	 * Instance representing a type that is no array.
	 */
	public static final SQLArray NO_ARRAY = new SQLArray();

	/**
	 * Integer value representing an unknown array dimension.
	 */
	public static final int UNKNOWN_DIMENSION = 0;

	/**
	 * Integer value representing an unknown array size.
	 */
	public static final int UNKNOWN_SIZE = -1;

	private final boolean enabled;

	private final Class<?> elementType;

	private final int dimension;

	private final int[] sizes;

	private final String elementTypeName;

	/**
	 * Default constructor.
	 */
	public SQLArray() {
		super();
		this.enabled = false;
		this.elementType = null;
		this.dimension = UNKNOWN_DIMENSION;
		this.sizes = null;
		this.elementTypeName = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param elementType the element type
	 * @param elementTypeName the element type name used in the database
	 * @param dimension the array dimension
	 * @param sizes the array sizes by dimension
	 */
	public SQLArray(Class<?> elementType, String elementTypeName, int dimension,
			@Nullable int[] sizes) {
		super();
		this.enabled = true;
		this.elementType = elementType;
		this.dimension = dimension;
		this.sizes = sizes;
		this.elementTypeName = elementTypeName;
	}

	/**
	 * @return the element binding
	 */
	@Nullable
	public Class<?> getElementType() {
		return elementType;
	}

	/**
	 * @return the array dimension, {#value UNKNOWN_DIMENSION} if the dimension
	 *         is unknown
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Get the size for a specific dimension.
	 * 
	 * @param dimension the dimension (0 for the first dimension)
	 * @return the dimension size or {#value UNKNOWN_SIZE}
	 */
	public int getSize(int dimension) {
		if (sizes == null) {
			return UNKNOWN_SIZE;
		}

		if (dimension < sizes.length) {
			return sizes[dimension];
		}

		return UNKNOWN_SIZE;
	}

	/**
	 * @return the list of sizes per dimension
	 */
	public List<Integer> getSizes() {
		List<Integer> result;
		if (sizes != null) {
			result = new ArrayList<>(sizes.length);
			for (int size : sizes) {
				result.add(size);
			}
		}
		else {
			result = Collections.<Integer> emptyList();
		}
		return result;
	}

	/**
	 * States if a size is known for a specific dimension.
	 * 
	 * @param dimension the dimension (0 for the first dimension)
	 * @return the dimension size or {#value UNKNOWN_SIZE}
	 */
	public boolean hasSize(int dimension) {
		if (sizes == null) {
			return false;
		}

		if (dimension < sizes.length) {
			return sizes[dimension] != UNKNOWN_SIZE;
		}

		return false;
	}

	/**
	 * @return the element type name used in the database
	 */
	@Nullable
	public String getElementTypeName() {
		return elementTypeName;
	}

	/**
	 * @return if the type represents an SQL array
	 */
	public boolean isArray() {
		return enabled;
	}

	@Override
	public boolean isInheritable() {
		return true;
	}

}
