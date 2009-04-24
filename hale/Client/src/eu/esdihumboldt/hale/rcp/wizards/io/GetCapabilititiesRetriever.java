/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.rcp.wizards.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * FIXME Add Type description.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GetCapabilititiesRetriever {
	
	private static Logger _log = Logger.getLogger(GetCapabilititiesRetriever.class);

	public static URL buildURL(
			String host, 
			String applicationPath, 
			int selectionIndex) throws Exception {
		StringBuffer complete_url = new StringBuffer(host + applicationPath);
		if (!complete_url.toString().contains("?")) {
			complete_url.append("?");
		}
		char last_char = complete_url.toString().charAt(
				complete_url.length() - 1);
		switch (selectionIndex) {
		case -1: 
			throw new Exception("No valid Protocol selection was made.");
		case 0: // 1.1.0, HTTP GET
			if (!(last_char == '&') && !(last_char == '?')) {
				complete_url.append("&");
			}
			complete_url.append("request=GetCapabilities&version=1.1.0");
			return new URL(complete_url.toString());
		default: // 1.0.0/1.1.0 XML POST
			if ((last_char == '&') || (last_char == '?')) {
				return new URL((String) 
						complete_url.toString().subSequence(0, 
								complete_url.toString().length() -2));
			} else {
				return new URL(complete_url.toString());
			}
		}
	}
	
	public static void buildXML() {
		// FIXME
	}
	
	public static String readFromUrl(URL url) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						url.openConnection().getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb.toString();
	}
	
	public static boolean validate(String uri) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
	
			// read the XML file
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(uri);
	
			// create a SchemaFactory and a Schema
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(uri);
			Schema schema = schemaFactory.newSchema(schemaFile);
	
			// create a Validator object and validate the XML file
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(doc));
			
		} catch (SAXException e) {
			_log.warn("Validation failed: " + e.getMessage());
			return false;
		} catch (IOException e) {
			_log.warn("Validation failed: " + e.getMessage());
			return false;
		} catch (ParserConfigurationException e) {
			_log.warn("Validation failed: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static int countOccurences(String original, String value)  
	 {  
	     int occurences = 0;  
	   
	     if (original != null)  
	     {  
	         int foundIndex = original.indexOf(value);  
	         while (foundIndex >= 0)  
	         {  
	             occurences++;  
	             foundIndex = original.indexOf(value, foundIndex);  
	         }  
	     }  
	   
	     return occurences;  
	 }

}
