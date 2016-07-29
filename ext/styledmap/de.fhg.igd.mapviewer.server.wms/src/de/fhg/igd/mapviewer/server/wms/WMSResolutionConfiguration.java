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

package de.fhg.igd.mapviewer.server.wms;

import java.util.prefs.Preferences;

/**
 * Extend WMSMap configuration to configure resolution for orthophotos.
 * 
 * @author Benedikt Hiemenz
 */
public class WMSResolutionConfiguration extends WMSConfiguration {

	/**
	 * Default size x
	 */
	public static final int DEFAULT_X_SIZE = 2048;

	/**
	 * Default size y
	 */
	public static final int DEFAULT_Y_SIZE = 2048;

	// preference names
	private static final String X_SIZE = "xSize"; //$NON-NLS-1$
	private static final String Y_SIZE = "ySize"; //$NON-NLS-1$

	private int xTileSize = DEFAULT_X_SIZE;
	private int yTileSize = DEFAULT_Y_SIZE;

	/**
	 * The preferences
	 */
	private static final Preferences PREF_SERVERS = Preferences
			.userNodeForPackage(WMSResolutionConfiguration.class).node("configuration"); //$NON-NLS-1$

	@Override
	protected void saveProperties(Preferences node) {

		super.saveProperties(node);
		node.putInt(X_SIZE, getxTileSize());
		node.putInt(Y_SIZE, getxTileSize());
	}

	@Override
	protected void loadProperties(Preferences node) {

		super.loadProperties(node);
		setxTileSize(node.getInt(X_SIZE, DEFAULT_X_SIZE));
		setyTileSize(node.getInt(Y_SIZE, DEFAULT_Y_SIZE));
	}

	/**
	 * @return the xTileSize
	 */
	public int getxTileSize() {
		return xTileSize;
	}

	/**
	 * @param xTileSize the xTileSize to set
	 */
	public void setxTileSize(int xTileSize) {
		this.xTileSize = xTileSize;
	}

	/**
	 * @return the yTileSize
	 */
	public int getyTileSize() {
		return yTileSize;
	}

	/**
	 * @param yTileSize the yTileSize to set
	 */
	public void setyTileSize(int yTileSize) {
		this.yTileSize = yTileSize;
	}

	@Override
	protected Preferences getPreferences() {
		return PREF_SERVERS;
	}
}
