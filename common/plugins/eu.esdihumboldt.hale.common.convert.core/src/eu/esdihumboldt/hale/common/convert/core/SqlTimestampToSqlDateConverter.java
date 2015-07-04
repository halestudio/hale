/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.convert.core;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.core.convert.converter.Converter;

/**
 * Converts sql timestamp to sql date
 * 
 * @author sameer sheikh
 */
public class SqlTimestampToSqlDateConverter implements Converter<Timestamp, Date> {

	@Override
	public Date convert(Timestamp source) {
		if (source == null) {
			return null;
		}
		return new Date(source.getTime());
	}

}
