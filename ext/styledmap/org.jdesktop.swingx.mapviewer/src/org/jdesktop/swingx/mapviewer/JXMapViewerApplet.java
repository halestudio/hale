/*
 * JXMapViewerApplet.java
 *
 * Created on December 19, 2006, 11:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JApplet;

/**
 * 
 * @author joshy
 */
public class JXMapViewerApplet extends JApplet {

	private static final long serialVersionUID = 8488941248384896693L;

	/**
	 * The map kit
	 */
	protected JXMapKit kit;

	/** Creates a new instance of JXMapViewerApplet */
	public JXMapViewerApplet() {
	}

	/**
	 * @see JApplet#init()
	 */
	@Override
	public void init() {
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					createGUI();
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete");
			e.printStackTrace();
		}
	}

	/**
	 * Create the UI
	 */
	protected void createGUI() {
		kit = new JXMapKit();
		// GeoPosition origin = new GeoPosition(0, 0, GeoPosition.WGS_84_EPSG);
		GeoPosition sanjose = new GeoPosition(37, 20, 0, -121, -53, 0);
		GeoPosition statlib = new GeoPosition(40, 41, 20, -74, -2, -42.4);
		Set<Waypoint> set = new HashSet<Waypoint>();
		set.add(new Waypoint(statlib));
		set.add(new Waypoint(sanjose));
		WaypointPainter<JXMapViewer> wp = new WaypointPainter<JXMapViewer>();
		wp.setWaypoints(set);
		kit.getMainMap().setOverlayPainter(wp);
		kit.getMainMap().setCenterPosition(new GeoPosition(-100, 38.5, GeoPosition.WGS_84_EPSG));
		kit.setZoom(2);
		this.add(kit);
	}

}
