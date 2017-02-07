/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.view.arecalculation;

import java.awt.geom.Point2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.geotools.referencing.CRS;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;
import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import de.fhg.igd.geom.Point3D;
import de.fhg.igd.geom.algorithm.FaceTriangulation;

/**
 * This class manages the calculation of any 2D surface.
 * 
 * @author <a href="mailto:andreas.burchert@igd.fhg.de">Andreas Burchert</a>
 */
public class AreaCalc extends Thread {

	/**
	 * Instance of this class.
	 */
	private static AreaCalc instance = null;

	/**
	 * Calculation is active (default: false)
	 */
	private static boolean active = false;

	/**
	 * True if a calculation is in progress.
	 */
	private boolean calculation = false;

//	/**
//	 * Contains true if a update for GeoPositions is available.
//	 */
//	private boolean updated = false;

	/**
	 * This is needed for not calculating permanently.
	 */
	private boolean bufferIsFinished = false;

	/**
	 * Last update run for GeoCoordinates.
	 */
	private long lastUpdate = 0;

	/**
	 * Delay in ms.
	 */
	private final long delay = 100;

	/**
	 * Contains the view.
	 */
	private JXMapViewer map = null;

	/**
	 * Contains all vertexes
	 */
	private List<GeoPosition> geoPos = new ArrayList<GeoPosition>(300);

	/**
	 * Contains the current GeoPosition
	 */
	private GeoPosition currentGeoPos = new GeoPosition(0, 0, 0);

	private final Set<AreaListener> listeners = new HashSet<AreaListener>();

	/**
	 * Contains the type of selection: rectangle, polygon, buffer, line FIXME
	 * use an enumeration for this
	 */
	private String selectionType = "";

	/**
	 * Contains a formatted String with the surface area.
	 */
	private String area = "";

	/**
	 * Constructor.
	 */
	public AreaCalc() {
		/* nothing */
	}

	/**
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		while (isActive()) {
			this.calculate();

			try {
				sleep(this.delay);
			} catch (InterruptedException e) {
				e.printStackTrace();

				// try to get this thread back
				instance.start();
			}
		}
	}

	/**
	 * Add an area listener
	 * 
	 * @param listener the listener to add
	 */
	public void addListener(AreaListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove an area listener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(AreaListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return current instance of AreaCalc
	 */
	public static AreaCalc getInstance() {
		if (instance == null) {
			AreaCalc tmp = new AreaCalc();
			tmp.setName("AreaCalculation");
			tmp.start();
			instance = tmp;
		}

		return instance;
	}

	/**
	 * @param state activestate
	 */
	public void setActive(boolean state) {
		if (!AreaCalc.active) {
			new Thread(instance).start();
		}

		active = state;
		fireActiveChanged();
	}

	/**
	 * @return action state
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Adds the ToolTip to {@link JXMapViewer}.
	 * 
	 * @param map mapviewer
	 */
	public void setMap(JXMapViewer map) {
		if (this.map == null) {
			// set "big" map
			this.map = map;
		}
	}

	/**
	 * Setter for current {@link GeoPosition}.
	 * 
	 * @param pos current GeoPosition
	 */
	public void setCurrentGeoPos(GeoPosition pos) {
		if (!pos.equals(this.currentGeoPos)) {
			this.currentGeoPos = pos;
//			this.updated = true;
		}
	}

	/**
	 * Setter for all selected {@link GeoPosition}s.
	 * 
	 * @param pos from AbstractMapTool
	 */
	public void setGeoPositions(List<GeoPosition> pos) {
		if (!pos.equals(this.geoPos)) {
			this.geoPos = pos;
//			this.updated = true;
		}
	}

	/**
	 * Converts a Polygon to GeoPositions.
	 * 
	 * @param buffer contains the polygon
	 */
	public void setBufferPolygon(java.awt.Polygon buffer) {
		if (!calculation && this.lastUpdate < System.currentTimeMillis()) {
			List<java.awt.geom.Point2D> pts = new ArrayList<java.awt.geom.Point2D>(buffer.npoints);

			// create Point2D and add them
			for (int i = 0; i < buffer.xpoints.length; i++) {
				pts.add(new Point2D.Double(buffer.xpoints[i], buffer.ypoints[i]));
			}

			this.setPoint2DAsGeoPositions(pts);
		}
	}

	/**
	 * This function sets {@link java.awt.geom.Point2D} coordinates as
	 * {@link GeoPosition} to calculate the surface area.
	 * 
	 * @param points List of {@link java.awt.geom.Point2D}
	 */
	public void setPoint2DAsGeoPositions(List<java.awt.geom.Point2D> points) {
		if (isActive()) {
			this.geoPos = this.map.convertAllPointsToGeoPositions(points);
		}

		this.lastUpdate = System.currentTimeMillis() + this.delay;
	}

	/**
	 * Setter for selection type.
	 * 
	 * @param type new selection type
	 */
	public void setSelectionType(String type) {
		if (!type.equals(this.selectionType)) {
			if (!type.equals("buffer")) {
				this.bufferIsFinished = false;
			}
			this.selectionType = type;
			this.area = "";
			fireAreaChanged();
		}
	}

	/**
	 * 
	 */
	public void bufferReset() {
		this.bufferIsFinished = false;
		this.geoPos.clear();
	}

	/**
	 * Setter for {@link AreaCalc#area}. Used for external access.
	 * 
	 * @param text new text
	 */
	public void setArea(String text) {
		this.area = text;
		fireAreaChanged();
	}

	private void fireAreaChanged() {
		for (AreaListener listener : listeners) {
			listener.areaChanged(area);
		}
	}

	private void fireActiveChanged() {
		for (AreaListener listener : listeners) {
			listener.activationStateChanged(active);
		}
	}

	/**
	 * Returns lastUpdate.
	 * 
	 * @return lastUpdate in milliseconds
	 */
	public long getLastUpdate() {
		return this.lastUpdate;
	}

	/**
	 * Returns the formatted area.
	 * 
	 * @return the area
	 */
	public String getArea() {
		return this.area;
	}

	/**
	 * Checks if GeoPosition are in a metric system and if not convert them if
	 * necessary.
	 * 
	 * @param pos List of {@link GeoPosition}
	 * 
	 * @return List of {@link GeoPosition}
	 */
	public List<GeoPosition> checkEPSG(List<GeoPosition> pos) {
		// list is empty
		if (pos.size() == 0) {
			return pos;
		}

		//
		int epsg = pos.get(0).getEpsgCode();
		int FALLBACK_EPSG = 3395; // Worldmercator
		FALLBACK_EPSG = 4326; // WGS84

		try {
			CoordinateSystem cs = CRS.decode("EPSG:" + epsg).getCoordinateSystem();

			for (int i = 0; epsg != FALLBACK_EPSG && i < cs.getDimension(); i++) {
				CoordinateSystemAxis axis = cs.getAxis(i);

				try {
					Unit<Length> unit = axis.getUnit().asType(Length.class);

					if (!unit.toString().equals("m")) { //$NON-NLS-1$
						// not metric
						epsg = FALLBACK_EPSG;
					}
				} catch (ClassCastException e) {
					// no length unit
					epsg = FALLBACK_EPSG;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// convert all coordinates
		try {
			GeotoolsConverter g = (GeotoolsConverter) GeotoolsConverter.getInstance();
			pos = g.convertAll(pos, epsg);
		} catch (IllegalGeoPositionException e1) {
			e1.printStackTrace();
		}

		return pos;
	}

	/**
	 * This function calculates the area of a polygon.
	 */
	public void calculate() {
		if (isActive() && this.geoPos.size() > 0 && !this.calculation
		/* && this.updated */ && !this.bufferIsFinished) {
			// set calculation to true to prevent double calculating
			this.calculation = true;

			// but allow new position data
//			this.updated = false;

			List<GeoPosition> pos = new ArrayList<GeoPosition>();
			pos.addAll(this.geoPos);
			pos.add(this.currentGeoPos);

			// contains information for the tooltip
			String tip = "";

			// check epsg code and maybe convert to a metric system
			pos = this.checkEPSG(pos);

			// initialize formater
			NumberFormat df = NumberFormat.getNumberInstance();

			// calculate size
			double area = 0.0;

			// rectangle
			if (pos.size() == 2 && this.selectionType.equals("rectangle")) {
				area = this.calculateRectangle(pos.get(0), pos.get(1));
				if (area > 1000000) {
					area /= 1000000;
					tip = df.format(area) + " km\u00B2";
				}
				else {
					tip = df.format(area) + " m\u00B2";
				}
			}
			// distance (polygon tool)
			else if (pos.size() == 2 && this.selectionType.equals("polygon")) {
				area = AreaCalc.calculateDistance(pos.get(0), pos.get(1));
				tip = df.format(area) + " m";
			}
			// distance (buffer tool)
			else if (pos.size() > 1 && this.selectionType.equals("line")) {
				double temp = 0.0;

				for (int i = 0; i < pos.size() - 1; i++) {
					temp += AreaCalc.calculateDistance(pos.get(i), pos.get(i + 1));
				}
				tip = df.format(temp) + " m";
			}
			// polygon
			else if (pos.size() > 2 && this.selectionType.equals("polygon")) {
				area = this.calculatePolygon(pos);
				if (area > 1000000) {
					area /= 1000000;
					tip = df.format(area) + " km\u00B2";
				}
				else {
					tip = df.format(area) + " m\u00B2";
				}
			}

			// buffer tool
			else if (this.selectionType.equals("buffer")) {
				if (pos.size() == 2) {
					area = AreaCalc.calculateDistance(pos.get(0), pos.get(1));
					tip = df.format(area) + " m";
				}
				else {
					// calculate
					area = this.calculatePolygon(pos);
					if (area > 1000000) {
						area /= 1000000;
						tip = df.format(area) + " km\u00B2";
					}
					else {
						tip = df.format(area) + " m\u00B2";
					}
				}
			}

			// update tooltip
			this.area = tip;

			fireAreaChanged();

			// calculation has finished
			this.calculation = false;
		}
	}

	/**
	 * Calculates the distance between two geo coordinates using the haversine
	 * formula.
	 * 
	 * @param a first {@link GeoPosition}
	 * @param b second {@link GeoPosition}
	 * 
	 * @return area
	 */
	public static double calculateDistance(GeoPosition a, GeoPosition b) {
		double value = 0.0;

		// use haversine for wgs84 related systems
		if (a.getEpsgCode() >= 4326 && a.getEpsgCode() <= 4329) {
			value = AreaCalc.haversine(a, b);
		}
		else {
			value = Math
					.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getY()), 2));
		}

		return value;
	}

	/**
	 * Calculates the distance between to points.
	 * 
	 * @param StartP source
	 * @param EndP destination
	 * 
	 * @return distance
	 */
	public static double haversine(GeoPosition StartP, GeoPosition EndP) {
		// Earth radius
		double Radius = 6.371;

		// latitude
		double lat1 = StartP.getY();
		double lat2 = EndP.getY();

		// longitude
		double lon1 = StartP.getX();
		double lon2 = EndP.getX();

		// convert to radians
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		// calculation
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));

		// multiplication with 1mio is needed for correct values
		return Radius * c * Math.pow(1000, 2);
	}

	/**
	 * Calculate the area of a rectangle.
	 * 
	 * @param a first {@link GeoPosition}
	 * @param b second {@link GeoPosition}
	 * 
	 * @return area
	 */
	public double calculateRectangle(GeoPosition a, GeoPosition b) {
		double value = 0.0;

		value = AreaCalc.calculateDistance(new GeoPosition(a.getX(), a.getY(), a.getEpsgCode()),
				new GeoPosition(b.getX(), a.getY(), a.getEpsgCode()))
				* AreaCalc.calculateDistance(new GeoPosition(a.getX(), a.getY(), a.getEpsgCode()),
						new GeoPosition(a.getX(), b.getY(), a.getEpsgCode()));

		return Math.abs(value);
	}

	/**
	 * Function to calculate the surface of a polygon.
	 * 
	 * @param pos List of {@link GeoPosition}
	 * 
	 * @return area
	 */
	public double calculatePolygon(List<GeoPosition> pos) {
		// contains the result
		double result = 0.0;

		// epsg code
		int epsg = pos.get(0).getEpsgCode();

		// check if it can be done with the shoelace formula
		if (epsg >= 31461 && epsg <= 31469) {
			result = this.shoelaceFormula(pos);
		}

		if (result == 0.0) {
			result = this.heronFormula(pos);
		}

		return result;
	}

	/**
	 * Heron formula.
	 * 
	 * @param pos List of {@link GeoPosition}
	 * 
	 * @return area
	 */
	private double heronFormula(List<GeoPosition> pos) {
		double result = 0.0;

		List<Triangle> triangles = this.triangulate(pos);

		for (Triangle t : triangles) {
			result += t.getArea();
		}

		return result;
	}

	/**
	 * Triangulates the polygon.
	 * 
	 * @param pos List of {@link GeoPosition}
	 * 
	 * @return List of {@link Triangle}
	 */
	private List<Triangle> triangulate(List<GeoPosition> pos) {
		// contains all triangles
		List<Triangle> triangles = new ArrayList<Triangle>(pos.size() - 1);

		// standard epsg code
		int epsg = pos.get(0).getEpsgCode();

		// check if it's already a triangle
		if (pos.size() == 3) {
			triangles.add(new Triangle(pos.get(0), pos.get(1), pos.get(2)));
			return triangles;
		}

		// contains all points from the surface
		List<Point3D> face = new ArrayList<>();

		// convert Point2D to Vertex
		for (int i = 0; i < pos.size(); i++) {
			GeoPosition p = pos.get(i);
			face.add(new Point3D(p.getX(), p.getY(), 0.0));
		}

		// create FaceSet and triangulate
		FaceTriangulation fst = new FaceTriangulation();
		List<List<Point3D>> faces = fst.triangulateFace(face);

		// convert
		for (List<Point3D> f : faces) {
			// create GeoPositions
			GeoPosition p1, p2, p3;
			p1 = new GeoPosition(f.get(0).getX(), f.get(0).getY(), epsg);
			p2 = new GeoPosition(f.get(1).getX(), f.get(1).getY(), epsg);
			p3 = new GeoPosition(f.get(2).getX(), f.get(2).getY(), epsg);

			// add triangle
			triangles.add(new Triangle(p1, p2, p3));
		}

		return triangles;
	}

	/**
	 * Gauss' Area Formula
	 * 
	 * @param positions polygon
	 * 
	 * @return the base of the polygon
	 */
	private double shoelaceFormula(List<GeoPosition> positions) {
		double result = 0;
//		int epsg = positions.get(0).getEpsgCode();

		ArrayList<Double> listY = new ArrayList<Double>();
		ArrayList<Double> listX = new ArrayList<Double>();
		for (GeoPosition v : positions) {
			listY.add(v.getY());
			listX.add(v.getX());
		}

		for (int i = 0; i < listX.size(); i++) {
			result = result + listX.get(i)
					* (listY.get(getFirst(i, listY)) - listY.get(getSecond(i, listY)));
		}

		result = result / 2;

		//
		double reduction = 0.0;

		// earth's radius
//		double Radius = 6.371;

		// default: no reduction
//		double y;
//		
//		switch(epsg) {
//		// 3-degree Gauss zone 1
//		case 31461:
//				y = 1;
//			break;
//		
//		// 3-degree Gauss zone 2
//		case 31462:
//		case 31466:
//				y = 2;
//			break;
//		
//		// 3-degree Gauss zone 3
//		case 31463:
//		case 31467:
//				y = 3;
//			break;
//		
//		// 3-degree Gauss zone 4
//		case 31464:
//		case 31468:
//				y = 4;
//			break;
//		
//		// 3-degree Gauss zone 5
//		case 31465:
//		case 31469:
//			y = 5;
//		break;
//		default:
//			y = 0;
//		}

		// calculate reduction for gauss-kruger-systems
		// reduction = (result * Math.pow(y, 2))/Math.pow(Radius, 2);

		return Math.abs(result - reduction)/* *10000 */;
	}

	/**
	 * Help method for gauss
	 * 
	 * @param i nn
	 * @param listY nn
	 * 
	 * @return the first value
	 */
	private int getFirst(int i, ArrayList<Double> listY) {
		if (i == listY.size() - 1) {
			return 0;
		}
		return (i + 1);
	}

	/**
	 * Help method for gauss
	 * 
	 * @param i nn
	 * @param listY nn
	 * 
	 * @return the second value
	 */
	private int getSecond(int i, ArrayList<Double> listY) {
		if (i == 0) {
			return listY.size() - 1;
		}
		return (i - 1);
	}
}
