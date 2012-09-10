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
package eu.esdihumboldt.specification.mediator.constraints;

import java.util.List;

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;

/**
 * This type of constraint expresses what kind of information is to be retrieved
 * for a certain request. The {@link ThematicConstraint} is therefore expressed
 * as a set of concepts from one or multiple InformationModels. As an example,
 * consider the following: Somebody is requesting a map product with five
 * layers. Out of these five layers, three are basic geoinformation, such as a
 * shaded DGM, the road network and the parcels. In addition, he requests two
 * layers from more specific thematic domains, such as the zoning plan and a
 * plan of protected ground water areas. This would represent three
 * {@link Concept}s from one {@link ConceptualSchema} and two from another
 * {@link ConceptualSchema}. Consequently, {@link #getThemes()} would return a
 * {@link List} containing two {@link ConceptualSchema}s, one containing two
 * {@link Concept}s, the other containing three {@link Concept}s.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ThematicConstraint extends Constraint {

	/**
	 * @return a List of InformationModels. The IMs can consist of very small
	 *         subsets of actual persisted IMs. In the simplest case, the IMs
	 *         are just collections of isolated concepts; this equals the set of
	 *         layers or FeatureTypes used in OGC WebServices such as WFS and
	 *         WMS.
	 */
	public List<ConceptualSchema> getThemes();

	/**
	 * @return the topic as the TopicCategoryCode defined in ISO 19115.
	 */
	public TopicCode getTopic();

	/**
	 * MD_TopicCategoryCode as defined in ISO 19115.
	 */
	public enum TopicCode {
		farming, biota, boundaries, climatologyMeteorologyAtmosphere, economy, elevation, environment, geoscientificInformation, health, imageryBaseMapsEarthCover, intelligenceMilitary, inlandWaters, location, oceans, planningCadastre, society, structure, transportation, utilitiesCommunication
	}
}
