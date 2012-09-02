/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.core.io.project.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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

/**
 * Update the xml schema to store/load in archives
 * 
 * @author Patrick Lieb
 */
// implements ResourceHandler
public class XMLSchemaUpdater {
	
	private List<File> imports = new ArrayList<File>();

	/**
	 * @param resource
	 *            the file of the new resource
	 * @param oldFile
	 *            the file of the old resource
	 * @see eu.esdihumboldt.hale.common.core.io.ResourceHandler#execute()
	 */
	// @Override
	public void execute(File resource, File oldFile) {

		File res = resource;
		File old = oldFile;
		
		changeNodeAndCopyFile(res, old, "schema/import");

		changeNodeAndCopyFile(resource, oldFile, "schema/include");
	}

	private void changeNodeAndCopyFile(File res, File old, String path){
		
		
		File resource = res;
		File oldRes = old;

		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {

				return new InputSource(new StringReader(""));
			}
		});
		
		Document doc = null;
		try {
			doc = builder.parse(resource);
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// find schemaLocation of import and resolve address
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node identifier = null;
		try {
			identifier = ((NodeList) xpath.evaluate(path, doc,
					XPathConstants.NODESET)).item(0);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		if (identifier == null)
			return;
		Node locationNode = identifier.getAttributes().getNamedItem(
				"schemaLocation");
		
		String location = locationNode.getNodeValue();

		// XXX better check for local file?
		if (!location.startsWith("http")) {

			File oldFile = oldRes;
			// get directory of the file
			String locationCheck = location;
			oldFile = oldFile.getParentFile();
			while (locationCheck.startsWith("../")) {
				locationCheck = locationCheck.substring(
						locationCheck.indexOf("../") + 3,
						locationCheck.length());
			}
			// the absolute location of the old resource
			oldFile = new File(oldFile, location);
			try {
				oldFile = oldFile.getCanonicalFile();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			if(imports.contains(oldFile))
				return;

//			if(!resource.isDirectory())
//				resource = resource.getParentFile();
			// the new absolute location where resource should be copied to
			resource = resource.getParentFile();
			// if(!location.contains("/")){
			//
			// location = "/" + location;
			// }
				
				
			File newFile = new File(resource, locationCheck);
			try {
				newFile = newFile.getCanonicalFile();
				newFile.getParentFile().mkdirs();
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

			// copy to correct directory
			try {
				Files.copy(oldFile.getCanonicalFile(),
						newFile.getCanonicalFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			locationNode.setNodeValue("./" + locationCheck);

			// write new XML-File
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = null;
			try {
				transformer = transformerFactory.newTransformer();
			} catch (TransformerConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			DOMSource source = new DOMSource(doc);
			StreamResult result = null;
			result = new StreamResult(res);
			try {
				transformer.transform(source, result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			imports.add(oldFile);
			execute(newFile, oldFile);

		}
	}

	// /**
	// * @see
	// eu.esdihumboldt.hale.common.core.io.ResourceHandler#setDirectory(java.io.File)
	// */
	// @Override
	// public void setDirectory(File directory) {
	// this.directory = directory;
	//
	// }
	//
	// /**
	// * @see
	// eu.esdihumboldt.hale.common.core.io.ResourceHandler#setResource(java.io.File)
	// */
	// @Override
	// public void setResource(File resource) {
	// this.resource = resource;
	//
	// }

	// private static String resolveAddress(String address, String newDirectory)
	// {
	// String absoluteAddress = "";
	// if (!address.contains("../")) {
	// return address;
	// }
	//
	// absoluteAddress = newDirectory.replace("\\", "/").concat(
	// address.substring(address.indexOf("../") + 2));
	// return absoluteAddress;
	// }

}
