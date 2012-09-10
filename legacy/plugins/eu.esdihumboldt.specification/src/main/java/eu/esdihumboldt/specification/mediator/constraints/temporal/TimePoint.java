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
package eu.esdihumboldt.specification.mediator.constraints.temporal;

import java.util.Map;

import org.opengis.referencing.cs.TimeCS;

/**
 * A TimePoint describes a single point in a time reference system in arbitrary
 * precision. Precision can be set by defining a certain granularity. It is
 * possible to omit granularities (i.e. defining seconds and day, but not hours
 * and minutes). In this example, this TimeSpan would be considered to have
 * "second" precision.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TimePoint extends TemporalPrimitive {

	/**
	 * @return the TimeCS that this TimePoint is expressed in.
	 */
	public TimeCS getTemporalReferenceSystem();

	/**
	 * @param tg
	 *            the TemporalGranularity of the time element to retrieve.
	 * @return the value for the specified TemporalGranularity. This will return
	 *         just the vlaue stored for the selected TemporalGranularity and
	 *         not take into account any other value saved.
	 */
	public int getTimeElement(TemporalGranularity tg);

	/**
	 * @param tg
	 *            the TemporalGranularity of the time element to retrieve.
	 * @return the value for the specified TemporalGranularity. This value will
	 *         take into account all other TemporalGranularity also specified,
	 *         basing the calculation on this TimePoint's
	 *         TemporalReferenceSystem. So, if the requested TemporalGranularity
	 *         is year, but there are also month = 6 and weeks = 2 stored, the
	 *         result will be year + 6/12 + 2/52 = year+0,5384...
	 */
	public double getTimeAs(TemporalGranularity tg);

	/**
	 * @return a Map containing all time values defined for this TimePoint.
	 */
	public Map<TemporalGranularity, Integer> getTime();

	/**
	 * The TemporalGranularity describes the precision of a certain TimePoint or
	 * TimeComplex. The values in the enumeration are an extension of those
	 * values available in ISO 19108, which carries only second to year.
	 */
	public enum TemporalGranularity {
		millisecond, second, minute, hour, day, week, month, year, decade, century, millenium, million_years, billion_years
	}

}
