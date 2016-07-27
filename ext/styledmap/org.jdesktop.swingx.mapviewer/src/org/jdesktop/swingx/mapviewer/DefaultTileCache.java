/*
 * TileCache.java
 *
 * Created on January 2, 2007, 7:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * An implementation only class for now. For internal use only.
 * 
 * @author joshua.marinacci@sun.com
 */
public class DefaultTileCache extends AbstractBufferedImageTileCache {

	private final Map<URI, BufferedImage> imgmap = new HashMap<URI, BufferedImage>();
	private final LinkedList<URI> imgmapAccessQueue = new LinkedList<URI>();
	private int imagesize = 0;

	// private Map<URI, byte[]> bytemap = new HashMap<URI, byte[]>();
	// private LinkedList<URI> bytemapAccessQueue = new LinkedList<URI>();
	// private int bytesize = 0;

	/**
	 * Default constuctor.
	 */
	public DefaultTileCache() {
	}

	/**
	 * @see AbstractBufferedImageTileCache#doPut(TileInfo, BufferedImage)
	 */
	@Override
	protected void doPut(TileInfo tile, BufferedImage image) {
		addToImageCache(tile.getIdentifier(), image);
	}

	/**
	 * @see AbstractBufferedImageTileCache#doGet(TileInfo)
	 */
	@Override
	public BufferedImage doGet(TileInfo tile) {
		URI uri = tile.getIdentifier();
		synchronized (imgmap) {
			if (imgmap.containsKey(uri)) {
				imgmapAccessQueue.remove(uri);
				imgmapAccessQueue.addLast(uri);
				return imgmap.get(uri);
			}
		}
		/*
		 * synchronized (bytemap) { if (bytemap.containsKey(uri)) { p(
		 * "retrieving from bytes"); bytemapAccessQueue.remove(uri);
		 * bytemapAccessQueue.addLast(uri); BufferedImage img = ImageIO.read(new
		 * ByteArrayInputStream(bytemap.get(uri))); addToImageCache(uri, img);
		 * return img; } }
		 */
		return null;
	}

	/**
	 * @see TileCache#clear()
	 */
	@Override
	public void clear() {
		imgmap.clear();
		p("HACK! need more memory: freeing up memory");
	}

	private void addToImageCache(final URI uri, final BufferedImage img) {
		if (img == null)
			return;

		synchronized (imgmap) {
			while (imagesize > 1000 * 1000 * 50 && !imgmapAccessQueue.isEmpty()) {
				URI olduri = imgmapAccessQueue.removeFirst();
				BufferedImage oldimg = imgmap.remove(olduri);
				if (oldimg != null) {
					imagesize -= oldimg.getWidth() * oldimg.getHeight() * 4;
				}
				p("removed 1 img from image cache");
			}

			imgmap.put(uri, img);
			imagesize += img.getWidth() * img.getHeight() * 4;
			imgmapAccessQueue.addLast(uri);
		}
		/*
		 * p("added to cache: " + " uncompressed = " + imgmap.keySet().size() +
		 * " / " + imagesize / 1000 + "k" + " compressed = " +
		 * bytemap.keySet().size() + " / " + bytesize / 1000 + "k");
		 */
	}

	@SuppressWarnings("unused")
	private void p(String string) {
		// System.out.println(string);
	}

}
