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

import com.google.common.io.ByteStreams;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.project.impl.ArchiveProjectWriter;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.report.impl.IOMessageImpl;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;

/**
 * Class for updating XML schemas in the {@link ArchiveProjectWriter}.<br>
 * Resolves the imported/included xml schemas, copies them next to the given
 * schema (or a subdirectory) and adapts the import/include location in the
 * schema.
 * 
 * @author Patrick Lieb
 */
public class XMLSchemaUpdater {

	private static final ALogger log = ALoggerFactory.getLogger(XMLSchemaUpdater.class);

	private final static String IMPORT = "schema/import";
	private final static String INCLUDE = "schema/include";

	/**
	 * Reads the given xml schema (resource) and searches for included and
	 * imported schemas in the file. If these files are local, the function
	 * tries to copy the resources into a new directory next to the given schema
	 * (resource) and adapts the dependencies in the resource. The oldFile is
	 * the path of the xml schema before it was copied to his new directory (eg.
	 * temporary directory). The oldFile keeps untouched. Resource has to be a
	 * copy of oldFile. <br>
	 * <br>
	 * Example:<br>
	 * resource file is 'C:/Local/Temp/1348138164029-0/watercourse/wfs_va.xsd' <br>
	 * oldFile is 'C:/igd/hale/watercourse/wfs_va.xsd'.<br>
	 * wfs_va.xsd has one schema import with location
	 * 'C:/igd/hale/watercourse/schemas/hydro.xsd'<br>
	 * So hydro.xsd is copied into 'C:/Local/Temp/1348138164029-0/watercourse/'
	 * (or a subdirectory) and the import location in wfs_va.xsd will be
	 * adapted.<br>
	 * Resources only will be copied once. In this case the schema location is
	 * solved relative to the originally schema.
	 * 
	 * @param resource the file of the new resource (will be adapted)
	 * @param oldFile the file of the old resource (will be untouched)
	 * @param includeWebResources true if web resources should be copied and
	 *            updated too otherwise false
	 * @param reporter the reporter of the current I/O process where errors
	 *            should be reported to
	 * @throws IOException if file can not be updated
	 */
	public static void update(File resource, URI oldFile, boolean includeWebResources,
			IOReporter reporter) throws IOException {
		update(resource, oldFile, includeWebResources, reporter, new HashMap<URI, File>());
	}

	private static void update(File resource, URI oldFile, boolean includeWebResources,
			IOReporter reporter, Map<URI, File> imports) throws IOException {
		changeNodeAndCopyFile(resource, oldFile, IMPORT, includeWebResources, reporter, imports);
		changeNodeAndCopyFile(resource, oldFile, INCLUDE, includeWebResources, reporter, imports);
	}

	private static void changeNodeAndCopyFile(File currentSchema, URI oldPath,
			String xPathExpression, boolean includeWebResources, IOReporter reporter,
			Map<URI, File> imports) throws IOException {
		File curSchema = currentSchema;

		// counter for the directory because every resource should have his own
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
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
					IOException {
				// FIXME some documentation would be nice why this is OK here?!
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
			throw new IOException("The XPathExpression is wrong", e);
		}

		// iterate over all imports or includes and get the schemaLocations
		for (int i = 0; i < nodelist.getLength(); i++) {
			Node identifier = nodelist.item(i);
			if (identifier == null)
				return;
			Node locationNode = identifier.getAttributes().getNamedItem("schemaLocation");
			String location = locationNode.getNodeValue();

			URI locationUri = null;
			try {
				locationUri = new URI(location);
			} catch (URISyntaxException e1) {
				reporter.error(new IOMessageImpl("The schemaLocation is no valid file", e1));
				continue;
			}

			if (!locationUri.isAbsolute()) {
				locationUri = oldPath.resolve(locationUri);
			}

			String scheme = locationUri.getScheme();
			InputStream input = null;
			if (scheme != null) {
				if (includeWebResources || // web resources are OK
						!(scheme.equals("http") || scheme.equals("https"))
				// or not a web resource
				) {
					DefaultInputSupplier supplier = new DefaultInputSupplier(locationUri);
					input = supplier.getInput();
				}
				else {
					// web resource that should not be included this time
					continue;
				}
			}
			else {
				// file is invalid - at least report that
				reporter.error(new IOMessageImpl(
						"Skipped resource because it cannot be loaded from "
								+ locationUri.toString(), null));
				continue;
			}

			// every file needs his own directory because if name conflicts
			String filename = location;
			if (location.contains("/"))
				filename = location.substring(location.lastIndexOf("/") + 1);
			filename = count + "/" + filename;

			File includednewFile = null;

			if (imports.containsKey(locationUri)) {
				// if the current xml schema is already updated we have to
				// find the relative path to this resource
				String relative = getRelativePath(imports.get(locationUri).toURI().toString(),
						currentSchema.toURI().toString(), "/");
				locationNode.setNodeValue(relative);
			}
			else if (input != null) {

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
				OutputStream output = new FileOutputStream(includednewFile);
				ByteStreams.copy(input, output);
				output.close();

				// set new location in the xml schema
				locationNode.setNodeValue(filename);

				// every xml schema should be updated (and copied) only once
				// so we save the currently adapted resource in a map
				imports.put(locationUri, includednewFile);
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

			count += 1;

			// if the newFile is not null we found a new file which is not
			// read yet so we have to update it
			if (includednewFile != null) {
				update(includednewFile, locationUri, includeWebResources, reporter, imports);
			}
		}
	}

	// this solution is copied from stackoverflow
	// (http://stackoverflow.com/a/1288584) to get a relative path based on two
	// paths
	private static String getRelativePath(String targetPath, String basePath, String pathSeparator) {

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
