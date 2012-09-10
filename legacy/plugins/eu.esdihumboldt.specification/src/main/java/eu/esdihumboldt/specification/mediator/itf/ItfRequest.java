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
 * Classname    : eu.esdihumboldt.mediator.itf/ItfRequest.java 
 * 
 * Author       : Bernd Schneiders, Logica
 * 
 * Created on   : Sep 9, 2008 -- 2:47:47 PM
 */

package eu.esdihumboldt.specification.mediator.itf;

import java.util.Map;
import java.util.UUID;

/**
 * This file defines an interface which holds all necessary information of an
 * OGC WebService request. (e.g. WFS/WMS request).
 * 
 * @author Bernd Schneiders, Logica
 */
public interface ItfRequest {

	/**
	 * @return String which contains the OGC Service name (e.g. "WFS")
	 */
	public String getServiceName();

	/**
	 * @return String containing the operation name of the request (e.g.
	 *         "GetFeature")
	 */
	public String getOperationName();

	/**
	 * @return String containing the WFS version (e.g. "1.0.0", "1.1.0")
	 */
	public String getVersion();

	/**
	 * @return UUID of the context which is stored in the ContextService and
	 *         related to the user
	 */
	public UUID getContextUuid();

	/**
	 * @return UUID of the transaction.
	 */
	public UUID getTransactionID();

	/**
	 * @return true if the request has an XML body/payload which contains the
	 *         XML request message. Returns false if it is an KvP request.
	 */
	public Boolean hasXmlPayload();

	/**
	 * @return XML payload of the request, null if it is an KvP request.
	 */
	public String getXmlPayload();

	/**
	 * @return Map of all KvP. It contains at minimum one KvP ("request",
	 *         <operationName>")
	 */
	public Map<String, String[]> getKvp();
}
