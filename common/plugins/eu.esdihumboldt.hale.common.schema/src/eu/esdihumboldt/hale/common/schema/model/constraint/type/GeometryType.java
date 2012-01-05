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

package eu.esdihumboldt.hale.common.schema.model.constraint.type;

import java.util.HashMap;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;

/**
 * Specifies the geometry type for properties with a {@link GeometryProperty}
 * binding.
 * @author Simon Templer
 * @since 2.5
 */
@Constraint(mutable = false)
public class GeometryType implements TypeConstraint {
	
	/**
	 * Geometry binding singletons, binding class mapped to the corresponding 
	 * geometry type constraint.
	 */
	private static final Map<Class<? extends Geometry>, GeometryType> singletons = new HashMap<Class<? extends Geometry>, GeometryType>();
	
	/**
	 * Get the geometry type constraint with the given JTS geometry binding.
	 * @param binding the type's geometry binding
	 * @return the binding constraint (which is a singleton)
	 */
	public static GeometryType get(Class<? extends Geometry> binding) {
		GeometryType bc = singletons.get(binding);
		if (bc == null) {
			bc = new GeometryType(binding);
			singletons.put(binding, bc);
		}
		return bc;
	}

	/**
	 * The geometry binding
	 */
	private final Class<? extends Geometry> binding;

	/**
	 * Creates a default geometry constraint classifying the type as being no 
	 * geometry type.
	 * @see Constraint 
	 */
	public GeometryType() {
		this(null);
	}

	/**
	 * Creates a constraint with the given geometry binding
	 * @param binding the JTS geometry binding
	 */
	private GeometryType(Class<? extends Geometry> binding) {
		super();
		
		this.binding = binding;
	}
	
	/**
	 * Get the geometry binding of the type.
	 * @return the binding, <code>null</code> if it is no geometry
	 * 
	 * @see #isGeometry()
	 */
	public Class<? extends Geometry> getBinding() {
		return binding;
	}
	
	/**
	 * Specifies if the type the constraint is associated to is a geometry type
	 * (meaning it has a {@link GeometryProperty} value).
	 * @return if the type is a geometry type
	 */
	public boolean isGeometry() {
		return binding != null;
	}

}
