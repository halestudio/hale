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

package de.fhg.igd.mapviewer.tip;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdesktop.swingx.mapviewer.JXMapViewer;
import org.jdesktop.swingx.painter.Painter;

import de.fhg.igd.mapviewer.BasicMapKit;
import de.fhg.igd.mapviewer.MapPainter;

/**
 * Manages multiple map tips to assure only the one with the highest priority is
 * painted.
 * 
 * @author Simon Templer
 */
public class MapTipManager implements MapPainter, ClipboardOwner {

	/**
	 * Map tip container
	 */
	private static class MapTipContainer implements Comparable<MapTipContainer> {

		private final MapTip mapTip;

		private final int priority;

		/**
		 * Create a map tip container
		 * 
		 * @param mapTip the map tip
		 * @param priority the tip priority
		 */
		public MapTipContainer(MapTip mapTip, int priority) {
			super();
			this.mapTip = mapTip;
			this.priority = priority;
		}

		/**
		 * @return the map tip
		 */
		public MapTip getMapTip() {
			return mapTip;
		}

		/**
		 * @see Comparable#compareTo(Object)
		 */
		@Override
		public int compareTo(MapTipContainer other) {
			if (priority < other.priority) {
				return 1;
			}
			else if (other.priority < priority) {
				return -1;
			}
			else {
				return mapTip.toString().compareTo(other.mapTip.toString());
			}
		}

		/**
		 * @see Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((mapTip == null) ? 0 : mapTip.hashCode());
			return result;
		}

		/**
		 * @see Object#equals(Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MapTipContainer other = (MapTipContainer) obj;
			if (mapTip == null) {
				if (other.mapTip != null)
					return false;
			}
			else if (!mapTip.equals(other.mapTip))
				return false;
			return true;
		}

	}

	private SortedSet<MapTipContainer> mapTips = new TreeSet<MapTipContainer>();

	private MapTip lastTip = null;

	private BasicMapKit mapKit = null;

	/**
	 * Add a map tip to the manager
	 * 
	 * @param tip the map tip
	 * @param priority the map tip's priority (map tips with a higher priority
	 *            will be preferred for painting)
	 */
	public void addMapTip(MapTip tip, int priority) {
		mapTips.add(new MapTipContainer(tip, priority));
		if (mapKit != null) {
			tip.getPainter().setMapKit(mapKit);
		}
	}

	/**
	 * @see Painter#paint(Graphics2D, Object, int, int)
	 */
	@Override
	public void paint(Graphics2D g, JXMapViewer object, int width, int height) {
		for (MapTipContainer c : mapTips) {
			if (c.getMapTip().wantsToPaint()) {
				synchronized (this) {
					c.getMapTip().getPainter().paint(g, object, width, height);
					lastTip = c.getMapTip();
				}
				return;
			}
		}
	}

	/**
	 * @see MapPainter#setMapKit(BasicMapKit)
	 */
	@Override
	public void setMapKit(BasicMapKit mapKit) {
		this.mapKit = mapKit;

		for (MapTipContainer c : mapTips) {
			c.getMapTip().init(mapKit.getMainMap());
			c.getMapTip().getPainter().setMapKit(mapKit);
		}

		mapKit.getMainMap().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				synchronized (this) {
					if (lastTip != null && e.getKeyChar() == 'c') {
						String text = lastTip.getLastText();
						if (text != null) {
							StringSelection stringSelection = new StringSelection(text);
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							clipboard.setContents(stringSelection, MapTipManager.this);
						}
					}
				}
			}
		});
	}

	/**
	 * @see MapPainter#getTipText(Point)
	 */
	@Override
	public String getTipText(Point point) {
		return null;
	}

	/**
	 * @see MapPainter#dispose()
	 */
	@Override
	public void dispose() {
		for (MapTipContainer c : mapTips) {
			c.getMapTip().getPainter().dispose();
		}
	}

	/**
	 * @see ClipboardOwner#lostOwnership(Clipboard, Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// ignore
	}

}
