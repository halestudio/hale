/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.specification.workflow;

import org.dom4j.Document;

import eu.esdihumboldt.specification.mediator.MediatorComplexRequest;
import eu.esdihumboldt.specification.workflow.exceptions.InconsistentWorkflowException;

/**
 * The workflow generator provide an interface that has got the sole purpose of
 * generating an executable and grounded workflow and supplying it to its
 * clients This interface has got one method that ecapsulates this functionality.
 * The interface is implemented by the workflow service interface.
 * 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 * @author Moses Gone
 */
public interface WorkflowGenerator {

	/**
	 * This is the main operation called by the Mediator to retrieve a workflow.
	 * The retrieved Workflow is a Mediator workflow Model and is different from
	 * the Internal Workflow Service Basic Workflow. In this method, the design
	 * time preconditions are concretized and groundings for the construction
	 * time preconditions are retrieved. in addition, any required transformers
	 * are also plugged into the BW. The resulting valid basic workflow is then
	 * used to generate a workflow that the mediator executes.
	 * 
	 * @param mcr
	 *            An Mediator Complex Request with user constraints and task
	 *            concept
	 * @return A Mediator Workflow Xml instance document that can be executable
	 *         by the Mediator
	 * @throws InconsistentWorkflowException
	 */
	public Document getWorkflow(MediatorComplexRequest mcr)
			throws InconsistentWorkflowException;

}
