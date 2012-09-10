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

package eu.esdihumboldt.hale.common.align.io.impl.internal;

import javax.xml.namespace.QName;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 * Field handler for {@link QName}s.
 * 
 * @author Simon Templer
 */
public class QNameFieldHandler extends GeneralizedFieldHandler {

	/**
	 * @see GeneralizedFieldHandler#convertUponGet(Object)
	 */
	@Override
	public Object convertUponGet(Object value) {
		if (value == null) {
			return null;
		}
		return ((QName) value).toString();
	}

	/**
	 * @see GeneralizedFieldHandler#convertUponSet(Object)
	 */
	@Override
	public Object convertUponSet(Object value) {
		return QName.valueOf(value.toString());
	}

	/**
	 * @see GeneralizedFieldHandler#getFieldType()
	 */
	@Override
	public Class<?> getFieldType() {
		return QName.class;
	}

}
