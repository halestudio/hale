/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
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
import java.util.UUID;

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty;

/**
 * A standard implementation of the ConceptProperty, which is Serializable.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: PrototypeFeatureProperty.java,v 1.4 2007-12-06 13:20:25 pitaeva
 *          Exp $
 */
public class PrototypeFeatureProperty implements ConceptProperty, Serializable {

	// Fields ..................................................................

	private long id;

	private UUID uid;

	private Cardinality cardinality;

	private String definition;

	private String localName;

	// Constructors ............................................................

	/**
	 * Protected no-args Constructor for Hibernate etc.
	 */
	public PrototypeFeatureProperty() {
		super();
		this.uid = UUID.randomUUID();
	}

	/**
	 * Default Constructor.
	 * 
	 * @param cardinality
	 *            the {@link Cardinality} that this {@link ConceptProperty} has
	 *            in relation to the {@link Concept} it is associated to.
	 * @param definition
	 *            {@link ConceptProperty#getDefinition()}
	 * @param localName
	 *            {@link ConceptProperty#getLocalName()}
	 */
	protected PrototypeFeatureProperty(Cardinality cardinality,
			String definition, String localName) {
		super();
		this.uid = UUID.randomUUID();
		this.cardinality = cardinality;
		this.definition = definition;
		this.localName = localName;
	}

	// ConceptProperty operations ..............................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty#getCardinality()
	 */
	public Cardinality getCardinality() {
		return this.cardinality;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty#getDefinition()
	 */
	public String getDefinition() {
		return this.definition;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty#getLocalName()
	 */
	public String getLocalName() {
		return this.localName;
	}

	// PrototypeFeatureProperty operations .....................................

	/**
	 * @return the uid
	 */
	public UUID getUid() {
		return uid;
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
	 * @param uid
	 *            the uid to set
	 */
	@SuppressWarnings("unused")
	private void setUid(UUID uid) {
		this.uid = uid;
	}

	/**
	 * @param cardinality
	 *            the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}

	/**
	 * @param definition
	 *            the definition to set
	 */
	public void setDefinition(String definition) {
		this.definition = definition;
	}

	/**
	 * @param localName
	 *            the localName to set
	 */
	public void setLocalName(String localName) {
		this.localName = localName;
	}

}
