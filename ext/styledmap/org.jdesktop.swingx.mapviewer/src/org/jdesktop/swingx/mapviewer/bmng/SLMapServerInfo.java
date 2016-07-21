package org.jdesktop.swingx.mapviewer.bmng;

import org.jdesktop.swingx.mapviewer.TileFactoryInfo;

/**
 * A TileFactoryInfo subclass which knows how to connect to the SwingLabs map
 * server. This server contains 2k resolution Blue Marble data from NASA.
 */
@Deprecated
@SuppressWarnings("javadoc")
public class SLMapServerInfo extends TileFactoryInfo {

	private static final int pyramid_top = 8 + 1;
	private static final int midpoint = 5;
	private static final int normal_tile_size = 675;

	public SLMapServerInfo() {
		this("http://maps.joshy.net/tiles/bmng_tiles_3");
	}

	public SLMapServerInfo(String baseURL) {
		// joshy: this was version one of the tiles
		// super(0, 5, 5, 256, true, false,
		// "http://maps.joshy.net/bmng_tiles_1", "", "", "");
		// super(0, pyramid_top-1, pyramid_top,
		// 675,
		// true, false, "http://maps.joshy.net/bmng_tiles_2", "", "", "");
		// super(0, pyramid_top-1, pyramid_top,
		// normal_tile_size,
		// true, false,
		// "file:/Users/joshy/projects/java.net/ImageTileCutter/tiles", "", "",
		// "");
		super(0, pyramid_top - 1, pyramid_top, normal_tile_size, true, false, "", "", "", baseURL);
		setDefaultZoomLevel(0);
	}

	public int getMidpoint() {
		return midpoint;
	}

	@Override
	public int getTileSize(int zoom) {
		int size = super.getTileSize(zoom);
		if (zoom < midpoint) {
			return size;
		}
		else {
			for (int i = 0; i < zoom + 1 - midpoint; i++) {
				size = size / 2;
			}
			return size;
		}
	}

	@Override
	public int getMapWidthInTilesAtZoom(int zoom) {
		if (zoom < midpoint) {
			return (int) Math.pow(2, midpoint - zoom);
		}
		else {
			return 1;
		}
	}

	@Override
	public String[] getTileUrls(int x, int y, int zoom) {
		int ty = y;
		int tx = x;

		// int width_in_tiles = (int)Math.pow(2,pyramid_top-zoom);
		int width_in_tiles = getMapWidthInTilesAtZoom(zoom);
		// System.out.println("width in tiles = " + width_in_tiles + " x = " +
		// tx + " y = " + ty);
		if (ty < 0) {
			return null;
		}
		if (zoom < midpoint) {
			if (ty >= width_in_tiles / 2) {
				return null;
			}
		}
		else {
			if (ty != 0) {
				return null;
			}
		}

		String[] result = new String[baseURLs.length];
		for (int i = 0; i < baseURLs.length; ++i) {
			String url = this.baseURLs[i] + "/" + zoom + "/" + ty + "/" + tx + ".jpg";
			result[i] = url;
		}
		return result;
	}

}