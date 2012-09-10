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

package eu.esdihumboldt.specification.mediator.usermanagement.exceptions;

/**
 * 
 * This exception is thrown when a Service with the given service name does not
 * exist.<br/>
 * 
 * @author Anna Pitaev / LogicaCMG
 * @version $Id: ServiceNotFoundException.java,v 1.1 2007-10-19 10:03:12 pitaeva
 *          Exp $
 */
public class ServiceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @param description
	 *            - A meaningful description of the exception.
	 */
	public ServiceNotFoundException(java.lang.String description) {
		super(description);
	}

	/**
	 * @param root
	 *            - The root cause of this Exception.
	 */
	public ServiceNotFoundException(java.lang.Throwable root) {
		super(root);
	}

	/**
	 * @param description
	 *            - A meaningful description of the exception.
	 * @param root
	 *            - The root cause of this Exception.
	 */
	public ServiceNotFoundException(java.lang.String description,
			java.lang.Throwable root) {
		super(description, root);
	}

}
