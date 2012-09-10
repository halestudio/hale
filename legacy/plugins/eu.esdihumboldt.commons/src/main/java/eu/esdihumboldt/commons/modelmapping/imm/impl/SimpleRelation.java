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

import eu.esdihumboldt.specification.modelrepository.abstractfc.Relation;

/**
 * A very simple relation type that can be compared to an simple unspecified
 * associated in UML.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class SimpleRelation implements Relation, Serializable {

	// Fields ..................................................................

	private UUID uid;

	private String description;

	private String name;

	// Constructors ............................................................

	/**
	 * Protected no-args Constructor for Hibernate etc.
	 */
	protected SimpleRelation() {
		super();
		this.uid = UUID.randomUUID();
	}

	/**
	 * Default constructor with minimum natural-language information.
	 * 
	 * @param description
	 *            the String describing the nature of this {@link Relation}.
	 * @param name
	 *            the name of this relation (in UML, this is also called the
	 *            role name)
	 */
	public SimpleRelation(String description, String name) {
		this();
		this.description = description;
		this.name = name;
	}

	// Relation operations .....................................................

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Relation#getDescription()
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @see eu.esdihumboldt.specification.modelrepository.abstractfc.Relation#getName()
	 */
	public String getName() {
		return this.name;
	}

	// SimpleRelation operations ...............................................

	/**
	 * @return the uid
	 */
	public UUID getUid() {
		return uid;
	}

}
