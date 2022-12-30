/*
 * Copyright (c) 2022 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.convert.core;

import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.ImmutableSet;

/**
 * Abstract base class for classes that convert a String to some form of date,
 * time or date+time object.
 * 
 * @param <T> Type that the String is converted to
 * 
 * @author Florian Esser
 */

public abstract class AbstractStringToDateTimeTypeConverter<T> implements Converter<String, T> {

	/**
	 * Some String representations of dates have a strange sense of representing
	 * <code>null</code> values.
	 */
	public static final ImmutableSet<String> NULL_VALUES = ImmutableSet.of( //
			"0000-00-00T00:00:00", //
			"0000-00-00 00:00:00", //
			"0000-00-00T00:00:00.000", //
			"0000-00-00 00:00:00.000", //
			"0000-00-00");

	/**
	 * Parse the input String and create the target object.
	 * 
	 * @param source String representation
	 * 
	 * @return Target object
	 */
	protected abstract T parse(String source);

	@Override
	public final T convert(String source) {
		if (source == null || NULL_VALUES.contains(source)) {
			return null;
		}

		return this.parse(source);
	}

}
