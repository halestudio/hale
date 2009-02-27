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

/**This interface is used to provide a full definition of a proccess, including 
 * all inputs and output parameters
 * 
 * @author Moses Gone
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */

public interface ProcessDescription extends Description {

    /**Indicates if all complex data output(s) from this process can be stored 
     * by the WPS server as web-accessible resources
     * 
     * @return False if response is returned directly
     */
    public boolean storeSupported();

    /**Indicates if the Execute operation response can be returned quickly with 
     * status information
     * 
     * @return default is false
     */
    public boolean statusSupported();
    
      /**
     * This operation is invoked at execution time to determine whether this
     * Transformer can work on partial responses.
     *
     * @return true if this Transformer can be used on a partial
     * response, such as when a layer is assembled from two independent data
     * sources, but the processing result does not depend on the availability
     * of both responses.
     */
    public boolean isAtomic();

}

