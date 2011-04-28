/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.io.gml.writer.internal.simpletype.converters;

import java.util.Date;

import org.apache.commons.convert.AbstractConverter;
import org.apache.commons.convert.ConversionException;
import org.apache.xmlbeans.GDate;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDateTime;
import org.apache.xmlbeans.XmlTime;

/**
 * Converters for xs:datetime
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface DateTimeConverters {
	
	/**
	 * Convert {@link Date} to xs:datetime 
	 */
	public class DateToXmlDateTime extends AbstractConverter<Date, XmlDateTime> {

		/**
		 * Default constructor
		 */
		public DateToXmlDateTime() {
			super(Date.class, XmlDateTime.class);
		}

		@Override
		public XmlDateTime convert(Date value) throws ConversionException {
			XmlDateTime result = XmlDateTime.Factory.newInstance();
			result.setDateValue(value);
			return result;
		}
	}
	
	/**
	 * Convert {@link Date} to xs:time 
	 */
	public class DateToXmlTime extends AbstractConverter<Date, XmlTime> {

		/**
		 * Default constructor
		 */
		public DateToXmlTime() {
			super(Date.class, XmlTime.class);
		}

		@Override
		public XmlTime convert(Date value) throws ConversionException {
			XmlTime result = XmlTime.Factory.newInstance();
			result.setGDateValue(new GDate(value));
			return result;
		}
	}
	
	/**
	 * Convert {@link Date} to xs:date
	 */
	public class DateToXmlDate extends AbstractConverter<Date, XmlDate> {

		/**
		 * Default constructor
		 */
		public DateToXmlDate() {
			super(Date.class, XmlDate.class);
		}

		@Override
		public XmlDate convert(Date value) throws ConversionException {
			XmlDate result = XmlDate.Factory.newInstance();
			result.setDateValue(value);
			return result;
		}
	}

}
