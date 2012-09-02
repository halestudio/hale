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
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

import com.google.common.io.Files;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * Update the xml schema to store/load in archives
 * 
 * @author Patrick Lieb
 */
public class XMLSchemaUpdater {

	private static final ALogger log = ALoggerFactory.getLogger(XMLSchemaUpdater.class);

	private final Map<File, File> imports = new HashMap<File, File>();

	private static String IMPORT = "schema/import";
	private static String INCLUDE = "schema/include";

	// every resource should have his own directory
	private static int COUNT = 0;

	/**
	 * @param resource the file of the new resource
	 * @param oldFile the file of the old resource
	 * @throws IOException if file can not be updated
	 */
	public void update(File resource, File oldFile) throws IOException {

		changeNodeAndCopyFile(resource, oldFile, IMPORT);

		changeNodeAndCopyFile(resource, oldFile, INCLUDE);
	}

	private void changeNodeAndCopyFile(File currentSchema, File oldPath, String xPathExpression)
			throws IOException {

		File curSchema = currentSchema;
		File oldSchemaPath = oldPath;

		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.debug("Can not create a DocumentBuilder", e);
			return;
		}
		builder.setEntityResolver(new EntityResolver() {

			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
					IOException {

				return new InputSource(new StringReader(""));
			}
		});

		Document doc = null;
		try {
			doc = builder.parse(curSchema);
		} catch (SAXException e1) {
			throw new IOException("Can not parse resource " + curSchema.toString(), e1);
		}

		// find schemaLocation of imports/includes via XPath
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodelist = null;
		try {
			nodelist = ((NodeList) xpath.evaluate(xPathExpression, doc, XPathConstants.NODESET));
		} catch (XPathExpressionException e) {
			// should not happen because of constant variables IMPORT and
			// INCLUDE
			log.debug("The XPathExpression is wrong", e);
			return;
		}

		// iterate over all imports or includes and get the schemaLocations
		for (int i = 0; i < nodelist.getLength(); i++) {
			Node identifier = nodelist.item(i);
			if (identifier == null)
				return;
			Node locationNode = identifier.getAttributes().getNamedItem("schemaLocation");
			String location = locationNode.getNodeValue();

			URI file = null;
			try {
				file = new URI(location);
			} catch (URISyntaxException e1) {
				log.debug("The schemaLocation is no valid file", e1);
				continue;
			}
			// only local resources have to be updated
			if (file.getScheme() == null || file.getScheme().equals("file")) {

				File includedOldFile = oldSchemaPath;
				String filename = location;
				if (location.contains("/"))
					filename = location.substring(location.lastIndexOf("/"));
				includedOldFile = includedOldFile.getParentFile();

				// every file needs his own directory because if name conflicts
				filename = COUNT + "/" + filename;

				// the absolute location of the included xml schema
				includedOldFile = new File(includedOldFile, location);

				File includednewFile = null;

				if (imports.containsKey(includedOldFile)) {
					// if the current xml schema is already updated we have to
					// find the relative path to this resource
					String relative = getRelativePath(imports.get(includedOldFile).toURI()
							.toString(), currentSchema.toURI().toString(), "/");
					locationNode.setNodeValue(relative);
				}
				else {

					// we need the directory of the file
					curSchema = currentSchema.getParentFile();

					// path where included schema should be copied to
					includednewFile = new File(curSchema, filename);
					try {
						includednewFile.getParentFile().mkdirs();
					} catch (SecurityException e) {
						throw new IOException("Can not create directories "
								+ includednewFile.getParent(), e);
					}

					// copy to new directory
					Files.copy(includedOldFile, includednewFile);

					// set new location in the xml schema
					locationNode.setNodeValue(filename);

					// every xml schema should be updated (and copied) only once
					// so we save the currently adapted resource in a map
					imports.put(includedOldFile, includednewFile);
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
				StreamResult result = null;
				result = new StreamResult(currentSchema);
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					log.debug("Can not create new XMl file", e);
					return;
				}

				COUNT += 1;

				// if the newFile is not null we found a new file which is not
				// read yet so we have to update it
				if (includednewFile != null) {
					update(includednewFile, includedOldFile);

				}
			}
		}
	}

	// this solution is copied from stackoverflow
	// (http://stackoverflow.com/a/1288584)
	private String getRelativePath(String targetPath, String basePath, String pathSeparator) {

		// We need the -1 argument to split to make sure we get a trailing
		// "" token if the base ends in the path separator and is therefore
		// a directory. We require directory paths to end in the path
		// separator -- otherwise they are indistinguishable from files.
		String[] base = basePath.split(Pattern.quote(pathSeparator), -1);
		String[] target = targetPath.split(Pattern.quote(pathSeparator), 0);

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer buf = new StringBuffer();
		int commonIndex = 0;
		for (int i = 0; i < target.length && i < base.length; i++) {

			if (target[i].equals(base[i])) {
				buf.append(target[i]).append(pathSeparator);
				commonIndex++;
			}
			else
				break;
		}

		String common = buf.toString();

		if (commonIndex == 0) {
			// Whoops -- not even a single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized. Return the target path.
			return targetPath;
			// This should never happen when all absolute paths
			// begin with / as in *nix.
		}

		String relative = "";
		if (base.length == commonIndex) {
			// Comment this out if you prefer that a relative path not start
			// with ./
			relative = "." + pathSeparator;
		}
		else {
			int numDirsUp = base.length - commonIndex - 1;
			// The number of directories we have to backtrack is the length of
			// the base path MINUS the number of common path elements, minus
			// one because the last element in the path isn't a directory.
			for (int i = 1; i <= (numDirsUp); i++) {
				relative += ".." + pathSeparator;
			}
		}
		relative += targetPath.substring(common.length());

		return relative;
	}

}
