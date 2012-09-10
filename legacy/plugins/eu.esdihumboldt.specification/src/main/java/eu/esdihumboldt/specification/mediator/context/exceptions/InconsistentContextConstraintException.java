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
 * This exception is thrown when the ContextConstraint is not valid.<br/>
 * 
 * @author Anna Pitaev / LogicaCMG
 * @version $Id: InconsistentContextConstraintException.java,v 1.1 2007-11-08
 *          12:52:25 pitaeva Exp $
 */

public class InconsistentContextConstraintException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param description
	 *            - A meaningful description of the exception.
	 */
	public InconsistentContextConstraintException(java.lang.String description) {
		super(description);
	}

	/**
	 * @param root
	 *            - The root cause of this Exception.
	 */
	public InconsistentContextConstraintException(java.lang.Throwable root) {
		super(root);
	}

	/**
	 * @param description
	 *            - A meaningful description of the exception.
	 * @param root
	 *            - The root cause of this Exception.
	 */
	public InconsistentContextConstraintException(java.lang.String description,
			java.lang.Throwable root) {
		super(description, root);
	}

}
