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

package eu.esdihumboldt.hale.common.instance.geometry.impl;

import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * CRS definition based on a code
 * @author Simon Templer
 */
public class CodeDefinition implements CRSDefinition {

	private static final long serialVersionUID = -7637649402983702957L;
	
	private final String code;
	private CoordinateReferenceSystem crs;

	/**
	 * Constructor
	 * 
	 * @param code the CRS code (e.g. EPSG:4326)
	 * @param crs the coordinate reference system, may be <code>null</code>
	 */
	public CodeDefinition(String code, CoordinateReferenceSystem crs) {
		this.code = code;
		this.crs = crs;
	}

	/**
	 * @see CRSDefinition#getCRS()
	 */
	@Override
	public CoordinateReferenceSystem getCRS() {
		if (crs == null) {
			try {
				crs = CRS.decode(code);
			} catch (Exception e) {
				throw new IllegalStateException("Invalid CRS code", e);
			}
		}
		
		return crs;
	}

	/**
	 * Get the CRS code
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeDefinition other = (CodeDefinition) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		return true;
	}

}
