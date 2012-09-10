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
 * Name: eu.esdihumboldt.modelrepository.exceptions / NoSuchMappingException<br/>
 * Purpose: This Exception is thrown when no mapping was found by a service that
 * satisfies the stated requirements a user has.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class NoSuchMappingException extends Exception {

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 */
	public NoSuchMappingException(String _description) {
		super(_description);
	}

	/**
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public NoSuchMappingException(Throwable _root) {
		super(_root);
	}

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 * @param _root
	 *            - the root cause of this Exception.
	 */
	public NoSuchMappingException(String _description, Throwable _root) {
		super(_description, _root);
	}

}
