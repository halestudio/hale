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

package eu.esdihumboldt.hale.ui.geometry;

import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Definition/Instance related geometry utilities
 * @author Simon Templer
 */
public abstract class GeometryUtil {
	
	/**
	 * Get the default geometry of an instance
	 * @param instance the instance
	 * @return the default geometry or <code>null</code> if there is none
	 */
	public static GeometryProperty<?> getDefaultGeometry(Instance instance) {
		return null;
	}

}
