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
package eu.esdihumboldt.specification.mediator.usermanagement;

import java.util.Date;

/**
 * A ValidatyDates Interface allows to specify a start and end date of a period
 * in which user data are valid.
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: ValidatyDates.java,v 1.1 2007-10-19 10:03:07 pitaeva Exp $
 * @Deprecated too complex for the prototype needs, can be used for the
 *             Production
 */

@Deprecated
public interface ValidatyDates {

	/**
	 * 
	 * @return the first point in time on which validity/effectivity occurs.
	 */

	public Date getStartDate();

	/**
	 * 
	 * @return the last point in time for which a validity/effectivity occurs.
	 * 
	 */
	public Date getEndDate();

}
