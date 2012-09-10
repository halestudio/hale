/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the project web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc;

/**
 * A ConceptAssociation is a type of {@link ConceptProperty} that is defined by
 * another {@link Concept} in the same {@link ConceptualSchema}. It is primarily
 * used to describe Aggregations and Compositions.
 * 
 * The {@link ConceptAssociation} is similar to the ISO 19110
 * FC_AssociationRole.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConceptAssociation extends ConceptProperty {

	/**
	 * Identical to ISO 19110 FC_AssociationRole.cardinality.
	 * 
	 * @return the Cardinality object defining the number of instances of the
	 *         {@link Concept} that can act in this role relative to a single
	 *         instance of the target {@link Concept} type of the
	 *         ConceptAssociation.
	 */
	public Cardinality getCardinality();

	/**
	 * Equal to ISO 19110 FC_AssociationRole.type.
	 * 
	 * @return the AssociationType for this {@link ConceptAssociation}.
	 */
	public AssociationType getAssociationType();

	/**
	 * Equal to ISO 19110 FC_AssociationRole.isOrdered
	 * 
	 * @return true if the concept instances that this
	 *         {@link ConceptAssociation} refers to have to be ordered.
	 */
	public boolean isOrdered();

	/**
	 * Equal to ISO 19110 FC_AssociationRole.isNavigable
	 * 
	 * @return true if this {@link ConceptAssociation} is navigable from it's
	 *         enclosing Concept to the value type.
	 */
	public boolean isNavigable();

	/**
	 * Equal to ISO 19110 FC_AssociationRole.valueType
	 * 
	 * @return the {@link Concept} that this ConceptAssociation points at.
	 */
	public Concept getValueType();

	/**
	 * Similar to ISO 19110 FC_RoleType, but leaves out "ordinary" since the
	 * specification does not explain what "ordinary" could be.
	 */
	public enum AssociationType {
		/** "is part of" semantics */
		aggregation,
		/** "is a member of" semantics, i.e. can't exist on it's own */
		composition
	}

}
