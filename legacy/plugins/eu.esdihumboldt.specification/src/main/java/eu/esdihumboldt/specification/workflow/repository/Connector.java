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

package eu.esdihumboldt.specification.workflow.repository;

import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.workflow.process.Description;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ProcessInput;
import eu.esdihumboldt.specification.workflow.transformer.inputoutputs.ProcessOutput;

/**
 * This is an interface that defines the connectors used to connect two
 * transfomers It has method that retrieves information on the connection
 * between Transformers
 * 
 * @author mgone
 */
public interface Connector {

	/**
	 * 
	 * @return The target of the source transformer
	 */
	public ProcessInput getTarget();

	/**
	 * 
	 * @return the origin of the connection
	 */
	public ProcessOutput getSource();

	/**
	 * Sets the constraints for this connection
	 * 
	 * @param constraint
	 *            A logical constraint that combines all the Constraints
	 *            restricting this connection
	 */
	public void setConnectionConstraints(Set<Constraint> constraint);

	/**
	 * 
	 * @return a unique identifier for this connector
	 */
	public UUID getConnectorId();

	/**
	 * This method gives a description of the connector
	 * 
	 * @return
	 */
	public Description getConnectorDescription();

	/**
	 * This method gives sets a description of the connector
	 * 
	 * @param _connectorDescription
	 *            A brief description of the connector
	 */
	public void setConnectorDescription(Description _connectorDescription);

	/**
	 * Sets the constraints for this connection
	 * 
	 * @return A set of constraints that restricts this link
	 */
	public Set<Constraint> getConnectionConstraints();

}
