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
import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.ConceptProperty;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Relation;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Transition;

/**
 * Simple Serializable implementation of a Transition, without too much logic on
 * it's own.
 * 
 * TODO: Think about whether a name in case of a Transtion should only be
 * accepted as a concept (i.e. derived from it's name and UUID or something
 * similar).
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class DefaultTransition extends SimpleRelation implements Transition,
		Serializable {

	// Fields ..................................................................

	private Concept representedConcept;

	private Map<String, ConceptProperty> inputReferences;

	// Constructors ............................................................

	/**
	 * Protected no-args Constructor for Hibernate etc.
	 */
	protected DefaultTransition() {
		super();
	}

	/**
	 * Minimum constructor based on one {@link ConceptProperty}. Note that in
	 * the case of a {@link Transition}, a name always has to be assigned,
	 * otherwise the matching strategy implementation cannot be found.
	 * 
	 * @param name
	 * @param representedConcept
	 * @param propertyName
	 * @param inputProperty
	 */
	public DefaultTransition(String name, Concept representedConcept,
			String propertyName, ConceptProperty inputProperty) {
		super(null, name);
		if (name == null || name.equals("")) {
			throw new NullPointerException("A name given to a Transition may "
					+ "not be null or empty.");
		}
		this.representedConcept = representedConcept;
		this.inputReferences = new HashMap<String, ConceptProperty>();
		this.inputReferences.put(propertyName, inputProperty);
	}

	/**
	 * Full constructor.
	 * 
	 * @param description
	 *            the String describing the nature of this {@link Relation}.
	 * @param name
	 *            the name of this relation (in UML, this is also called the
	 *            role name)
	 * @param representedConcept
	 *            the {@link Concept} created through this {@link Transition}.
	 * @param inputReferences
	 *            a Map of named references to {@link ConceptProperty} objects
	 */
	public DefaultTransition(String description, String name,
			Concept representedConcept,
			Map<String, ConceptProperty> inputReferences) {
		super(description, name);
		if (name == null || name.equals("")) {
			throw new NullPointerException("A name given to a Transition may "
					+ "not be null or empty.");
		}
		this.representedConcept = representedConcept;
		this.inputReferences = inputReferences;
	}

	// Transition operations ...................................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Transition#getInputReferences()
	 */
	public Map<String, ConceptProperty> getInputReferences() {
		return this.inputReferences;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Transition#getRepresentedConcept()
	 */
	public Concept getRepresentedConcept() {
		return this.representedConcept;
	}

}
