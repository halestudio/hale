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

import java.util.Set;

/**
 * A {@link Concept} describes a class of objects, i.e. sets, collections, or
 * types of objects. Concepts are core elements of an {@link ConceptualSchema}.
 * An example of a {@link Concept} is a certain FeatureType. The modeling of
 * this {@link Concept} consequently follows the notion of a FeatureType as
 * described in ISO 19110 (Feature Catalogues).
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Concept extends SchemaElement {

	/**
	 * Identical to ISO 19110 FC_FeatureType.typeName, except that currently no
	 * Localization is supported. TODO: This will be added later, with an
	 * integrated Localization Concept throughout the metamodels.
	 * 
	 * @return a String that uniquely identifies this Concept within the
	 *         ConceptualSchema that contains this Concept.
	 */
	public String getTypeName();

	/**
	 * Identical to ISO 19110 FC_FeatureType.definition.
	 * 
	 * @return definition of the feature type in a natural language.
	 */
	public String getInformalDefinition();

	/**
	 * Identical to ISO 19110 FC_FeatureType.isAbstract.
	 * 
	 * @return true if this Concept is abstract, i.e. cannot directly be
	 *         attached to an Instance.
	 */
	public boolean isAbstract();

	/**
	 * This operation includes ISO 19110 FC_FeatureType.inheritsFrom, and
	 * FC_FeatureType.inheritsTo. FC_InheritanceRelation is thus a subclass of
	 * Relation.
	 * 
	 * @return a Set of Relation objects that describe how this concept is
	 *         related to other concepts in the same ConceptualSchema or in
	 *         other InformationModels.
	 */
	public Set<Relation> getRelations();

	/**
	 * This operation is equal to ISO 19110
	 * FC_FeatureType.carrierOfCharacteristics.
	 * 
	 * @return a Set of ConceptProperty objects defining the characteristics of
	 *         this Concept.
	 */
	public Set<ConceptProperty> getProperties();

	/**
	 * Identical to ISO 19110 FC_FeatureType.featureCatalogue.
	 * 
	 * @return the {@link ConceptualSchema} that this Concept belongs to.
	 */
	public ConceptualSchema getInformationModel();

}
