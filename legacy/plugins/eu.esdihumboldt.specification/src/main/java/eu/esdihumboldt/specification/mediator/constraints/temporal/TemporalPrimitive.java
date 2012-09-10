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

/**
 * A TemporalPrimitive describes a single time value. It may consist of a
 * TimeStamp or TimeSpan.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TemporalPrimitive {

	/**
	 * This method returns the TemporalGranularity of the complex value.
	 * 
	 * @return the TemporalGranularity of the contained TimePoint or TimeSpan.
	 */
	public TimePoint.TemporalGranularity getGranularity();

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out which one is older.
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if this instance is older, otherwise false.
	 */
	public boolean before(TemporalPrimitive tp);

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out whether they touch each other.
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if the values touch each other, otherwise false.
	 */
	public boolean touch(TemporalPrimitive tp);

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out whether they intersect each other
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if the values intersect, otherwise false.
	 */
	public boolean intersects(TemporalPrimitive tp);

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out whether they have the same start value.
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if they have the same start value, otherwise false.
	 */
	public boolean startsWith(TemporalPrimitive tp);

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out whether they have the same end value
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if they have the same end value, otherwise false.
	 */
	public boolean endsWith(TemporalPrimitive tp);

	/**
	 * This method compares the given TemporalPrimitive with this instance to
	 * find out whether this instance is enclosed by the given value
	 * 
	 * @param tp
	 *            the value to compare.
	 * @return true if this instance is enclosed by the given value, otherwise
	 *         false.
	 */
	public boolean enclosedBy(TemporalPrimitive tp);

}
