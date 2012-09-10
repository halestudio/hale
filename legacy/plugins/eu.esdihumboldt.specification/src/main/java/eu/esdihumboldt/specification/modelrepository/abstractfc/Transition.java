/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
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

import java.util.Map;

import eu.esdihumboldt.specification.mediator.MediatorComplexRequest;

/**
 * A Transition is a special type of {@link Relation} that could be described as
 * activatable. While a normal relationship is something static (such as a Woman
 * is-a Human), a transition allows a concept to be transformed into another by
 * executing an activity, such as Union(A,B) -> C. Transitions can be used to
 * automatically construct workflows in cases where the constraints set in a
 * {@link MediatorComplexRequest} cannot be satisfied from already computed
 * products but need to be processed from more basic products. While being a
 * {@link Relation}, a Transition at the same time also represents a "virtual"
 * {@link Concept}.
 * 
 * A Transition does not have a direct counterpart in ISO 19110, but similar
 * ideas can be found in some Ontology languages where they are sometimes called
 * functions.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface Transition extends Relation {

	/**
	 * @return a Map of named input references. The names are gives as Strings
	 *         and have the same semantics as a parameter name in an operation,
	 *         whereas the references are given as {@link ConceptProperty}
	 *         objects. There is no limit to the number of {@link Concept}
	 *         objects that the {@link ConceptProperty} objects may come from.
	 */
	public Map<String, ConceptProperty> getInputReferences();

	/**
	 * @return the {@link Concept} created through this transition.
	 */
	public Concept getRepresentedConcept();

}
