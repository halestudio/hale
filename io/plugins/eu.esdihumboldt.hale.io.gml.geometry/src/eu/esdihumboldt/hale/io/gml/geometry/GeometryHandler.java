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

import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.GeometryType;

/**
 * Provides support for configuring geometry schema types and reading geometry
 * objects for certain GML geometry types. A geometry handler must be immutable
 * (not holding any state).
 * 
 * @author Simon Templer
 * @since 2.5.0
 */
public interface GeometryHandler {

	/**
	 * Get the geometry types supported by the geometry handler.
	 * 
	 * @return the names of the supported types
	 */
	public Set<QName> getSupportedTypes();

	/**
	 * Get the type constraints to associated with a geometry type definition.
	 * <br>
	 * <br>
	 * This method should at least return the {@link Binding} and
	 * {@link GeometryType} constraints. Usually the binding should be
	 * {@link GeometryProperty}.
	 * 
	 * @param type the type definition the constraints will be associated to
	 * @return the type constraints to be assigned to the type definition
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	public Iterable<TypeConstraint> getTypeConstraints(TypeDefinition type)
			throws GeometryNotSupportedException;

	/**
	 * Create a geometry value from a given instance.
	 * 
	 * @param instance the instance
	 * @param srsDimension the dimension of the instance
	 * @param reader the I/O provider to get reader value
	 * @return the geometry value derived from the instance, the return type
	 *         should match the {@link Binding} created in
	 *         {@link #getTypeConstraints(TypeDefinition)}.
	 * @throws GeometryNotSupportedException if the type definition doesn't
	 *             represent a geometry type supported by the handler
	 */
	public Object createGeometry(Instance instance, int srsDimension, IOProvider reader)
			throws GeometryNotSupportedException;

}
