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

package eu.esdihumboldt.workflow.repository;

import eu.esdihumboldt.mediator.constraints.LogicalConstraint;
import eu.esdihumboldt.workflow.processdescription.Description;
import eu.esdihumboldt.workflow.transformer.inputoutputs.ProcessInput;
import eu.esdihumboldt.workflow.transformer.inputoutputs.ProcessOutput;
import java.util.UUID;

/**
 *This is an interface that defines the connectors used to connect two transfomers
 * It has method that retrieves information on the connection between Transformers
 * @author mgone
 */
public interface TransformerConnector {

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
     * @param constraint A logical constraint that combines all the Constraints restricting
     * this connection
     */
    public void setConnectionConstraints(LogicalConstraint constraint);

    /**
     *
     * @return a unique identifier for this connector
     */
    public UUID getConnectorId();

    /**
     * This method gives a description of the connector
     * @return
     */
    public Description getConnectorDescription();

    /**
     * This method gives sets a description of the connector
     * @param _connectorDescription A brief description of the connector

     */
    public void setConnectorDescription(Description _connectorDescription);

    /**
     * Convienience method for retrieving the Source  Transformer of this link
     * @return
     */
    public Transformer getSourceTransformer();
    /**
     * Convienience method for retrieving the Target  transformer of this link
     * @return
     */
    public Transformer getTargetTransformer();

    /**
     * Sets the constraints for this connection
     * @return A logical constraints that is a combination of constraints that restricts this link
     */
    public LogicalConstraint getConnectionConstraints();


}
