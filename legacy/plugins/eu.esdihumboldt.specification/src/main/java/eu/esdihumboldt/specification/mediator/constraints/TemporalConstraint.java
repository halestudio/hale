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

import eu.esdihumboldt.specification.mediator.constraints.temporal.TemporalPrimitive;
import eu.esdihumboldt.specification.mediator.constraints.temporal.TimePoint.TemporalGranularity;

/**
 * A TemporalConstraint is a special metadata constraint used to express a
 * temporal boundary for the geoinformation to be used. It is defined as an
 * interface of its own because of the high complexity involved with temporal
 * operations. The GeoAPI temporal expression possibilities are integrated as
 * just another dimension, not taking into account several points specific to
 * temporal attributes.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TemporalConstraint extends Constraint {

	/**
	 * @return the TemporalPrimitive describing the TimeSpans or TimePoints of
	 *         this constraint.
	 */
	public TemporalPrimitive getT0();

	/**
	 * This operation can be used to find out whether a time series is being
	 * requested.
	 * 
	 * @param tg
	 *            the TemporalGranularity in which the interval step size is to
	 *            be expressed. Example 1 day, 2 hours...
	 * @return 0 if no time series was requested, otherwise the step size
	 *         between snapshots in the given TemporalGranularity
	 */
	public double getInterval(TemporalGranularity tg);

	/**
	 * @return the RelationType that this Constraint represents. To express
	 *         combinations of RelationTypes, use multiple TemporalConstraint
	 *         combined by LogicalConstraints.
	 */
	public RelationType getRelationType();

	/**
	 * The RelationType defines what kind of temporal relation objects need to
	 * fulfill to satisfy this criterion. It corresponds to ISO
	 * 19136:relativePosition. In all {@link RelationType}s, A corresponds to
	 * the value contained with this {@link Constraint}, B corresponds to the
	 * value contained in the candidate dataset.
	 */
	public enum RelationType {
		/** A ends before B starts. */
		Before,
		/** A starts after B is completed. */
		After,
		/** A and B start at the same time. */
		Begins,
		/** A and B end at the same time. */
		Ends,
		/** A starts after B starts and ends before B ends. */
		During,
		/** A and B start and end at the same time. */
		Equals,
		/** B starts after A starts and ends before A ends. */
		Contains,
		/** A starts before B but ends within B. */
		Overlaps,
		/** A ends when B starts. */
		Meets,
		/** A starts after B started and ends after B ended. */
		OverlappedBy,
		/** A starts when B ends. */
		MetBy,
		/** B and A start at the same time (FIXME - cross-check) */
		BegunBy,
		/** B and A end at the same time (FIXME - cross-check) */
		EndedBy
	}

}
