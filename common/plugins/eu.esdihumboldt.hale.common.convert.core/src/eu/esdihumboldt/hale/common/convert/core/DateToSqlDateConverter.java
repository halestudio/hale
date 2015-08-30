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

package eu.esdihumboldt.hale.common.convert.core;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a {@link Date} to a {@link java.sql.Date}.
 * 
 * @author Simon Templer
 */
public class DateToSqlDateConverter implements Converter<Date, java.sql.Date> {

	@Override
	public java.sql.Date convert(Date source) {
		if (source == null) {
			return null;
		}
		return new java.sql.Date(source.getTime());
	}

}
