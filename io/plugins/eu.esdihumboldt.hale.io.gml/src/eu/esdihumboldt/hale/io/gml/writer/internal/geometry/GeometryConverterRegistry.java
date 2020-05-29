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

package eu.esdihumboldt.hale.io.gml.writer.internal.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;

import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters.MultiLineStringToLineString;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters.MultiPointToPoint;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters.MultiPolygonToMultiLineString;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters.MultiPolygonToPolygon;
import eu.esdihumboldt.hale.io.gml.writer.internal.geometry.converters.PolygonToLineString;

/**
 * Registry for {@link GeometryConverter}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class GeometryConverterRegistry {

	/**
	 * Conversion ladder - offers conversion for a geometry in the style of an
	 * iterator. Best conversions will be served first.
	 */
	public class ConversionLadder implements Iterator<Geometry> {

		private final Map<Class<? extends Geometry>, Geometry> results = new HashMap<Class<? extends Geometry>, Geometry>();

		private final Queue<Geometry> pendingGeometries = new LinkedList<Geometry>();

		private final Queue<GeometryConverter<?, ?>> pendingConverters = new LinkedList<GeometryConverter<?, ?>>();

		private final boolean noLossOnly;

		/**
		 * Create a conversion ladder for the given geometry
		 * 
		 * @param geometry the geometry
		 * @param noLossOnly if only no-loss converters may be used
		 */
		protected ConversionLadder(Geometry geometry, boolean noLossOnly) {
			this.noLossOnly = noLossOnly;

			Class<? extends Geometry> geomClass = geometry.getClass();
			results.put(geomClass, geometry);

			pendingGeometries.add(geometry);
		}

		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public synchronized boolean hasNext() {
			if (pendingConverters.isEmpty()) {
				prepareNext();
			}

			return !pendingConverters.isEmpty();
		}

		/**
		 * Prepare next step
		 */
		@SuppressWarnings("unchecked")
		private void prepareNext() {
			if (!pendingConverters.isEmpty()) {
				return;
			}

			Geometry geom = pendingGeometries.poll();
			if (geom != null) {
				// prepare best conversions reachable from geom
				Class<? extends Geometry> geomClass = geom.getClass();

				// find level one converters
				Set<GeometryConverter<?, ?>> l1 = converters.get(geomClass);
				if (l1 != null) {
					// sort by loss/no loss
					List<GeometryConverter<?, ?>> noloss = new ArrayList<GeometryConverter<?, ?>>();
					List<GeometryConverter<?, ?>> loss = new ArrayList<GeometryConverter<?, ?>>();
					for (GeometryConverter<?, ?> converter : l1) {
						@SuppressWarnings("rawtypes")
						GeometryConverter conv = converter; // correct source
															// type is assured

						if (!ignore(converter)) {
							if (conv.lossOnConversion(geom)) {
								loss.add(converter);
							}
							else {
								noloss.add(converter);
							}
						}
					}

					// TODO collect converters through more levels to allow
					// multi-level-noloss is preferred to loss?

					pendingConverters.addAll(noloss);
					if (!noLossOnly) {
						pendingConverters.addAll(loss);
					}
				}
			}
		}

		/**
		 * Tells if to ignore a converter
		 * 
		 * @param converter the converter
		 * 
		 * @return if to ignore the converter
		 */
		private boolean ignore(GeometryConverter<?, ?> converter) {
			return results.containsKey(converter.getTargetType());
		}

		/**
		 * @see Iterator#next()
		 */
		@SuppressWarnings("unchecked")
		@Override
		public synchronized Geometry next() {
			prepareNext();

			@SuppressWarnings("rawtypes")
			GeometryConverter converter = pendingConverters.poll();

			if (results.containsKey(converter.getTargetType())) {
				// for any reason this was already calculated (e.g.
				// preconversion when descending into converter levels)
				return results.get(converter.getTargetType());
			}

			// convert
			Geometry source = results.get(converter.getSourceType());
			Geometry target = converter.convert(source);
			results.put(converter.getTargetType(), target);
			pendingGeometries.add(target);

			return target;
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private static final GeometryConverterRegistry INSTANCE = new GeometryConverterRegistry();

	/**
	 * Get the singleton instance of the registry
	 * 
	 * @return the geometry converter registry
	 */
	public static GeometryConverterRegistry getInstance() {
		return INSTANCE;
	}

	/**
	 * Converters organized by source geometry type
	 */
	private final Map<Class<? extends Geometry>, Set<GeometryConverter<?, ?>>> converters = new HashMap<Class<? extends Geometry>, Set<GeometryConverter<?, ?>>>();

	/**
	 * Default constructor
	 */
	private GeometryConverterRegistry() {
		super();

		init();
	}

	/**
	 * Initialize the registry
	 */
	private void init() {
		// built-in converters
		registerConverter(new MultiPolygonToMultiLineString());
		registerConverter(new PolygonToLineString());
		registerConverter(new MultiPolygonToPolygon());
		registerConverter(new MultiLineStringToLineString());
		registerConverter(new MultiPointToPoint());

		// TODO other converters?
	}

	/**
	 * Register a geometry converter
	 * 
	 * @param converter the converter
	 */
	public void registerConverter(GeometryConverter<?, ?> converter) {
		Set<GeometryConverter<?, ?>> cs = converters.get(converter.getSourceType());
		if (cs == null) {
			cs = new HashSet<GeometryConverter<?, ?>>();
			converters.put(converter.getSourceType(), cs);
		}

		cs.add(converter);
	}

	/**
	 * Create a conversion ladder for the given geometry
	 * 
	 * @param geometry the geometry
	 * 
	 * @return the conversion ladder
	 */
	public ConversionLadder createLadder(Geometry geometry) {
		return new ConversionLadder(geometry, false);
	}

	/**
	 * Create a conversion ladder for the given geometry that does only no-loss
	 * conversions.
	 * 
	 * @param geometry the geometry
	 * 
	 * @return the conversion ladder
	 */
	public ConversionLadder createNoLossLadder(Geometry geometry) {
		return new ConversionLadder(geometry, true);
	}

}
