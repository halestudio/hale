/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.geometry;

import java.util.Collection;
import java.util.Collections;

import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Base class for geometry handlers that provide fixed type constraints.
 * 
 * @author Simon Templer
 */
public abstract class FixedConstraintsGeometryHandler extends AbstractGeometryHandler {

	private Iterable<TypeConstraint> constraints;

	/**
	 * @see GeometryHandler#getTypeConstraints(TypeDefinition)
	 */
	@Override
	public Iterable<TypeConstraint> getTypeConstraints(TypeDefinition type)
			throws GeometryNotSupportedException {
		checkType(type);

		if (constraints == null) {
			constraints = Collections.unmodifiableCollection(initConstraints());
		}
		return constraints;
	}

	/**
	 * Check if the given type definition is supported by the geometry handler.
	 * This implementation assumes the type is valid. Override to change this
	 * behavior.
	 * 
	 * @param type the type definition to check
	 * @throws GeometryNotSupportedException if the check failed
	 */
	@SuppressWarnings("unused")
	protected void checkType(TypeDefinition type) throws GeometryNotSupportedException {
		// by default assume the type is valid
	}

	/**
	 * Create the associated type constraints.
	 * 
	 * @return the type constraints to set on an associated geometry type
	 */
	protected abstract Collection<? extends TypeConstraint> initConstraints();

}
