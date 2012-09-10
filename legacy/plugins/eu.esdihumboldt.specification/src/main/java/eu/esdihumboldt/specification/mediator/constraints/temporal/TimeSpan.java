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
 * A TimeSpan has a defined start and end, and is continuous in between. An
 * implementation should make sure that both TimePoints have the same
 * TemporalGranularity and the same Temporal Reference System.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface TimeSpan extends TemporalPrimitive {

	/**
	 * @return the TimePoint representing the begin of this Timespan.
	 */
	public TimePoint getT0();

	/**
	 * @return the TimePoint representing the end of this Timespan.
	 */
	public TimePoint getT1();

}
