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
