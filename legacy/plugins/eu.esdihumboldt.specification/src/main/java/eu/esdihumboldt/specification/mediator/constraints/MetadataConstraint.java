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

/**
 * This type of Constraint allows to test for conformance to other metadata
 * elements than those which have explicit constraint types, such as responsible
 * party or a certain identification such as keywords.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MetadataConstraint extends Constraint {

	/**
	 * @return the object used to express the value of this Constraint, i.e. the
	 *         property that needs to be in this.getRelationType() to the
	 *         property of a dataset to sue.
	 */
	public Object getConstraintValue();

	/**
	 * @return the RelationType against which to check in this Constraint.
	 */
	public RelationType getRelationType();

	/**
	 * @return the MetadataType being constrained by this object.
	 */
	public MetadataType getMetadataType();

	/**
	 * These are some prelimiary Metadata types that can be useful as
	 * constraints.
	 */
	public enum MetadataType {
		Adress, Contact, Telephone, ContextAbstract, Title, Citiation, Extension, ResponsibleParty, LegalAccessUseConstraint, SecurityAccessUseConstraint, Keywords, Identification, Series
	}

	/**
	 * These RelationTypes describe how the given input should match a Metadata
	 * property of a dataset to use.
	 */
	public enum RelationType {
		/** The entire value needs to be equal. */
		equals,
		/**
		 * A part of the metadata value needs to be equal to the Constraint
		 * value.
		 */
		equals_partial,
		/**
		 * A part of the metadata value needs to be lexically close to the
		 * Constraint value.
		 */
		like
	}

}
