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

package eu.esdihumboldt.specification.mediator.context.exceptions;

/**
 * 
 * This exception is thrown when a UserContext with the given idenitifier does
 * not exist.<br/>
 * 
 * @author Bernd Schneiders / LogicaCMG
 */
public class UserContextNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 */
	public UserContextNotFoundException(java.lang.String _description) {
		super(_description);
	}

	/**
	 * @param _root
	 *            - The root cause of this Exception.
	 */
	public UserContextNotFoundException(java.lang.Throwable _root) {
		super(_root);
	}

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 * @param _root
	 *            - The root cause of this Exception.
	 */
	public UserContextNotFoundException(java.lang.String _description,
			java.lang.Throwable _root) {
		super(_description, _root);
	}

}
