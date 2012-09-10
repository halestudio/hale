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
package eu.esdihumboldt.commons.mediator.constraints.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.opengis.style.Style;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.PortrayalConstraint;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.NamedLayer;
import eu.esdihumboldt.specification.mediator.constraints.portrayal.UserLayer;

/**
 * A PortrayalConstraint Interface allows access to the Style Inforamtion
 * details like:
 * <ul>
 * <li>NamedStyleDescription,</li>
 * <li>UserStyleDescription.</li>
 * 
 * </ul>
 * 
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: PortrayalConstraint.java,v 1.2 2007-11-06 09:32:36 pitaeva Exp
 *          $
 * 
 */
public class PortrayalConstraintImpl implements PortrayalConstraint,
		Serializable {

	private List<Style> styles;

	private String name;

	private String title;

	private String abstractSLD;

	private Set<NamedLayer> namedLayer;

	private Set<UserLayer> userLayer;

	private UUID identifier;

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

	private boolean write = false;

	private boolean sharedConstraint = false;

	/**
	 * Allows access to the styles structure, if the StyledLayerDescriptor not
	 * used.
	 * 
	 * @return List of named styles.
	 */
	public List<Style> getStyle() {
		return this.styles;
	}

	/**
	 * @param styles
	 */
	public void setStyle(List<Style> styles) {
		this.styles = styles;
	}

	/**
	 * @return the Name, that is an optional element of the SLD.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the Title, that is an optional element of the SLD.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the Abstract, that is an optional element of the SLD.
	 */
	public String getAbstract() {
		return this.abstractSLD;
	}

	/**
	 * @param abstractSDL
	 */
	public void setAbstract(String abstractSDL) {
		this.abstractSLD = abstractSDL;
	}

	/**
	 * @return the List of the NamedLayer, defined for this SLD.
	 */
	public Set<NamedLayer> getNamedLayer() {
		return this.namedLayer;
	}

	/**
	 * @param namedLayer
	 */
	public void setNamedLayer(Set<NamedLayer> namedLayer) {
		this.namedLayer = namedLayer;
	}

	/**
	 * @return the List of the UserLayer, defined for this SLD.
	 */
	public Set<UserLayer> getUserLayer() {
		return this.userLayer;
	}

	/**
	 * @param userLayer
	 */
	public void setUserLayer(Set<UserLayer> userLayer) {
		this.userLayer = userLayer;
	}

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
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}

}
