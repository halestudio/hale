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

package eu.esdihumboldt.hale.common.schema.geometry;

import java.io.Serializable;

import org.locationtech.jts.geom.Geometry;

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
