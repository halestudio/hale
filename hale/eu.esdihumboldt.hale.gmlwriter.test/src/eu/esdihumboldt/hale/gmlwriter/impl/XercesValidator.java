/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.gmlwriter.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * Methods for validating XML using Xerces
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class XercesValidator {

	private static void setFeature(SAXParser parser, String feature,
			boolean setting) {

		try {
			parser.setFeature(feature, setting);
		} catch (SAXNotRecognizedException e) {
			System.out.print("Unrecognized feature: ");
			System.out.println(feature);
		} catch (SAXNotSupportedException e) {
			System.out.print("Unrecognized feature: ");
			System.out.println(feature);
		}

	}

	/**
	 * Validate an XML document using a Xerces {@link DOMParser}
	 * 
	 * @param xml the XML input stream
	 */
	public static void validate(InputStream xml) {
		SAXParser parser = new SAXParser();

		setFeature(parser, "http://xml.org/sax/features/validation", true);
		setFeature(parser, "http://apache.org/xml/features/validation/schema", true);
		
		try {
			parser.parse(new InputSource(xml));
		} catch (IOException ie) {
			System.out.println("Could not read file.");
		} catch (SAXException e) {
			System.out.print("Could not create Document: ");
			System.out.println(e.getMessage());
		} finally {
			try {
				xml.close();
			} catch (IOException e) {
				// ignore
			}
		}

	}
}
