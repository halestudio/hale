/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.mediator;

import eu.esdihumboldt.specification.dataaccess.AccessResponse;

/**
 * This enumeration collects status types an {@link AccessResponse} or
 * {@link MediatorResponse} can have.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public enum ResponseStatus {

	/**
	 * unknown: the default state.
	 */
	unknown,
	/**
	 * pending: is sent when the request was sent successfully, but execution is
	 * done in an asnychronous matter.
	 */
	pending,
	/**
	 * completed: the request was fulfilled successfully.
	 */
	completed,
	/**
	 * even though no exception was sent, the service was not able to conduct
	 * the requested operation.
	 */
	failed
}
