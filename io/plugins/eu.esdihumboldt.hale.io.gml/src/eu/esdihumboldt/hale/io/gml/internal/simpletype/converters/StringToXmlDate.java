/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.gml.internal.simpletype.converters;

import java.util.Date;

import org.apache.xmlbeans.XmlDate;
import org.springframework.core.convert.converter.Converter;

/**
 * Convert {@link Date} to xs:date
 */
public class StringToXmlDate implements Converter<String, XmlDate> {

	/**
	 * @see Converter#convert(Object)
	 */
	@Override
	public XmlDate convert(String value) {
		if (value == null) {
			return null;
		}

		XmlDate result = XmlDate.Factory.newInstance();
		result.setStringValue(value);
		return result;
	}

}
