/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.io.ByteStreams;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;
import eu.esdihumboldt.util.io.IOUtils;

/**
 * Class for updating location values in XML files.<br>
 * Loads a XML file, searches for specified nodes with location values, resolves
 * the referenced resources, copies them next to the given XML file (or a
 * sub-directory) and adapts the location value in the XML file. This process is
 * done recursively.
 * 
 * @author Patrick Lieb
 * @author Kai Schwierczek
 */
public class XMLPathUpdater {

	private static final ALogger log = ALoggerFactory.getLogger(XMLPathUpdater.class);

	/**
	 * Updates the specified resource file.<br>
	 * <br>
	 * The specified file <code>xmlResource</code>, which was originally placed
	 * at <code>oldFile</code> gets updated. All node values found by the XPath
	 * expression <code>locationXPath</code> get copied (and the node values are
	 * updated accordingly) to a new relative path (except for web resources if
	 * <code>includeWebResources</code> is false). The copied files are then
	 * checked the same way.<br>
	 * Resources (identified by their absolute URI) will be copied only once.
	 * Note however, that this is only true for a single call of this method. So
	 * if this method is called multiple times for the same file and some
	 * referenced resources are present in both calls, they are copied multiple
	 * times. You should select your XPath expression accordingly. <br>
	 * Example:<br>
	 * resource file is 'C:/Local/Temp/1348138164029-0/watercourse/wfs_va.xsd'
	 * <br>
	 * oldFile is 'C:/igd/hale/watercourse/wfs_va.xsd'.<br>
	 * wfs_va.xsd has one schema import with location
	 * 'C:/igd/hale/watercourse/schemas/feature.xsd'<br>
	 * So feature.xsd is copied into
	 * 'C:/Local/Temp/1348138164029-0/watercourse/' (or a sub-directory) and the
	 * import location in wfs_va.xsd will be adapted.
	 * 
	 * @param xmlResource the XML resource file that gets updated
	 * @param oldPath its original location, may be <code>null</code> in case it
	 *            didn't exist before
	 * @param locationXPath a XPath expression to find nodes that should be
	 *            processed
	 * @param includeWebResources whether web resources should be copied and
	 *            updates, too
	 * @param reporter the reporter of the current IO process where errors
	 *            should be reported to
	 * @throws IOException if an IO exception occurs
	 */
	public static void update(File xmlResource, URI oldPath, String locationXPath,
			boolean includeWebResources, IOReporter reporter) throws IOException {
		update(xmlResource, oldPath, locationXPath, includeWebResources, reporter,
				new HashMap<URI, File>());
	}

	/**
	 * Actual implementation of the update method.
	 * 
	 * @param xmlResource the XML resource file that gets updated
	 * @param oldPath its original location
	 * @param locationXPath a XPath expression to find nodes that should be
	 *            processed
	 * @param includeWebResources whether web resources should be copied and
	 *            updates, too
	 * @param reporter the reporter of the current IO process where errors
	 *            should be reported to
	 * @param updates a map of already copied files which is used and gets
	 *            filled by this method. Needed for multiple updates on the same
	 *            file.
	 * @throws IOException if an IO exception occurs
	 */
	private static void update(File xmlResource, URI oldPath, String locationXPath,
			boolean includeWebResources, IOReporter reporter, Map<URI, File> updates)
					throws IOException {
		// every XML resource should be updated (and copied) only once
		// so we save the currently adapted resource in a map
		updates.put(oldPath, xmlResource);

		// counter for the directory because every resource should have its own
		// directory
		int count = 0;

		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IOException("Can not create a DocumentBuilder", e);
		}
		builder.setEntityResolver(new EntityResolver() {

			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				// FIXME some documentation would be nice why this is OK here?!
				return new InputSource(new StringReader(""));
			}
		});

		Document doc = null;
		try {
			doc = builder.parse(xmlResource);
		} catch (SAXException e1) {
			// if the file is no XML file simply stop the recursion
			return;
		}

		// find schemaLocation of imports/includes via XPath
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodelist = null;
		try {
			nodelist = ((NodeList) xpath.evaluate(locationXPath, doc, XPathConstants.NODESET));
		} catch (XPathExpressionException e) {
			throw new IOException("The XPath expression is wrong", e);
		}

		// iterate over all imports or includes and get the schemaLocations
		for (int i = 0; i < nodelist.getLength(); i++) {
			Node locationNode = nodelist.item(i);
			String location = locationNode.getNodeValue();

			URI locationUri = null;
			try {
				locationUri = new URI(location);
			} catch (Exception e1) {
				reporter.error(new IOMessageImpl("The location is no valid URI.", e1));
				continue;
			}

			if (!locationUri.isAbsolute()) {
				locationUri = oldPath.resolve(locationUri);
			}

			String scheme = locationUri.getScheme();
			InputStream input = null;
			if (scheme != null) {
				// should the resource be included?
				if (includeWebResources || !(scheme.equals("http") || scheme.equals("https"))) {
					DefaultInputSupplier supplier = new DefaultInputSupplier(locationUri);
					input = supplier.getInput();
				}
				else
					continue;
			}
			else {
				// file is invalid - at least report that
				reporter.error(
						new IOMessageImpl("Skipped resource because it cannot be loaded from "
								+ locationUri.toString(), null));
				continue;
			}

			// every file needs its own directory because of name conflicts
			String filename = location;
			if (location.contains("/"))
				filename = location.substring(location.lastIndexOf("/") + 1);
			filename = count + "/" + filename;

			File includednewFile = null;

			if (updates.containsKey(locationUri)) {
				// if the current XML schema is already updated we have to
				// find the relative path to this resource
				URI relative = IOUtils.getRelativePath(updates.get(locationUri).toURI(),
						xmlResource.toURI());
				locationNode.setNodeValue(relative.toString());
			}
			else if (input != null) {
				// we need the directory of the file
				File xmlResourceDir = xmlResource.getParentFile();

				// path where the file should be copied to
				includednewFile = new File(xmlResourceDir, filename);
				try {
					includednewFile.getParentFile().mkdirs();
				} catch (SecurityException e) {
					throw new IOException(
							"Can not create directories " + includednewFile.getParent(), e);
				}

				// copy to new directory
				OutputStream output = new FileOutputStream(includednewFile);
				ByteStreams.copy(input, output);
				output.close();
				input.close();

				// set new location in the XML resource
				locationNode.setNodeValue(filename);

				update(includednewFile, locationUri, locationXPath, includeWebResources, reporter,
						updates);

				count++;
			}

			// write new XML-File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = null;
			try {
				transformer = transformerFactory.newTransformer();
			} catch (TransformerConfigurationException e) {
				log.debug("Can not create transformer for creating XMl file", e);
				return;
			}
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlResource);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				log.debug("Cannot create new XML file", e);
				return;
			}
		}
	}

}
