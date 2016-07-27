/*
 * WMSTileFactory.java
 *
 * Created on October 7, 2006, 6:07 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer.wms;

import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.TileCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.TileFactoryInfoTileProvider;

/**
 * A tile factory that uses a WMS service.
 * 
 * Provides wrong coordinates.
 * 
 * @author joshy
 */
@Deprecated
public class WMSTileFactory extends DefaultTileFactory {

	/*
	 * todos: nuke the google url. it's not needed. rework the var names to make
	 * them make sense remove
	 */
	/**
	 * Creates a new instance of WMSTileFactory
	 * 
	 * @param wms
	 * @param cache
	 */
	@SuppressWarnings("javadoc")
	public WMSTileFactory(final WMSService wms, TileCache cache) {
		// tile size and x/y orientation is r2l & t2b
		super(new TileFactoryInfoTileProvider(
				new TileFactoryInfo(0, 15, 17, 500, true, true, "x", "y", "zoom", "") {

					@Override
					public String[] getTileUrls(int x, int y, int zoom) {
						int zz = 17 - zoom;
						int z = 4;
						z = (int) Math.pow(2, (double) zz - 1);
						return new String[] {
								wms.toWMSURL(x - z, z - 1 - y, zz, getTileSize(zoom)) };
					}

				}, GeotoolsConverter.getInstance()), cache);
	}

}
