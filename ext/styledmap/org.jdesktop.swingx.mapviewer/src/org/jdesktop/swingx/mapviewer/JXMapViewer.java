/*
 * MapViewer.java
 *
 * Created on March 14, 2006, 2:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.DesignMode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.mapviewer.empty.EmptyTileFactory;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * A tile oriented map component that can easily be used with tile sources on
 * the web like Google and Yahoo maps, satellite data such as NASA imagery, and
 * also with file based sources like pre-processed NASA images.
 * 
 * @author Joshua.Marinacci@sun.com
 */
public class JXMapViewer extends JXPanel implements DesignMode {

	private static final Color UNLOADED_COLOR = Color.LIGHT_GRAY;

	private static final long serialVersionUID = -7093111390636710157L;

	private static final Log log = LogFactory.getLog(JXMapViewer.class);

	/**
	 * The zoom level. Generally a value between 1 and 15 (TODO Is this true for
	 * all the mapping worlds? What does this mean if some mapping system
	 * doesn't support the zoom level?
	 */
	private int zoom = 1;

	/**
	 * The position, in <I>map coordinates</I> of the center point. This is
	 * defined as the distance from the top and left edges of the map in pixels.
	 * Dragging the map component will change the center position. Zooming
	 * in/out will cause the center to be recalculated so as to remain in the
	 * center of the new "map".
	 */
	private Point2D center = new Point2D.Double(0, 0);

	/**
	 * Center position
	 */
	private GeoPosition centerPos = new GeoPosition(0, 0, GeoPosition.WGS_84_EPSG);

	/**
	 * Indicates whether or not to draw the borders between tiles. Defaults to
	 * false.
	 *
	 * TODO Generally not very nice looking, very much a product of testing
	 * Consider whether this should really be a property or not.
	 */
	private boolean drawTileBorders = false;

	/**
	 * Factory used by this component to grab the tiles necessary for painting
	 * the map.
	 */
	private transient TileFactory factory;

	/**
	 * The position in latitude/longitude of the "address" being mapped. This is
	 * a special coordinate that, when moved, will cause the map to be moved as
	 * well. It is separate from "center" in that "center" tracks the current
	 * center (in pixels) of the viewport whereas this will not change when
	 * panning or zooming. Whenever the addressLocation is changed, however, the
	 * map will be repositioned.
	 */
	// private GeoPosition addressLocation;

	/**
	 * Specifies whether panning is enabled. Panning is being able to click and
	 * drag the map around to cause it to move
	 */
	private boolean panEnabled = true;

	/**
	 * Specifies whether zooming is enabled (the mouse wheel, for example,
	 * zooms)
	 */
	private boolean zoomEnabled = true;

	/**
	 * Indicates whether the component should recenter the map when the "middle"
	 * mouse button is pressed
	 */
	private boolean recenterOnClickEnabled = true;

	/**
	 * The overlay to delegate to for painting the "foreground" of the map
	 * component. This would include painting waypoints, day/night, etc. Also
	 * receives mouse events.
	 */
	private Painter<JXMapViewer> overlay;

	private boolean designTime;

	// private float zoomScale = 1;

	private Image loadingImage;

	private boolean restrictOutsidePanning = false;
	private boolean horizontalWrapped = true;

	private final SortedSet<TileOverlayPainter> tileOverlays = new TreeSet<TileOverlayPainter>();

	/**
	 * Create a new JXMapViewer. By default it will use the EmptyTileFactory
	 */
	public JXMapViewer() {
		factory = new EmptyTileFactory();
		MouseInputListener mia = new PanMouseInputListener();
		setRecenterOnClickEnabled(false);
		this.addMouseListener(mia);
		this.addMouseMotionListener(mia);
		this.addMouseWheelListener(new ZoomMouseWheelListener());
		this.addKeyListener(new PanKeyListener());

		// make a dummy loading image
		try {
			URL url = JXMapViewer.class.getResource("resources/loading.png");
			this.setLoadingImage(ImageIO.read(url));
		} catch (Throwable ex) {
			log.warn("could not load 'loading.png'");
			BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = img.createGraphics();
			g2.setColor(Color.black);
			g2.fillRect(0, 0, 16, 16);
			g2.dispose();
			this.setLoadingImage(img);
		}

		// setAddressLocation(new GeoPosition(37.392137,-121.950431)); // Sun
		// campus

		setBackgroundPainter(new AbstractPainter<JXPanel>() {

			@Override
			protected void doPaint(Graphics2D g, JXPanel component, int width, int height) {
				doPaintComponent(g);
			}
		});
	}

	// the method that does the actual painting
	private void doPaintComponent(
			Graphics g) {/*
							 * if (isOpaque() || isDesignTime()) {
							 * g.setColor(getBackground());
							 * g.fillRect(0,0,getWidth(),getHeight()); }
							 */

		if (isDesignTime()) {
			// ?
		}
		else {
			int zoom = getZoom();
			Rectangle viewportBounds = getViewportBounds();
			drawMapTiles(g, zoom, viewportBounds);
			drawMapOverlay(g);
			drawOverlays(zoom, g, viewportBounds);
		}

		super.paintBorder(g);
	}

	/**
	 * Indicate that the component is being used at design time, such as in a
	 * visual editor like NetBeans' Matisse
	 * 
	 * @param b indicates if the component is being used at design time
	 */
	@Override
	public void setDesignTime(boolean b) {
		this.designTime = b;
	}

	/**
	 * Indicates whether the component is being used at design time, such as in
	 * a visual editor like NetBeans' Matisse
	 * 
	 * @return boolean indicating if the component is being used at design time
	 */
	@Override
	public boolean isDesignTime() {
		return designTime;
	}

	/**
	 * Draw the map tiles. This method is for implementation use only.
	 * 
	 * @param g Graphics
	 * @param zoom zoom level to draw at
	 * @param viewportBounds the bounds to draw within
	 */
	protected void drawMapTiles(final Graphics g, final int zoom, Rectangle viewportBounds) {
		int tileWidth = getTileFactory().getTileProvider().getTileWidth(zoom);
		int tileHeight = getTileFactory().getTileProvider().getTileHeight(zoom);

		// calculate the "visible" viewport area in tiles
		int numWide = viewportBounds.width / tileWidth + 2;
		int numHigh = viewportBounds.height / tileHeight + 2;

		int tpx = (int) Math.floor(viewportBounds.getX() / tileWidth);
		int tpy = (int) Math.floor(viewportBounds.getY() / tileHeight);

		// fetch the tiles from the factory and store them in the tiles cache
		// attach the tileLoadListener
		for (int x = 0; x <= numWide; x++) {
			for (int y = 0; y <= numHigh; y++) {
				int itpx = x + tpx;
				int itpy = y + tpy;

				// only proceed if the specified tile point lies within the area
				// being painted
				if (g.getClipBounds().intersects(new Rectangle(itpx * tileWidth - viewportBounds.x,
						itpy * tileHeight - viewportBounds.y, tileWidth, tileHeight))) {

					int ox = ((itpx * tileWidth) - viewportBounds.x);
					int oy = ((itpy * tileHeight) - viewportBounds.y);

					if (horizontalWrapped) {
						int width = getTileFactory().getTileProvider().getMapWidthInTiles(zoom);

						// convert/wrap tile x coordinates
						if (itpx < 0)
							while (itpx < 0)
								itpx += width;
						else
							itpx = itpx % width;
					}

					// if the tile is off the map then just don't paint anything
					if (!TileProviderUtils.isValidTile(getTileFactory().getTileProvider(), itpx,
							itpy, zoom)) {
						if (isOpaque()) {
							g.setColor(getBackground());
							g.fillRect(ox, oy, tileWidth, tileHeight);
						}
					}
					else {
						Tile tile = getTileFactory().getTile(itpx, itpy, zoom);
						tile.addUniquePropertyChangeListener("loaded", tileLoadListener); // this
																							// is
																							// a
																							// filthy
																							// hack

						if (tile.isLoaded()) {
							g.drawImage(tile.getImage(), ox, oy, null);
						}
						else {
							int imageX = (tileWidth - getLoadingImage().getWidth(null)) / 2;
							int imageY = (tileHeight - getLoadingImage().getHeight(null)) / 2;
							g.setColor(UNLOADED_COLOR);
							g.fillRect(ox, oy, tileWidth, tileHeight);
							g.drawImage(getLoadingImage(), ox + imageX, oy + imageY, null);
						}

						drawTileOverlays(g, itpx, itpy, zoom, viewportBounds.x + ox,
								viewportBounds.y + oy, ox, oy, tileWidth, tileHeight,
								viewportBounds);

						if (isDrawTileBorders()) {
							g.setColor(Color.black);
							g.drawRect(ox, oy, tileWidth, tileHeight);
							g.drawRect(ox + tileWidth / 2 - 5, oy + tileHeight / 2 - 5, 10, 10);
							g.setColor(Color.white);
							g.drawRect(ox + 1, oy + 1, tileWidth, tileHeight);

							String text = itpx + ", " + itpy + ", " + getZoom();
							g.setColor(Color.BLACK);
							g.drawString(text, ox + 10, oy + 30);
							g.drawString(text, ox + 10 + 2, oy + 30 + 2);
							g.setColor(Color.WHITE);
							g.drawString(text, ox + 10 + 1, oy + 30 + 1);
						}
					}
				}
			}
		}
	}

	private void drawTileOverlays(final Graphics g, final int tileX, final int tileY,
			final int zoom, final int worldPixelX, final int worldPixelY, final int viewPixelX,
			final int viewPixelY, final int tileWidth, final int tileHeight,
			Rectangle viewportBounds) {

		g.translate(viewPixelX, viewPixelY);

		final PixelConverter converter = getTileFactory().getTileProvider().getConverter();

		for (TileOverlayPainter overlay : tileOverlays) {
			overlay.paintTile((Graphics2D) g, tileX, tileY, zoom, worldPixelX, worldPixelY,
					tileWidth, tileHeight, converter, viewportBounds);
		}

		g.translate(-viewPixelX, -viewPixelY);
	}

	@SuppressWarnings("unused")
	private void drawOverlays(final int zoom, final Graphics g, final Rectangle viewportBounds) {
		if (overlay != null) {
			overlay.paint((Graphics2D) g, this, getWidth(), getHeight());
		}
	}

	private void drawMapOverlay(final Graphics g) {
		Painter<JXMapViewer> painter = factory.getTileProvider().getMapOverlayPainter();
		if (painter != null) {
			painter.paint((Graphics2D) g, this, getWidth(), getHeight());
		}
	}

	/**
	 * Sets the map overlay. This is a Painter which will paint on top of the
	 * map. It can be used to draw waypoints, lines, or static overlays like
	 * text messages.
	 * 
	 * @param overlay the map overlay to use
	 * @see Painter
	 */
	public void setOverlayPainter(Painter<JXMapViewer> overlay) {
		Painter<?> old = getOverlayPainter();
		this.overlay = overlay;
		firePropertyChange("mapOverlay", old, getOverlayPainter());
		repaint();
	}

	/**
	 * Gets the current map overlay
	 * 
	 * @return the current map overlay
	 */
	public Painter<JXMapViewer> getOverlayPainter() {
		return overlay;
	}

	/**
	 * @return the tileOverlays
	 */
	public SortedSet<TileOverlayPainter> getTileOverlays() {
		return tileOverlays;
	}

	/**
	 * @param tileOverlays the tileOverlays to set
	 */
	public void setTileOverlays(SortedSet<TileOverlayPainter> tileOverlays) {
		synchronized (tileOverlays) {
			this.tileOverlays.clear();
			this.tileOverlays.addAll(tileOverlays);
		}

		for (TileOverlayPainter overlay : tileOverlays) {
			overlay.setTileProvider(factory.getTileProvider());
		}

		repaint();
	}

	/**
	 * Add a tile overlay painter
	 * 
	 * @param overlay the tile overlay painter
	 */
	public void addTileOverlay(TileOverlayPainter overlay) {
		synchronized (tileOverlays) {
			tileOverlays.add(overlay);
		}

		overlay.setTileProvider(factory.getTileProvider());

		repaint();
	}

	/**
	 * Remove a tile overlay painter
	 * 
	 * @param overlay the tile overlay painter
	 */
	public void removeTileOverlay(TileOverlayPainter overlay) {
		synchronized (tileOverlays) {
			tileOverlays.remove(overlay);
		}

		repaint();
	}

	/**
	 * Returns the bounds of the viewport in pixels. This can be used to
	 * transform points into the world bitmap coordinate space.
	 * 
	 * @return the bounds in <em>pixels</em> of the "view" of this map
	 */
	public Rectangle getViewportBounds() {
		return calculateViewportBounds(getCenter());
	}

	private Rectangle calculateViewportBounds(Point2D center) {
		Insets insets = getInsets();
		// calculate the "visible" viewport area in pixels
		int viewportWidth = getWidth() - insets.left - insets.right;
		int viewportHeight = getHeight() - insets.top - insets.bottom;
		double viewportX = (center.getX() - viewportWidth / 2);
		double viewportY = (center.getY() - viewportHeight / 2);
		return new Rectangle((int) viewportX, (int) viewportY, viewportWidth, viewportHeight);
	}

	/**
	 * Sets whether the map should recenter itself on mouse clicks (middle mouse
	 * clicks?)
	 * 
	 * @param b if should recenter
	 */
	public void setRecenterOnClickEnabled(boolean b) {
		boolean old = isRecenterOnClickEnabled();
		recenterOnClickEnabled = b;
		firePropertyChange("recenterOnClickEnabled", old, isRecenterOnClickEnabled());
	}

	/**
	 * Indicates if the map should recenter itself on mouse clicks.
	 * 
	 * @return boolean indicating if the map should recenter itself
	 */
	public boolean isRecenterOnClickEnabled() {
		return recenterOnClickEnabled;
	}

	/**
	 * Set the current zoom level
	 * 
	 * @param zoom the new zoom level
	 */
	public void setZoom(int zoom) {
		if (zoom == this.zoom) {
			return;
		}

		TileProvider info = getTileFactory().getTileProvider();
		// don't repaint if we are out of the valid zoom levels
		if (info != null && (zoom < info.getMinimumZoom() || zoom > info.getMaximumZoom())) {
			return;
		}

		int oldzoom = this.zoom;
		this.zoom = zoom;
		this.firePropertyChange("zoom", oldzoom, zoom);

		recenter();

		repaint();
	}

	/**
	 * Recenter on the {@link GeoPosition} set by
	 * {@link #setCenterPosition(GeoPosition)}
	 */
	private void recenter() {
		try {
			Point2D center = getTileFactory().getTileProvider().getConverter().geoToPixel(centerPos,
					zoom);
			updateCenter(center);
		} catch (IllegalGeoPositionException e) {
			// log.warn("Error recentering map on center position, centering on
			// map center instead", e);
			log.info("Error recentering map on center position, centering on map center instead");
			try {
				centerOnPixel(TileProviderUtils
						.getMapCenterInPixels(getTileFactory().getTileProvider(), zoom));
			} catch (Exception e2) {
				// ignore
			}
		}
	}

	/**
	 * Sets the center position to the given pixel coordinates
	 * 
	 * @param point the pixel coordinates
	 * 
	 * @see #setCenterPosition(GeoPosition)
	 */
	public void centerOnPixel(Point2D point) {
		GeoPosition pos = getTileFactory().getTileProvider().getConverter().pixelToGeo(point,
				getZoom());

		setCenterPosition(pos);
	}

	/**
	 * Gets the current zoom level
	 * 
	 * @return the current zoom level
	 */
	public int getZoom() {
		return this.zoom;
	}

	/**
	 * Indicates if the tile borders should be drawn. Mainly used for debugging.
	 * 
	 * @return the value of this property
	 */
	public boolean isDrawTileBorders() {
		return drawTileBorders;
	}

	/**
	 * Set if the tile borders should be drawn. Mainly used for debugging.
	 * 
	 * @param drawTileBorders new value of this drawTileBorders
	 */
	public void setDrawTileBorders(boolean drawTileBorders) {
		boolean old = isDrawTileBorders();
		this.drawTileBorders = drawTileBorders;
		firePropertyChange("drawTileBorders", old, isDrawTileBorders());
		repaint();
	}

	/**
	 * A property indicating if the map should be pannable by the user using the
	 * mouse.
	 * 
	 * @return property value
	 */
	public boolean isPanEnabled() {
		return panEnabled;
	}

	/**
	 * A property indicating if the map should be pannable by the user using the
	 * mouse.
	 * 
	 * @param panEnabled new property value
	 */
	public void setPanEnabled(boolean panEnabled) {
		boolean old = isPanEnabled();
		this.panEnabled = panEnabled;
		firePropertyChange("panEnabled", old, isPanEnabled());
	}

	/**
	 * A property indicating if the map should be zoomable by the user using the
	 * mouse wheel.
	 * 
	 * @return the current property value
	 */
	public boolean isZoomEnabled() {
		return zoomEnabled;
	}

	/**
	 * A property indicating if the map should be zoomable by the user using the
	 * mouse wheel.
	 * 
	 * @param zoomEnabled the new value of the property
	 */
	public void setZoomEnabled(boolean zoomEnabled) {
		boolean old = isZoomEnabled();
		this.zoomEnabled = zoomEnabled;
		firePropertyChange("zoomEnabled", old, isZoomEnabled());
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @param geoPosition the new property value
	 */
	public void setCenterPosition(GeoPosition geoPosition) {
		GeoPosition oldVal = getCenterPosition();
		this.centerPos = geoPosition;
		recenter();
		repaint();
		GeoPosition newVal = getCenterPosition();
		firePropertyChange("centerPosition", oldVal, newVal);
	}

	/**
	 * A property indicating the center position of the map
	 * 
	 * @return the current center position
	 */
	public GeoPosition getCenterPosition() {
		return centerPos;
	}

	/**
	 * Get the current factory
	 * 
	 * @return the current property value
	 */
	public TileFactory getTileFactory() {
		return factory;
	}

	/**
	 * Set the current tile factory
	 * 
	 * @param factory the new property value
	 */
	public void setTileFactory(TileFactory factory) {
		this.factory = factory;

		setHorizontalWrapped(factory.getTileProvider().getAllowHorizontalWrapping());
		setDrawTileBorders(factory.getTileProvider().getDrawTileBorders());

		// setZoom(factory.getTileProvider().getDefaultZoom());

		for (TileOverlayPainter overlay : tileOverlays) {
			overlay.setTileProvider(factory.getTileProvider());
		}
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @return the current property value
	 */
	public Image getLoadingImage() {
		return loadingImage;
	}

	/**
	 * A property for an image which will be display when an image is still
	 * loading.
	 * 
	 * @param loadingImage the new property value
	 */
	public void setLoadingImage(Image loadingImage) {
		this.loadingImage = loadingImage;
	}

	/**
	 * Gets the current pixel center of the map. This point is in the global
	 * bitmap coordinate system, not as lat/longs.
	 * 
	 * @return the current center of the map as a pixel value
	 */
	public Point2D getCenter() {
		return center;
	}

	/**
	 * Sets the new center of the map in pixel coordinates.
	 * 
	 * Utility function! Should only be called by recenter
	 * 
	 * @param center the new center of the map in pixel coordinates
	 */
	private void updateCenter(Point2D center) {
		Point2D old = this.getCenter();

		if (isRestrictOutsidePanning()) {
			Insets insets = getInsets();
			int viewportHeight = getHeight() - insets.top - insets.bottom;
			int viewportWidth = getWidth() - insets.left - insets.right;

			// don't let the user pan over the top edge
			Rectangle newVP = calculateViewportBounds(center);
			if (newVP.getY() < 0) {
				double centerY = viewportHeight / 2;
				center = new Point2D.Double(center.getX(), centerY);
			}

			// don't let the user pan over the left edge
			if (!isHorizontalWrapped() && newVP.getX() < 0) {
				double centerX = viewportWidth / 2;
				center = new Point2D.Double(centerX, center.getY());
			}

			// don't let the user pan over the bottom edge
			Dimension mapSize = TileProviderUtils.getMapSize(getTileFactory().getTileProvider(),
					getZoom());
			int mapHeight = (int) mapSize.getHeight()
					* getTileFactory().getTileProvider().getTileHeight(getZoom());
			if (newVP.getY() + newVP.getHeight() > mapHeight) {
				double centerY = mapHeight - viewportHeight / 2;
				center = new Point2D.Double(center.getX(), centerY);
			}

			// don't let the user pan over the right edge
			int mapWidth = (int) mapSize.getWidth()
					* getTileFactory().getTileProvider().getTileWidth(getZoom());
			if (!isHorizontalWrapped() && (newVP.getX() + newVP.getWidth() > mapWidth)) {
				double centerX = mapWidth - viewportWidth / 2;
				center = new Point2D.Double(centerX, center.getY());
			}

			// if map is to small then just center it vert
			if (mapHeight < newVP.getHeight()) {
				double centerY = mapHeight / 2;// viewportHeight/2;// -
												// mapHeight/2;
				center = new Point2D.Double(center.getX(), centerY);
			}

			// if map is too small then just center it horiz
			if (!isHorizontalWrapped() && mapWidth < newVP.getWidth()) {
				double centerX = mapWidth / 2;
				center = new Point2D.Double(centerX, center.getY());
			}
		}

		// joshy: this is an evil hack to force a property change event
		// i don't know why it doesn't work normally
		old = new Point(5, 6);

		this.center = center;
		firePropertyChange("center", old, this.center);
		repaint();
	}

	// a property change listener which forces repaints when tiles finish
	// loading
	private transient TileLoadListener tileLoadListener = new TileLoadListener();

	private final class TileLoadListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("loaded".equals(evt.getPropertyName()) && Boolean.TRUE.equals(evt.getNewValue())) {
				TileInfo t = (TileInfo) evt.getSource();
				if (t.getZoom() == getZoom()) {
					repaint();
					/*
					 * this optimization doesn't save much and it doesn't work
					 * if you wrap around the world Rectangle viewportBounds =
					 * getViewportBounds(); TilePoint tilePoint =
					 * t.getLocation(); Point point = new Point(tilePoint.getX()
					 * * getTileFactory().getTileSize(), tilePoint.getY() *
					 * getTileFactory().getTileSize()); Rectangle tileRect = new
					 * Rectangle(point, new
					 * Dimension(getTileFactory().getTileSize(),
					 * getTileFactory().getTileSize())); if
					 * (viewportBounds.intersects(tileRect)) { //convert
					 * tileRect from world space to viewport space repaint(new
					 * Rectangle( tileRect.x - viewportBounds.x, tileRect.y -
					 * viewportBounds.y, tileRect.width, tileRect.height )); }
					 */
				}
			}
		}
	}

	// used to pan using the arrow keys
	private class PanKeyListener extends KeyAdapter {

		private static final int OFFSET = 10;

		@Override
		public void keyPressed(KeyEvent e) {
			int delta_x = 0;
			int delta_y = 0;

			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				delta_x = -OFFSET;
				break;
			case KeyEvent.VK_RIGHT:
				delta_x = OFFSET;
				break;
			case KeyEvent.VK_UP:
				delta_y = -OFFSET;
				break;
			case KeyEvent.VK_DOWN:
				delta_y = OFFSET;
				break;
			}

			if (delta_x != 0 || delta_y != 0) {
				Rectangle bounds = getViewportBounds();
				double x = bounds.getCenterX() + delta_x;
				double y = bounds.getCenterY() + delta_y;
				centerOnPixel(new Point2D.Double(x, y));
				repaint();
			}
		}
	}

	// used to pan using press and drag mouse gestures
	private class PanMouseInputListener implements MouseInputListener {

		Point prev;

		@Override
		public void mousePressed(MouseEvent evt) {
			// if the middle mouse button is clicked, recenter the view
			if (isRecenterOnClickEnabled() && (SwingUtilities.isMiddleMouseButton(evt)
					|| (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2))) {
				recenterMap(evt);
			}
			else {
				// otherwise, just remember this point (for panning)
				prev = evt.getPoint();
			}
		}

		private void recenterMap(MouseEvent evt) {
			Rectangle bounds = getViewportBounds();
			double x = bounds.getX() + evt.getX();
			double y = bounds.getY() + evt.getY();
			centerOnPixel(new Point2D.Double(x, y));
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent evt) {
			if (isPanEnabled()) {
				try {
					Point current = evt.getPoint();
					double x = getCenter().getX() - (current.x - prev.x);
					double y = getCenter().getY() - (current.y - prev.y);

					if (y < 0) {
						y = 0;
					}

					int maxHeight = getTileFactory().getTileProvider()
							.getMapHeightInTiles(getZoom())
							* getTileFactory().getTileProvider().getTileHeight(getZoom());
					if (y > maxHeight) {
						y = maxHeight;
					}

					prev = current;
					centerOnPixel(new Point2D.Double(x, y));
					repaint();
					setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
				} catch (Exception e) {
					// ignore
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent evt) {
			prev = null;
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					requestFocusInWindow();
				}
			});
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// override me
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// override me
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// override me
		}
	}

	// zooms using the mouse wheel
	private class ZoomMouseWheelListener implements MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (isZoomEnabled()) {
				setZoom(getZoom() + e.getWheelRotation());
			}
		}
	}

	/**
	 * Get if outside panning shall be restricted
	 * 
	 * @return if outside panning shall be restricted
	 */
	public boolean isRestrictOutsidePanning() {
		return restrictOutsidePanning;
	}

	/**
	 * Set if outside panning shall be restricted
	 * 
	 * @param restrictOutsidePanning if outside panning shall be restricted
	 */
	public void setRestrictOutsidePanning(boolean restrictOutsidePanning) {
		this.restrictOutsidePanning = restrictOutsidePanning;
	}

	/**
	 * Set if the map shall be horizontally wrapped
	 * 
	 * @return if the map shall be horizontally wrapped
	 */
	public boolean isHorizontalWrapped() {
		return horizontalWrapped;
	}

	/**
	 * Set if the map shall be horizontally wrapped
	 * 
	 * @param horizontalWrapped if the map shall be horizontally wrapped
	 */
	public void setHorizontalWrapped(boolean horizontalWrapped) {
		this.horizontalWrapped = horizontalWrapped;
	}

	/**
	 * Converts the specified GeoPosition to a point in the JXMapViewer's local
	 * coordinate space. This method is especially useful when drawing lat/long
	 * positions on the map.
	 * 
	 * @param pos a GeoPosition on the map
	 * @return the point in the local coordinate space of the map
	 * @throws IllegalGeoPositionException if converting the position fails
	 */
	public Point2D convertGeoPositionToPoint(GeoPosition pos) throws IllegalGeoPositionException {
		// convert from geo to world bitmap
		Point2D pt = getTileFactory().getTileProvider().getConverter().geoToPixel(pos, getZoom());
		// convert from world bitmap to local
		Rectangle bounds = getViewportBounds();
		return new Point2D.Double(pt.getX() - bounds.getX(), pt.getY() - bounds.getY());
	}

	/**
	 * Converts the specified Point2D in the JXMapViewer's local coordinate
	 * space to a GeoPosition on the map. This method is especially useful for
	 * determining the GeoPosition under the mouse cursor.
	 * 
	 * @param pt a point in the local coordinate space of the map
	 * @return the point converted to a GeoPosition
	 */
	public GeoPosition convertPointToGeoPosition(Point2D pt) {
		// convert from local to world bitmap
		Rectangle bounds = getViewportBounds();
		Point2D pt2 = new Point2D.Double(pt.getX() + bounds.getX(), pt.getY() + bounds.getY());

		// convert from world bitmap to geo
		GeoPosition pos = getTileFactory().getTileProvider().getConverter().pixelToGeo(pt2,
				getZoom());
		return pos;
	}

	/**
	 * Converts a {@link List} of {@link Point2D} to a {@link List} of
	 * {@link GeoPosition}.
	 * 
	 * @see JXMapViewer#convertPointToGeoPosition(Point2D)
	 * @param pts points to be converted
	 * 
	 * @return {@link List} of {@link GeoPosition}
	 */
	public List<GeoPosition> convertAllPointsToGeoPositions(List<Point2D> pts) {
		List<GeoPosition> pos = new ArrayList<GeoPosition>(pts.size());

		Rectangle bounds = getViewportBounds();
		int zoom = getZoom();

		for (Point2D pt : pts) {
			// convert from local to world bitmap
			pt.setLocation(pt.getX() + bounds.getX(), pt.getY() + bounds.getY());

			// convert from world bitmap to geo
			pos.add(getTileFactory().getTileProvider().getConverter().pixelToGeo(pt, zoom));
		}

		return pos;
	}
}
