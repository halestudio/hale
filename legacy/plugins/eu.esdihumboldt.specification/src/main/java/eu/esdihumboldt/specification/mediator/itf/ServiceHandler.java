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
 * Classname    : eu.esdihumboldt.mediator.itf/ServiceHandler.java 
 * 
 * Author       : Bernd Schneiders, Logica
 * 
 * Created on   : Sep 9, 2008 -- 2:46:01 PM
 *
 */
package eu.esdihumboldt.specification.mediator.itf;

/**
 * An implementation of this interface handles an OGC WebService request. For
 * example an Handler for WFS GetFeature or WFS GetCapabilities request.
 * 
 * @author Bernd Schneiders, Logica
 * 
 */

public interface ServiceHandler {
	/**
	 * An implementation of this interface has to implement this method to
	 * indicate that the implementation can handle the request.
	 * 
	 * @param request
	 * @return true if the request can be handled, else false.
	 */
	Boolean canHandle(ItfRequest request);

	/**
	 * This method executes a request.
	 * 
	 * @param request
	 * @return ItfReponse containing the Service response (including GML data).
	 */
	ItfResponse execute(ItfRequest request);

}
