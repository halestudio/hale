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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import javax.annotation.Nullable;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * A constraint for the geometry metadata.
 * 
 * @author Dominik Reuter
 */
@Constraint(mutable = false)
public class GeometryMetadata implements TypeConstraint {

	/**
	 * Represents an unknown dimension.
	 */
	public static final int UNKNOWN_DIMENSION = 0;

	/**
	 * The the WKT definition of the spatial reference system or
	 * <code>null</code>.
	 */
	private final String srsText;

	/**
	 * The spatial reference system code.
	 */
	private final String srs;

	/**
	 * The dimension.
	 */
	private final int dimension;

	/**
	 * The name of the authority defining the SRS code, e.g. EPSG.
	 */
	private final String auth_name;

	/**
	 * Default constructor
	 */
	public GeometryMetadata() {
		this.auth_name = null;
		this.srs = null;
		this.dimension = UNKNOWN_DIMENSION;
		this.srsText = null;
	}

	/**
	 * The constructor to set the SRS and dimension information.
	 * 
	 * @param srs the spatial reference system code
	 * @param dimension the dimension, or <code>0</code> if unknown
	 * @param srsText the WKT definition of the spatial reference system
	 * @param auth_name the name of the authority defining the SRS code, e.g.
	 *            EPSG
	 */
	public GeometryMetadata(@Nullable String srs, int dimension, @Nullable String srsText,
			@Nullable String auth_name) {
		this.auth_name = auth_name;
		this.srs = srs;
		this.dimension = dimension;
		this.srsText = srsText;
	}

	/**
	 * Create geometry metadata without SRS information.
	 * 
	 * @param dimension the dimension, or <code>0</code> if unknown
	 */
	public GeometryMetadata(int dimension) {
		this(null, dimension, null, null);
	}

	/**
	 * @return the spatial reference system code or <code>null</code>
	 */
	@Nullable
	public String getSrs() {
		return srs;
	}

	/**
	 * @return the WKT definition of the spatial reference system or
	 *         <code>null</code>
	 */
	@Nullable
	public String getSrsText() {
		return srsText;
	}

	/**
	 * @return the dimension, <code>0</code> represents an unknown dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @return the name of the authority defining the SRS code, e.g. EPSG
	 */
	@Nullable
	public String getAuthName() {
		return auth_name;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}
}
