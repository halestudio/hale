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

package eu.esdihumboldt.hale.io.jdbc.constraints.internal;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.io.jdbc.GeometryAdvisor;

/**
 * Type constraint that stores the associated {@link GeometryAdvisor}. By
 * default no advisor is set.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class GeometryAdvisorConstraint implements TypeConstraint {

	private final GeometryAdvisor<?> advisor;

	/**
	 * Default constructor, which creates a constraint w/o geometry advisor set.
	 */
	public GeometryAdvisorConstraint() {
		super();
		this.advisor = null;
	}

	/**
	 * Create a constraint with the given advisor.
	 * 
	 * @param advisor the geometry advisor
	 */
	public GeometryAdvisorConstraint(GeometryAdvisor<?> advisor) {
		super();
		this.advisor = advisor;
	}

	/**
	 * Get the associated geometry advisor.
	 * 
	 * @return the geometry advisor or <code>null</code>
	 */
	public GeometryAdvisor<?> getAdvisor() {
		return advisor;
	}

	@Override
	public boolean isInheritable() {
		return false;
	}

}
