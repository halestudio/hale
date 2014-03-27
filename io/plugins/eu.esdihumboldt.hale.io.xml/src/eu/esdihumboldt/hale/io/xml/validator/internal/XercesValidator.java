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

package eu.esdihumboldt.hale.io.xml.validator.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.fhg.igd.slf4jplus.ATransaction;
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
