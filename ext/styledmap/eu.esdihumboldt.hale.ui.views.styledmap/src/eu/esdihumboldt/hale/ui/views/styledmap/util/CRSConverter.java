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

package eu.esdihumboldt.hale.ui.views.styledmap.util;

import java.util.HashMap;
import java.util.Map;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Point3D;

/**
 * Geotools based CRS converter.
 * 
 * @author Simon Templer
 */
public class CRSConverter {

	/**
	 * Thread local direct position initialized with a {@link DirectPosition2D}.
	 */
	public static class ThreadLocalDirectPosition2D extends ThreadLocal<DirectPosition> {

		@Override
		protected DirectPosition initialValue() {
			return new DirectPosition2D();
		}

	}

	/**
	 * Create a CRS converter between the given coordinate reference systems.
	 * 
	 * @param source the source CRS
	 * @param target the target CRS
	 * @return the CRS converter
	 * @throws FactoryException if creating the transformer fails
	 */
	public synchronized static CRSConverter getConverter(CoordinateReferenceSystem source,
			CoordinateReferenceSystem target) throws FactoryException {
		CRSConverter converter = null;

		// try retrieving converter from map
		Map<CoordinateReferenceSystem, CRSConverter> sourceConverters = converters.get(source);
		if (sourceConverters != null) {
			converter = sourceConverters.get(target);
		}

		if (converter == null) {
			// create and store the converter
			converter = new CRSConverter(source, target);

			if (sourceConverters == null) {
				sourceConverters = new HashMap<CoordinateReferenceSystem, CRSConverter>();
				converters.put(source, sourceConverters);
			}

			sourceConverters.put(target, converter);
		}

		return converter;
	}

	/**
	 * Source CRS mapped to target CRS mapped to converter
	 */
	private static final Map<CoordinateReferenceSystem, Map<CoordinateReferenceSystem, CRSConverter>> converters = new HashMap<CoordinateReferenceSystem, Map<CoordinateReferenceSystem, CRSConverter>>();

	private final CoordinateReferenceSystem source;

	private final CoordinateReferenceSystem target;

	private final boolean initialFlip;

	private final boolean finalFlip;

	private final MathTransform math;

	/*
	 * temporary positions to lower the impact on GC
	 */
	private final ThreadLocal<DirectPosition> _tempPos2A = new ThreadLocalDirectPosition2D();
	private final ThreadLocal<DirectPosition> _tempPos2B = new ThreadLocalDirectPosition2D();

	/**
	 * Create a CRS converter between the given coordinate reference systems.
	 * 
	 * @param source the source CRS
	 * @param target the target CRS
	 * @throws FactoryException if creating the transformer fails
	 */
	private CRSConverter(CoordinateReferenceSystem source, CoordinateReferenceSystem target)
			throws FactoryException {
		this.source = source;
		this.target = target;

//		this.math = CRS.findMathTransform(source, target);
		// XXX lenient mode to find transformation also if Bursa Wolf parameters
		// are not present (ok for display according to Geotools)
		// http://docs.geotools.org/latest/userguide/faq.html#q-bursa-wolf-parameters-required
		// XXX does this error only occur for CRS related to shapefiles?
		this.math = CRS.findMathTransform(source, target, true);

		/*
		 * XXX do not flip the coordinates - the math transformation should
		 * handle it correctly, because it is based on the CRS definitions
		 */
		this.initialFlip = false; // flipCRS(source);
		this.finalFlip = false; // flipCRS(target);
	}

	/**
	 * Convert a bounding box.
	 * 
	 * @param bb the bounding box in the source CRS
	 * @return the bounding box in the target CRS
	 * @throws TransformException if the conversion fails
	 */
	public BoundingBox convert(BoundingBox bb) throws TransformException {
		Point3D targetCorners[] = { null, null };
		targetCorners[0] = convert(bb.getMinX(), bb.getMinY(), bb.getMinZ());
		targetCorners[1] = convert(bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
		return new BoundingBox(//
				targetCorners[0].getX(), //
				targetCorners[0].getY(), //
				targetCorners[0].getZ(), //
				targetCorners[1].getX(), //
				targetCorners[1].getY(), //
				targetCorners[1].getZ());
	}

	/**
	 * This method checks if the the CoordinateSystem is in the way we expected,
	 * or if we have to flip the coordinates.
	 * 
	 * @param crs The CRS to be checked.
	 * @return True, if we have to flip the coordinates
	 */
	@SuppressWarnings("unused")
	private boolean flipCRS(CoordinateReferenceSystem crs) {
		if (crs.getCoordinateSystem().getDimension() == 2) {
			AxisDirection direction = crs.getCoordinateSystem().getAxis(0).getDirection();
			if (direction.equals(AxisDirection.NORTH) || direction.equals(AxisDirection.UP)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method converts the given coordinates and returns them as a Point3D.
	 * 
	 * @param x the x ordinate
	 * @param y the y ordinate
	 * @param z the z ordinate
	 * @return The converted coordinates as a Point3D.
	 * @throws TransformException if the coordinate transformation fails
	 */
	public Point3D convert(double x, double y, double z) throws TransformException {
		DirectPosition position = _tempPos2A.get();
		if (this.initialFlip) {
			position.setOrdinate(0, y);
			position.setOrdinate(1, x);
		}
		else {
			position.setOrdinate(0, x);
			position.setOrdinate(1, y);
		}

		DirectPosition targetPosition = _tempPos2B.get();
		math.transform(position, targetPosition);

		return createPoint3D(targetPosition.getOrdinate(0), targetPosition.getOrdinate(1), z);
	}

	/**
	 * This method creates a Point3D from the given coordinates.
	 * 
	 * @param x The X axis value of the coordinate.
	 * @param y The Y axis value of the coordinate.
	 * @param z The Z axis value of the coordinate.
	 * @return A Point3D from the given coordinates.
	 */
	private Point3D createPoint3D(double x, double y, double z) {
		if (finalFlip) {
			return new Point3D(y, x, z);
		}
		return new Point3D(x, y, z);
	}

	/**
	 * @return the source coordinate reference system
	 */
	public CoordinateReferenceSystem getSource() {
		return source;
	}

	/**
	 * @return the target coordinate reference system
	 */
	public CoordinateReferenceSystem getTarget() {
		return target;
	}

}
