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
public class WorkflowExpansionException extends Exception {

	public WorkflowExpansionException(Throwable cause) {
		super(cause);
	}

	public WorkflowExpansionException(String message, Throwable cause) {
		super(message, cause);
	}

	public WorkflowExpansionException(String message) {
		super(message);
	}

}
