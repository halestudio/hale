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
package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import eu.esdihumboldt.commons.modelmapping.imm.impl.PrototypeInformationModel;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.ThematicConstraint;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptualSchema;

/**
 * This type implements the {@link ThematicConstraint} interface. It is
 * {@link Serializable}, as usual for a Constraint implementation.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: ThematicConstraintImpl.java,v 1.11 2007-12-17 15:34:13 pitaeva
 *          Exp $
 */
public class ThematicConstraintImpl implements ThematicConstraint {

	// Fields ..................................................................
	/**
	 * The internal storage for the ConceptualSchemas defining the
	 * ThematicConstraint.
	 */
	private List<ConceptualSchema> themes;
	/**
	 * the {@link TopicCode} of this {@link ThematicConstraint}.
	 */
	private TopicCode topic;
	/**
	 * the {@link ConstraintSource} of this {@link ThematicConstraint}.
	 */
	private ConstraintSource constraintSource;

	/**
	 * the {@link UUID} uniquely identifying this {@link ThematicConstraint}.
	 */
	private UUID identifier;

	/**
	 * The unique identifier for the database
	 * 
	 */
	private long uid;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;

	private boolean finalized = false;

	private boolean sharedConstraint = false;

	// Constructors ............................................................
	/**
	 * 
	 * protected default constructor for the hibernate and castor needs only
	 * must be public, because of Castor-requirements.
	 */
	public ThematicConstraintImpl() {
		this.themes = new ArrayList<ConceptualSchema>();
		this.constraintSource = ConstraintSource.parameter;
		// this.uid = UUID.randomUUID();
		// this.satisfied = true;
	}

	/**
	 * Use this Constructor to specify the topic to be used.
	 * 
	 * @param _topic
	 */
	public ThematicConstraintImpl(TopicCode _topic) {
		this.topic = _topic;
	}

	/**
	 * Use this Constructor to specify a single Concept to be used.
	 * 
	 * @param _concept
	 */
	public ThematicConstraintImpl(Concept _concept) {
		this.themes = new ArrayList<ConceptualSchema>();
		ConceptualSchema im = new PrototypeInformationModel(
				("transient:" + UUID.randomUUID().toString()));
		im.getConcepts().add(_concept);
		this.themes.add(im);
		this.constraintSource = ConstraintSource.parameter;
		this.identifier = UUID.randomUUID();
	}

	/**
	 * Use this Constructor to specify a single ConceptualSchema to be used.
	 * 
	 * @param _im
	 *            the ConceptualSchema to use.
	 */
	public ThematicConstraintImpl(ConceptualSchema _im) {
		this.themes = new ArrayList<ConceptualSchema>();
		this.themes.add(_im);
		this.constraintSource = ConstraintSource.parameter;
		this.identifier = UUID.randomUUID();
	}

	/**
	 * Use this Constructor to specify a single ConceptualSchema as well as a
	 * specific {@link ConstraintSource}.
	 * 
	 * @param _im
	 *            the ConceptualSchema to use.
	 * @param _constraintSource
	 *            the {@link ConstraintSource} that this
	 *            {@link ThematicConstraint} originates from.
	 */
	public ThematicConstraintImpl(ConceptualSchema _im,
			ConstraintSource _constraintSource) {
		this.themes = new ArrayList<ConceptualSchema>();
		this.themes.add(_im);
		this.constraintSource = _constraintSource;
		this.identifier = UUID.randomUUID();
		this.satisfied = false;
	}

	// ThematicConstraint operations ...........................................
	/**
	 * @return
	 * @see eu.esdihumboldt.specification.mediator.constraints.ThematicConstraint#getThemes()
	 */
	public List<ConceptualSchema> getThemes() {
		// return new ArrayList<ConceptualSchema>(this.themes);
		return this.themes;
	}

	/**
	 * @return
	 * @see eu.esdihumboldt.specification.mediator.constraints.ThematicConstraint#getTopic()
	 */
	public TopicCode getTopic() {
		return this.topic;
	}

	// Constraint operations ...................................................
	/**
	 * @return
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	/**
	 * @return
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		// TODO Auto-generated method stub
		return this.satisfied;
	}

	/**
	 * @return the UUID uniquely identifying this {@link ThematicConstraint}.
	 */
	public UUID getUid() {
		return this.identifier;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return uid;
	}

	/**
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.uid = id;
	}

	/**
	 * @param themes
	 *            the themes to set
	 */
	public void setThemes(List<ConceptualSchema> themes) {
		this.themes = themes;
	}

	/**
	 * @param topic
	 *            the topic to set (TopicCode as defined in ISO 19115)
	 */
	public void setTopic(TopicCode _topic) {
		this.topic = _topic;
	}

	/**
	 * @param constraintSource
	 *            the constraintSource to set
	 */
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(UUID uid) {
		this.identifier = uid;
	}

	public boolean isFinalized() {

		return this.finalized;
	}

	public void setFinalized(boolean write) {

		this.finalized = write;
	}

	public void setSatisfied(boolean satisfied) {

		this.satisfied = satisfied;
	}

	public UUID getIdentifier() {
		return this.identifier;
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public boolean compatible(Constraint constraint) {
		if (constraint instanceof ThematicConstraint) {
			for (ConceptualSchema conceptualSchema : ((ThematicConstraint) constraint)
					.getThemes()) {
				if (this.getThemes().contains(conceptualSchema)) {
					return true;
				}
			}

		}
		return false;

	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}
