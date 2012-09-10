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

package eu.esdihumboldt.hale.common.schema.geometry;

import java.io.Serializable;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.Binding;

/**
 * {@link Binding} for geometry properties.
 * 
 * @param <T> the concrete geometry type
 * 
 * @author Simon Templer
 * @since 2.2
 */
public interface GeometryProperty<T extends Geometry> extends Serializable {

	/**
	 * Get the definition of the coordinate reference system associated with the
	 * geometry.
	 * 
	 * @return the definition of the coordinate reference system or
	 *         <code>null</code>
	 */
	public CRSDefinition getCRSDefinition();

	/**
	 * Get the geometry.
	 * 
	 * @return the geometry
	 */
	public T getGeometry();

}
