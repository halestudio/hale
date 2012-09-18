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

package eu.esdihumboldt.hale.common.instance.geometry;

import org.opengis.referencing.ReferenceIdentifier;

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Default implementation of a {@link GeometryProperty}
 * 
 * @param <T> the geometry type
 * 
 * @author Simon Templer
 */
public class DefaultGeometryProperty<T extends Geometry> implements GeometryProperty<T> {

	private static final long serialVersionUID = 9160846585636648227L;

	private CRSDefinition crsDef;
	private T geometry;

	// FIXME use custom mechanism for serialization? e.g. WKB for geometries

	/**
	 * Create a geometry property
	 * 
	 * @param crsDef the definition of the coordinate reference system, may be
	 *            <code>null</code>
	 * @param geometry the geometry
	 */
	public DefaultGeometryProperty(CRSDefinition crsDef, T geometry) {
		super();
		this.crsDef = crsDef;
		this.geometry = geometry;
	}

	/**
	 * @see GeometryProperty#getCRSDefinition()
	 */
	@Override
	public CRSDefinition getCRSDefinition() {
		return crsDef;
	}

	/**
	 * @see GeometryProperty#getGeometry()
	 */
	@Override
	public T getGeometry() {
		return geometry;
	}

	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		if (crsDef != null && crsDef.getCRS() != null) {
			ReferenceIdentifier name = crsDef.getCRS().getName();
			if (name != null) {
				String ident;
				if (name.getCode() != null && !name.getCode().isEmpty()) {
					ident = name.getCode();
				}
				else {
					ident = name.toString();
				}
				return "{CRS=" + ident + "} " + geometry.toString();
			}
		}

		return geometry.toString();
	}

}
