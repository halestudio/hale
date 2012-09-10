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
import java.util.UUID;

import org.opengis.metadata.identification.Resolution;

import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.ResolutionConstraint;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;

/**
 * A ResolutionConstraint Interface allows access to the WindowElement, that
 * presents the size in pixels of the map the Context describes.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: ResolutionConstraint.java,v 1.2 2007-11-06 09:32:36 pitaeva Exp
 *          $
 * 
 */
public class ResolutionConstraintImpl implements ResolutionConstraint,
		Serializable {
	// Fields ..................................................................

	private UUID identifier;

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
	 * Resolution
	 */
	private Resolution resolution;

	/**
	 * Window width in pixel
	 */
	private int windowWidth;

	/**
	 * Window height in pixel
	 */
	private int windowHeight;

	/**
	 * the {@link ConstraintSource} of this {@link SpatialConstraint}.
	 */
	private ConstraintSource constraintSource;

	private boolean write = false;

	private boolean sharedConstraint = false;

	/**
	 * @return the Resolution Object, that is defined by ISO 19115
	 */
	public Resolution getResolution() {
		return this.resolution;
	}

	/**
	 * @param resolution
	 */
	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the height in pixels of the map.
	 */
	public int getWindowHeight() {
		return this.windowHeight;
	}

	/**
	 * @param windowHeight
	 *            in pixel
	 */
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}

	/**
	 * 
	 * @return the width in pixels of the map.
	 */
	public int getWindowWidth() {
		return this.windowWidth;
	}

	/**
	 * @param windowWidth
	 *            in pixel
	 */
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
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
