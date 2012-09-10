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
 * This constraint allows the definition of quality parameters that the data in
 * question will need to fulfill. For this, ISO 19113 quality definitions have
 * been used. These are also encoded into ISO 19115 (Metadata).
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface QualityConstraint extends Constraint {

	/**
	 * @return the QualityConstraintType of the Element contained in this
	 *         Constraint.
	 */
	public QualityConstraintType getConstraintType();

	/**
	 * @return a value expressing the maximum tolerated error for this
	 *         QualityConstraint. The concrete meaning depends on the
	 *         QualityConstraintType; for those which are boolean, the value
	 *         represents the portion of the objects that have to fulfill the
	 *         QualityConstraint in any given dataset. This portion is expressed
	 *         as [0..1]. Example: If ConceptualConsistency is checked and
	 *         Tolerance is set to 0, the first error will lead to the
	 *         QualityConstraint not being satisfied.
	 */
	public double getTolerance();

	/**
	 * @return the ToleranceType for this QualityConstraint.
	 */
	public ToleranceType getToleranceType();

	/**
	 * These are ISO 19113 Quality Metadata element types. Please refer to the
	 * ISO 19113/19115 documentation for details.
	 */
	public enum QualityConstraintType {
		Completeness, ConceptualConsistency, DomainConsistency, FormalConsistency, LogicalConsistency, PositionalAccuracy, QuantitativeAttributeAccuracy, TemporalConsistency, TopologicalConsistency
	}

	/**
	 * The ToleranceType indicates what kind of tolerance is being tested for.
	 * The ToleranceTypes defined so far are:
	 * <ul>
	 * <li>absolute: the absolute maximum numeric error that may occur.
	 * Allowable Values: unrestricted</li>
	 * <li>relative: the maximum relative (local) error that may occur.
	 * Allowable Values: [0..1]</li>
	 * <li>portion: the portion of objects not satisfying a boolean criterium.
	 * Allowable Values: [0..1]</li>
	 * <li>mean: the mean absolute error that may occur. Allowable Values:
	 * unrestricted</li>
	 * </ul>
	 */
	public enum ToleranceType {
		/**
		 * The absolute maximum numeric error that may occur. Allowable Values:
		 * unrestricted
		 */
		absolute,
		/**
		 * The maximum relative (local) error that may occur. Allowable Values:
		 * [0..1]
		 */
		relative,
		/**
		 * The maximum portion of objects not satisfying a boolean criterium.
		 * Allowable Values: [0..1]
		 */
		portion,
		/**
		 * mean: the mean absolute error that may occur. Allowable Values:
		 * unrestricted
		 */
		mean
	}
}
