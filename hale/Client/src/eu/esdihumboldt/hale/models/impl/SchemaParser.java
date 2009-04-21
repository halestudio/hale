package eu.esdihumboldt.hale.models.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SchemaParser extends DefaultHandler {

	private String file;
	
	private Map<String, String> schemas = new HashMap<String, String>();
	
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
	}

	@Override
	public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {
//		System.out.println(namespaceURI + ", " + localName + ", " + qName);
//		for ( int i = 0; i < atts.getLength(); i++ ) System.out.println(atts.getQName(i));
		
		
		if ((qName.indexOf("import") > -1 || qName.indexOf("include") > -1) && atts.getIndex("schemaLocation") > -1) {
//			System.out.println("schemaLocation: " + atts.getValue("schemaLocation"));
			SchemaParser parser = new SchemaParser();
			String path = file.substring(0, file.lastIndexOf("/") );
			
			schemas.put(atts.getValue("schemaLocation"), path + "/" + atts.getValue("schemaLocation"));

			Map<String, String> map = parser.parse( path + "/" + atts.getValue("schemaLocation") );
			System.out.println("***************************" + path + "/"  + atts.getValue("schemaLocation"));
			schemas.putAll(map);
		}

	}

	/**
	 * Returns a list of schemas which are in the import/include tag.
	 * @param file
	 * @return
	 */
	public Map<String, String> parse(String file) {
		SAXParserFactory factory = SAXParserFactoryImpl.newInstance();
		
		// Extracts the path without the filename
		this.file = file;
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(file, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File " + file + " does not exist!");
		}
		
		return schemas;
	}
}
