/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
