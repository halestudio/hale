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

import java.util.Calendar;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * Convert a {@link Calendar} to a {@link Date}.
 * 
 * @author Simon Templer
 */
public class CalendarToDateConverter implements Converter<Calendar, Date> {

	@Override
	public Date convert(Calendar source) {
		if (source == null) {
			return null;
		}
		return source.getTime();
	}

}
