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
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.DBLocale;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint;
import eu.esdihumboldt.specification.util.IdentifierManager;

/**
 * Default implementation of a LanguageConstraint which is {@link Serializable}.
 * 
 * @author Thorsten Reitz
 * @version $Id: LanguageConstraintImpl.java,v 1.9 2007-12-17 15:12:08 pitaeva
 *          Exp $
 */
public class LanguageConstraintImpl implements LanguageConstraint, Serializable {

	// Fields ..................................................................
	private UUID identifier;
	/**
     *
     */
	private static final long serialVersionUID = 1L;
	/** The unique constraint identifire in the database */
	private long id;
	/**
	 * The status of this constraint.
	 */
	private boolean satisfied = false;
	/**
	 * The unique identifier in the current VM.
	 */
	private long uid;
	/**
	 * The List storing the {@link Locale} objects defining the language
	 * constraint.
	 */
	private List<String> locales;
	/**
	 * This stores locale in its position in the list, to store its then in the
	 * database.
	 */
	private Set<DBLocale> db_locales;
	/**
	 * the {@link ConstraintSource} of this {@link LanguageConstraint}.
	 */
	private ConstraintSource constraintSource;
	private boolean write = false;

	private boolean sharedConstraint = false;

	// Constructors ............................................................
	/**
	 * 
	 * no-args constructor for hibernate need to be public, because of
	 * Castor-requirements.
	 */
	public LanguageConstraintImpl() {
		// this.uid = IdentifierManager.next();
		// this.locales = new HashSet<Locale>();
		// this.constraintSource = ConstraintSource.parameter;
		// this.satisfied = true;
	}

	/**
	 * Use this constructor to specify multiple {@link Locale} objects to be
	 * accepted.
	 * 
	 * @param _locales
	 *            the priorized List of {@link Locale} objects to be used as
	 *            Constraint.
	 */
	public LanguageConstraintImpl(List<String> _locales) {
		this.uid = IdentifierManager.next();
		this.locales = _locales;
		this.constraintSource = ConstraintSource.parameter;
		this.satisfied = false;
	}

	/**
	 * Default constructor with just one {@link Locale}.
	 * 
	 * @param _locale
	 *            the {@link Locale} to be used as Constraint.
	 */
	public LanguageConstraintImpl(String _locale) {
		this.uid = IdentifierManager.next();
		this.locales = new ArrayList<String>();
		this.locales.add(_locale);
		this.constraintSource = ConstraintSource.parameter;

	}

	/**
	 * Constructor with one {@link Locale} that also allows specification of an
	 * identifier.
	 * 
	 * @param _locale
	 *            the {@link Locale} to be used as Constraint.
	 * @param _constraintSource
	 *            the ConstraintSource from which this Constraint stems.
	 */
	public LanguageConstraintImpl(ConstraintSource _constraintSource,
			String _locale) {
		this.uid = IdentifierManager.next();
		this.locales = new ArrayList<String>();
		this.locales.add(_locale);
		this.constraintSource = _constraintSource;

	}

	// Operations implemented from LanguageConstraint ..........................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.LanguageConstraint#getLanguageCodes()
	 */
	public List<String> getLanguageCodes() {
		return this.locales;
	}

	// Operations implemented from Constraint ..................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#isSatisfied()
	 */
	public boolean isSatisfied() {
		return this.satisfied;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.Constraint#getConstraintSource()
	 */
	public ConstraintSource getConstraintSource() {
		return this.constraintSource;
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
	 * @param id
	 *            unique identifier for the database.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the locales
	 */
	@SuppressWarnings("unused")
	public List<String> getLocales() {
		return locales;
	}

	/**
	 * @param locales
	 *            the locales to set
	 */
	@SuppressWarnings("unused")
	public void setLocales(List<String> locales) {
		this.locales = locales;
	}

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

	/**
	 * @return the db_locales
	 */
	public Set<DBLocale> getDb_locales() {
		return db_locales;
	}

	/**
	 * @param db_locales
	 *            the db_locales to set
	 */
	public void setDb_locales(Set<DBLocale> db_locales) {
		this.db_locales = db_locales;
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

		if (constraint.getClass() == this.getClass()) {
			LanguageConstraintImpl lcTarget = (LanguageConstraintImpl) constraint;
			// Check if the at least one source language is defined in the
			// target
			if (this.getLocales().equals(lcTarget.getLocales())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public Constraint getCompatibleLocales(Constraint constraint) {
		List<String> compatiblelocales = new ArrayList<String>();
		if (constraint.getClass() == this.getClass()) {

			LanguageConstraintImpl lcTarget = (LanguageConstraintImpl) constraint;
			// Check if the at least one source language is defined in the
			// target
			for (String language : this.getLocales()) {
				if (lcTarget.getLocales().contains(language)) {
					compatiblelocales.add(language);
				}
			}
		}
		return new LanguageConstraintImpl(compatiblelocales);
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}
