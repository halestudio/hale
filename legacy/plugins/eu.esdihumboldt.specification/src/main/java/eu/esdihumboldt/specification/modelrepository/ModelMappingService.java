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

import eu.esdihumboldt.specification.annotations.spec.RequiredIn;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.mapping.Mapping;
import eu.esdihumboldt.specification.modelrepository.abstractfc.mapping.MappingConstraint;
import eu.esdihumboldt.specification.modelrepository.exceptions.NoSuchMappingException;

/**
 * The ModelMappingService provides the Mediation Tier of the HUMBOLDT systems
 * with information on whether two InformationModels can be transformed and
 * manages the transformation rules that have to be applied. It offers
 * possiblities to find, edit and save mappings.<br/>
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ModelMappingService {

	/**
	 * Retrieve a mapping based on the unique identifiers of the
	 * InformationModels that it connects.
	 * 
	 * @param id1
	 *            the URL of the first ConceptualSchema.
	 * @param id2
	 *            the URL of the second ConceptualSchema.
	 * @return an uni- or bidirectional Mapping allowing to make a
	 *         transformation from the InformationModels id1 to id2.
	 * @throws NoSuchMappingException
	 *             if no Mapping from id1 to id2 can be found.
	 */
	public Mapping getMapping(URL id1, URL id2) throws NoSuchMappingException;

	/**
	 * Retrieve a mapping based on the unique identifiers of the
	 * InformationModels that it connects, with additional constraints which
	 * allow the system express restrictions to the mapping expressed by the
	 * User. One such restriction might be the author of the mapping, or a
	 * minimum coverage.
	 * 
	 * @param id1
	 *            the URL of the first ConceptualSchema.
	 * @param id2
	 *            the URL of the second ConceptualSchema.
	 * @param constraints
	 *            A Collection of MappingConstraints
	 * @return an uni- or bidirectional Mapping allowing to make a
	 *         transformation from the InformationModels id1 to id2.
	 * @throws NoSuchMappingException
	 *             if no Mapping from id1 to id2 can be found.
	 */
	public Mapping getMapping(URL id1, URL id2,
			Collection<? extends MappingConstraint> constraints)
			throws NoSuchMappingException;

	/**
	 * Retrieve a mapping based on two InformationModels, without additional
	 * constraints. This operation is useful to map a subset of a known
	 * ConceptualSchema, for instance.
	 * 
	 * @param im1
	 *            the ConceptualSchema from which data is to be transformed.
	 * @param im2
	 *            the ConceptualSchema into which data is to be transformed.
	 * @return an uni- or bidirectional Mapping allowing to make a
	 *         transformation from im1 to im2.
	 * @throws NoSuchMappingException
	 *             if no Mapping from im1 to im2 can be found. Is also thrown if
	 *             one of the two InformationModels does not exist.
	 */
	@RequiredIn("UC0001.6, UC0001.AP01.2")
	public Mapping getMapping(ConceptualSchema im1, ConceptualSchema im2)
			throws NoSuchMappingException;

	/**
	 * Retrieve a mapping based on two InformationModels, with additional
	 * constraints which allow the system express restrictions to the mapping
	 * expressed by the User. One such restriction might be the author of the
	 * mapping, or a minimum coverage. This operation is also mainly useful when
	 * working on ad-hoc InformationModels or on dynamic subsets of known
	 * InformationModels.
	 * 
	 * @param im1
	 *            the ConceptualSchema from which data is to be transformed.
	 * @param im2
	 *            the ConceptualSchema into which data is to be transformed.
	 * @param constraints
	 *            A Collection of MappingConstraints
	 * @return an uni- or bidirectional Mapping allowing to make a
	 *         transformation from im1 to im2.
	 * @throws NoSuchMappingException
	 *             if no Mapping from im1 to im2 can be found. Is also thrown if
	 *             one of the two InformationModels does not exist.
	 */
	@RequiredIn("UC0001.5, UC0003.3")
	public Mapping getMapping(ConceptualSchema im1, ConceptualSchema im2,
			Collection<? extends MappingConstraint> constraints)
			throws NoSuchMappingException;

	/**
	 * Retrieve a Mapping by its known unique identifier.
	 * 
	 * @param identifier
	 *            - the URL object that uniquely identifies a given Mapping.
	 * @return the Mapping associated with the identifier.
	 * @throws NoSuchMappingException
	 *             if no Mapping with the given identifier was found.
	 */
	@RequiredIn("UC0002.5, UC0002.6, UC0002.7")
	public Mapping getMapping(URL identifier) throws NoSuchMappingException;

	/**
	 * Retrieve a mapping based on constraints, such as the author, the name or
	 * the date expressed by the User.
	 * 
	 * @param constraints
	 *            A Collection of MappingConstraints
	 * @return an uni- or bidirectional Mapping allowing to make a
	 *         transformation from im1 to im2.
	 * @throws NoSuchMappingException
	 *             if no Mapping fitting the constraints can be found.
	 */
	public Mapping getMapping(
			Collection<? extends MappingConstraint> constraints)
			throws NoSuchMappingException;

}
