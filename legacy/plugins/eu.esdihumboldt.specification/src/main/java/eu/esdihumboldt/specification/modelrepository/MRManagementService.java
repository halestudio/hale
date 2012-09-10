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
package eu.esdihumboldt.specification.modelrepository;

import java.net.URL;

import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.mapping.Mapping;
import eu.esdihumboldt.specification.modelrepository.exceptions.InconsistentMappingException;
import eu.esdihumboldt.specification.modelrepository.exceptions.InconsistentModelException;
import eu.esdihumboldt.specification.modelrepository.exceptions.InformationModelNotFoundException;
import eu.esdihumboldt.specification.modelrepository.exceptions.NoSuchMappingException;

/**
 * This interface can be sued for management operations on the resources stored
 * in a MR node, such as removing/deprecating, updating or storing mappings and
 * models.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface MRManagementService {

	// operations that work on mappings ........................................

	/**
	 * Use this operation to deprecate the mapping with the given URL.
	 * 
	 * @param mapping_id
	 *            the URL of the mapping to be deprecated.
	 * 
	 * @return true if the operation was successful.
	 * @throws NoSuchMappingException
	 *             if no Mapping fitting the ID URL can be found.
	 */
	public boolean deprecateMapping(URL mapping_id)
			throws NoSuchMappingException;

	/**
	 * Use this operation to deprecate the mapping with the given URL and to
	 * show which one replaces it.
	 * 
	 * @param old_mapping_id
	 *            the URL of the mapping to be deprecated.
	 * @param new_mapping_id
	 *            the URL of the mapping to register as replacement.
	 * @return true if the operation was successful.
	 * @throws NoSuchMappingException
	 *             if no Mapping fitting the ID URL can be found.
	 */
	public boolean deprecateMapping(URL old_mapping_id, URL new_mapping_id)
			throws NoSuchMappingException;

	/**
	 * Use this operation to publish or remove (unpublish) the mapping with the
	 * given URL.
	 * 
	 * @param mapping_id
	 *            the URL of the mapping to be deleted.
	 * @param rs
	 *            the ResourceStatus to set the given mapping to.
	 * @return true if the operation was successful.
	 * @throws NoSuchMappingException
	 *             if no Mapping fitting the ID URL can be found.
	 */
	public boolean setMappingStatus(URL mapping_id, ResourceStatus rs)
			throws NoSuchMappingException;

	/**
	 * This method updates a given Mapping in the repository used by this
	 * service. If the given Mapping does not yet exist, is is persisted as a
	 * new Mapping.
	 * 
	 * @param mapping
	 *            the Mapping to update or save.
	 * @throws InconsistentMappingException
	 *             if the given Mapping is not valid, i.e. missing required
	 *             parameters.
	 */
	public void updateMapping(Mapping mapping)
			throws InconsistentMappingException;

	/**
	 * This method saves a given Mapping to the repository used by this service.
	 * 
	 * @param mapping
	 *            the Mapping to save.
	 * @throws InconsistentMappingException
	 *             if the given Mapping is not valid, i.e. missing required
	 *             parameters.
	 */
	public void putMapping(Mapping mapping) throws InconsistentMappingException;

	// operations that work on schemas .........................................

	/**
	 * Use this operation to deprecate the schema with the given URL.
	 * 
	 * @param schema_id
	 *            the URL of the schema to be deprecated.
	 * @return true if the operation was successful.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	public boolean deprecateSchema(URL schema_id)
			throws InformationModelNotFoundException;

	/**
	 * Use this operation to deprecate the schema with the given URL and to show
	 * which one replaces it.
	 * 
	 * @param old_schema_id
	 *            the UUID of the schema to be deprecated.
	 * @param new_schema_id
	 *            the UUID of the schema to register as replacement.
	 * @return true if the operation was successful.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	public boolean deprecateSchema(URL old_schema_id, URL new_schema_id)
			throws InformationModelNotFoundException;

	/**
	 * Use this operation to publish or remove (unpublish) the schema with the
	 * given URL.
	 * 
	 * @param schema_id
	 *            the URL of the schema to be deleted.
	 * @param rs
	 *            the {@link ResourceStatus} that this schema should be in.
	 * @return true if the operation was successful.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	public boolean setSchemaStatus(URL schema_id, ResourceStatus rs)
			throws InformationModelNotFoundException;

	/**
	 * This method updates a given ConceptualSchema to the repository used by
	 * this service. If the given ConceptualSchema does not yet exist, is is
	 * persisted as a new ConceptualSchema.
	 * 
	 * @param im
	 *            the ConceptualSchema to update or save.
	 * @throws InconsistentModelException
	 *             if the given model is not valid, i.e. missing required
	 *             parameters.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	public void updateInformationModel(ConceptualSchema im)
			throws InconsistentModelException,
			InformationModelNotFoundException;

	/**
	 * This method saves a given ConceptualSchema to the repository used by this
	 * service.
	 * 
	 * @param im
	 *            the ConceptualSchema to save.
	 * @throws InconsistentModelException
	 *             if the given model is not valid, i.e. missing required
	 *             parameters.
	 */
	public void putInformationModel(ConceptualSchema im)
			throws InconsistentModelException;

	/**
	 * This method saves a given ConceptualSchema to the repository as a profile
	 * of the ConceptualSchema identified by parent_im_id.
	 * 
	 * @param im
	 *            the ConceptualSchema to save.
	 * @param parent_im_id
	 *            the ConceptualSchema's URL of which the new IM is a profile.
	 * @throws InconsistentModelException
	 *             if the given model is not valid, i.e. missing required
	 *             parameters.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	public void putInformationModel(ConceptualSchema im, URL parent_im_id)
			throws InconsistentModelException,
			InformationModelNotFoundException;

	/**
	 * This enumeration contains the publishing statuses in which a mapping or
	 * schema can be. Please note that deprecation is an additional status that
	 * can be applicable to all of the three publishing statuses below.
	 * 
	 * @author Thorsten Reitz
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 * @version $Id$
	 */
	public enum ResourceStatus {
		/**
		 * A resource has been stored, but is not yet fit for use or not fit
		 * anymore.
		 */
		unpublished,
		/**
		 * A resource has been published for use to those who have direct access
		 * to the MR it is stored in.
		 */
		local_published,
		/**
		 * A resource has been published so that everybody with access to any MR
		 * can access it.
		 */
		public_published
	}

}
