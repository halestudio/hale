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

package eu.esdihumboldt.hale.common.convert.core;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a {@link Date} to a {@link java.sql.Date}.
 * @author Simon Templer
 */
public class DateToSqlDateConverter implements Converter<Date, java.sql.Date> {

	/**
	 * @see Converter#convert(java.lang.Object)
	 */
	@Override
	public java.sql.Date convert(Date source) {
		return new java.sql.Date(source.getTime());
	}

}
