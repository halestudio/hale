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
package eu.esdihumboldt.workflow;

import eu.esdihumboldt.annotations.spec.ReferenceSpecification;
import eu.esdihumboldt.workflow.processdescription.ProcessDescription;
import java.util.UUID;

/**
 * This is the superinterface for all processing components.  It contains those
 * operations required in the OGC WPS 1.0 specification, but no operations that
 * the HUMBOLDT framework requires for management of locally running processing
 * components.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@ReferenceSpecification("OGC 05-007r6:6.1")
public interface WebProcessingService {

    /**This method provides a description of the process including the 
     * description of the sub-processesThis interface specifies the three 
     * operations that can be requested by a client and performed by a WPS server.
     * The ProcessDescription details in- and outputs just as described in the 
     * WPS 1.0 specification.
     * 
     * @return ProcessDescriptions describing this process
     */
    public ProcessDescription describeProcess();

    /**
     * This operation is called by the TransformationQueueManager to actually
     * start the processing in a WebProcessingService component.
     * @return the UUID for the started process.
     */
    public UUID processRequest();
}
