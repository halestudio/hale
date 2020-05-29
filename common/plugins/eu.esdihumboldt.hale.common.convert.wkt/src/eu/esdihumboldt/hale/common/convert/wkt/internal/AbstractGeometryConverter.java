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

package eu.esdihumboldt.hale.common.convert.wkt.internal;

import org.springframework.core.convert.converter.Converter;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Converts strings to geometries
 * 
 * @author Kevin Mais
 * @param <T> Geometry Type to convert to
 */
public abstract class AbstractGeometryConverter<T extends Geometry> implements Converter<String, T> {

	private static WKTReader reader;

	/**
	 * @return the reader
	 */
	public static WKTReader getReader() {
		if (reader == null) {
			reader = new WKTReader();
		}
		return reader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T convert(String string) {
		if (string == null || string.isEmpty()) {
			return null;
		}
		T result;
		try {
			result = (T) getReader().read(string);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
}
