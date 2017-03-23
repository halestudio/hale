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
 * A constraint for the metadata geometry
 * 
 * @author Dominik Reuter
 */
@Constraint(mutable = false)
public class GeometryMetadata implements TypeConstraint {

	/**
	 * The srs text
	 */
	private final String srsText;

	/**
	 * The spatial reference system
	 */
	private final String srs;

	/**
	 * The dimension
	 */
	private final int dimension;

	/**
	 * The auth name e.g. EPSG
	 */
	private final String auth_name;

	/**
	 * Default constructor
	 */
	public GeometryMetadata() {
		this.auth_name = null;
		this.srs = null;
		this.dimension = 0;
		this.srsText = null;
	}

	/**
	 * The constructor to set the srs and dimension
	 * 
	 * @param srs the spatial reference system
	 * @param dimension the dimension
	 * @param srsText the srsText or null if there isnt one
	 * @param auth_name the auth name e.g. EPSG
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
	 * @param dimension the dimension
	 */
	public GeometryMetadata(int dimension) {
		this(null, dimension, null, null);
	}

	/**
	 * @return the srs
	 */
	@Nullable
	public String getSrs() {
		return srs;
	}

	/**
	 * @return the srsText
	 */
	@Nullable
	public String getSrsText() {
		return srsText;
	}

	/**
	 * @return the dimension
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @return the auth_name
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
