
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
package eu.esdihumboldt.workflow.processdescription;

import eu.esdihumboldt.annotations.spec.ReferenceSpecification;
import java.util.Set;

/**This interface is used for the description to the processes (Transformers)at 
 * design-time
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@ReferenceSpecification("OGC 05-007r6:1.0.0 9.3.1")
public interface ProcessDescriptions extends Description{

   
    /**Full description of processes within this transformer. It is used especially 
     * when the Transformer is composed of other transformers
     * 
     * @return A set of process discriptions, one for each Process identified 
     * in the operation request
     */
    public Set<ProcessDescription> getProcessDescription();
    
    /**This method provide the language used for this service
     * 
     * @return the RFC4646 language code of the human readable text
     */
    public String getLanguage();

}

