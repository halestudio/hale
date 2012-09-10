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

package eu.esdihumboldt.hale.io.xml.validator.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import de.cs3d.util.logging.ATransaction;
import eu.esdihumboldt.hale.io.xml.validator.Report;
import eu.esdihumboldt.hale.io.xml.validator.Validator;

/**
 * Validate using Xerces directly.
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class XercesValidator implements Validator {

	private static final ALogger log = ALoggerFactory.getLogger(XercesValidator.class);

	private static void setFeature(SAXParser parser, String feature, boolean setting) {

		try {
			parser.setFeature(feature, setting);
		} catch (SAXNotRecognizedException e) {
			System.out.print("Unrecognized feature: "); //$NON-NLS-1$
			System.out.println(feature);
		} catch (SAXNotSupportedException e) {
			System.out.print("Unrecognized feature: "); //$NON-NLS-1$
			System.out.println(feature);
		}

	}

	/**
	 * @see Validator#validate(InputStream)
	 */
	@Override
	public Report validate(InputStream xml) {
		final ReportImpl report = new ReportImpl();
		SAXParser parser = new SAXParser();

		setFeature(parser, "http://xml.org/sax/features/validation", true); //$NON-NLS-1$
		setFeature(parser, "http://apache.org/xml/features/validation/schema", true); //$NON-NLS-1$

		parser.setErrorHandler(new ReportErrorHandler(report) {

			@Override
			public void error(SAXParseException e) throws SAXException {
				// XXX this error occurs even if the element is present
				if (e.getMessage()
						.equals("cvc-elt.1: Cannot find the declaration of element 'gml:FeatureCollection'.")) { //$NON-NLS-1$
					return;
				}

				super.error(e);
			}

		});

		ATransaction trans = log.begin("Validating XML file"); //$NON-NLS-1$
		try {
			parser.parse(new InputSource(xml));
			return report;
		} catch (Exception e) {
			throw new IllegalStateException("Error validating XML file", e); //$NON-NLS-1$
		} finally {
			try {
				xml.close();
			} catch (IOException e) {
				// ignore
			}
			trans.end();
		}

	}
}
