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

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint;
import eu.esdihumboldt.specification.util.IdentifierManager;

/**
 * This constraint allows to define a metadata item to test against. It is
 * {@link Serializable}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: MetadataConstraintImpl.java,v 1.7 2007-11-27 13:30:32 pitaeva
 *          Exp $
 */
public class MetadataConstraintImpl implements MetadataConstraint, Serializable {

	// Fields ..................................................................
	private UUID identifier;

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;

	/**
	 * The unique identifier in the current VM.
	 */
	private long uid;

	/**
	 * The List storing the list of locale objects defining the language
	 * constraint.
	 */
	private MetadataType metadatatype;

	/**
	 * The RelationType declared for the MetadataType and the constraintvalue.
	 */
	private RelationType relationtype;

	/**
	 * The actual value to test the metadatatype for.
	 */
	private Object constraintvalue;

	/**
	 * the {@link ConstraintSource} of this {@link LanguageConstraint}.
	 */
	private ConstraintSource constraintSource;

	/**
	 * unique identifier in the database.
	 * 
	 */
	private long id;

	private boolean write = false;

	private List<String> keywords_ = new ArrayList<String>();

	private boolean sharedConstraint = false;

	// Constructors ............................................................
	/**
	 * no-args constructor, to enable hibernate-mapping. need to be public,
	 * because of Castor-requirements.
	 */
	public MetadataConstraintImpl() {
		// TODO: discuss the default value for constraint
		// this.constraintvalue = new Object();
		// sets default relation type.
		// this.relationtype = RelationType.equals;
		// set default metadata-type
		// TODO: discuss the default value for the metadata-type.
		// this.metadatatype = MetadataType.Adress;
		// this.uid = IdentifierManager.next();
		// this.constraintSource = ConstraintSource.parameter;
		// this.satisfied = true;
	}

	public MetadataConstraintImpl(MetadataType metadatatype,
			List<String> keywords, RelationType relationtype) {
		this.metadatatype = metadatatype;
		this.relationtype = relationtype;
		this.keywords_ = keywords;
		this.uid = IdentifierManager.next();
		this.constraintSource = ConstraintSource.parameter;

	}

	/**
	 * The default constructor.
	 * 
	 * @param _metadatatype
	 *            the MetadataType, i.e. field of metadata for which this
	 *            constraint is to be valid.
	 * @param _reltype
	 *            the RelationType that has to be satisfied between
	 *            _metadatatype and _constraintvalue.
	 * @param _constraintvalue
	 *            the query value to test against.
	 */
	public MetadataConstraintImpl(MetadataType _metadatatype,
			RelationType _reltype, Object _constraintvalue) {
		this.constraintvalue = _constraintvalue;
		this.relationtype = _reltype;
		this.metadatatype = _metadatatype;
		this.uid = IdentifierManager.next();
		this.constraintSource = ConstraintSource.parameter;
		this.keywords_.add((String) _constraintvalue);

	}

	// MetadataConstraint operations ...........................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint#getConstraintValue()
	 */
	public Object getConstraintValue() {
		return this.constraintvalue;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint#getMetadataType()
	 */
	public MetadataType getMetadataType() {
		return this.metadatatype;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.MetadataConstraint#getRelationType()
	 */
	public RelationType getRelationType() {
		return this.relationtype;
	}

	// Operations implemented from Constraint ..................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		// TODO: return actual satisfaction.
		return this.satisfied;
	}

	// Other operations ........................................................
	/**
	 * @return the Uid that has been assigned to this LanguageConstraint.
	 */
	public long getUid() {
		return this.uid;
	}

	/**
	 * @return unique identifier for the database.
	 */
	public long getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the metadatatype
	 */
	@SuppressWarnings("unused")
	public MetadataType getMetadatatype() {
		return metadatatype;
	}

	public List<String> getKeywords() {
		return this.keywords_;
	}

	/**
	 * @param metadatatype
	 *            the metadatatype to set
	 */
	@SuppressWarnings("unused")
	public void setMetadatatype(MetadataType metadatatype) {
		this.metadatatype = metadatatype;
	}

	/**
	 * @return the relationtype
	 */
	@SuppressWarnings("unused")
	public RelationType getRelationtype() {
		return relationtype;
	}

	/**
	 * @param relationtype
	 *            the relationtype to set
	 */
	@SuppressWarnings("unused")
	public void setRelationtype(RelationType relationtype) {
		this.relationtype = relationtype;
	}

	/**
	 * @return the constraintvalue
	 */
	@SuppressWarnings("unused")
	public Object getConstraintvalue() {
		return constraintvalue;
	}

	/**
	 * @param constraintvalue
	 *            the constraintvalue to set
	 */
	@SuppressWarnings("unused")
	public void setConstraintvalue(Object constraintvalue) {
		this.constraintvalue = constraintvalue;
	}

	/**
	 * @param satisfied
	 *            the satisfied to set
	 */
	@SuppressWarnings("unused")
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	/**
	 * @param constraintSource
	 *            the constraintSource to set
	 */
	@SuppressWarnings("unused")
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public boolean isFinalized() {
		return this.write;
	}

	public void setFinalized(boolean write) {
		this.write = write;
	}

	public boolean compatible(Constraint constraint) {

		if (constraint == null || (constraint instanceof MetadataConstraint)) {
			return false;
		}
		MetadataConstraint metadataConstraint = (MetadataConstraint) constraint;
		boolean isSameType = this.metadatatype.equals(metadataConstraint
				.getMetadataType());
		boolean isSameRelation = this.relationtype.equals(metadataConstraint
				.getRelationType());
		boolean isSameValue = this.constraintvalue.equals(metadataConstraint
				.getConstraintValue());
		if (isSameValue && isSameRelation && isSameType) {
			return true;
		} else {
			return false;
		}

	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}

}
