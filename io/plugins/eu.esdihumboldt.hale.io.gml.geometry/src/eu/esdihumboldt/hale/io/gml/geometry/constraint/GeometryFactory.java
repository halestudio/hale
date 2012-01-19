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

package eu.esdihumboldt.hale.io.gml.geometry.constraint;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryHandler;
import eu.esdihumboldt.hale.io.gml.geometry.GeometryNotSupportedException;

/**
 * Constraint associating a geometry handler with a type. By default no geometry
 * handler is associated.
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
	 * @param instance the instance
	 * @return the geometry value derived from the instance, the return type
	 *   should match the {@link Binding}, may be <code>null</code> if no
	 *   geometry could be created or if no geometry handler is associated
	 */
	public Object createGeometry(Instance instance) {
		if (handler == null) {
			return null;
			//XXX instead fall back to Geometries.getInstance()?
		}
		
		try {
			return handler.createGeometry(instance);
		} catch (GeometryNotSupportedException e) {
			//TODO report error?
			//TODO try creating the geometry in any other way?
			
			return null;
		}
	}

}
