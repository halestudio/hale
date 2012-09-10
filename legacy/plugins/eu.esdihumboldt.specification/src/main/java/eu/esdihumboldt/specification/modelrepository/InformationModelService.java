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
 */
package eu.esdihumboldt.specification.modelrepository;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.opengis.metadata.MetaData;

import eu.esdihumboldt.specification.annotations.spec.RequiredIn;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.exceptions.InconsistentModelException;
import eu.esdihumboldt.specification.modelrepository.exceptions.InformationModelNotFoundException;

/**
 * This interface describes methods used to find, read and modify information
 * models used in the HUMBOLDT scenarios.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface InformationModelService {

	/**
	 * Retrieve the ModelTaxonomy Tree used by this InformationModelService.
	 * 
	 * @param depth
	 *            the maximum depth until which the taxonomy should be returned.
	 *            If this value is 0, the entire Taxonomy will be returned.
	 * @param metadata
	 *            if true, the MetaData of the InformationModels found in the
	 *            Taxonomy that are also not deeper in the Taxonomy than
	 *            specified in depth are also returned.
	 * @return the ModelTaxonomy up to the defined level an optionally including
	 *         MetaData for encountered InformationModels.
	 */
	@RequiredIn("UC0001.2")
	public ModelTaxonomy getModelTaxonomy(int depth, boolean metadata);

	/**
	 * Retrieve all models for a given Topic from the ModelTaxonomy. If a Node
	 * that is not available in the ModelTaxonomy is requested, an unchecked
	 * Exception may be used.
	 * 
	 * @param node
	 *            a ModelTaxonomyNode out of this Service's ModelTaxonomy.
	 * @param follow
	 *            if true, the method will recursively look in
	 *            ModelTaxonomyNodes situated beneath the given
	 *            ModelTaxonomyNode.
	 * @return a List of InformationModels, either consisting of the
	 *         InformationModels directly attached to the given node or
	 *         including all InformationModels attached to child nodes of the
	 *         given node.
	 */
	@RequiredIn("UC0001.2")
	public List<ConceptualSchema> getModelsInNode(ModelTaxonomyNode node,
			boolean follow);

	/**
	 * Retrieve an ConceptualSchema by its known unique identifier.
	 * 
	 * @param identifier
	 *            - the URL object that uniquely identifies a given
	 *            ConceptualSchema.
	 * @return the ConceptualSchema associated with the identifier.
	 * @throws InformationModelNotFoundException
	 *             if no model with the given identifier was found.
	 */
	@RequiredIn("UC0001.3")
	public ConceptualSchema getInformationModel(URL identifier)
			throws InformationModelNotFoundException;

	/**
	 * Retrieve an ConceptualSchema by its possibly non-unique name.
	 * 
	 * @param name
	 *            - the name that the ConceptualSchema has.
	 * @return the ConceptualSchema associated with the name.
	 * @throws InformationModelNotFoundException
	 *             if no model or more than one model with the given name was
	 *             found.
	 */
	@RequiredIn("UC0002.AP01.1")
	public ConceptualSchema getNamedInformationModel(String name)
			throws InformationModelNotFoundException;

	/**
	 * Retrieve a List of InformationModels matching the given one as close as
	 * possible.
	 * 
	 * @param templatemodel
	 *            the ConceptualSchema to be used as an example.
	 * @param max
	 *            the maximum number of results to be sent.
	 * @return a list of InformationModels that seem to be related to the given
	 *         one.
	 * @throws InformationModelNotFoundException
	 *             if no matching ConceptualSchema was found.
	 * @throws InconsistentModelException
	 *             if the given {@link ConceptualSchema} is inconsistent.
	 */
	@RequiredIn("UC0002.2")
	public List<ConceptualSchema> getMatchingModel(
			ConceptualSchema templatemodel, int max)
			throws InformationModelNotFoundException,
			InconsistentModelException;

	/**
	 * Retrieve a List of InformationModels based on a given MetaData set. This
	 * can be used to find InformationModels created by a certain author, at a
	 * certain point in time or fulfilling any other constraint that can be
	 * expressed using the ISO19115 metadata elements.
	 * 
	 * @param metadata
	 *            the MetaData to be used as search parameters.
	 * @param unsharp
	 *            if true, partial matches can also be returned, i.e. results
	 *            where not all criteria are met.
	 * @return a list of InformationModels that seem to be related to the given
	 *         one.
	 * @throws InformationModelNotFoundException
	 *             if no matching ConceptualSchema was found.
	 */
	public List<ConceptualSchema> getMatchingModel(MetaData metadata,
			boolean unsharp) throws InformationModelNotFoundException;

	/**
	 * @param identifier
	 *            the URL used as identifier for the specified ConceptualSchema.
	 * @return just the MetaData for a specified ConceptualSchema.
	 * @throws InformationModelNotFoundException
	 *             if no matching ConceptualSchema was found.
	 */
	@RequiredIn("UC0001.2")
	public MetaData getInformationModelMetadata(URL identifier)
			throws InformationModelNotFoundException;

	/**
	 * @return a Collection containing the Metadata for all InformationModels
	 *         accessible to this service.
	 */
	@RequiredIn("UC0001.2")
	public Collection<MetaData> getAllModelsMetadata();

}
