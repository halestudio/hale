/*
 * Tile.java
 *
 * Created on March 14, 2006, 4:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.mapviewer;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.Random;

import javax.swing.SwingUtilities;

import org.jdesktop.beans.AbstractBean;

/**
 * The Tile class represents a particular square image piece of the world bitmap
 * at a particular zoom level.
 * 
 * @author joshy
 * @author Simon Templer
 * 
 * @version $Id$
 */

public class Tile extends AbstractBean implements TileInfo {

	/**
	 * Priority enumeration
	 */
	public enum Priority {
		/** High priority */
		High, /** Low priority */
		Low
	}

	private Priority priority = Priority.High;

	// private static final Log log = LogFactory.getLog(Tile.class);

	private boolean isLoading = false;

	private TileFactory tileFactory;

	/**
	 * If an error occurs while loading a tile, store the exception here.
	 */
	private Throwable error;

	/**
	 * The url of the image to load for this tile and its alternatives
	 */
	private URI[] uris;

	/**
	 * The index of the URI in {@link #uris} that should be returned when
	 * {@link #getURI()} is called. Will be increased every time,
	 * {@link #notifyBeforeRetry()} is called.
	 */
	private int currentURI = 0;

	/**
	 * Indicates that loading has succeeded. A PropertyChangeEvent will be fired
	 * when the loading is completed
	 */
	private boolean loaded = false;

	/**
	 * The tile coordinates and zoom level
	 */
	private final int zoom, x, y;

	/**
	 * The image loaded for this Tile
	 */
	SoftReference<BufferedImage> image = new SoftReference<BufferedImage>(null);

	/**
	 * Create a new Tile at the specified tile point and zoom level
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param zoom the zoom level
	 */
	public Tile(int x, int y, int zoom) {
		loaded = false;
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new Tile that loads its data from the given URI. The URI must
	 * resolve to an image
	 * 
	 * @param x the tile x ordinate
	 * @param y the tile y ordinate
	 * @param zoom the tile's zoom level
	 * @param uris the tile image URI (and its alternatives)
	 * @param priority the priority
	 * @param tileFactory the tile factory
	 */
	Tile(int x, int y, int zoom, Priority priority, TileFactory tileFactory, URI... uris) {
		this.uris = uris;
		if (uris != null) {
			currentURI = new Random().nextInt(uris.length);
		}
		loaded = false;
		this.zoom = zoom;
		this.x = x;
		this.y = y;
		this.priority = priority;
		this.tileFactory = tileFactory;
		// startLoading();
	}

	/**
	 *
	 * Indicates if this tile's underlying image has been successfully loaded
	 * yet.
	 * 
	 * @return true if the Tile has been loaded
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}

	/**
	 * Toggles the loaded state, and fires the appropriate property change
	 * notification
	 * 
	 * @param loaded the loaded state to set
	 */
	synchronized void setLoaded(boolean loaded) {
		boolean old = isLoaded();
		this.loaded = loaded;
		firePropertyChange("loaded", old, isLoaded());
	}

	/**
	 * Returns the last error in a possible chain of errors that occurred during
	 * the loading of the tile
	 * 
	 * @return the last error that occurred while loading the tile
	 */
	public Throwable getUnrecoverableError() {
		return error;
	}

	/**
	 * Returns the {@link Throwable} tied to any error that may have ocurred
	 * while loading the tile. This error may change several times if multiple
	 * errors occur
	 * 
	 * @return the error that occurred while loading the tile
	 */
	public Throwable getLoadingError() {
		return error;
	}

	/**
	 * Returns the Image associated with this Tile. This is a read only property
	 * This may return null at any time, however if this returns null, a load
	 * operation will automatically be started for it.
	 * 
	 * @return the tile image
	 */
	public BufferedImage getImage() {
		BufferedImage img = image.get();
		if (img == null) {
			setLoaded(false);
			tileFactory.startLoading(this);
		}

		return img;
	}

	/**
	 * @see org.jdesktop.swingx.mapviewer.TileInfo#getZoom()
	 */
	@Override
	public int getZoom() {
		return zoom;
	}

	////////////////// JavaOne Hack///////////////////
	private PropertyChangeListener uniqueListener = null;

	/**
	 * Adds a single property change listener. If a listener has been previously
	 * added then it will be replaced by the new one.
	 * 
	 * @param propertyName
	 * @param listener
	 */
	@SuppressWarnings("javadoc")
	public void addUniquePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		if (uniqueListener != null && uniqueListener != listener) {
			removePropertyChangeListener(propertyName, uniqueListener);
		}
		if (uniqueListener != listener) {
			uniqueListener = listener;
			addPropertyChangeListener(propertyName, uniqueListener);
		}
	}

	///////////////// End JavaOne Hack/////////////////

	/**
	 * Fire a property change on the event dispatch thread
	 * 
	 * @param propertyName the property name
	 * @param oldValue the old property value
	 * @param newValue the new property value
	 */
	void firePropertyChangeOnEDT(final String propertyName, final Object oldValue,
			final Object newValue) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					firePropertyChange(propertyName, oldValue, newValue);
				}
			});
		}
	}

	/**
	 * @return the error
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(Throwable error) {
		this.error = error;
	}

	/**
	 * @return the isLoading
	 */
	public boolean isLoading() {
		return isLoading;
	}

	/**
	 * @param isLoading the isLoading to set
	 */
	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
	}

	/**
	 * Gets the loading priority of this tile.
	 * 
	 * @return the tile's priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Set the loading priority of this tile.
	 * 
	 * @param priority the priority to set
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * @see org.jdesktop.swingx.mapviewer.TileInfo#getURI()
	 */
	@Override
	public URI getURI() {
		if (uris == null) {
			return null;
		}
		return uris[currentURI];
	}

	@Override
	public URI getIdentifier() {
		if (uris == null) {
			return null;
		}
		return uris[0];
	}

	/**
	 * @see org.jdesktop.swingx.mapviewer.TileInfo#getX()
	 */
	@Override
	public int getX() {
		return x;
	}

	/**
	 * @see org.jdesktop.swingx.mapviewer.TileInfo#getY()
	 */
	@Override
	public int getY() {
		return y;
	}

	/**
	 * Notifies the tile that the tile runner is about to retry loading it.
	 */
	public void notifyBeforeRetry() {
		if (uris != null) {
			currentURI = (currentURI + 1) % uris.length;
		}
	}
}
