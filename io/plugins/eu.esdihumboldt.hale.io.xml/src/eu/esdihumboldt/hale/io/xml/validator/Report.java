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

package eu.esdihumboldt.hale.io.xml.validator;

import java.util.List;

import org.xml.sax.SAXParseException;

/**
 * A validation report
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Report {

	/**
	 * Get if the XML document is valid
	 * 
	 * @return if the XML document is valid
	 */
	public boolean isValid();

	/**
	 * @return the warnings
	 */
	public List<SAXParseException> getWarnings();

	/**
	 * @return the errors
	 */
	public List<SAXParseException> getErrors();

}
