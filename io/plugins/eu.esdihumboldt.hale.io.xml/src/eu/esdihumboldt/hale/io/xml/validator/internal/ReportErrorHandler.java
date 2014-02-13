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

import java.text.MessageFormat;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Error handler populating a {@link ReportImpl}
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportErrorHandler implements ErrorHandler {

	private static final ALogger log = ALoggerFactory.getLogger(ReportErrorHandler.class);

	private final ReportImpl report;

	/**
	 * Constructor
	 * 
	 * @param report the report to populate
	 */
	public ReportErrorHandler(ReportImpl report) {
		super();
		this.report = report;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		log.warn(MessageFormat.format("Line {0} - {1}", e.getLineNumber(), e.getLocalizedMessage()));
		report.addWarning(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		error(e);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		log.error(MessageFormat.format("Line {0} - {1}", e.getLineNumber(), e.getLocalizedMessage()));
		report.addError(e);
	}

}
