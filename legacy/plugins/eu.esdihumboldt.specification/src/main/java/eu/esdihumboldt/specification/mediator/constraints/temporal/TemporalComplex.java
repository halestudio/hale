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

package eu.esdihumboldt.specification.mediator.constraints.temporal;

import java.util.Set;

/**
 * A TemporalComplex describes a composite time value. It may consist of
 * different temporal primitives such as a TimeStamp or TimeSpan.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TemporalComplex extends TemporalPrimitive {

	/**
	 * @return a Set containing the TemporalPrimitives that have comprise this
	 *         TemporalComplex.
	 */
	public Set<TemporalPrimitive> getTemporalElements();

	/**
	 * This method returns the TemporalGranularity of the complex value. If this
	 * value consists of more than one temporal primitive the given
	 * TemporalGranularity is mapped to the lowest common denominator.
	 * 
	 * @return the mapped TemporalGranularity.
	 */
	public TimePoint.TemporalGranularity getGranularity();

	/**
	 * @param tg
	 *            the TemporalGranularity for which to test if this
	 *            TemporalComplex does not have gaps.
	 * @return true if the TemporalComplex/TemporalPrimitive objects stored in
	 *         this TemporalComplex do not have any gaps at the specified
	 *         TemporalGranularity.
	 */
	public boolean isContinuous(TimePoint.TemporalGranularity tg);

	/**
	 * @param tg
	 *            the TemporalGranularity for the returned TimeSpan.
	 * @return a new TimeSpan object starting at the earliest TimePoint in this
	 *         complex and ending at the latest one, in the specified precision.
	 *         If there are gaps or insufficient precision in the extreme
	 *         TimePoints,
	 * 
	 */
	public boolean getAsTimeSpan(TimePoint.TemporalGranularity tg);

}
