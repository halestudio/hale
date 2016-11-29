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

package eu.esdihumboldt.hale.io.gml.geometry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.springframework.core.convert.ConversionException;

import com.google.common.base.Splitter;
import com.vividsolutions.jts.geom.Coordinate;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.convert.ConversionUtil;
import eu.esdihumboldt.hale.common.instance.helper.BreadthFirstInstanceTraverser;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.CRSDefinition;

/**
 * Utility methods for reading GML geometries from an {@link Instance} model.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("deprecation")
public abstract class GMLGeometryUtil {

	private static final ALogger log = ALoggerFactory.getLogger(GMLGeometryUtil.class);

	/**
	 * Parse coordinates from a GML CoordinatesType instance.
	 * 
	 * @param coordinates the coordinates instance
	 * @return the coordinates or <code>null</code> if the instances contains no
	 *         coordinates
	 * @throws ParseException if parsing the coordinates fails
	 */
	public static Coordinate[] parseCoordinates(Instance coordinates) throws ParseException {
		// XXX should the type be checked to match CoordinatesType?

		Object value = coordinates.getValue();

		if (value != null) {
			try {
				String coordinatesString = ConversionUtil.getAs(value, String.class);
				if (coordinatesString.isEmpty()) {
					return null;
				}

				// determine symbols
				String decimal = getCoordinatesDecimal(coordinates);
				String cs = getCoordinateSeparator(coordinates);
				String ts = getTupleSeparator(coordinates);

				Splitter coordinateSplitter = Splitter.on(cs).trimResults();
				Splitter tupleSplitter = Splitter.on(ts).trimResults();
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				if (format instanceof DecimalFormat) {
					DecimalFormat decFormat = ((DecimalFormat) format);
					DecimalFormatSymbols symbols = decFormat.getDecimalFormatSymbols();
					symbols.setDecimalSeparator(decimal.charAt(0));
					decFormat.setDecimalFormatSymbols(symbols);
				}

				List<Coordinate> coordList = new ArrayList<Coordinate>();

				// split into tuples
				Iterable<String> tuples = tupleSplitter.split(coordinatesString.trim());
				for (String tuple : tuples) {
					Coordinate coord = parseTuple(tuple, coordinateSplitter, format);
					if (coord != null) {
						coordList.add(coord);
					}
				}
				return coordList.toArray(new Coordinate[coordList.size()]);
			} catch (ConversionException e) {
				log.error("Error parsing geometry coordinates", e);
			}
		}

		return null;
	}

	/**
	 * Parse a tuple in a GML CoordinatesType string.
	 * 
	 * @param tuple the tuple
	 * @param coordinateSplitter the coordinate splitter
	 * @param format the number format
	 * @return the coordinate or <code>null</code>
	 * @throws ParseException if parsing the coordinates fails
	 */
	private static Coordinate parseTuple(String tuple, Splitter coordinateSplitter,
			NumberFormat format) throws ParseException {
		if (tuple == null || tuple.isEmpty()) {
			return null;
		}

		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;

		Iterable<String> coordinates = coordinateSplitter.split(tuple);
		Iterator<String> itCoordinates = coordinates.iterator();
		int index = 0;
		while (index <= 2 && itCoordinates.hasNext()) {
			String coord = itCoordinates.next();

			// parse coordinate value
			Number value = format.parse(coord);
			switch (index) {
			case 0:
				x = value.doubleValue();
				break;
			case 1:
				y = value.doubleValue();
				break;
			case 2:
				z = value.doubleValue();
				break;
			}

			index++;
		}

		return new Coordinate(x, y, z);
	}

	private static String getTupleSeparator(Instance coordinates) {
		return getAttributeValue(coordinates, new QName("ts"), " "); // default
																		// separator
																		// within
																		// a
																		// tuple
																		// is a
																		// space
	}

	private static String getCoordinateSeparator(Instance coordinates) {
		return getAttributeValue(coordinates, new QName("cs"), ","); // default
																		// separator
																		// within
																		// a
																		// tuple
																		// is a
																		// comma
	}

	private static String getCoordinatesDecimal(Instance coordinates) {
		return getAttributeValue(coordinates, new QName("decimal"), "."); // default
																			// decimal
																			// point
																			// is
																			// a
																			// dot
	}

	private static String getAttributeValue(Instance coordinates, QName propertyName, String def) {
		Object[] values = coordinates.getProperty(propertyName);

		if (values != null && values.length > 0) {
			Object value = values[0];
			try {
				String decimal = ConversionUtil.getAs(value, String.class);
				if (decimal != null && !decimal.isEmpty()) { // don't accept
																// empty values
					return decimal;
				}
			} catch (ConversionException e) {
				// ignore, just use the default then
			}
		}

		return def;
	}

	/**
	 * Parse a coordinate from a GML DirectPositionType instance.
	 * 
	 * @param directPosition the direct position instance
	 * @return the coordinate or <code>null</code> if the instance contains not
	 *         direct position
	 * @throws GeometryNotSupportedException if no valid coordinate could be
	 *             created from the direct position
	 */
	public static Coordinate parseDirectPosition(Instance directPosition)
			throws GeometryNotSupportedException {
		// XXX should the type be checked to match CoordinatesType?

		Object value = directPosition.getValue();

		if (value != null) {
			// binding for DirectPositionType is Collection/Double
			try {
				List<Double> values = ConversionUtil.getAsList(value, Double.class, true);
				if (values.size() == 2) {
					return new Coordinate(values.get(0), values.get(1));
				}
				else if (values.size() >= 3) {
					return new Coordinate(values.get(0), values.get(1), values.get(2));
				}
				else {
					throw new GeometryNotSupportedException(
							"DirectPosition with invalid number of coordinates: " + values.size());
				}
			} catch (ConversionException e) {
				throw new GeometryNotSupportedException(e);
			}
		}

		return null;
	}

	/**
	 * Parse a coordinate from a GML PosList instance.
	 * 
	 * @param posList the PosList instance
	 * @param srsDimension the Dimension of the instance
	 * @return the array of the coordinates or <code>null</code> if the instance
	 *         contains not a PosList
	 * @throws GeometryNotSupportedException if no valid coordinate could be
	 *             created from the PosList
	 */
	public static Coordinate[] parsePosList(Instance posList, int srsDimension)
			throws GeometryNotSupportedException {

		Object value = posList.getValue();
		Coordinate[] coordinates = null;

		// XXX Coordinate support only 2D and 3D coordinates

		if (value != null) {
			try {
				List<Double> values = ConversionUtil.getAsList(value, Double.class, true);

				/*
				 * Filter null values that may have been created because of
				 * whitespace, e.g. at the end or beginning of the list.
				 * 
				 * XXX An alternative would be trimming the list string before
				 * splitting it (in SimpleTypeUtil.convertFromXml), though I am
				 * not sure what the behavior actually should be according to
				 * XML Schema (is whitespace at the beginning/end just ignored
				 * or not?)
				 */
				values.removeAll(Collections.singleton(null));

				List<Coordinate> cs = new ArrayList<Coordinate>();

				// validate dimension
				if (values.size() % srsDimension != 0) {
					// try alternative dimension
					int alternative = (srsDimension == 2) ? (3) : (2);

					if (values.size() % alternative != 0) {
						// still not valid
						throw new GeometryNotSupportedException(
								"Value count in posList not compatible to given dimension.");
					}
					else {
						log.debug("Assuming " + alternative
								+ "-dimensional coordinates, as value count doesn't match "
								+ srsDimension + " dimensions.");
						srsDimension = alternative;
					}
				}

				if (srsDimension == 2) {
					for (int i = 0; i < values.size(); i++) {
						cs.add(new Coordinate(values.get(i), values.get(++i)));
					}
					coordinates = cs.toArray(new Coordinate[values.size() / 2]);
				}
				else if (srsDimension == 3) {
					for (int i = 0; i < values.size(); i++) {
						cs.add(new Coordinate(values.get(i), values.get(++i), values.get(++i)));
					}
					coordinates = cs.toArray(new Coordinate[values.size() / 3]);
				}
				else {
					throw new GeometryNotSupportedException(
							"DirectPosition with invalid number of coordinates: " + values.size());
				}

			} catch (ConversionException e) {
				throw new GeometryNotSupportedException(e);
			}
		}

		return coordinates;
	}

	/**
	 * Parse a coordinate from a GML CoordType instance.
	 * 
	 * @param instance the coord instance
	 * @return the coordinate
	 * @throws GeometryNotSupportedException if a valid coordinate can't be
	 *             created
	 */
	public static Coordinate parseCoord(Instance instance) throws GeometryNotSupportedException {
		double x = Double.NaN;
		double y = Double.NaN;
		double z = Double.NaN;

		Collection<Object> values = PropertyResolver.getValues(instance, "X", false);
		if (values == null || values.isEmpty()) {
			throw new GeometryNotSupportedException("Missing X coordinate");
		}
		x = ConversionUtil.getAs(values.iterator().next(), Double.class);

		values = PropertyResolver.getValues(instance, "Y", false);
		if (values == null || values.isEmpty()) {
			throw new GeometryNotSupportedException("Missing Y coordinate");
		}
		y = ConversionUtil.getAs(values.iterator().next(), Double.class);

		values = PropertyResolver.getValues(instance, "Z", false);
		if (values != null && !values.isEmpty()) {
			z = ConversionUtil.getAs(values.iterator().next(), Double.class);
		}

		return new Coordinate(x, y, z);
	}

	/**
	 * Find the CRS definition to be associated with the geometry contained in
	 * the given instance.
	 * 
	 * @param instance the given instance
	 * @return the CRS definition or <code>null</code> if none could be
	 *         identified
	 */
	public static CRSDefinition findCRS(Instance instance) {
		BreadthFirstInstanceTraverser traverser = new BreadthFirstInstanceTraverser();

		CRSFinder finder = new CRSFinder();
		traverser.traverse(instance, finder);

		return finder.getDefinition();
	}

}
