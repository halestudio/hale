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

package eu.esdihumboldt.hale.common.core.io.project.model.internal;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * Field handler for {@link Class}es in an OSGi context.
 * 
 * @author Simon Templer
 */
public class OsgiClassFieldHandler extends GeneralizedFieldHandler {

	/**
	 * @see GeneralizedFieldHandler#convertUponGet(Object)
	 */
	@Override
	public Object convertUponGet(Object value) {
		if (value == null) {
			return null;
		}
		return ((Class<?>) value).getName();
	}

	/**
	 * @see GeneralizedFieldHandler#convertUponSet(Object)
	 */
	@Override
	public Object convertUponSet(Object value) {
		return OsgiUtils.loadClass((String) value, null);
	}

	/**
	 * @see GeneralizedFieldHandler#getFieldType()
	 */
	@Override
	public Class<?> getFieldType() {
		return Class.class;
	}

}
