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
