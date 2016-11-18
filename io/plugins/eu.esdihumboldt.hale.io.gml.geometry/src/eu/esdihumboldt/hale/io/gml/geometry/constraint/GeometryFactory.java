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

package eu.esdihumboldt.hale.io.gml.geometry.constraint;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;

/**
 * Constraint associating a geometry handler with a type. By default no geometry
 * handler is associated.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = false)
public class GeometryFactory implements TypeConstraint {

	private final GeometryHandler handler;

	/**
	 * Create a default geometry factory constraint.
	 */
	public GeometryFactory() {
		handler = null;
	}

	/**
	 * Create a geometry factory constraint based on the given geometry handler.
	 * 
	 * @param handler the geometry handler
	 */
	public GeometryFactory(GeometryHandler handler) {
		super();
		this.handler = handler;
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// inherited unless overridden
		return true;
	}

	/**
	 * Create a geometry value from a given instance.
	 * 
	 * @param instance the instance
	 * @param srsDimension the dimension of the instance
	 * @param reader the I/O Provider to get value
	 * @return the geometry value derived from the instance, the return type
	 *         should match the {@link Binding}, may be <code>null</code> if no
	 *         geometry could be created or if no geometry handler is associated
	 */
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader) {
		if (handler == null) {
			return null;
			// XXX instead fall back to Geometries.getInstance()?
		}

		try {
			return handler.createGeometry(instance, srsDimension, reader);
		} catch (GeometryNotSupportedException e) {
			// TODO report error?
			// TODO try creating the geometry in any other way?

			return null;
		}
	}

}
