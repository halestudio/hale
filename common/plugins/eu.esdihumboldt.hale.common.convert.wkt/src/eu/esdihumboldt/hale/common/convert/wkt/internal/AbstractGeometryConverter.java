/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.convert.wkt.internal;

import org.springframework.core.convert.converter.Converter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

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
