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
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.opengis.feature.type.FeatureType;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This utility class is used to build and handle WFS GetCapabilities Requests
 * and Responses.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class GetCapabilititiesRetriever {
	
	private static Logger _log = Logger.getLogger(GetCapabilititiesRetriever.class);

	/**
	 * Builds the URL to use for Getting Capabilities of a WFS.
	 * @param host the hostname of the WFS.
	 * @param selectionIndex 0 for HTTP GET, 1 and 2 for XML POST.
	 * @return a complete URL.
	 * @throws Exception if any parsing of the URL components fails.
	 */
	public static URL buildURL(String host, int selectionIndex) 
			throws Exception {
		
		StringBuffer complete_url = new StringBuffer(host);
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
	
	/**
	 * Helper method for reading a resource identified through an URL to a 
	 * String.
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String readFromUrl(URL url) throws IOException {
		_log.info("Reading from URL " +url.toString());
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
	
	/**
	 * Load and validate the schema provided at the given URI string.
	 * @param uri the URI as a string where the schema can be found.
	 * @return true if all checks are passed.
	 */
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
			validator.setErrorHandler(new ErrorHandler() {


				public void error(SAXParseException exception)
						throws SAXException {
					// TODO Auto-generated method stub
					_log.debug("error");
				}


				public void fatalError(SAXParseException exception)
						throws SAXException {
					// TODO Auto-generated method stub
					_log.debug("fatalError");
				}


				public void warning(SAXParseException exception)
						throws SAXException {
					// TODO Auto-generated method stub
 
				}
				
			});
			validator.validate(new DOMSource(doc));
			
		} catch (SAXException e) {
			if (e.getMessage().startsWith("s4s-elt-character")) {
				_log.info("Ignoring non-whitespace warning."); // FIXME: This is a hack!
				return true;
			} 
			else {
				_log.warn("Validation failed: " + e.getMessage());
				return false;
			}
		} catch (IOException e) {
			_log.warn("Reading failed: " + e.getMessage());
			return false;
		} catch (ParserConfigurationException e) {
			_log.warn("Parsing failed: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * counts the number of occurences of a string declared in another string.
	 * @param original the full string
	 * @param value the search string
	 * @return the count how often value occured in original.
	 */
	public static int countOccurences(String original, String value) {
		int occurences = 0;
		if (original != null) {
			int foundIndex = original.indexOf(value);
			while (foundIndex >= 0) {
				occurences++;
				foundIndex = original.indexOf(value, foundIndex + 1);
			}
		}
		return occurences;
	}
	
	/**
	 * Get the data store for the given capabilities URL
	 * 
	 * @param getCapabilitiesUrl
	 * @return the data store
	 * @throws IOException
	 */
	public static DataStore getDataStore(String getCapabilitiesUrl) throws IOException {
		_log.info("Getting Capabilities from " + getCapabilitiesUrl);
		
		// Connection Definition
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", 
				getCapabilitiesUrl);
		connectionParameters.put("WFSDataStoreFactory:TIMEOUT", new Integer(30000));
				
		// Step 2 - connection
		return DataStoreFinder.getDataStore( connectionParameters );
	}
	
	/**
	 * 
	 * @param getCapabilitiesUrl
	 * @return
	 * @throws IOException
	 */
	public static List<FeatureType> readFeatureTypes(String getCapabilitiesUrl) 
		throws IOException {
		DataStore data = getDataStore(getCapabilitiesUrl);
		
		//WFSDataStore wfs = (WFSDataStore) data;
				
		// Step 3 - discovery and result assembly
		List<FeatureType> result = new ArrayList<FeatureType>();
		if (data != null) {
			String typeNames[] = data.getTypeNames();
			for (String typename : typeNames) {
				try { 
					result.add(data.getSchema( typename ));
				} catch (Exception ex) {
					_log.warn("A FeatureType could not be added: " + ex.getMessage());
				}
			}
		}
		return result;
	}

}
