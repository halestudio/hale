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
package eu.esdihumboldt.hale.io.gml.ui.wfs.wizard.capabilities;

import java.io.IOException;
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.opengis.feature.type.FeatureType;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.io.wfs.ui.internal.Messages;

/**
 * This utility class is used to build and handle WFS GetCapabilities Requests
 * and Responses.
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class GetCapabilititiesRetriever {

	private static ALogger _log = ALoggerFactory.getLogger(GetCapabilititiesRetriever.class);

	/**
	 * Builds the URL to use for Getting Capabilities of a WFS.
	 * 
	 * @param host the hostname of the WFS.
	 * @param selectionIndex 0 for HTTP GET, 1 and 2 for XML POST.
	 * @return a complete URL.
	 * @throws Exception if any parsing of the URL components fails.
	 */
	public static URL buildURL(String host, int selectionIndex) throws Exception {

		StringBuffer complete_url = new StringBuffer(host);
		if (!complete_url.toString().contains("?")) { //$NON-NLS-1$
			complete_url.append("?"); //$NON-NLS-1$
		}
		char last_char = complete_url.toString().charAt(complete_url.length() - 1);
		switch (selectionIndex) {
		case -1:
			throw new Exception("No valid Protocol selection was made."); //$NON-NLS-1$
		case 0: // 1.1.0, HTTP GET
			if (!(last_char == '&') && !(last_char == '?')) {
				complete_url.append("&"); //$NON-NLS-1$
			}
			complete_url.append("request=GetCapabilities&version=1.1.0"); //$NON-NLS-1$
			return new URL(complete_url.toString());
		default: // 1.0.0/1.1.0 XML POST
			if ((last_char == '&') || (last_char == '?')) {
				return new URL((String) complete_url.toString().subSequence(0,
						complete_url.toString().length() - 2));
			}
			else {
				return new URL(complete_url.toString());
			}
		}
	}

	/**
	 * Load and validate the schema provided at the given URI string.
	 * 
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
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Source schemaFile = new StreamSource(uri);
			Schema schema = schemaFactory.newSchema(schemaFile);

			// create a Validator object and validate the XML file
			Validator validator = schema.newValidator();
			validator.setErrorHandler(new ErrorHandler() {

				@Override
				public void error(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub
					_log.debug("error"); //$NON-NLS-1$
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub
					_log.debug("fatalError"); //$NON-NLS-1$
				}

				@Override
				public void warning(SAXParseException exception) throws SAXException {
					// TODO Auto-generated method stub

				}

			});
			validator.validate(new DOMSource(doc));

		} catch (SAXException e) {
			if (e.getMessage().startsWith("s4s-elt-character")) { //$NON-NLS-1$
				_log.info("Ignoring non-whitespace warning."); // FIXME: This is a hack! //$NON-NLS-1$
				return true;
			}
			else {
				_log.warn("Validation failed: " + e.getMessage()); //$NON-NLS-1$
				return false;
			}
		} catch (IOException e) {
			_log.warn("Reading failed: " + e.getMessage()); //$NON-NLS-1$
			return false;
		} catch (ParserConfigurationException e) {
			_log.warn("Parsing failed: " + e.getMessage()); //$NON-NLS-1$
			return false;
		}
		return true;
	}

	/**
	 * counts the number of occurences of a string declared in another string.
	 * 
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
	 * @param getCapabilitiesUrl the GetCapabilities URL
	 * @return the data store
	 * @throws IOException if reading the capabilities failed
	 */
	public static DataStore getDataStore(String getCapabilitiesUrl) throws IOException {
		_log.info("Getting Capabilities from " + getCapabilitiesUrl); //$NON-NLS-1$

		// Connection Definition
		Map<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", //$NON-NLS-1$
				getCapabilitiesUrl);
		connectionParameters.put("WFSDataStoreFactory:TIMEOUT", new Integer(5000)); //$NON-NLS-1$

		// Step 2 - connection
		return DataStoreFinder.getDataStore(connectionParameters);
	}

	/**
	 * Get the feature type from a capabilities document
	 * 
	 * @param getCapabilitiesUrl the GetCapabilities URL
	 * @param monitor the progress monitor
	 * @return the list of feature types
	 * @throws IOException if reading the capabilities or features failed
	 */
	public static List<FeatureType> readFeatureTypes(String getCapabilitiesUrl,
			IProgressMonitor monitor) throws IOException {
		DataStore data = getDataStore(getCapabilitiesUrl);

		// WFSDataStore wfs = (WFSDataStore) data;

		// Step 3 - discovery and result assembly
		List<FeatureType> result = new ArrayList<FeatureType>();
		if (data != null) {
			String typeNames[] = data.getTypeNames();
			monitor.beginTask(Messages.GetCapabilititiesRetriever_Retriever, typeNames.length);
			int worked = 0;
			for (String typename : typeNames) {
				if (monitor.isCanceled()) {
					break;
				}
				monitor.subTask(typename + " (" + (++worked) + "/" + typeNames.length + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				try {
					// FIXME takes to long for many feature types, there should
					// be another way to get the namespaces
					result.add(data.getSchema(typename));
				} catch (Exception ex) {
					_log.warn("A FeatureType could not be added", ex); //$NON-NLS-1$
				}
				monitor.worked(1);
			}
			monitor.done();
		}
		return result;
	}

}
