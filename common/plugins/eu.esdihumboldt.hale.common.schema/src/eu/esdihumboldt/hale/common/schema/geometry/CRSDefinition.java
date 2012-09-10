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

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Represents a CRS definition using WKT or a CODE
 * 
 * @author Simon Templer
 */
public interface CRSDefinition extends Serializable {

	/**
	 * Get the coordinate reference system
	 * 
	 * @return the coordinate reference system
	 */
	public CoordinateReferenceSystem getCRS();

}
