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
package de.fhg.igd.mapviewer.tools;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.IllegalGeoPositionException;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapTool;
import de.fhg.igd.mapviewer.MapToolRenderer;
import de.fhg.igd.mapviewer.view.arecalculation.AreaCalc;

/**
 * AbstractMapTool
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public abstract class AbstractMapTool implements MapTool, Comparable<AbstractMapTool> {

	/**
	 * ActivationListener
	 */
	public static interface ActivationListener {

		/**
		 * Called when the activation state of the tool has changed
		 * 
		 * @param tool the tool that was activated/deactivated
		 * @param active the new state of the tool
		 */
		public void activated(AbstractMapTool tool, boolean active);

	}

	private final List<GeoPosition> positions = new ArrayList<GeoPosition>();

	private final Set<ActivationListener> listeners = new HashSet<ActivationListener>();

	private MapToolRenderer renderer;

	private boolean active = false;

	private Activator activator;

	/**
	 * The map kit
	 */
	protected BasicMapKit mapKit;

	private URL iconURL;
	private String name;
	private String description;

	private String id;

	private int priority = 0;

	private boolean lastPressedPopupTrigger = false;
	private boolean lastReleasedPopupTrigger = false;

	/**
	 * Set the map kit
	 * 
	 * @param mapKit the map kit to set
	 */
	public void setMapKit(BasicMapKit mapKit) {
		this.mapKit = mapKit;
	}

	/**
	 * @return the iconURL
	 */
	@Override
	public URL getIconURL() {
		return iconURL;
	}

	/**
	 * @param iconURL the iconURL to set
	 */
	public void setIconURL(URL iconURL) {
		this.iconURL = iconURL;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @see MapTool#getPositions()
	 */
	@Override
	public List<GeoPosition> getPositions() {
		return positions;
	}

	/**
	 * Get the positions as pixel coordinates
	 * 
	 * @return the pixel coordinates
	 * 
	 * @throws IllegalGeoPositionException if a conversion fails
	 */
	public List<Point2D> getPoints() throws IllegalGeoPositionException {
		List<Point2D> points = new ArrayList<Point2D>();

		for (GeoPosition pos : positions) {
			points.add(mapKit.getMainMap().convertGeoPositionToPoint(pos));
		}

		return points;
	}

	/**
	 * @see MapTool#getRenderer()
	 */
	@Override
	public MapToolRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Sets the renderer for this tool
	 * 
	 * @param renderer the renderer
	 */
	public void setRenderer(MapToolRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Add a position
	 * 
	 * @param pos the position to add
	 */
	protected void addPosition(GeoPosition pos) {
		positions.add(pos);
		repaint();

		// notify AreaCalc
		AreaCalc.getInstance().setGeoPositions(this.positions);
	}

	/**
	 * Remove the last position that was added
	 */
	protected void removeLastPosition() {
		if (positions.size() > 0) {
			positions.remove(positions.size() - 1);
			repaint();
		}
	}

	/**
	 * @see MapTool#reset()
	 */
	@Override
	public void reset() {
		positions.clear();
		repaint();
	}

	/**
	 * Triggers a repaint of the map
	 */
	protected void repaint() {
		mapKit.getMainMap().repaint();
	}

	/**
	 * Set the {@link Activator} that selects the Tool in its map environment
	 * 
	 * @param activator the activator to set
	 */
	public void setActivator(Activator activator) {
		this.activator = activator;
	}

	/**
	 * Activate the tool
	 */
	public void activate() {
		activator.activate();
	}

	/**
	 * @see MapTool#getCursor()
	 */
	@Override
	public Cursor getCursor() {
		return Cursor.getDefaultCursor();
	}

	/**
	 * @see MapTool#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active) {
		this.active = active;

		if (active) {
			activate();
		}

		for (ActivationListener listener : listeners) {
			listener.activated(this, active);
		}
	}

	/**
	 * @return if the tool is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Adds an {@link ActivationListener}
	 * 
	 * @param listener the listener to add
	 */
	public void addActivationListener(ActivationListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes an {@link ActivationListener}
	 * 
	 * @param listener the listener to remove
	 */
	public void removeActivationListener(ActivationListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Called when a popup event occurs
	 * 
	 * @param me the mouse event
	 * @param pos the geo position
	 */
	protected abstract void popup(MouseEvent me, GeoPosition pos);

	/**
	 * Called when a mouse button was clicked that was no popup trigger
	 * 
	 * @param me the mouse event
	 * @param pos the geo position
	 */
	protected abstract void click(MouseEvent me, GeoPosition pos);

	/**
	 * Called when a mouse button was pressed
	 * 
	 * @param me the mouse event
	 * @param pos the geo position
	 */
	protected abstract void pressed(MouseEvent me, GeoPosition pos);

	/**
	 * Called when a mouse button was released
	 * 
	 * @param me the mouse event
	 * @param pos the geo position
	 */
	protected abstract void released(MouseEvent me, GeoPosition pos);

	/**
	 * @see MapTool#mouseClicked(MouseEvent, GeoPosition)
	 */
	@Override
	public void mouseClicked(MouseEvent me, GeoPosition pos) {
		if (!me.isPopupTrigger() && !lastPressedPopupTrigger && !lastReleasedPopupTrigger) {
			click(me, pos);
		}
	}

	/**
	 * @see MapTool#mousePressed(MouseEvent, GeoPosition)
	 */
	@Override
	public void mousePressed(MouseEvent me, GeoPosition pos) {
		if (me.isPopupTrigger()) {
			popup(me, pos);
			lastPressedPopupTrigger = true;
		}
		else {
			pressed(me, pos);
			lastPressedPopupTrigger = false;
		}
	}

	/**
	 * @see MapTool#mouseReleased(MouseEvent, GeoPosition)
	 */
	@Override
	public void mouseReleased(MouseEvent me, GeoPosition pos) {
		if (me.isPopupTrigger()) {
			popup(me, pos);
			lastReleasedPopupTrigger = true;
		}
		else {
			released(me, pos);
			lastReleasedPopupTrigger = false;
		}
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(AbstractMapTool other) {
		if (priority > other.priority)
			return -1;
		else if (priority < other.priority)
			return 1;
		else
			return getId().compareTo(other.getId());
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

}
