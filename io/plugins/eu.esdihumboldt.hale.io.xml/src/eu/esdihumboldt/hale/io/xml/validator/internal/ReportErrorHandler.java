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

import java.text.MessageFormat;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

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
