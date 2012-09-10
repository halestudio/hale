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

package eu.esdihumboldt.specification.informationgrounding.igs;

import eu.esdihumboldt.specification.informationgrounding.gcmanager.GC_Manager;
import eu.esdihumboldt.specification.informationgrounding.gcmanager.GroundingCatalogue;
import eu.esdihumboldt.specification.informationgrounding.requesthandler.GroundingService;
import eu.esdihumboldt.specification.informationgrounding.requesthandler.RequestHandler;

/**
 * Name: eu.esdihumboldt.informationgrounding.igs / InformationGroundingService<br/>
 * Purpose: This interface describes methods used to find Geospatial information
 * resources distributed on the Internet. They are the called Grounding
 * Services. For this purpose this interface provides methods to find
 * information about {@link GroundingService} which fulfill several imposed
 * constraints and to manage the {@link GroundingCatalogue} where these
 * Grounding Services are metadated.<br/>
 * 
 * Note that for an implementation in the Mediator, this Service should consist
 * of a local service for local information groundings as well as a remote
 * service which represents the actual catalogue of available services and data
 * sources.
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface InformationGroundingService extends RequestHandler, GC_Manager {

}
