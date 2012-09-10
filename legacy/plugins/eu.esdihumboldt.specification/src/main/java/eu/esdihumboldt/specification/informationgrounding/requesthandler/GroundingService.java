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

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.esdihumboldt.specification.informationgrounding.exceptions.InconsistentGroundingServiceException;
import eu.esdihumboldt.specification.mediator.TypeKey;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;

/**
 * Name: eu.esdihumboldt.informationgrounding.requesthandler / GroundingService<br/>
 * Purpose: This interface describes a Grounding Service. Grounding Services are
 * available services which provide an interface allowing requests for any
 * subsets of a multi-dimensional and multitemporal geospatial data for a
 * specific geographic region.
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface GroundingService {
	/**
	 * @return this GroundingService's name as a String. It is the name by which
	 *         the resource is known.
	 * @throws {@link InconsistentGroundingServiceException} if the name is
	 *         missing.
	 */
	public java.lang.String getName()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's ID (fileIdentifier) as a String.
	 * @throws {@link InconsistentGroundingServiceException} if the id is
	 *         missing.
	 */
	public java.lang.String getId()
			throws InconsistentGroundingServiceException;

	// /**
	// * A convinient method that retieves all the constraints that have not
	// been
	// * satisfied by this grouding service. The values of the constraints are
	// the
	// * actuall values found in a grounding that is different from what was
	// * required. For instance, if a spatial constraint with CRS ESPG:4326 is
	// required
	// * but instead a grounding is found that has CRS as ESPG:24432, then a
	// spatial
	// * constraint with the unsatisfied value should be returned.
	// * @return
	// */
	// public Map<TypeKey,Constraint> getUnsatisfiedConstraints();
	// /**
	// * Convinent method to retrieve all the constraints which have been
	// satisfied
	// * by this grounding service
	// * @return Map of constraints satisfied by this GS
	// */
	// public Map<TypeKey,Constraint> getSatisfiedConstraints();
	/**
	 * @return PreconditionIds satisfied by the Grounding Service as a String.
	 *         It is the unique id which identifies a before request to the IGS.
	 *         It allows remember the Grounding Services which satisfy the
	 *         preconditions managed by the Workflow Service.
	 * @throws {@link InconsistentGroundingServiceException} if the
	 *         preconditionId is missing.
	 */
	public java.lang.String getPreconditionId()
			throws InconsistentGroundingServiceException;

	/**
	 * @return the List of URLs identifying this GroundingService. It is the URL
	 *         which the resource can be obtained from.
	 * @throws {@link InconsistentGroundingServiceException} if the URL is
	 *         missing.
	 */
	public List<java.net.URL> getUrl()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Description as a String. It is the brief
	 *         narrative summary of the content of the resource.
	 * @throws {@link InconsistentGroundingServiceException} if the description
	 *         is missing.
	 */
	public java.lang.String getDescription()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Protocol as a String. It is the
	 *         connection protocol to be used
	 * @throws {@link InconsistentGroundingServiceException} if the protocol is
	 *         missing.
	 */
	public java.lang.String getProtocol()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's topic code as a String The topic is the
	 *         name defined in the MD_TopicCategoryCode(ISO 19115). It is the
	 *         alphanumeric value identifying the reference system.
	 * @throws {@link InconsistentGroundingServiceException} if the topic is
	 *         missing.
	 */
	public String getTopic() throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Reference System code as a String. It is
	 *         the alphanumeric value identifying the reference system.
	 * @throws {@link InconsistentGroundingServiceException} if the reference
	 *         system is missing.
	 */
	public java.lang.String getRefSys()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Language as a String. It is the language
	 *         used for documenting metadata. The language code follows ISO
	 *         639-2.
	 * @throws {@link InconsistentGroundingServiceException} if the language is
	 *         missing.
	 */
	public java.lang.String getLang()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Keyword as a String. It is the commonly
	 *         used word(s) or formalised word(s) or phrase(s) used to describe
	 *         the subject.
	 * @throws {@link InconsistentGroundingServiceException} if the keyword is
	 *         missing.
	 */
	public java.lang.String getKeyword()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's West bounding longitude as a Double. It
	 *         is the western-most coordinate of the limit of the dataset
	 *         extent, expressed in longitude in decimal degrees (positive
	 *         east).
	 * @throws {@link InconsistentGroundingServiceException} if the west
	 *         bounding longitude is missing.
	 */
	public java.lang.Double getWestBL()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's East bounding longitude as a Double. It
	 *         is the eastern-most coordinate of the limit of the dataset
	 *         extent, expressed in longitude in decimal degrees (positive
	 *         east).
	 * @throws {@link InconsistentGroundingServiceException} if the east
	 *         bounding longitude is missing.
	 */
	public java.lang.Double getEastBL()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's North bounding latitude as a Double. It
	 *         is the northern-most coordinate of the limit of the dataset
	 *         extent, expressed in latitude in decimal degrees (positive
	 *         north).
	 * @throws {@link InconsistentGroundingServiceException} if the north
	 *         bounding latitude is missing.
	 */
	public java.lang.Double getNorthBL()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's South bounding latitude as a Double. It
	 *         is the southern-most coordinate of the limit of the dataset
	 *         extent, expressed in latitude in decimal degrees (positive
	 *         north).
	 * @throws {@link InconsistentGroundingServiceException} if the south
	 *         bounding latitude is missing.
	 */
	public java.lang.Double getSouthBL()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Service as a String. It is the
	 *         alphanumeric value identifying the service. (e.g. "WMS").
	 * @throws {@link InconsistentGroundingServiceException} if the service is
	 *         missing.
	 */
	public java.lang.String getService()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's ServiceVersion as a String. It is the
	 *         alphanumeric value identifying the service version. (e.g.
	 *         "1.1.0").
	 * @throws {@link InconsistentGroundingServiceException} if the service
	 *         version is missing.
	 */
	public java.lang.String getServiceVersion()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Temporal Validity as a Date. It is the
	 *         validity of data with respect to time.
	 * @throws {@link InconsistentGroundingServiceException} if the temporal
	 *         validity information is missing.
	 */
	public Date getTemporalValidity()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Temporal Consistency as a Date.
	 * @throws {@link InconsistentGroundingServiceException} if the temporal
	 *         consistency information is missing.
	 */
	public Date getTemporalConsistency()
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Relevancy. It is the relevancy weighting
	 *         {@see Relevancy} fulfilling the constraints which were imposed.
	 * @throws {@link InconsistentGroundingServiceException} if the Relevancy is
	 *         missing.
	 */
	public Relevancy getRelevancy(Map<TypeKey, Constraint> Constraint)
			throws InconsistentGroundingServiceException;

	/**
	 * @return this GroundingService's Feature Name as a String.
	 * @throws {@link InconsistentGroundingServiceException} if the feature name
	 *         information is missing.
	 */
	public String getFeatureName() throws InconsistentGroundingServiceException;

	/**
	 * Convinent method to retrieve all the constraints which have been
	 * satisfied by this grounding service
	 * 
	 * @return a map of the constraints satisfied by this GroundingService.
	 *         (among the required constraints in the request).
	 */
	public Map<TypeKey, Constraint> getSatisfiedConstraints();

	/**
	 * A convinient method that retieves all the constraints that have not been
	 * satisfied by this grouding service. The values of the constraints are the
	 * actuall values found in a grounding that is different from what was
	 * required. For instance, if a spatial constraint with CRS ESPG:4326 is
	 * required but instead a grounding is found that has CRS as ESPG:24432,
	 * then a spatial constraint with the unsatisfied value should be returned.
	 * 
	 * @return a map of unsatisfied constraints. The map keyset consist of
	 *         Constraints Required (in the precondition)and the values contains
	 *         Constraints Found (as found in the grounding services).
	 * 
	 */
	public Map<Map<TypeKey, Constraint>, Map<TypeKey, Constraint>> getUnsatisfiedConstraints();

	/**
	 * @return the constraints satisfied by this GroundingService.
	 */
	public Map<TypeKey, Constraint> getAllConstraints();

}
