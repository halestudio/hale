package eu.esdihumboldt.commons.modelmapping.imm.impl;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.opengis.metadata.citation.Citation;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.ResponsiblePartyHelper;
import eu.esdihumboldt.specification.modelrepository.abstractfc.AccessConstraint;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;
import eu.esdihumboldt.specification.modelrepository.abstractfc.VersionInformation;

/**
 * Prototype Implementation of the InformationModel.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: PrototypeInformationModel.java,v 1.6 2007-12-06 13:20:24
 *          pitaeva Exp $
 */
public class PrototypeInformationModel implements ConceptualSchema,
		Serializable {

	// Fields ..................................................................

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Set<AccessConstraint> accessConstraints = null;

	private Set<String> applicationFields = null;

	private Set<Concept> concepts;

	private String name;
	private eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper dbName;

	private Citation parentIMcitation = null;

	private Set<eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper> dbParentIMcitation;

	private ResponsiblePartyImpl producer = null;

	private Set<ResponsiblePartyHelper> dbProducer;

	private VersionInformation versionInformation = null;
	private Set<VersionInformation> dbVersionInformation;

	private long id;

	private URI uri;

	// Constructors ............................................................

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            unique identifier for tne database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Default no-args constructor must be public (castor).
	 */
	public PrototypeInformationModel() {
		super();
		// TODO: discuss default constructoe
	}

	/**
	 * Default full constructor.
	 */
	public PrototypeInformationModel(Set<AccessConstraint> accessConstraints,
			Set<String> applicationFields, Set<Concept> concepts, String name,
			Citation parentIMcitation, ResponsiblePartyImpl producer,
			VersionInformation versionInformation) {
		this(name, concepts);
		this.accessConstraints = accessConstraints;
		this.applicationFields = applicationFields;
		this.concepts = concepts;
		this.name = name;
		this.parentIMcitation = parentIMcitation;
		this.producer = producer;
		this.versionInformation = versionInformation;
	}

	/**
	 * This should be the default constructor.
	 */
	public PrototypeInformationModel(String _name, Set<Concept> _concepts) {
		super();
		this.name = _name;
		this.concepts = _concepts;
	}

	/**
	 * Default minimum constructor - a name must always be given.
	 */
	public PrototypeInformationModel(String _name) {
		super();
		this.name = _name;
		this.concepts = new HashSet<Concept>();
	}

	// InformationModel methods ................................................

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getAccessConstraints()
	 */
	public Set<AccessConstraint> getAccessConstraints() {
		return this.accessConstraints;
	}

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getApplicationFields()
	 */
	public Set<String> getApplicationFields() {
		return this.applicationFields;
	}

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getConcepts()
	 */
	public Set<Concept> getConcepts() {
		return this.concepts;
	}

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getName()
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getParentInformationModel()
	 */
	public Citation getParentIMcitation() {
		return this.parentIMcitation;
	}

	/**
	 * @see eu.esdihumboldt.modelrepository.abstractfc.InformationModel#getVersionInformation()
	 */
	public VersionInformation getVersionInformation() {
		return this.versionInformation;
	}

	/**
	 * @param applicationFields
	 */
	public void setApplicationFields(Set<String> applicationFields) {
		this.applicationFields = applicationFields;
	}

	/**
	 * @param concepts
	 */
	public void setConcepts(Set<Concept> concepts) {
		this.concepts = concepts;
	}

	/**
	 * @param parentIMcitation
	 */
	public void setParentIMcitation(Citation parentIMcitation) {
		this.parentIMcitation = parentIMcitation;
	}

	/**
	 * @param accessConstraints
	 */
	public void setAccessConstraints(Set<AccessConstraint> accessConstraints) {
		this.accessConstraints = accessConstraints;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param versionInformation
	 */
	public void setVersionInformation(VersionInformation versionInformation) {
		this.versionInformation = versionInformation;
	}

	public ResponsiblePartyImpl getProducer() {

		return this.producer;
	}

	/**
	 * @return the dbName
	 */
	public eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(
			eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the dbParentIMcitation
	 */
	public Set<eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper> getDbParentIMcitation() {
		return dbParentIMcitation;
	}

	/**
	 * @param dbParentIMcitation
	 *            the dbParentIMcitation to set
	 */
	public void setDbParentIMcitation(
			Set<eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.CitationHelper> dbParentIMcitation) {
		this.dbParentIMcitation = dbParentIMcitation;
	}

	public Citation getParentInformationModel() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param producer
	 *            the producer to set
	 */
	public void setProducer(ResponsiblePartyImpl producer) {
		this.producer = producer;
	}

	/**
	 * @return the dbProducer
	 */
	public Set<ResponsiblePartyHelper> getDbProducer() {
		return dbProducer;
	}

	/**
	 * @param dbProducer
	 *            the dbProducer to set
	 */
	public void setDbProducer(Set<ResponsiblePartyHelper> dbProducer) {
		this.dbProducer = dbProducer;
	}

	/**
	 * @return the dbVersionInformation
	 */
	public Set<VersionInformation> getDbVersionInformation() {
		return dbVersionInformation;
	}

	/**
	 * @param dbVersionInformation
	 *            the dbVersionInformation to set
	 */
	public void setDbVersionInformation(
			Set<VersionInformation> dbVersionInformation) {
		this.dbVersionInformation = dbVersionInformation;
	}

	public void setIdentifier(URI uri) {
		this.uri = uri;
	}

	public URI getIdentifier() {
		return this.uri;
	}

}
