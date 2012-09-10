/*
 * HUMBOLDT: A Framework for Data Harmonistation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.constraints.portrayal.impl;

import java.io.Serializable;
import java.util.Set;

import eu.esdihumboldt.specification.mediator.constraints.Constraint.ConstraintSource;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.FeatureTypeStyle;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.UserStyle;

/**
 * A UserStyle allows user-defined styling and is semantically equivalent to a
 * WMS named style.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: UserStyle.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 * 
 */
public class UserStyleImpl implements UserStyle, Serializable {

	private String name;

	private String title;

	private String abstractUS;

	private boolean isDefault;

	private Set<FeatureTypeStyle> featureTypeStyle;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The unique identifier of the constraint int the database
	 */

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
	 * the {@link ConstraintSource} of this {@link SpatialConstraint}.
	 */
	private ConstraintSource constraintSource;

	/**
	 * @param constraintSource
	 *            the constraintSource to set
	 */
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

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
		// TODO Auto-generated method stub
		return this.satisfied;
	}

	/**
	 * @return the Uid that has been assigned to this SpatialConstraint.
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
	 * @param satisfied
	 */
	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the abstractUS
	 */
	public String getAbstract() {
		return abstractUS;
	}

	/**
	 * @param abstractUS
	 *            the abstractUS to set
	 */
	public void setAbstract(String abstractUS) {
		this.abstractUS = abstractUS;
	}

	/**
	 * @return the isDefault
	 */
	public boolean getDefault() {
		return isDefault;
	}

	/**
	 * @param isDefault
	 *            the isDefault to set
	 */
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * @return the featureTypeStyle
	 */
	public Set<FeatureTypeStyle> getFeatureTypeStyle() {
		return featureTypeStyle;
	}

	/**
	 * @param featureTypeStyle
	 *            the featureTypeStyle to set
	 */
	public void setFeatureTypeStyle(Set<FeatureTypeStyle> featureTypeStyle) {
		this.featureTypeStyle = featureTypeStyle;
	}
}
