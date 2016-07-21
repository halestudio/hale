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
package de.fhg.igd.mapviewer.server.wms.capabilities;

/**
 * Layer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 *
 * @version $Id$
 */
public class Layer {

	private String name;

	private String displayName;

	private String description;

	private boolean selected = true;

	/**
	 * Constructor
	 * 
	 * @param name the map name
	 * @param displayName the display name
	 * @param description the description
	 */
	public Layer(String name, String displayName, String description) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.description = description;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		if (displayName == null)
			return name;
		else
			return displayName;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

}
