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
package eu.esdihumboldt.commons.modelmapping.imm.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.geotools.metadata.iso.IdentifierImpl;
import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.identification.IdentificationImpl;
import org.geotools.util.SimpleInternationalString;
import org.opengis.metadata.identification.Identification;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.IdentificationHelper;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Relation;

/**
 * This is the default implementation of a {@link Concept} for the Prototype.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: PrototypeFeatureType.java,v 1.6 2007-12-06 13:20:24 pitaeva Exp
 *          $
 */
public class PrototypeFeatureType implements Concept, Serializable {

	// Fields ..................................................................

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// can conatain 0..1 identification
	private Identification identification;
	private Set<IdentificationHelper> dbIdentification;

	private String informal_defintion;

	private ConceptualSchema feature_catalogue;

	private Set<ConceptProperty> properties;

	private Set<Relation> relations;

	private boolean is_abstract = false;

	private long id;

	private URI uri;

	private String typeName;

	// Constructors ............................................................

	/**
	 * 
	 * no-args constructor for Hibernate
	 */

	public PrototypeFeatureType() {
		super();

	}

	/**
	 * Minimum constructor - at least a single language type name has always to
	 * be given.
	 */
	public PrototypeFeatureType(String _typeName) {
		super();
		this.typeName = _typeName;

		// Build a full Citation and Identifier.
		CitationImpl ci = new CitationImpl();
		ci.setTitle(new SimpleInternationalString(_typeName));

		// create unique identifier and IdentifierType for Citation.
		List<IdentifierImpl> identifiers = new ArrayList<IdentifierImpl>();
		UUID uuid = UUID.randomUUID();
		IdentifierImpl identifier = new IdentifierImpl(uuid.toString());
		identifiers.add(identifier);
		ci.setIdentifiers(identifiers);
		/*
		 * List<String> identifierTypes = new ArrayList<String>();
		 * identifierTypes.add(uuid.getClass().getName());
		 */
		// ci.setIdentifierTypes(identifierTypes);

		// assign created Citation(Identification to this FT.
		IdentificationImpl ii = new IdentificationImpl();
		ii.setCitation(ci);
		this.identification = ii;
	}

	/**
	 * Default constructor.
	 */
	public PrototypeFeatureType(String _typeName, ConceptualSchema _belongsTo,
			boolean _is_abstract) {
		this(_typeName);
		this.feature_catalogue = _belongsTo;
		this.is_abstract = _is_abstract;
	}

	/**
	 * Full constructor.
	 * 
	 * @param identification
	 * @param informal_defintion
	 * @param feature_catalogue
	 * @param properties
	 * @param relations
	 * @param is_abstract
	 */
	public PrototypeFeatureType(Identification identification,
			String informal_defintion, ConceptualSchema feature_catalogue,
			Set<ConceptProperty> properties, Set<Relation> relations,
			boolean is_abstract) {
		super();
		// this.identification = identification;
		this.informal_defintion = informal_defintion;
		this.feature_catalogue = feature_catalogue;
		this.properties = properties;
		this.relations = relations;
		this.is_abstract = is_abstract;
	}

	// Concept Operation implementations .......................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getIdentification()
	 */
	public Identification getIdentification() {
		return this.identification;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getInformalDefinition()
	 */
	public String getInformalDefinition() {
		return this.informal_defintion;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getInformationModel()
	 */
	public ConceptualSchema getInformationModel() {
		return this.feature_catalogue;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getProperties()
	 */
	public Set<ConceptProperty> getProperties() {
		return this.properties;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getRelations()
	 */
	public Set<Relation> getRelations() {
		return this.relations;
	}

	/**
	 * This implementation will always use the default {@link Locale} defined
	 * for the VM it runs in if localized type names are available.
	 * 
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#getTypeName()
	 */
	public String getTypeName() {
		// TODO: Is this correct??
		// return this.identification.getCitation().getTitle().toString();

		return this.typeName;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Concept#isAbstract()
	 */
	public boolean isAbstract() {
		return this.is_abstract;
	}

	// other Operations ........................................................

	/**
	 * @param _concept
	 *            a {@link ConceptProperty} to add to this
	 *            {@link PrototypeFeatureType}.
	 */
	public void addProperty(ConceptProperty _concept) {
		this.properties.add(_concept);
	}

	/**
	 * 
	 * @param _relation
	 *            a {@link Relation} to add to this {@link PrototypeFeatureType}
	 *            .
	 */
	public void addRelation(Relation _relation) {
		this.relations.add(_relation);
	}

	/**
	 * @param informal_defintion
	 *            the {@link String} that describes this
	 *            {@link PrototypeFeatureType} informally, i.e. in natural
	 *            language.
	 */
	public void setInformalDefintion(String informal_defintion) {
		this.informal_defintion = informal_defintion;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the informal_defintion
	 */
	public String getInformal_defintion() {
		return informal_defintion;
	}

	/**
	 * @param informal_defintion
	 *            the informal_defintion to set
	 */
	public void setInformal_defintion(String informal_defintion) {
		this.informal_defintion = informal_defintion;
	}

	/**
	 * @return the feature_catalogue
	 */
	public ConceptualSchema getFeature_catalogue() {
		return feature_catalogue;
	}

	/**
	 * @param feature_catalogue
	 *            the feature_catalogue to set
	 */
	public void setFeature_catalogue(ConceptualSchema feature_catalogue) {
		this.feature_catalogue = feature_catalogue;
	}

	/**
	 * @return the is_abstract
	 */
	public boolean isIs_abstract() {
		return is_abstract;
	}

	/**
	 * @param is_abstract
	 *            the is_abstract to set
	 */
	public void setIs_abstract(boolean is_abstract) {
		this.is_abstract = is_abstract;
	}

	/**
	 * @param identification
	 *            the identification to set
	 */
	public void setIdentification(Identification identification) {
		this.identification = identification;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Set<ConceptProperty> properties) {
		this.properties = properties;
	}

	/**
	 * @param relations
	 *            the relations to set
	 */
	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}

	/**
	 * @return the dbIdentification
	 */
	public Set<IdentificationHelper> getDbIdentification() {
		return dbIdentification;
	}

	/**
	 * @param dbIdentification
	 *            the dbIdentification to set
	 */
	public void setDbIdentification(Set<IdentificationHelper> dbIdentification) {
		this.dbIdentification = dbIdentification;
	}

	public void setIdentifier(URI uri) {
		this.uri = uri;
	}

	public URI getIdentifier() {
		return this.uri;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
