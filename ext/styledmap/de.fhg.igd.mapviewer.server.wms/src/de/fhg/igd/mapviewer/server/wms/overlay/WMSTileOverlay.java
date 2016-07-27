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
package de.fhg.igd.mapviewer.server.wms.overlay;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.mapviewer.GeoConverter;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;
import org.jdesktop.swingx.mapviewer.PixelConverter;

import de.fhg.igd.mapviewer.AbstractTileOverlayPainter;
import de.fhg.igd.mapviewer.MapKitTileOverlayPainter;
import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.server.wms.Messages;
import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSBounds;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilities;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSCapabilitiesException;
import de.fhg.igd.mapviewer.server.wms.capabilities.WMSUtil;

import eu.esdihumboldt.util.http.ProxyUtil;

/**
 * Tile overlay displaying WMS data
 * 
 * @author Simon Templer
 */
public class WMSTileOverlay extends MapKitTileOverlayPainter {

	private static final Log log = LogFactory.getLog(WMSTileOverlay.class);

	/**
	 * The preferences
	 */
	private static final Preferences PREF_OVERLAYS = Preferences
			.userNodeForPackage(WMSTileOverlay.class).node("overlays"); //$NON-NLS-1$

	private static final Set<String> supportedFormats = new LinkedHashSet<String>();

	static {
		// order is important, png is preferred
		supportedFormats.add("image/png"); //$NON-NLS-1$
		supportedFormats.add("image/gif"); //$NON-NLS-1$
		supportedFormats.add("image/jpeg"); //$NON-NLS-1$
	}

	private PixelConverter lastConverter = null;

	/**
	 * The WMS client configuration
	 */
	private final WMSConfiguration configuration = new WMSConfiguration() {

		@Override
		protected Preferences getPreferences() {
			return PREF_OVERLAYS;
		}
	};

	private volatile WMSCapabilities capabilities = null;

	/**
	 * Default constructor
	 */
	public WMSTileOverlay() {
		super(4);
	}

	/**
	 * Constructor
	 * 
	 * @param name the configuration name
	 */
	public WMSTileOverlay(String name) {
		this();

		if (!configuration.load(name)) {
			throw new IllegalArgumentException("Error loading WMS configuration"); //$NON-NLS-1$
		}
	}

	/**
	 * @return the configuration
	 */
	public WMSConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @see AbstractTileOverlayPainter#getMaxOverlap()
	 */
	@Override
	protected int getMaxOverlap() {
		// no overlapping
		return 0;
	}

	/**
	 * @see AbstractTileOverlayPainter#repaintTile(int, int, int, int,
	 *      PixelConverter, int)
	 */
	@Override
	public BufferedImage repaintTile(int posX, int posY, int width, int height,
			PixelConverter converter, int zoom) {
		// the first converter isn't regarded as a new converter because it's
		// always the empty map
		boolean isNewConverter = lastConverter != null && !converter.equals(lastConverter);
		lastConverter = converter;

		if (!converter.supportsBoundingBoxes()) {
			if (isNewConverter) {
				handleError(Messages.WMSTileOverlay_0 + configuration.getName()
						+ Messages.WMSTileOverlay_1);
			}
			return null;
		}

		synchronized (this) {
			if (capabilities == null) {
				try {
					capabilities = WMSUtil.getCapabilities(configuration.getBaseUrl());
				} catch (WMSCapabilitiesException e) {
					log.error("Error getting WMS capabilities"); //$NON-NLS-1$
				}
			}
		}

		if (capabilities != null) {
			int mapEpsg = converter.getMapEpsg();

			WMSBounds box;
			synchronized (this) {
				if (capabilities.getSupportedSRS().contains("EPSG:" + mapEpsg)) { //$NON-NLS-1$
					// same SRS supported
				}
				else {
					// SRS not supported
					if (isNewConverter) {
						StringBuilder message = new StringBuilder();
						message.append(Messages.WMSTileOverlay_2);
						message.append(configuration.getName());
						message.append(Messages.WMSTileOverlay_3);
						boolean init = true;
						for (String srs : capabilities.getSupportedSRS()) {
							if (init) {
								init = false;
							}
							else {
								message.append(", "); //$NON-NLS-1$
							}
							message.append(srs);
						}
						handleError(message.toString());
					}
					return null;
				}

				box = WMSUtil.getBoundingBox(capabilities, mapEpsg);
			}

			String srs = box.getSRS();

			if (srs.startsWith("EPSG:")) { //$NON-NLS-1$
				// determine format
				String format = null;
				Iterator<String> itFormat = supportedFormats.iterator();
				synchronized (this) {
					while (format == null && itFormat.hasNext()) {
						String supp = itFormat.next();
						if (capabilities.getFormats().contains(supp)) {
							format = supp;
						}
					}
				}
				if (format == null) {
					// no compatible format
					return null;
				}

				try {
					// check if tile lies within the bounding box
					int epsg = Integer.parseInt(srs.substring(5));

					GeoPosition topLeft = converter.pixelToGeo(new Point(posX, posY), zoom);
					GeoPosition bottomRight = converter
							.pixelToGeo(new Point(posX + width, posY + height), zoom);

					// WMS bounding box
					BoundingBox wms = new BoundingBox(box.getMinX(), box.getMinY(), -1,
							box.getMaxX(), box.getMaxY(), 1);

					GeoConverter geotools = GeotoolsConverter.getInstance();
					GeoPosition bbTopLeft = geotools.convert(topLeft, epsg);
					GeoPosition bbBottomRight = geotools.convert(bottomRight, epsg);

					double minX = Math.min(bbTopLeft.getX(), bbBottomRight.getX());
					double minY = Math.min(bbTopLeft.getY(), bbBottomRight.getY());
					double maxX = Math.max(bbTopLeft.getX(), bbBottomRight.getX());
					double maxY = Math.max(bbTopLeft.getY(), bbBottomRight.getY());

					BoundingBox tile = new BoundingBox(minX, minY, -1, maxX, maxY, 1);

					// check if bounding box and tile overlap
					if (wms.intersectsOrCovers(tile) || tile.covers(wms)) {
						WMSBounds bounds;
						if (epsg == mapEpsg) {
							bounds = new WMSBounds(srs, minX, minY, maxX, maxY);
						}
						else {
							// determine bounds for request
							minX = Math.min(topLeft.getX(), bottomRight.getX());
							minY = Math.min(topLeft.getY(), bottomRight.getY());
							maxX = Math.max(topLeft.getX(), bottomRight.getX());
							maxY = Math.max(topLeft.getY(), bottomRight.getY());
							bounds = new WMSBounds("EPSG:" + mapEpsg, minX, minY, maxX, maxY); //$NON-NLS-1$
						}

						URI uri;
						synchronized (this) {
							uri = WMSUtil.getMapURI(capabilities, configuration, width, height,
									bounds, null, format, true);
						}

						Proxy proxy = ProxyUtil.findProxy(uri);

						InputStream in = uri.toURL().openConnection(proxy).getInputStream();

						BufferedImage image = GraphicsUtilities.loadCompatibleImage(in);

						// apply transparency to the image
						BufferedImage result = GraphicsUtilities.createCompatibleTranslucentImage(
								image.getWidth(), image.getHeight());
						Graphics2D g = result.createGraphics();
						try {
							AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC,
									0.5f);
							g.setComposite(ac);
							g.drawImage(image, 0, 0, null);
						} finally {
							g.dispose();
						}

						return result;
					}
				} catch (Throwable e) {
					log.warn("Error painting WMS overlay", e); //$NON-NLS-1$
				}
			}
		}

		return null;
	}

	/**
	 * Handle errors messages
	 * 
	 * @param message the error message
	 */
	private void handleError(final String message) {
		final Display display = PlatformUI.getWorkbench().getDisplay();

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				MessageDialog.openWarning(display.getActiveShell(), Messages.WMSTileOverlay_5,
						message);
			}
		});
	}

	/**
	 * Remove the configuration with the given name
	 * 
	 * @param name the name
	 * 
	 * @return if removing the configuration succeeded
	 */
	public static boolean removeConfiguration(String name) {
		try {
			PREF_OVERLAYS.node(name).removeNode();
			return true;
		} catch (BackingStoreException e) {
			log.error("Error removing configuration " + name, e); //$NON-NLS-1$
			return false;
		}
	}

	/**
	 * Get the names of the existing configurations
	 * 
	 * @return the configuration names
	 */
	public static String[] getConfigurationNames() {
		try {
			return PREF_OVERLAYS.childrenNames();
		} catch (BackingStoreException e) {
			return new String[] {};
		}
	}

}
