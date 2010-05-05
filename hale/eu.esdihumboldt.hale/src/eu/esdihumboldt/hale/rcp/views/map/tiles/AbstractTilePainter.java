/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.views.map.tiles;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.coverage.grid.GeneralGridRange;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.operation.builder.GridToEnvelopeMapper;
import org.geotools.renderer.lite.RendererUtilities;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;

import eu.esdihumboldt.hale.rcp.HALEActivator;
import eu.esdihumboldt.hale.rcp.views.map.Messages;
import eu.esdihumboldt.hale.rcp.views.map.tiles.TileCache.TileListener;

/**
 * Abstract painter based on tiles
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public abstract class AbstractTilePainter implements PaintListener,
		MouseWheelListener, MouseMoveListener, MouseListener,
		MouseTrackListener, TileConstraints, ControlListener,
		TileListener {
	
	/**
	 * Interface for painter state listeners
	 */
	public interface StateListener {
		
		/**
		 * Notifies the listener that the zoom level has changed
		 * 
		 * @param zoom the current zoom level
		 */
		public void zoomChanged(int zoom);
		
	}
	
	/**
	 * Abstract state listener action
	 */
	public abstract class StateListenerAction extends Action implements StateListener {

		/**
		 * Default constructor
		 */
		public StateListenerAction() {
			super();
			
			AbstractTilePainter.this.addStateListener(this);
		}
		
	}
	
	/**
	 * The state listeners
	 */
	private final Set<StateListener> listeners = new HashSet<StateListener>();
	
	/**
	 * The minimum tile size
	 */
	private static final int MIN_TILE_SIZE = 256;
	
	private static final Log log = LogFactory.getLog(AbstractTilePainter.class);

	/**
	 * If panning is currently enabled
	 */
	private boolean pan = false;
	
	/**
	 * The last x coordinate while panning
	 */
	private int panX;
	
	/**
	 * The last y coordinate while panning
	 */
	private int panY;
	
	/**
	 * The minimum zoom (for display) [0 <= {@link #minZoom} <= {@link #maxZoom}]
	 */
	private int minZoom = 0;
	
	/**
	 * The maximum zoom (for display) [0 <= {@link #minZoom} <= {@link #maxZoom}]
	 */
	private int maxZoom = 4;
	
	/**
	 * The tile width in pixels
	 */
	private int tileWidth;
	
	/**
	 * The tile height in pixels
	 */
	private int tileHeight;
	
	/**
	 * The current map zoom [{@link #minZoom} <= {@link #currentZoom} <= {@link #maxZoom}]
	 */
	private int currentZoom;
	
	/**
	 * The x offset of the map at the current zoom
	 */
	private long xOffset;
	
	/**
	 * The y offset of the map at the current zoom
	 */
	private long yOffset;
	
	/**
	 * The control to paint
	 */
	private Control control;
	
	private Cursor defCursor;
	
	private Cursor panCursor;
	
	/**
	 * The map area
	 */
	private ReferencedEnvelope mapArea = null;
	
	/**
	 * Initialize the tile painter for the given control and map area
	 * 
	 * @param control the control to paint on
	 * @param mapArea the map area
	 */
	public void init(final Control control, final ReferencedEnvelope mapArea) {
		if (this.control != null) {
			this.control.removePaintListener(this);
			this.control.removeMouseListener(this);
			this.control.removeMouseMoveListener(this);
			this.control.removeMouseWheelListener(this);
			this.control.removeMouseTrackListener(this);
			this.control.removeControlListener(this);
		}
		
		pan = false;
		
		updateMap(mapArea);
		
		this.control = control;
		
		defCursor = new Cursor(control.getDisplay(), SWT.CURSOR_HAND);
		panCursor = new Cursor(control.getDisplay(), SWT.CURSOR_SIZEALL);
		
		control.setCursor(defCursor);
		
		if (this.control != null) {
			this.control.addPaintListener(this);
			this.control.addMouseListener(this);
			this.control.addMouseMoveListener(this);
			this.control.addMouseWheelListener(this);
			this.control.addMouseTrackListener(this);
			this.control.addControlListener(this);
		}
	}
	
	/**
	 * Get the control
	 * 
	 * @return the control
	 */
	protected Control getControl() {
		return control;
	}
	
	/**
	 * Update the map
	 * 
	 * @param mapArea the new map area
	 */
	public void updateMap(final ReferencedEnvelope mapArea) {
		synchronized (this) {
			if (mapArea == null || mapArea.getWidth() == 0 || mapArea.getHeight() == 0) {
				// invalid area
				this.mapArea = null;
				refresh();
				return;
			}
			
			this.mapArea = mapArea;
			
			resetTiles();
			
			if (mapArea != null) {
				// determine current zoom
				currentZoom = minZoom;
				
				// determine tile size
				double ratio = mapArea.getWidth() / mapArea.getHeight();
				
				if (ratio >= 1.0) {
					// wider than high (or square)
					tileHeight = MIN_TILE_SIZE;
					tileWidth = (int) Math.round(tileHeight * ratio);
				}
				else {
					// higher than wide
					tileWidth = MIN_TILE_SIZE;
					tileHeight = (int) Math.round(tileWidth / ratio);
				}
				
				// determine x/y offset
				xOffset = 0;
				yOffset = 0;
				
				for (StateListener listener : listeners) {
					listener.zoomChanged(currentZoom);
				}
			}
			
			refresh();
		}
	}
	
	/**
	 * @see TileCache.TileListener#tileLoaded(int, int, int)
	 */
	@Override
	public void tileLoaded(int zoom, int x, int y) {
		synchronized (this) {
			if (currentZoom != zoom)
				return;
			
			if ((x + 1) * tileWidth < xOffset || x * tileWidth > xOffset + control.getBounds().width)
				return;
			
			if ((y + 1) * tileHeight < yOffset || y * tileHeight > yOffset + control.getBounds().height)
				return;
			
			refresh();
		}
	}

	/**
	 * Refresh the map
	 */
	protected void refresh() {
		if (control != null) {
			if (Display.getCurrent() != null) {
				control.redraw();	
			}
			else {
				final Display display = PlatformUI.getWorkbench().getDisplay();
				display.syncExec(new Runnable() {
					
					@Override
					public void run() {
						control.redraw();
					}
				});
			}
		}
	}
	
	/**
	 * Reset all tiles
	 */
	protected abstract void resetTiles();
	
	/**
	 * Get the current zoom level
	 * 
	 * @return the current zoom level
	 */
	public int getZoom() {
		return currentZoom;
	}
	
	/**
	 * Get the tile width
	 * 
	 * @return the tile width in pixels
	 */
	@Override
	public int getTileWidth() {
		return tileWidth;
	}
	
	/**
	 * Get the tile height
	 * 
	 * @return the tile height in pixels
	 */
	@Override
	public int getTileHeight() {
		return tileHeight;
	}
	
	/**
	 * @see TileConstraints#getTileArea(int, int, int)
	 */
	@Override
	public ReferencedEnvelope getTileArea(int zoom, int x, int y) {
		if (mapArea != null) {
			float xMinRatio = (float) x / (float) getTiles(zoom);
			float xMaxRatio = (float) (x + 1) / (float) getTiles(zoom);
			
			float yMinRatio = 1 - (float) y / (float) getTiles(zoom);
			float yMaxRatio = 1 - (float) (y + 1) / (float) getTiles(zoom);
			
			return new ReferencedEnvelope(
					mapArea.getMinX() + mapArea.getWidth() * xMinRatio,
					mapArea.getMinX() + mapArea.getWidth() * xMaxRatio,
					mapArea.getMinY() + mapArea.getHeight() * yMinRatio,
					mapArea.getMinY() + mapArea.getHeight() * yMaxRatio,
					mapArea.getCoordinateReferenceSystem());
		}
		
		return null;
	}

	/**
	 * @see TileConstraints#getCRS()
	 */
	@Override
	public CoordinateReferenceSystem getCRS() {
		if (mapArea != null)
			return mapArea.getCoordinateReferenceSystem();
		
		return null;
	}

	/**
	 * Get the map x offset
	 * 
	 * @return the map x offset in pixels
	 */
	public long getXOffset() {
		return xOffset;
	}
	
	/**
	 * Get the map y offset
	 * 
	 * @return the map y offset in pixels
	 */
	public long getYOffset() {
		return yOffset;
	}
	
	/**
	 * Get the map width/height in tiles for the given zoom level
	 * 
	 * @param zoom the zoom level
	 * @return the map width/height in tiles
	 */
	protected static int getTiles(int zoom) {
		return 1 << zoom;
	}
	
	/**
	 * Set the current zoom level
	 * 
	 * @param zoom the new zoom level
	 */
	public void setZoom(int zoom) {
		synchronized (this) {
			if (mapArea == null || control == null)
				return;
			
			zoom = Math.max(Math.min(zoom, maxZoom), minZoom);
			
			if (zoom != currentZoom) {
				int zoomDiff = zoom - currentZoom;
				
				// update zoom
				currentZoom = zoom;
				
				// update offsets
				double xTileNum = (double) (xOffset + control.getBounds().width / 2) / (double) tileWidth;
				double yTileNum = (double) (yOffset + control.getBounds().height / 2) / (double) tileHeight;
				
				if (zoomDiff > 0) {
					// zoom in
					for (int i = 0; i < zoomDiff; i++) {
						xTileNum *= 2.0;
						yTileNum *= 2.0;
					}
				}
				else {
					// zoom out
					for (int i = 0; i < -zoomDiff; i++) {
						xTileNum /= 2.0;
						yTileNum /= 2.0;
					}
				}
				
				long x = Math.round(xTileNum * tileWidth) - control.getBounds().width / 2;
				long y = Math.round(yTileNum * tileHeight) - control.getBounds().height / 2;
				
				setOffsets(x, y); //refresh();
				
				for (StateListener listener : listeners) {
					listener.zoomChanged(zoom);
				}
			}
		}
	}
	
	/**
	 * Add a state listeners
	 * 
	 * @param listener the state listener
	 */
	public void addStateListener(StateListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove a state listeners
	 * 
	 * @param listener the state listener
	 */
	public void removeStateListener(StateListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Get a zoom in action
	 * 
	 * @return a zoom in action
	 */
	public IAction getZoomInAction() {
		Action action = new StateListenerAction() {
			
			@Override
			public void zoomChanged(int zoom) {
				setEnabled(zoom != maxZoom);
			}
		
			@Override
			public void run() {
				int zoom = getZoom();
				if (zoom < maxZoom) {
					setZoom(zoom + 1);
				}
			}
			
		};
		
		action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/add.gif")); //$NON-NLS-1$
		action.setToolTipText(Messages.AbstractTilePainter_ZoomIn);
		
		return action;
	}
	
	/**
	 * Get a zoom out action
	 * 
	 * @return a zoom out action
	 */
	public IAction getZoomOutAction() {
		Action action = new StateListenerAction() {
			
			@Override
			public void zoomChanged(int zoom) {
				setEnabled(zoom != minZoom);
			}

			@Override
			public void run() {
				int zoom = getZoom();
				if (zoom > minZoom) {
					setZoom(zoom - 1);
				}
			}
			
		};
		
		action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				HALEActivator.PLUGIN_ID, "/icons/minus.gif")); //$NON-NLS-1$
		action.setToolTipText(Messages.AbstractTilePainter_ZoomOut);
		
		return action;
	}
	
	/**
	 * Set the map offsets in pixels
	 * 
	 * @param x the new x offset
	 * @param y the new y offset
	 */
	public void setOffsets(long x, long y) {
		synchronized (this) {
			if (mapArea == null || control == null)
				return;
			
			// x offset
			long minX = 0;
			long maxX = (tileWidth * getTiles(currentZoom)) - control.getBounds().width;
			if (maxX < 0) { // allows moving a map that doesn't fill the whole control
				minX = maxX;
				maxX = 0;
			}
			
			x = Math.max(Math.min(x, maxX), minX);
			
			// y offset
			long minY = 0;
			long maxY = (tileHeight * getTiles(currentZoom)) - control.getBounds().height;
			if (maxY < 0) { // allows moving a map that doesn't fill the whole control
				minY = maxY;
				maxY = 0;
			}
			
			y = Math.max(Math.min(y, maxY), minY);
			
			// update
			if (xOffset != x || yOffset != y) {
				xOffset = x;
				yOffset = y;
				refresh();
			}
		}
	}

	/**
	 * @see PaintListener#paintControl(PaintEvent)
	 */
	@Override
	public void paintControl(PaintEvent e) {
		synchronized (this) {
			final GC gc = e.gc;
			
			if (mapArea == null) {
				gc.fillRectangle(e.x, e.y, e.width, e.height);
			}
			else {
				paintTiles(gc, e.x, e.y, e.width, e.height);
			}
		}
	}

	/**
	 * Paint the tiles for the current view
	 * 
	 * @param gc the graphics object
	 * @param x the 
	 * @param y
	 * @param width
	 * @param height
	 */
	protected void paintTiles(final GC gc, final int x, final int y, final int width,
			final int height) {
		if (tileWidth == 0 || tileHeight == 0) return;
		
		// determine which tiles have to be painted
		int minTileX = (int) ((xOffset + x) / tileWidth);
		int maxTileX = (int) ((xOffset + x + width) / tileWidth);
		
		int minTileY = (int) ((yOffset + y) / tileHeight);
		int maxTileY = (int) ((yOffset + y + height) / tileHeight);
		
		// paint each tile
		for (int tileX = minTileX; tileX <= maxTileX; tileX++) {
			for (int tileY = minTileY; tileY <= maxTileY; tileY++) {
				// is valid tile?
				boolean validTile = tileX >= 0 && tileX < getTiles(currentZoom)
					&& tileY >= 0 && tileY < getTiles(currentZoom);
				
				if (validTile) {
					paintTile(gc, tileX, tileY, currentZoom,
							(int) (tileX * tileWidth - xOffset), (int) (tileY * tileHeight - yOffset),
							tileWidth, tileHeight);
				}
				else {
					gc.fillRectangle((int) (tileX * tileWidth - xOffset), (int) (tileY * tileHeight - yOffset),
							tileWidth, tileHeight);
				}
			}
		}
	}

	/**
	 * Paint a tile
	 * 
	 * @param gc the graphics object to paint on
	 * @param tileX the tile x ordinate
	 * @param tileY the tile y ordinate
	 * @param zoom the zoom level
	 * @param x the x position where to draw the tile
	 * @param y the y position where to draw the tile
	 * @param tileWidth the tile width
	 * @param tileHeight the tile height
	 */
	protected abstract void paintTile(GC gc, int tileX, int tileY, int zoom,
			int x, int y, int tileWidth, int tileHeight);

	/**
	 * @see MouseWheelListener#mouseScrolled(MouseEvent)
	 */
	@Override
	public void mouseScrolled(MouseEvent e) {
		if (e.count == 0) return;
		
		setZoom(getZoom() + ((e.count > 0)?(1):(-1)));
	}
	
	/**
	 * Do the panning
	 * 
	 * @param x the current x coordinate
	 * @param y the current y coordinate
	 */
	private void doPan(int x, int y) {
		setOffsets(getXOffset() - (x - panX), getYOffset() - (y - panY));
		//TODO locking mechanism for panning?
		panX = x;
		panY = y;
	}

	/**
	 * @see MouseMoveListener#mouseMove(MouseEvent)
	 */
	@Override
	public void mouseMove(MouseEvent e) {
		if (pan) {
			doPan(e.x, e.y);
		}
	}

	/**
	 * @see MouseListener#mouseDoubleClick(MouseEvent)
	 */
	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// ignore
	}

	/**
	 * @see MouseListener#mouseDown(MouseEvent)
	 */
	@Override
	public void mouseDown(MouseEvent e) {
		control.setFocus();
		
		panX = e.x;
		panY = e.y;
		pan = true;
		
		control.setCursor(panCursor);
	}

	/**
	 * @see MouseListener#mouseUp(MouseEvent)
	 */
	@Override
	public void mouseUp(MouseEvent e) {
		pan = false;
		
		doPan(e.x, e.y);
		
		control.setCursor(defCursor);
	}

	/**
	 * @see MouseTrackListener#mouseEnter(MouseEvent)
	 */
	@Override
	public void mouseEnter(MouseEvent e) {
		//XXX ignore? or disable pan
	}

	/**
	 * @see MouseTrackListener#mouseExit(MouseEvent)
	 */
	@Override
	public void mouseExit(MouseEvent e) {
		//XXX ignore? or disable pan
	}

	/**
	 * @see MouseTrackListener#mouseHover(MouseEvent)
	 */
	@Override
	public void mouseHover(MouseEvent e) {
		// ignore
	}

	/**
	 * @see ControlListener#controlMoved(ControlEvent)
	 */
	@Override
	public void controlMoved(ControlEvent e) {
		// ignore
	}

	/**
	 * @see ControlListener#controlResized(ControlEvent)
	 */
	@Override
	public void controlResized(ControlEvent e) {
		// update offsets and force refresh
		setOffsets(getXOffset(), getYOffset());
		refresh();
	}
	
	/**
	 * Convert canvas pixel coordinates to map pixel coordinates
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return the map pixel coordinates 
	 */
	public Point toWorld(int x, int y) {
		return new Point((int) getXOffset() + x, (int) getYOffset() + y);
	}
	
	/**
	 * Convert canvas pixel coordinates to geo coordinates
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return the point in geo coordinates
	 */
	public Point2D toGeoCoordinates(int x, int y) {
		return toGeoCoordinates(toWorld(x, y), getZoom());
	}
	
	/**
	 * Convert map pixel coordinates to geo coordinates
	 * 
	 * @param mapPixels point in map pixel
	 * @param zoom the zoom level
	 * 
	 * @return the point in geo coordinates
	 */
	public Point2D toGeoCoordinates(Point mapPixels, final int zoom) {
		int mapX = mapPixels.x;
		int mapY = mapPixels.y;
		
		int tileNumX = mapX / getTileWidth();
		int tileNumY = mapY / getTileHeight();
		
		ReferencedEnvelope tileArea = getTileArea(zoom, tileNumX, tileNumY);
		
		int tileX = mapX % getTileWidth();
		int tileY = mapY % getTileHeight();
		
		return toGeoCoordinates(tileX, tileY, tileArea);
	}
	
	/**
     * Helper class for building affine transforms. We use one instance per thread,
     * in order to avoid the need for {@code synchronized} statements.
     * 
     * @see RendererUtilities
     */
    private static final ThreadLocal<GridToEnvelopeMapper> gridToEnvelopeMappers =
            new ThreadLocal<GridToEnvelopeMapper>() {
                @SuppressWarnings("deprecation")
				@Override
                protected GridToEnvelopeMapper initialValue() {
                    final GridToEnvelopeMapper mapper = new GridToEnvelopeMapper();
                    mapper.setGridType(PixelInCell.CELL_CORNER);
                    return mapper;
                }
    };

	/**
	 * Convert tile pixel coordinates to geo coordinates
	 * 
	 * @see RendererUtilities#worldToScreenTransform(ReferencedEnvelope, Rectangle)
	 * 
	 * @param tileX
	 * @param tileY
	 * @param tileArea the tile area
	 * 
	 * @return the point in geo coordinates, <code>null</code> if conversion failed
	 */
	@SuppressWarnings("deprecation")
	protected Point2D toGeoCoordinates(int tileX, int tileY,
			ReferencedEnvelope tileArea) {
		// creating transformation
		final Envelope2D genvelope = new Envelope2D(tileArea);
		final GridToEnvelopeMapper m = (GridToEnvelopeMapper) gridToEnvelopeMappers.get();
		try {
            m.setGridRange(new GeneralGridRange(new Rectangle(getTileWidth(), getTileHeight())));
            m.setEnvelope(genvelope);
            AffineTransform trans = m.createAffineTransform(); // creating transformation as in RendererUtilities, but without the inversion that is applied there
            
            Point2D.Double tile = new Point2D.Double(tileX, tileY);
            Point2D.Double result = new Point2D.Double(tileX, tileY);
            
            trans.transform(tile, result);
            
            return result;
		} catch (MismatchedDimensionException e) {
			log.error("Error creating transformation", e);
			return null;
		}
	}

}
