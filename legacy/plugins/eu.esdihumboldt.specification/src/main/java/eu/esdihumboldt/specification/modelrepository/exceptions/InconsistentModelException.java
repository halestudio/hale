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
package eu.esdihumboldt.specification.modelrepository.exceptions;

/**
 * Name: eu.esdihumboldt.modelrepository.ims.exceptions /
 * InconsistentModelException<br/>
 * Purpose: This exception is thrown when a model to be saved is not valid.
 * Implemented as a checked Exception since this depends on user input and
 * should be catchable.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class InconsistentModelException extends Exception {

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 */
	public InconsistentModelException(String _description) {
		super(_description);
	}

	/**
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public InconsistentModelException(Throwable _root) {
		super(_root);
	}

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public InconsistentModelException(String _description, Throwable _root) {
		super(_description, _root);
	}

}
