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

package eu.esdihumboldt.hale.instance.model;

import com.google.common.base.Preconditions;


/**
 * Instance model utility methods
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public abstract class InstanceModel {

	/**
	 * Copy the properties from one instance to another
	 * 
	 * @param source the source instance
	 * @param target the mutable target instance
	 */
	public static void copyProperties(Instance source, MutableInstance target) {
		if (source == null) {
			return;
		}
		
		Preconditions.checkNotNull(target);
		
		for (String property : source.getPropertyNames()) {
			target.setProperty(property, source.getProperty(property).clone());
		}
	}

}
