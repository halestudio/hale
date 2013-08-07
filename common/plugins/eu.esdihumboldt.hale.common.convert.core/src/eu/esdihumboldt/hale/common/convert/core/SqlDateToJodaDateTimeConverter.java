/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.convert.core;

import java.sql.Date;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a {@link Date} to a {@link DateTime}.
 * 
 * @author Christian Malewski
 */
public class SqlDateToJodaDateTimeConverter implements Converter<Date, DateTime> {

	/**
	 * @see Converter#convert(java.lang.Object)
	 */
	@Override
	public DateTime convert(Date source) {
		return new DateTime(source.getTime());
	}

}
