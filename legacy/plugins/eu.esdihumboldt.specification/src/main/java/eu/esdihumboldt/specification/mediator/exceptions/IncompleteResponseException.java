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
 * This exception is thrown when the (Sorted) Set of Responses
 * HarmonizationResponses is not completed correctly.
 * 
 * @author Guillermo Schwartz, Logica CMG
 * @version $Id$
 */
public class IncompleteResponseException extends Exception {

	/**
	 * This constructor of the exception provides
	 * 
	 * @param _description
	 *            - - A meaningful description of the exception.
	 */
	public IncompleteResponseException(String _description) {
		super(_description);
	}

	/**
	 * This constructor of the exception provides
	 * 
	 * @param _description
	 *            --A meaningful description of the exception.
	 * @param _root
	 *            - - The root cause of this Exception.
	 */
	public IncompleteResponseException(String _description, Throwable _root) {
		super(_description, _root);
	}

	/**
	 * This constructor of the exception provides
	 * 
	 * @param _root
	 *            - - The root cause of this Exception.
	 */
	public IncompleteResponseException(Throwable _root) {
		super(_root);
	}

}
