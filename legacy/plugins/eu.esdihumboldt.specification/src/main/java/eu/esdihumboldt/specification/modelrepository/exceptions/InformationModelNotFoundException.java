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
 * Name: eu.esdihumboldt.modelrepository.ims.serviceexceptions /
 * InformationModelNotFoundException<br/>
 * Purpose: This exception is thrown when a model could not be retrieved.
 * Implemented as a checked Exception since this depends on user input and
 * should be catchable.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class InformationModelNotFoundException extends Exception {

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 */
	public InformationModelNotFoundException(String _description) {
		super(_description);
	}

	/**
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public InformationModelNotFoundException(Throwable _root) {
		super(_root);
	}

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public InformationModelNotFoundException(String _description,
			Throwable _root) {
		super(_description, _root);
	}

}
