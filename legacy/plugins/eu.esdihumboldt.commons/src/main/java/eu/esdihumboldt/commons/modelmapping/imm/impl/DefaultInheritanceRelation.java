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

import eu.esdihumboldt.specification.modelrepository.abstractfc.Concept;
import eu.esdihumboldt.specification.modelrepository.abstractfc.InheritanceRelation;
import eu.esdihumboldt.specification.modelrepository.abstractfc.Relation;

/**
 * A simple, Serializable implementation of {@link InheritanceRelation}. Note
 * that this implementation does not support multiple inheritance via a single
 * {@link Relation}. Note that the implementation does not enforce
 * uniqueInstance by it's own.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class DefaultInheritanceRelation extends SimpleRelation implements
		InheritanceRelation, Serializable {

	// Fields ..................................................................

	private boolean uniqueInstance;

	private Concept supertype;

	private Concept subtype;

	// Constructors ............................................................

	/**
	 * Protected no-args Constructor for Hibernate etc.
	 */
	protected DefaultInheritanceRelation() {
		super();
	}

	/**
	 * Minimum constructor, just establishing a inheritance relation that is not
	 * named or described and is assumed to be non-exclusive.
	 * 
	 * @param supertype
	 *            the {@link Concept} that will be the supertype.
	 * @param subtype
	 *            the {@link Concept} that will be the subtype, i.e. inheriting
	 *            properties from the supertype.
	 */
	public DefaultInheritanceRelation(Concept supertype, Concept subtype) {
		super();
		this.supertype = supertype;
		this.subtype = subtype;
	}

	/**
	 * Full constructor.
	 * 
	 * @param description
	 *            the String describing the nature of this {@link Relation}.
	 * @param name
	 *            the name of this relation (in UML, this is also called the
	 *            role name)
	 * @param uniqueInstance
	 *            true if an instance of the supertype can be an instance of at
	 *            most one of its subtypes.
	 * @param supertype
	 *            the {@link Concept} that will be the supertype.
	 * @param subtype
	 *            the {@link Concept} that will be the subtype, i.e. inheriting
	 *            properties from the supertype.
	 */
	public DefaultInheritanceRelation(String description, String name,
			boolean uniqueInstance, Concept supertype, Concept subtype) {
		super(description, name);
		this.uniqueInstance = uniqueInstance;
		this.supertype = supertype;
		this.subtype = subtype;
	}

	//

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.InheritanceRelation#getSubtype()
	 */
	public Concept getSubtype() {
		return this.subtype;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.InheritanceRelation#getSupertype()
	 */
	public Concept getSupertype() {
		return this.supertype;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.InheritanceRelation#isUniqueInstance()
	 */
	public boolean isUniqueInstance() {
		return this.uniqueInstance;
	}

}
