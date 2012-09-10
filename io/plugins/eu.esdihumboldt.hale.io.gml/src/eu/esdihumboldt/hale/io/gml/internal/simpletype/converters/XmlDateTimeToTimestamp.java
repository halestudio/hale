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

package eu.esdihumboldt.hale.io.gml.internal.simpletype.converters;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.xmlbeans.XmlDateTime;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert xs:datetime to {@link Date}
 */
public class XmlDateTimeToTimestamp implements Converter<XmlDateTime, Timestamp> {

	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public Timestamp convert(XmlDateTime value) {
		if (value == null) {
			return null;
		}
		return new Timestamp(value.getCalendarValue().getTimeInMillis());
	}

}