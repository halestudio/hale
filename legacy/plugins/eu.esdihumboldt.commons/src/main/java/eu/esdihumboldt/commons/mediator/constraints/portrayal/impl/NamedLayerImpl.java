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
import eu.esdihumboldt.specification.mediator.constraints.portrayal.NamedLayer;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.NamedStyle;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.UserStyle;

/**
 * A NamedLayer describes a layer of data that has a name advertised by a WMS.
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: NamedLayer.java,v 1.2 2007-11-06 09:32:37 pitaeva Exp $
 * 
 */
public class NamedLayerImpl implements NamedLayer, Serializable {

	private String name;

	private Set<UserStyle> userStyle;

	private Set<NamedStyle> namedStyle;

	private LayerFeatureConstraintImpl layerFeatureConstraint;

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
	 * @return the userSyle
	 */
	public Set<UserStyle> getUserStyle() {
		return userStyle;
	}

	/**
	 * @param userSyle
	 *            the userSyle to set
	 */
	public void setUserStyle(Set<UserStyle> userSyle) {
		this.userStyle = userSyle;
	}

	/**
	 * @return the namedStyle
	 */
	public Set<NamedStyle> getNamedStyle() {
		return namedStyle;
	}

	/**
	 * @param namedStyle
	 *            the namedStyle to set
	 */
	public void setNamedStyle(Set<NamedStyle> namedStyle) {
		this.namedStyle = namedStyle;
	}

	/**
	 * @return the layerFeatureConstraint
	 */
	public LayerFeatureConstraintImpl getLayerFeatureConstraint() {
		return layerFeatureConstraint;
	}

	/**
	 * @param layerFeatureConstraint
	 *            the layerFeatureConstraint to set
	 */
	public void setLayerFeatureConstraint(
			LayerFeatureConstraintImpl layerFeatureConstraint) {
		this.layerFeatureConstraint = layerFeatureConstraint;
	}

}
