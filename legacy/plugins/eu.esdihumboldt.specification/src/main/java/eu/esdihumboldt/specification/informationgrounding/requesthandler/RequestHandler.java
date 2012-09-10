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

package eu.esdihumboldt.specification.informationgrounding.requesthandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.esdihumboldt.specification.informationgrounding.exceptions.GroundingServiceNotFoundException;
import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;

/**
 * Name: eu.esdihumboldt.informationgrounding.requesthandler / Request Handler<br/>
 * Purpose: This interface describes methods used to find
 * {@link GroundingService} (i.e. concrete OGC or other services that are
 * available in the Grounding Tier) which fulfill several imposed constraints.<br/>
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface RequestHandler {

	/**
	 * It retrieves a specific GroundingService according its id.
	 * 
	 * @param id
	 *            - id of the {@link GroundingService} searched.
	 * @return the {@link GroundingService} identified by this id.
	 * @throws {@link GroundingServiceNotFoundException} if any
	 *         {@link GroundingService} was not found. was found with the id
	 *         provided.
	 */
	public GroundingService getGroundingService(String id)
			throws GroundingServiceNotFoundException;

	/**
	 * It retrieves a List of Grounding Services which are according with the
	 * required constraints.
	 * 
	 * @param maxMatches
	 *            - maximum number of {@link GroundingService} to be retrieved
	 * @param constraint
	 *            - map of the {@link Constraint} and its {@link TypeKey}. They
	 *            are the constraints to be fulfilled.
	 * @return a List of ranked {@link GroundingService} according with the
	 *         required constraints.
	 * @throws {@link GroundingServiceNotFoundException} if any
	 *         {@link GroundingService} was not found with these constraints.
	 */
	public List<GroundingService> getGroundingServices(Integer maxMatches,
			Map<TypeKey, Constraint> constraint)
			throws GroundingServiceNotFoundException;

	/**
	 * This is a convenient overloaded method for retrieving a list of grounding
	 * services.
	 * 
	 * @param maxMatches
	 *            - number of matches that should be returned per precondition.
	 * @param constraints
	 *            - the constraints map together with the UUID of the
	 *            precondition that they belong to.
	 * @return A list of ranked {@link GroundingService} depending on the match.
	 * @throws {@link GroundingServiceNotFoundException} if any
	 *         {@link GroundingService} was not found with these constraints.
	 */
	public List<GroundingService> getGroundingServices(int maxMatches,
			Map<UUID, Map<TypeKey, Constraint>> precondition)
			throws GroundingServiceNotFoundException;

}
