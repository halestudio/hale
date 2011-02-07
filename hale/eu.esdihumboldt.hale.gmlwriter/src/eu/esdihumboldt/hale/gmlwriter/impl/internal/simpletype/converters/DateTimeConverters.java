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

package eu.esdihumboldt.hale.gmlwriter.impl.internal.simpletype.converters;

import java.util.Date;

import org.apache.commons.convert.AbstractConverter;
import org.apache.commons.convert.ConversionException;
import org.apache.xmlbeans.XmlDateTime;

/**
 * Converters for xs:datetime
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface DateTimeConverters {
	
	public class DateToXML extends AbstractConverter<Date, XmlDateTime> {

		/**
		 * Default constructor
		 */
		public DateToXML() {
			super(Date.class, XmlDateTime.class);
		}

		@Override
		public XmlDateTime convert(Date value) throws ConversionException {
			XmlDateTime result = XmlDateTime.Factory.newInstance();
			result.setDateValue(value);
			return result;
		}
	}

}
