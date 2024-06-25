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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.esdihumboldt.util.http.ProxyUtil;

/**
 * WMS capabilities
 * 
 * @author Simon Templer
 */
public class WMSCapabilities {

	private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory
			.newInstance();

	private static final XPathFactory xpathFactory = XPathFactory.newInstance();

	private final String version;

	private final String title;

	private final String mapURL;

	private final Set<String> formats = new LinkedHashSet<String>();

	private final Set<String> exceptionFormats = new LinkedHashSet<String>();

	private final Set<String> supportedSRS = new LinkedHashSet<String>();

	private final Map<String, WMSBounds> boundingBoxes = new HashMap<String, WMSBounds>();

	private final List<Layer> layers = new ArrayList<Layer>();

	/**
	 * Constructor
	 * 
	 * @param capabilitiesURI the URI of the capabilities document
	 * 
	 * @throws WMSCapabilitiesException if loading the capabilities failed
	 */
	private WMSCapabilities(URI capabilitiesURI) throws WMSCapabilitiesException {
		Document document;
		try {
			document = getDocument(capabilitiesURI);
		} catch (Exception e) {
			throw new WMSCapabilitiesException(e.getLocalizedMessage(), e);
		}

		XPath xpath = xpathFactory.newXPath();

		try {
			// [version] states the WMS version the corresponding code is
			// compatible to (may be incomplete)

			// only mandatory tags will be evaluated (exceptions: Layer)

			// get main node [1.1.1]
			Node main = ((NodeList) xpath.evaluate("WMT_MS_Capabilities", document, //$NON-NLS-1$
					XPathConstants.NODESET)).item(0);

			// determine version [1.1.1]
			version = main.getAttributes().getNamedItem("version").getTextContent(); //$NON-NLS-1$

			// determine title [1.1.1]
			Node titleNode = ((NodeList) xpath.evaluate("WMT_MS_Capabilities/Service/Title", //$NON-NLS-1$
					document, XPathConstants.NODESET)).item(0);
			title = titleNode.getTextContent();

			// GetMap capability

			// GetMap formats [1.1.1]
			NodeList mapFormats = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Request/GetMap/Format", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			for (int i = 0; i < mapFormats.getLength(); i++) {
				Node formatNode = mapFormats.item(i);
				formats.add(formatNode.getTextContent());
			}

			// GetMap URL [1.1.1], assumes there is a HTTP/Get URL given in the
			// document
			NodeList urlNodes = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Request/GetMap/DCPType/HTTP/Get/OnlineResource", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			if (urlNodes.getLength() > 0) {
				mapURL = urlNodes.item(0).getAttributes().getNamedItem("xlink:href") //$NON-NLS-1$
						.getTextContent();
			}
			else {
				throw new WMSCapabilitiesException(
						"No HTTP Get URL defined for the GetMap request"); //$NON-NLS-1$
			}

			// Exception formats [1.1.1]
			NodeList exceptions = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Exception/Format", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			for (int i = 0; i < exceptions.getLength(); i++) {
				Node formatNode = exceptions.item(i);
				exceptionFormats.add(formatNode.getTextContent());
			}

			// Layer

			// Layer SRS [1.1.1]
			NodeList srsNodes = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Layer/SRS", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			for (int i = 0; i < srsNodes.getLength(); i++) {
				Node srsNode = srsNodes.item(i);
				String srsText = srsNode.getTextContent();
				Pattern pattern = Pattern.compile("[Ee][Pp][Ss][Gg]:\\d*");
				Matcher matcher = pattern.matcher(srsText);
				while (matcher.find()) {
					String srs = matcher.group();
					srs = "EPSG:" + srs.substring(5);
					supportedSRS.add(srs);
				}
				// supportedSRS.add(srsNode.getTextContent());
			}

			// Layer Bounding Boxes [1.1.1]
			NodeList bbNodes = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Layer/BoundingBox", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			for (int i = 0; i < bbNodes.getLength(); i++) {
				Node bbNode = bbNodes.item(i);
				String srs = bbNode.getAttributes().getNamedItem("SRS").getTextContent(); //$NON-NLS-1$
				WMSBounds box = new WMSBounds(srs,
						Double.parseDouble(
								bbNode.getAttributes().getNamedItem("minx").getTextContent()), //$NON-NLS-1$
						Double.parseDouble(
								bbNode.getAttributes().getNamedItem("miny").getTextContent()), //$NON-NLS-1$
						Double.parseDouble(
								bbNode.getAttributes().getNamedItem("maxx").getTextContent()), //$NON-NLS-1$
						Double.parseDouble(
								bbNode.getAttributes().getNamedItem("maxy").getTextContent()) //$NON-NLS-1$
				);
				boundingBoxes.put(srs, box);
			}

			// WGS84 bounding box
			if (!boundingBoxes.containsKey("EPSG:4326")) { //$NON-NLS-1$
				NodeList llNodes = (NodeList) xpath.evaluate(
						"WMT_MS_Capabilities/Capability/Layer/LatLonBoundingBox", //$NON-NLS-1$
						document, XPathConstants.NODESET);

				if (llNodes.getLength() > 0) {
					Node bbNode = llNodes.item(0);
					String srs = "EPSG:4326"; //$NON-NLS-1$
					WMSBounds box = new WMSBounds(srs,
							Double.parseDouble(
									bbNode.getAttributes().getNamedItem("minx").getTextContent()), //$NON-NLS-1$
							Double.parseDouble(
									bbNode.getAttributes().getNamedItem("miny").getTextContent()), //$NON-NLS-1$
							Double.parseDouble(
									bbNode.getAttributes().getNamedItem("maxx").getTextContent()), //$NON-NLS-1$
							Double.parseDouble(
									bbNode.getAttributes().getNamedItem("maxy").getTextContent()) //$NON-NLS-1$
					);
					boundingBoxes.put(srs, box);
				}
			}

			// Layers [1.1.1]
			NodeList layerNodes = (NodeList) xpath.evaluate(
					"WMT_MS_Capabilities/Capability/Layer/Layer", //$NON-NLS-1$
					document, XPathConstants.NODESET);

			for (int i = 0; i < layerNodes.getLength(); i++) {
				Node layerNode = layerNodes.item(i);
				NodeList children = layerNode.getChildNodes();

				String name = null;
				String title = null;
				String description = null;

				for (int j = 0; j < children.getLength(); j++) {
					Node child = children.item(j);

					String nodeName = child.getNodeName();

					if (nodeName.equals("Name")) { //$NON-NLS-1$
						name = child.getTextContent();
					}
					else if (nodeName.equals("Title")) { //$NON-NLS-1$
						title = child.getTextContent();
					}
					else if (nodeName.equals("Abstract")) { //$NON-NLS-1$
						description = child.getTextContent();
					}
				}

				if (name != null) {
					Layer layer = new Layer(name, title, description);
					layers.add(layer);
				}
			}
		} catch (WMSCapabilitiesException e) {
			throw e;
		} catch (Exception e) {
			throw new WMSCapabilitiesException("Document is no valid WMS Capabilities document", e); //$NON-NLS-1$
		}
	}

	/**
	 * Load an XML document form an URI
	 * 
	 * @param uri the URI
	 * @return the XML document if loading was successful
	 * @throws ParserConfigurationException if an error occurred configuring the
	 *             document parser
	 * @throws IOException if an error occurred reading the document
	 * @throws MalformedURLException if creating an URL from the given URI fails
	 */
	private static Document getDocument(URI uri)
			throws ParserConfigurationException, MalformedURLException, IOException {
		builderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		builderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		builderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd",
				false);
		builderFactory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);

		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		Proxy proxy = ProxyUtil.findProxy(uri);
		URLConnection connection = uri.toURL().openConnection(proxy);

		// Ensure the input stream is closed properly
		try (InputStream inputStream = connection.getInputStream()) {
			return builder.parse(inputStream);
		} catch (IOException | SAXException e) {
			// Handle exceptions related to input stream and XML parsing
			throw new IOException("Error parsing the document from the URI: " + uri, e);
		}
	}

	/**
	 * Get the capabilities of a WMS service
	 * 
	 * @param capabilitiesURI the URI of the capabilities document
	 * @return the WMS capabilities
	 * 
	 * @throws WMSCapabilitiesException if reading the capabilities failed
	 */
	public static WMSCapabilities getCapabilities(URI capabilitiesURI)
			throws WMSCapabilitiesException {
		return new WMSCapabilities(capabilitiesURI);
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the mapURL
	 */
	public String getMapURL() {
		return mapURL;
	}

	/**
	 * @return the formats
	 */
	public Set<String> getFormats() {
		return formats;
	}

	/**
	 * @return the exceptionFormats
	 */
	public Set<String> getExceptionFormats() {
		return exceptionFormats;
	}

	/**
	 * @return the supportedSRS
	 */
	public Set<String> getSupportedSRS() {
		return supportedSRS;
	}

	/**
	 * @return the boundingBoxes
	 */
	public Map<String, WMSBounds> getBoundingBoxes() {
		return boundingBoxes;
	}

	/**
	 * @return the layers
	 */
	public List<Layer> getLayers() {
		return layers;
	}

}
