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

package eu.esdihumboldt.specification.workflow.exceptions;

/**
 * 
 * @author mgone
 */
public class InconsistentWorkflowException extends Exception {

	private static final long serialVersionUID = 5710590479640849683L;

	public InconsistentWorkflowException(Throwable cause) {
		super(cause);
	}

	public InconsistentWorkflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public InconsistentWorkflowException(String message) {
		super(message);
	}

}
