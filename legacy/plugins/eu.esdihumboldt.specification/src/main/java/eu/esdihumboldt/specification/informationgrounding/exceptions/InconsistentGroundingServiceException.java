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

package eu.esdihumboldt.specification.informationgrounding.exceptions;

/**
 * Name: eu.esdihumboldt.informationgrounding.exceptions /
 * InconsistentGroundingServiceException<br/>
 * Purpose: This exception is thrown when the Grounding Service is not valid.<br/>
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version $Id$
 */

public class InconsistentGroundingServiceException extends Exception {

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 */
	public InconsistentGroundingServiceException(java.lang.String _description) {
		super(_description);
	}

	/**
	 * @param _root
	 *            - The root cause of this Exception.
	 */
	public InconsistentGroundingServiceException(java.lang.Throwable _root) {
		super(_root);
	}

	/**
	 * @param _description
	 *            - A meaningful description of the exception.
	 * @param _root
	 *            - The root cause of this Exception.
	 */
	public InconsistentGroundingServiceException(java.lang.String _description,
			java.lang.Throwable _root) {
		super(_description, _root);
	}

}