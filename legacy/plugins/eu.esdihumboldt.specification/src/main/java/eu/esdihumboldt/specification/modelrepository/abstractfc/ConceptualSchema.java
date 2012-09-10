/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.modelrepository.abstractfc;

import java.util.Collection;
import java.util.Set;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;

/**
 * The ConceptualSchema summarizes all information required to describe the
 * conceptual application schema for a single domain. It contains Concepts,
 * Relationships and Transitions (i.e. Actions that allow to use otherwise
 * unrelated concepts). Transitions are being used by the RequestBroker to
 * decide which Transformers will need to be applied to a data set to satisfy a
 * client's constraints.
 * 
 * When compared to ISO 19110, a ConceptualSchema is conceptually very close to
 * the FC_FeatureCatalogue type. Compared to ISO 19110, this implementation
 * omits the scope attribute, since that currently cannot be formalized, and the
 * functionalLanguage attribute, since this structure is the language.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface ConceptualSchema extends SchemaElement {

	/**
	 * Equal to ISO 19110 FC_FeatureCatalogue.name, with the exception that a
	 * Citation is used for additional naming capabilities.
	 * 
	 * @return the Citation, including Name, identifying this specific
	 *         ConceptualSchema.
	 */
	public String getName();

	/**
	 * Equal to ISO 19110 FC_FeatureCatalogue.fieldOfApplication ("description
	 * of kind(s) of use to which this feature catalogue may be put").
	 * 
	 * @return a Set of Strings, of which each identifies one of the uses that
	 *         this ConceptualSchema. TODO: Should be formalized.
	 */
	public Set<String> getApplicationFields();

	/**
	 * @return if this ConceptualSchema is a profile/specialization of another,
	 *         more generic profile, then this operation returns that
	 *         information model's Citation, including the name and other
	 *         Identifiers.
	 */
	public Citation getParentInformationModel();

	/**
	 * @return a Set of all Concepts belonging to this ConceptualSchema.
	 *         Relations and Transitions are returned implicitly but not as
	 *         first-row elements in this Set.
	 */
	public Set<Concept> getConcepts();

	/**
	 * Equal to ISO 19110 FC_FeatureCatalogue.producer.
	 * 
	 * @return this ConceptualSchema's ResponsibleParty structure according to
	 *         ISO19115 including things like a name and identifier (both
	 *         required).
	 */
	public ResponsibleParty getProducer();

	/**
	 * Formalized version of ISO 19115 AccessConstraints, broadened to multiple
	 * uses within the HUMBOLDT Framework.
	 * 
	 * @return a Collection of AccessConstraints defining Restrictions on the
	 *         access and use of this ConceptualSchema.
	 */
	public Collection<AccessConstraint> getAccessConstraints();

	/**
	 * Equal to ISO 19110 FC_FeatureCatalogue.versionNumber and
	 * FC_FeatureCatalogue.versionDate.
	 * 
	 * @return the {@link VersionInformation} for this ConceptualSchema.
	 */
	public VersionInformation getVersionInformation();

}
