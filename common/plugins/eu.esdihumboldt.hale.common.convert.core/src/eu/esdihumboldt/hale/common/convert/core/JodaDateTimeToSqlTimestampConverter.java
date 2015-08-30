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

import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a {@link DateTime} to a {@link Timestamp}.
 * 
 * @author Simon Templer
 */
public class JodaDateTimeToSqlTimestampConverter implements Converter<DateTime, Timestamp> {

	@Override
	public Timestamp convert(DateTime source) {
		if (source == null) {
			return null;
		}
		return new Timestamp(source.toDate().getTime());
	}

}
