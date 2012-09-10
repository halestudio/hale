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
package eu.esdihumboldt.specification.workflow.transformer.inputoutputs;

import eu.esdihumboldt.specification.workflow.repository.Transformer.InputOutputStatus;

/**
 * This is a superclass of the process output type
 * 
 * @author mgone
 */
public interface ProcessOutput extends InputOutput {

	/**
	 * This method is used to define the target of a postcondition
	 * 
	 * @return The target of this ProcessInput
	 */
	public ProcessInput getTarget();

	/**
	 * Returns the status of this output
	 * 
	 * @return
	 */
	public InputOutputStatus getOutputStatus();

}
