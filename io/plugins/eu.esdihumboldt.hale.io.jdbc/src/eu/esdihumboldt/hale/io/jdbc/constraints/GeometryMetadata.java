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

package eu.esdihumboldt.hale.io.jdbc.constraints;

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
		this.auth_name = "";
		this.srs = "";
		this.dimension = 0;
		this.srsText = "";
	}

	/**
	 * The constructor to set the srs and dimension
	 * 
	 * @param srs the spatial reference system
	 * @param dimension the dimension
	 * @param srsText the srsText or null if there isnt one
	 * @param auth_name the auth name e.g. EPSG
	 */
	public GeometryMetadata(String srs, int dimension, String srsText, String auth_name) {
		this.auth_name = auth_name;
		this.srs = srs;
		this.dimension = dimension;
		this.srsText = srsText;
	}

	/**
	 * @return the srs
	 */
	public String getSrs() {
		return srs;
	}

	/**
	 * @return the srsText
	 */
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
	public String getAuthName() {
		return auth_name;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.schema.model.TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		return false;
	}
}
