/*+-------------+----------------------------------------------------------*
 *|  |  |_|_|_|_|   Fraunhofer-Institut fuer Graphische Datenverarbeitung  *
 *|__|__|_|_|_|_|     (Fraunhofer Institute for Computer Graphics)         *
 *|  |  |_|_|_|_|                                                          *
 *|__|__|_|_|_|_|                                                          *
 *|  __ |    ___|                                                          *
 *| /_  /_  / _ |     Fraunhoferstrasse 5                                  *
 *|/   / / /__/ |     D-64283 Darmstadt, Germany                           *
 *+-------------+----------------------------------------------------------*/
package org.jdesktop.swingx.mapviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.cache.CacheBuilder;

/**
 * GeotoolsConverter
 * 
 * @author Simon Templer
 */
public class GeotoolsConverter implements GeoConverter {

	private static final Log log = LogFactory.getLog(GeotoolsConverter.class);

	private static GeotoolsConverter INSTANCE;

	private final ConcurrentMap<Integer, CoordinateReferenceSystem> crsMap = CacheBuilder
			.newBuilder().softValues().<Integer, CoordinateReferenceSystem> build().asMap();

	private final ConcurrentMap<Long, MathTransform> transforms = CacheBuilder.newBuilder()
			.softValues().<Long, MathTransform> build().asMap();

	/**
	 * Get a GeotoolsConverter instance
	 * 
	 * @return the converter instance
	 */
	public static GeoConverter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GeotoolsConverter();
		}

		return INSTANCE;
	}

	private GeotoolsConverter() {
	}

	private CoordinateReferenceSystem getCRS(int epsg)
			throws NoSuchAuthorityCodeException, FactoryException {
		CoordinateReferenceSystem r = crsMap.get(epsg);
		if (r == null) {
			r = CRS.decode("EPSG:" + epsg, true);
			crsMap.put(epsg, r);
		}
		return r;
	}

	private MathTransform getTransform(int srcEpsg, int targetEpsg,
			CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
					throws FactoryException {
		long key = srcEpsg;
		key <<= Integer.SIZE;
		key |= targetEpsg;

		MathTransform r = transforms.get(key);
		if (r == null) {
			r = CRS.findMathTransform(sourceCRS, targetCRS, true);
			transforms.put(key, r);
		}
		return r;
	}

	/**
	 * @see GeoConverter#convert(GeoPosition, int)
	 */
	@Override
	public GeoPosition convert(GeoPosition pos, int targetEpsg) throws IllegalGeoPositionException {

		if (targetEpsg == pos.getEpsgCode())
			return new GeoPosition(pos.getX(), pos.getY(), targetEpsg);

		try {
			CoordinateReferenceSystem sourceCRS = getCRS(pos.getEpsgCode());
			CoordinateReferenceSystem targetCRS = getCRS(targetEpsg);
			MathTransform math = getTransform(pos.getEpsgCode(), targetEpsg, sourceCRS, targetCRS);

			int dimension = sourceCRS.getCoordinateSystem().getDimension();
			DirectPosition pt1;

			boolean flipSource = flipCRS(sourceCRS);

			switch (dimension) {
			case 2:
				pt1 = new GeneralDirectPosition((flipSource) ? (pos.getY()) : (pos.getX()),
						(flipSource) ? (pos.getX()) : (pos.getY()));
				break;
			case 3:
				pt1 = new GeneralDirectPosition((flipSource) ? (pos.getY()) : (pos.getX()),
						(flipSource) ? (pos.getX()) : (pos.getY()), 0);
				break;
			default:
				log.error("Unsupported dimension: " + dimension);
				throw new IllegalArgumentException("Unsupported dimension: " + dimension);
			}

			DirectPosition pt2 = math.transform(pt1, null);

			if (flipCRS(targetCRS))
				return new GeoPosition(pt2.getOrdinate(1), pt2.getOrdinate(0), targetEpsg);
			else
				return new GeoPosition(pt2.getOrdinate(0), pt2.getOrdinate(1), targetEpsg);
		} catch (Exception e) {
			throw new IllegalGeoPositionException(e);
		}
	}

	/**
	 * Tries to convert the given list of {@link GeoPosition} to the coordinate
	 * reference system specified by the given target epsg code.
	 * 
	 * @param positions a list containing {@link GeoPosition} to convert
	 * @param epsg the epsg code of the target coordinate reference system
	 * 
	 * @return the converted list of {@link GeoPosition}
	 * @throws IllegalGeoPositionException if the given {@link GeoPosition} is
	 *             invalid or conversion failed
	 */
	public List<GeoPosition> convertAll(List<GeoPosition> positions, int epsg)
			throws IllegalGeoPositionException {
		List<GeoPosition> newPositions = new ArrayList<GeoPosition>(positions.size());

		for (int i = 0; i < positions.size(); i++) {
			newPositions.add(this.convert(positions.get(i), epsg));
		}

		return newPositions;
	}

	/**
	 * This method checks if the the CoordinateSystem is in the way we expected,
	 * or if we have to flip the coordinates.
	 * 
	 * @param crs The CRS to be checked.
	 * @return True, if we have to flip the coordinates
	 */
	private boolean flipCRS(CoordinateReferenceSystem crs) {
		if (crs.getCoordinateSystem().getDimension() == 2) {
			AxisDirection direction = crs.getCoordinateSystem().getAxis(0).getDirection();
			if (direction.equals(AxisDirection.NORTH) || direction.equals(AxisDirection.UP)) {
				return true;
			}
		}
		return false;
	}

}
