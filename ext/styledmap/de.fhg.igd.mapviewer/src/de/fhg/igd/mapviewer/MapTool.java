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
package de.fhg.igd.mapviewer;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

import org.jdesktop.swingx.mapviewer.GeoPosition;

/**
 * MapTool
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public interface MapTool {

	/**
	 * Resets the tool
	 */
	public void reset();

	/**
	 * Get the tool id
	 * 
	 * @return the tool id
	 */
	public String getId();

	/**
	 * Get the tool name
	 * 
	 * @return the tool name
	 */
	public String getName();

	/**
	 * Get the tool description
	 * 
	 * @return the tool description
	 */
	public String getDescription();

	/**
	 * Get the icon URL
	 * 
	 * @return the icon URL
	 */
	public URL getIconURL();

	/**
	 * Get the tool cursor
	 * 
	 * @return the tool cursor
	 */
	public Cursor getCursor();

	/**
	 * Get the tool renderer
	 * 
	 * @return the tool renderer
	 */
	public MapToolRenderer getRenderer();

	/**
	 * Get if panning shall be enabled for the tool
	 * 
	 * @return if panning shall be enabled for the tool
	 */
	public boolean isPanEnabled();

	/**
	 * Get the list of positions collected by the tool
	 * 
	 * @return the list of positions
	 */
	public List<GeoPosition> getPositions();

	/**
	 * Called when a mouse button has been pressed
	 * 
	 * @param me the mouse event
	 * @param pos the corresponding geo position
	 */
	public void mousePressed(MouseEvent me, GeoPosition pos);

	/**
	 * Called when a mouse button has been released
	 * 
	 * @param me the mouse event
	 * @param pos the corresponding geo position
	 */
	public void mouseReleased(MouseEvent me, GeoPosition pos);

	/**
	 * Called when a mouse button has been clicked
	 * 
	 * @param me the mouse event
	 * @param pos the corresponding geo position
	 */
	public void mouseClicked(MouseEvent me, GeoPosition pos);

	/**
	 * Set if the tool is active
	 * 
	 * @param active if the tool is active
	 */
	public void setActive(boolean active);

}
