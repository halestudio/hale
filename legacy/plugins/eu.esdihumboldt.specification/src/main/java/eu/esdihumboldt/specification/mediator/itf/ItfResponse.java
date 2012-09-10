/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 *
 * Componet     : Humboldt
 * 	 
 * Classname    : eu.esdihumboldt.mediator.itf/ItfResponse.java 
 * 
 * Author       : Bernd Schneiders, Logica
 * 
 * Created on   : Sep 9, 2008 -- 3:10:07 PM
 *
 */
package eu.esdihumboldt.specification.mediator.itf;

import java.util.UUID;

/**
 * This interface provides a structure which holds the response of the ITF.
 * 
 * @author Bernd Schneiders, Logica
 */
public interface ItfResponse {

	/**
	 * @return String containing a OGC WebService response (e.g. WFS response).
	 */
	public String getXmlResponse();

	/**
	 * @return true if a request of a user is complete processed, false else.
	 */
	public boolean isProcessed();

	/**
	 * @return the transaction identifier of a request
	 */
	public UUID getTransactionID();
}
