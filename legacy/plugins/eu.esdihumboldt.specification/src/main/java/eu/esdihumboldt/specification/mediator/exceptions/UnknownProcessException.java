/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator.exceptions;

/**
 * This Type of Exception is mainly used by the TQM and AQM to state that a
 * requested process is not known.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class UnknownProcessException extends Exception {

	/**
	 * @param _description
	 *            A meaningful description of the exception.
	 */
	public UnknownProcessException(String _description) {
		super(_description);
	}

	/**
	 * @param _description
	 *            A meaningful description of the exception.
	 * @param _root
	 *            The root cause of this Exception.
	 */
	public UnknownProcessException(String _description, Throwable _root) {
		super(_description, _root);
	}

	/**
	 * @param _root
	 *            The root cause of this Exception.
	 */
	public UnknownProcessException(Throwable _root) {
		super(_root);
	}

}
