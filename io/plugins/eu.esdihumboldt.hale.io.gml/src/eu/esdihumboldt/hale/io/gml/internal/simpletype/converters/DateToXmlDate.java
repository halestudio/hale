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

import java.util.Date;

import org.apache.xmlbeans.XmlDate;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert {@link Date} to xs:date
 */
public class DateToXmlDate implements Converter<Date, XmlDate> {

	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public XmlDate convert(Date value) {
		XmlDate result = XmlDate.Factory.newInstance();
		result.setDateValue(value);
		return result;
	}

}