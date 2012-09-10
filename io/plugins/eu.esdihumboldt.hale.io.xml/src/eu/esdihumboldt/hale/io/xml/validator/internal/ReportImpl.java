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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xml.sax.SAXParseException;

import eu.esdihumboldt.hale.io.xml.validator.Report;

/**
 * {@link Report} implementation
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ReportImpl implements Report {

	private final List<SAXParseException> warnings = new ArrayList<SAXParseException>();

	private final List<SAXParseException> errors = new ArrayList<SAXParseException>();

	/**
	 * Add a warning
	 * 
	 * @param warning the warning to add
	 */
	public void addWarning(SAXParseException warning) {
		warnings.add(warning);
	}

	/**
	 * Add an error
	 * 
	 * @param error the error to add
	 */
	public void addError(SAXParseException error) {
		errors.add(error);
	}

	/**
	 * @see Report#getWarnings()
	 */
	@Override
	public List<SAXParseException> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/**
	 * @see Report#getErrors()
	 */
	@Override
	public List<SAXParseException> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	/**
	 * @see Report#isValid()
	 */
	@Override
	public boolean isValid() {
		return errors.isEmpty();
	}

}
