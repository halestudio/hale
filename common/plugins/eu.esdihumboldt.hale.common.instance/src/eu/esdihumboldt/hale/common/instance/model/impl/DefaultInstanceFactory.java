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

package eu.esdihumboldt.hale.common.instance.model.impl;

import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance factory based on {@link DefaultInstance}
 * @author Simon Templer
 */
public class DefaultInstanceFactory implements InstanceFactory {

	/**
	 * @see InstanceFactory#createInstance(TypeDefinition)
	 */
	@Override
	public MutableInstance createInstance(TypeDefinition type) {
		return new DefaultInstance(type, null); // initially no data set associated
	}

}
