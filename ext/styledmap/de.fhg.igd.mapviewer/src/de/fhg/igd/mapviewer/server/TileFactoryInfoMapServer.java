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
package de.fhg.igd.mapviewer.server;

import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactory;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileFactoryInfoTileProvider;

/**
 * TileFactoryInfoMapServer
 *
 * @author <a href="mailto:simon.templer@igd.fhg.de">Simon Templer</a>
 * @version $Id$
 * @deprecated use {@link TileProviderMapServer} instead
 */
@Deprecated
public class TileFactoryInfoMapServer extends AbstractMapServer {

	private int minimumZoomLevel;
	private int maximumZoomLevel;
	private int totalMapZoom;
	private int tileSize;
	private boolean xr2l;
	private boolean yt2b;
	private String baseUrl;
	private String xparam;
	private String yparam;
	private String zparam;

	private CustomTileFactory fact;

	/**
	 * @return the minimumZoomLevel
	 */
	public int getMinimumZoomLevel() {
		return minimumZoomLevel;
	}

	/**
	 * @param minimumZoomLevel the minimumZoomLevel to set
	 */
	public void setMinimumZoomLevel(int minimumZoomLevel) {
		this.minimumZoomLevel = minimumZoomLevel;
	}

	/**
	 * @return the maximumZoomLevel
	 */
	public int getMaximumZoomLevel() {
		return maximumZoomLevel;
	}

	/**
	 * @param maximumZoomLevel the maximumZoomLevel to set
	 */
	public void setMaximumZoomLevel(int maximumZoomLevel) {
		this.maximumZoomLevel = maximumZoomLevel;
	}

	/**
	 * @return the totalMapZoom
	 */
	public int getTotalMapZoom() {
		return totalMapZoom;
	}

	/**
	 * @param totalMapZoom the totalMapZoom to set
	 */
	public void setTotalMapZoom(int totalMapZoom) {
		this.totalMapZoom = totalMapZoom;
	}

	/**
	 * @return the tileSize
	 */
	public int getTileSize() {
		return tileSize;
	}

	/**
	 * @param tileSize the tileSize to set
	 */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}

	/**
	 * @return the xr2l
	 */
	public boolean isXr2l() {
		return xr2l;
	}

	/**
	 * @param xr2l the xr2l to set
	 */
	public void setXr2l(boolean xr2l) {
		this.xr2l = xr2l;
	}

	/**
	 * @return the yt2b
	 */
	public boolean isYt2b() {
		return yt2b;
	}

	/**
	 * @param yt2b the yt2b to set
	 */
	public void setYt2b(boolean yt2b) {
		this.yt2b = yt2b;
	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * @param baseUrl the baseUrl to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * @return the xparam
	 */
	public String getXparam() {
		return xparam;
	}

	/**
	 * @param xparam the xparam to set
	 */
	public void setXparam(String xparam) {
		this.xparam = xparam;
	}

	/**
	 * @return the yparam
	 */
	public String getYparam() {
		return yparam;
	}

	/**
	 * @param yparam the yparam to set
	 */
	public void setYparam(String yparam) {
		this.yparam = yparam;
	}

	/**
	 * @return the zparam
	 */
	public String getZparam() {
		return zparam;
	}

	/**
	 * @param zparam the zparam to set
	 */
	public void setZparam(String zparam) {
		this.zparam = zparam;
	}

	/**
	 * @see MapServer#getTileFactory(TileCache)
	 */
	@Override
	public TileFactory getTileFactory(TileCache cache) {
		TileFactoryInfo info = new TileFactoryInfo(minimumZoomLevel, maximumZoomLevel, totalMapZoom,
				tileSize, xr2l, yt2b, xparam, yparam, zparam, baseUrl);

		fact = new CustomTileFactory(
				new TileFactoryInfoTileProvider(info, GeotoolsConverter.getInstance()), cache);
		return fact;
	}

	/**
	 * @see MapServer#cleanup()
	 */
	@Override
	public void cleanup() {
		if (fact != null)
			fact.cleanup();
	}

}
