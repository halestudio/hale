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

import java.util.Set;

import javax.xml.namespace.QName;

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
 * @author Simon Templer
 * @since 2.5.0
 */
public interface GeometryHandler {
	
	/**
	 * Get the geometry types supported by the geometry handler. 
	 * @return the names of the supported types
	 */
	public Set<QName> getSupportedTypes();
	
	/**
	 * Get the type constraints to associated with a geometry type definition.<br>
	 * <br>
	 * This method should at least return the {@link Binding} and 
	 * {@link GeometryType} constraints. Usually the binding should be
	 * {@link GeometryProperty}.
	 * @param type the type definition the constraints will be associated to
	 * @return the type constraints to be assigned to the type definition  
	 * @throws GeometryNotSupportedException if the type definition doesn't 
	 *   represent a geometry type supported by the handler
	 */
	public Iterable<TypeConstraint> getTypeConstraints(TypeDefinition type) throws GeometryNotSupportedException;
	
	/**
	 * Create a geometry value from a given instance.
	 * @param instance the instance
	 * @param srsDimension the dimension of the instance
	 * @return the geometry value derived from the instance, the return type
	 *   should match the {@link Binding} created in
	 *   {@link #getTypeConstraints(TypeDefinition)}.
	 * @throws GeometryNotSupportedException if the type definition doesn't 
	 *   represent a geometry type supported by the handler
	 */
	public Object createGeometry(Instance instance, int srsDimension) throws GeometryNotSupportedException;

}
