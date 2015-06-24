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

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.ImmutableSet;

/**
 * Converts a {@link String} to a {@link DateTime}. Use this as base or
 * fall-back if creating other conversions from strings.
 * 
 * @author Simon Templer
 */
public class StringToJodaDateTimeConverter implements Converter<String, DateTime> {

	/**
	 * Some String representations of dates have a strange sense of representing
	 * <code>null</code> values.
	 */
	private static ImmutableSet<String> NULL_VALUES = ImmutableSet.of( //
			"0000-00-00T00:00:00", //
			"0000-00-00 00:00:00", //
			"0000-00-00 00:00:00.000", //
			"0000-00-00");

	@Override
	public DateTime convert(String source) {
		if (source == null || NULL_VALUES.contains(source)) {
			return null;
		}
		// parse ISO format
		return DateTime.parse(source);
	}

}
