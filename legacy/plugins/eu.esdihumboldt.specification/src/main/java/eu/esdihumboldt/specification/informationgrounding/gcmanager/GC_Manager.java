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

import java.util.List;

import eu.esdihumboldt.specification.informationgrounding.exceptions.GroundingCatalogueNotFoundException;
import eu.esdihumboldt.specification.informationgrounding.exceptions.GroundingCatalogueRepositoryException;
import eu.esdihumboldt.specification.informationgrounding.exceptions.InconsistentGroundingCatalogueException;
import eu.esdihumboldt.specification.informationgrounding.requesthandler.GroundingService;

/**
 * Name: eu.esdihumboldt.informationgrounding.gcmanager / GC_Manager <br/>
 * Purpose: This interface describes methods used to manage
 * {@link GroundingCatalogue} A Grounding Catalogue stores the descriptive data
 * (metadata) regarding the {@link GroundingService}. The interfaces which
 * describe the catalogue are defined by the OGC standards: the Catalogue
 * Service Web (CSW). <br/>
 * 
 * @author Ana Belen Anton
 * @partner 02 / ETRA Research and Development
 * @version Framework v1.0
 */

public interface GC_Manager {

	/**
	 * It adds a Grounding Catalogue to the IGS. In order to it is available for
	 * the user.
	 * 
	 * @param gc
	 *            - {@link GroundingCatalogue} to be added
	 * @throws GroundingCatalogueRepositoryException
	 *             if an error in the repository occurs
	 * @throws InconsistentGroundingCatalogueException
	 *             if the {@link GroundingCatalogue} to be added is not valid.
	 * @throws GroundingCatalogueNotFoundException
	 */
	void addGroundingCatalogue(GroundingCatalogue gc)
			throws GroundingCatalogueRepositoryException,
			InconsistentGroundingCatalogueException,
			GroundingCatalogueNotFoundException;

	/**
	 * It retrieves a list of Grounding Catalogues which were configured
	 * previously.
	 * 
	 * @return a List of {@link GroundingCatalogue} which were available in the
	 *         repository. Returns an empty list, if no GroundingCatalogue is
	 *         available.
	 * @throws InconsistentGroundingCatalogueException
	 *             if reading a GC is erroneous
	 * @throws GroundingCatalogueRepositoryException
	 *             if an error in the repository occurs
	 */
	List<GroundingCatalogue> getAllGroundingCatalogues()
			throws InconsistentGroundingCatalogueException,
			GroundingCatalogueRepositoryException;

	/**
	 * It removes a Grounding Catalogue from the IGS. In order to it is not
	 * available for the user.
	 * 
	 * @param gc
	 *            - {@link GroundingCatalogue} to be removed
	 * @throws GroundingCatalogueNotFoundException
	 *             if a GC is not found in the repository
	 * @throws GroundingCatalogueRepositoryException
	 *             if an error in the repository occurs
	 * @throws InconsistentGroundingCatalogueException
	 *             if a GC is inconsistent if the {@link GroundingCatalogue} to
	 *             be removed is not valid.
	 */
	void removeGroundingCatalogue(GroundingCatalogue gc)
			throws InconsistentGroundingCatalogueException,
			GroundingCatalogueNotFoundException,
			GroundingCatalogueRepositoryException;

}