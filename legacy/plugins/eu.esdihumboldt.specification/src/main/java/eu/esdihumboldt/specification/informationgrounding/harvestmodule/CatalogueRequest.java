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

package eu.esdihumboldt.specification.informationgrounding.harvestmodule;

import eu.esdihumboldt.specification.informationgrounding.exceptions.InconsistentGroundingCatalogueException;
import eu.esdihumboldt.specification.informationgrounding.gcmanager.GroundingCatalogue;

public interface CatalogueRequest {

	/**
	 * 
	 */
	public void execute();

	/**
	 * @return
	 */
	public HttpConfiguration getHttpConfiguration();

	/**
	 * @return
	 */
	public java.lang.String getReceivedData();

	/**
	 * @return
	 */

	public java.lang.String getSentData();

	/**
	 * @param username
	 * @param passsword
	 * @return
	 */

	public boolean login(String username, String passsword)
			throws InconsistentGroundingCatalogueException;

	/**
	 * @param groundingcatalogue
	 * @return
	 */
	public void setHttpConfiguration(GroundingCatalogue groundingcatalogue)
			throws InconsistentGroundingCatalogueException;

}
