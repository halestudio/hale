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
package de.fhg.igd.mapviewer.server.wms.capabilities;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.GeotoolsConverter;

import de.fhg.igd.mapviewer.server.wms.Messages;
import de.fhg.igd.mapviewer.server.wms.WMSConfiguration;

/**
 * WMS utility methods
 * 
 * @author Simon Templer
 */
public abstract class WMSUtil {

	private static final Log log = LogFactory.getLog(WMSUtil.class);

	/**
	 * Get the URI for a GetMap request
	 * 
	 * @param capabilities the WMS capabilities
	 * @param configuration the configuration
	 * @param width the image width
	 * @param height the image height
	 * @param bounds the map bounds
	 * @param styles the styles
	 * @param format the format
	 * @param transparent if a transparent background shall be used
	 * @return the GetMap URI
	 */
	public static URI getMapURI(WMSCapabilities capabilities, WMSConfiguration configuration,
			int width, int height, WMSBounds bounds, String styles, String format,
			boolean transparent) {
		if (styles == null)
			styles = ""; //$NON-NLS-1$
		if (format == null) {
			format = "image/png"; // FIXME //$NON-NLS-1$
		}

		// get server URL
		String serverUrl;
		String mapUrl = capabilities.getMapURL();

		if (mapUrl.endsWith("?") || mapUrl.endsWith("&")) //$NON-NLS-1$ //$NON-NLS-2$
			serverUrl = mapUrl;
		else if (mapUrl.indexOf('?') >= 0)
			serverUrl = mapUrl + '&';
		else
			serverUrl = mapUrl + '?';

		List<Layer> layerList = getLayers(configuration.getLayers(), capabilities);
		String layers = getLayerString(layerList, true);

		// create query string
		String url = serverUrl + "VERSION=" + capabilities.getVersion() + "&REQUEST=" + //$NON-NLS-1$ //$NON-NLS-2$
				"GetMap&SERVICE=WMS&Layers=" + layers + //$NON-NLS-1$
				"&FORMAT=" + format + //$NON-NLS-1$
				"&BBOX=" + bounds.getMinX() + "," + bounds.getMinY() + "," + bounds.getMaxX() + "," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				+ bounds.getMaxY() + "&WIDTH=" + width + "&HEIGHT=" + height + //$NON-NLS-2$
				"&SRS=" + bounds.getSRS() + //$NON-NLS-1$
				"&STYLES=" + styles + //$NON-NLS-1$
				"&TRANSPARENT=" + ((transparent) ? ("TRUE") : ("FALSE")) + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		// "&BGCOLOR=0xffffff" +
		"&EXCEPTIONS=application/vnd.ogc.se_inimage" + //$NON-NLS-1$
				""; //$NON-NLS-1$

		return URI.create(url);
	}

	/**
	 * Get the preferred bounding box. Tries to convert the bounding box if not
	 * found with the preferred SRS
	 * 
	 * @param capabilities the WMS capabilities
	 * @param preferredEpsg the preferred EPSG code
	 * @return the preferred bounding box, an available bounding box or
	 *         <code>null</code> if none is available
	 */
	public static WMSBounds getBoundingBox(WMSCapabilities capabilities, int preferredEpsg) {
		WMSBounds bounds = getPreferredBoundingBox(capabilities, preferredEpsg);
		String srs = "EPSG:" + preferredEpsg;

		if (bounds.getSRS().equals(srs)) {
			// matches preferred SRS
			return bounds;
		}
		else {
			try {
				// try to convert the bounding box to the preferred SRS
				int boxEpsg = Integer.parseInt(bounds.getSRS().substring(5));

				GeoPosition topLeft = new GeoPosition(bounds.getMinX(), bounds.getMinY(), boxEpsg);
				GeoPosition bottomRight = new GeoPosition(bounds.getMaxX(), bounds.getMaxY(),
						boxEpsg);

				topLeft = GeotoolsConverter.getInstance().convert(topLeft, preferredEpsg);
				bottomRight = GeotoolsConverter.getInstance().convert(bottomRight, preferredEpsg);

				return new WMSBounds(srs, Math.min(topLeft.getX(), bottomRight.getX()),
						Math.min(topLeft.getY(), bottomRight.getY()),
						Math.max(topLeft.getX(), bottomRight.getX()),
						Math.max(topLeft.getY(), bottomRight.getY()));
			} catch (Exception e) {
				// fall back to bounds
				return bounds;
			}
		}
	}

	/**
	 * Get the preferred bounding box
	 * 
	 * @param capabilities the WMS capabilities
	 * @param preferredEpsg the preferred EPSG code
	 * @return the preferred bounding box, an available bounding box or
	 *         <code>null</code>
	 */
	private static WMSBounds getPreferredBoundingBox(WMSCapabilities capabilities,
			int preferredEpsg) {
		// get bounding boxes
		Map<String, WMSBounds> bbs = capabilities.getBoundingBoxes();

		WMSBounds bb = null;

		if (!bbs.isEmpty()) {
			// bounding box present
			if (preferredEpsg != 0) {
				bb = bbs.get("EPSG:" + preferredEpsg); //$NON-NLS-1$
			}

			if (bb != null) {
				// log.info("Found bounding box for preferred srs");
				// //$NON-NLS-1$
			}
			else {
				Iterator<WMSBounds> itBB = bbs.values().iterator();

				while (bb == null && itBB.hasNext()) {
					WMSBounds temp = itBB.next();

					if (temp.getSRS().startsWith("EPSG:") //$NON-NLS-1$
					) {// &&
						// capabilities.getSupportedSRS().contains(temp.getSRS()))
						// {
						bb = temp;
						// log.info("Found epsg bounding box"); //$NON-NLS-1$
					}
				}
			}
		}
		return bb;
	}

	/**
	 * Get WMS capabilities
	 * 
	 * @param baseUrl the base URL of the WMS
	 * @return the WMS capabilities
	 * 
	 * @throws WMSCapabilitiesException if reading the capabilities fails
	 */
	public static WMSCapabilities getCapabilities(String baseUrl) throws WMSCapabilitiesException {
		try {
			// try getting capabilities directly from the given URL
			return WMSCapabilities.getCapabilities(URI.create(baseUrl));
		} catch (Exception e) {
			// add parameters to the URL and try again
			String capabilitiesUrl;
			if (baseUrl.endsWith("?") || baseUrl.endsWith("&")) //$NON-NLS-1$ //$NON-NLS-2$
				capabilitiesUrl = baseUrl;
			else if (baseUrl.indexOf('?') >= 0)
				capabilitiesUrl = baseUrl + '&';
			else
				capabilitiesUrl = baseUrl + '?';

			// add default version parameter
			if (!capabilitiesUrl.matches(".*\\?.*[Vv][Ee][Rr][Ss][Ii][Oo][Nn]=.*")) { //$NON-NLS-1$
				capabilitiesUrl += "VERSION=1.1.1&"; //$NON-NLS-1$
			}
			// add request parameter
			if (!capabilitiesUrl.matches(".*\\?.*[Rr][Ee][Qq][Uu][Ee][Ss][Tt]=.*")) { //$NON-NLS-1$
				capabilitiesUrl += "REQUEST=GetCapabilities&"; //$NON-NLS-1$
			}
			// add service parameter
			if (!capabilitiesUrl.matches(".*\\?.*[Ss][Ee][Rr][Vv][Ii][Cc][Ee]=.*")) { //$NON-NLS-1$
				capabilitiesUrl += "SERVICE=WMS&"; //$NON-NLS-1$
			}

			if (capabilitiesUrl.endsWith("&")) { //$NON-NLS-1$
				capabilitiesUrl = capabilitiesUrl.substring(0, capabilitiesUrl.length() - 1);
			}

			log.info("GetCapabilities URL: " + capabilitiesUrl); //$NON-NLS-1$

			try {
				URI uri = URI.create(capabilitiesUrl);
				return WMSCapabilities.getCapabilities(uri);
			} catch (IllegalArgumentException e1) {
				throw new WMSCapabilitiesException(Messages.WMSUtil_8);
			} catch (WMSCapabilitiesException e1) {
				throw e1;
			} catch (Exception e1) {
				throw new WMSCapabilitiesException(Messages.WMSUtil_9);
			}
		}
	}

	/**
	 * Get the WMS layers
	 * 
	 * @param layerString the layer string
	 * @param capabilities the WMS capabilities
	 * @return the list of layers
	 */
	public static List<Layer> getLayers(String layerString, WMSCapabilities capabilities) {
		// determine layers to show
		List<String> showLayers = null;
		if (layerString != null && !layerString.isEmpty()) {
			String[] layers = layerString.split(","); //$NON-NLS-1$
			showLayers = new ArrayList<String>();
			for (String layer : layers) {
				showLayers.add(layer);
			}
		}

		List<Layer> layers = new ArrayList<Layer>();

		for (Layer layer : capabilities.getLayers()) {
			if (showLayers != null) {
				try {
					layer.setSelected(showLayers.contains(layer.getName())
							|| showLayers.contains(URLEncoder.encode(layer.toString(), "UTF-8"))); //$NON-NLS-1$
				} catch (UnsupportedEncodingException e) {
					log.warn("Unsupported encoding", e); //$NON-NLS-1$
				}
			}

			layers.add(layer);
		}

		return layers;
	}

	/**
	 * Get the string representation for the given layers
	 * 
	 * @param layers the list of layers
	 * @param encode if the layer string shall be encoded
	 * @return the layers string
	 */
	public static String getLayerString(List<Layer> layers, boolean encode) {
		StringBuilder layBuf = new StringBuilder();
		boolean init = true;

		for (Layer layer : layers) {
			if (layer.isSelected()) {
				if (!init)
					layBuf.append(',');
				else
					init = false;

				try {
					if (encode) {
						layBuf.append(URLEncoder.encode(layer.getName(), "UTF-8")); //$NON-NLS-1$
					}
					else {
						layBuf.append(layer.getName());
					}
				} catch (UnsupportedEncodingException e) {
					log.error("Could not add layer " + layer.getName(), e); //$NON-NLS-1$
				}
			}
		}

		return layBuf.toString();
	}

}
