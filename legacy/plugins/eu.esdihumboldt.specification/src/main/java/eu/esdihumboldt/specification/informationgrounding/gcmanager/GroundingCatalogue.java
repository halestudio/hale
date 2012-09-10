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

package eu.esdihumboldt.specification.informationgrounding.gcmanager;

import java.util.Date;

import eu.esdihumboldt.specification.informationgrounding.exceptions.InconsistentGroundingCatalogueException;
import eu.esdihumboldt.specification.informationgrounding.requesthandler.GroundingService;

/**
 * Name: eu.esdihumboldt.informationgrounding.gcmanager / GroundingCatalogue<br/>
 * Purpose: This interface describes a Grounding Catalogue. A Grounding
 * Catalogue stores the descriptive data (metadata) regarding the
 * {@link GroundingService}. The interfaces which describe the catalogue are
 * defined by the OGC standards: the Catalogue Service Web (CSW). <br/>
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface GroundingCatalogue {

	/**
	 * @return the Service Type of the Grounding Catalogue. It is usually CSW.
	 */
	String getServiceType();

	/**
	 * @return the hostname of the Grounding Catalogue.
	 */
	String getHost();

	/**
	 * @return the Version of the Grounding Catalogue. It is usually 2.0.1 or
	 *         2.0.2.
	 */
	String getVersion();

	/**
	 * @return the port of the Grounding Catalogue.
	 */
	Integer getPort();

	/**
	 * @return the address of the Grounding Catalogue.
	 */
	String getAddress();

	/**
	 * @return the method used to connect to the Grounding Catalogue.
	 */
	Method getMethod();

	/**
	 * @return true, if the Grounding Catalogue uses SOAP.
	 */
	boolean isSoapUsed();

	/**
	 * @return address to login to the Grounding Catalogue.
	 */
	String getLoginAddress();

	/**
	 * @return user of the Grounding Catalogue.
	 */
	String getUsername();

	/**
	 * @return password of the Grounding Catalogue.
	 */
	String getPassword();

	/**
	 * @return the date of the last successful harvest of metadata
	 */
	Date getLastHarvestSuccess();

	/**
	 * @param serviceType
	 *            - the Service Type of the Grounding Catalogue. It is usually
	 *            CSW.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the Service Type is missing or is not valid.
	 */
	void setServiceType(String serviceType)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param version
	 *            - the Version of the Grounding Catalogue. It is usually 2.0.1
	 *            or 2.0.2.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the Version is missing or is not valid.
	 */
	void setVersion(String version)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param host
	 *            - the host name of the Grounding Catalogue.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the host name is missing or is not valid.
	 */
	void setHost(String host) throws InconsistentGroundingCatalogueException;

	/**
	 * @param port
	 *            - the port of the Grounding Catalogue.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the port is missing or is not valid.
	 */
	void setPort(Integer port) throws InconsistentGroundingCatalogueException;

	/**
	 * @param address
	 *            - the address of the Grounding Catalogue.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the address is missing or is not valid.
	 */
	void setAddress(String address)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param method
	 *            - the method to connect to the Grounding Catalogue.
	 * @throws InconsistentGroundingCatalogueException
	 *             if the method is missing or is not valid.
	 */
	void setMethod(Method method)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param useSoap
	 *            - true, if the Grounding Catalogue uses SOAP.
	 * @throws InconsistentGroundingCatalogueException
	 *             if SOAP with Get is used.
	 */
	void setSoapUsed(boolean useSoap)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param username
	 *            - the username of the Grounding Catalogue.
	 */
	void setUsername(String username);

	/**
	 * @param password
	 *            - the password of the Grounding Catalogue.
	 */
	void setPassword(String password);

	/**
	 * @param loginAddress
	 *            - the login address of the Grounding Catalogue.
	 */
	void setLoginAddress(String loginAddress);

	/**
	 * @param date
	 *            the date of the last successful harvest request
	 */
	void setLastHarvestSuccess(Date date);

	/**
	 * checks if two Grounding Catalogues are equal.
	 * 
	 * @param o
	 *            the object which is compared with this
	 * @return true, if equal
	 */
	boolean equals(Object o);

	enum Method {
		GET, POST;
	}

}