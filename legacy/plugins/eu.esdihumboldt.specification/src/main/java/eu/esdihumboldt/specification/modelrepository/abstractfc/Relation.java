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
package eu.esdihumboldt.specification.modelrepository.abstractfc;

/**
 * A Relation connects a Set of Concepts from an ConceptualSchema conceptually
 * to each other and defines of what type this relation is, such as
 * generalization/specializiation relationship or (partial) equals relationship. <br/>
 * <br/>
 * If the Relation is used to connect concepts from different InformationModels,
 * it represents a conceptual mapping for these concepts between the two
 * InformationModels.
 * 
 * A Relation includes the ISO 19110 FC_InheritanceRelation, but goes further.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Relation {

	/**
	 * Identical to ISO 19110 FC_InheritanceRelation.name. Please note that
	 * currently no Localization is supported. TODO: integrated Localization
	 * Concept throughout the metamodels.
	 * 
	 * @return a String that uniquely identifies this Relation within the
	 *         ConceptualSchema that contains this Relation.
	 */
	public String getName();

	/**
	 * Identical to ISO 19110 FC_InheritanceRelation.description.
	 * 
	 * @return a String containing a natural language description of this
	 *         relation.
	 */
	public String getDescription();

}
