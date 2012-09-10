/*
 * HUMBOLDT: A Framework for Data Harmonization and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 *
 * For more information on the project, please refer to the this website:
 * http://www.esdi-humboldt.eu
 *
 * LICENSE: For information on the license under which this program is
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers;

import java.io.Serializable;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class contains a GeneralEnvelope-field, id-field and no-args
 * constractor, to enable Hibernate the persisting of Envelope2D-objects.
 * 
 * 
 * @author Anna Pitaev, LogicaCMG, Bernd Schneiders, LogicaCMG
 * @version $Id: GeneralEnvelopeImpl.java,v 1.3 2007-11-30 10:24:05 pitaeva Exp
 *          $
 */
public class GeneralEnvelopeImpl implements Serializable {
	private long id;
	private double minX;
	private double minY;
	private double maxX;
	private double maxY;
	// Ana Belen Anton
	private CoordinateReferenceSystem crs;

	/**
	 * no-args constructor to enable hibernate-mapping.
	 */
	public GeneralEnvelopeImpl() {
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
	 * @return the minX
	 */
	public double getMinX() {
		return this.minX;
	}

	/**
	 * @param minX
	 *            the minX to set
	 */
	public void setMinX(double minX) {
		this.minX = minX;
	}

	/**
	 * @return the minY
	 */
	public double getMinY() {
		return this.minY;
	}

	// Ana Belen Anton
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return this.crs;
	}

	/**
	 * @param minY
	 *            the minY to set
	 */
	public void setMinY(double minY) {
		this.minY = minY;
	}

	/**
	 * @return the maxX
	 */
	public double getMaxX() {
		return this.maxX;
	}

	/**
	 * @param maxX
	 *            the maxX to set
	 */
	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}

	/**
	 * @return the maxY
	 */
	public double getMaxY() {
		return this.maxY;
	}

	/**
	 * @param maxY
	 *            the maxY to set
	 */
	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	// Ana Belen Anton
	public void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) {
		this.crs = crs;
	}
}
