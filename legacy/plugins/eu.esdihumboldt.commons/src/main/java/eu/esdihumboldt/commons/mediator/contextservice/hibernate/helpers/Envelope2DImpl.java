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

import org.geotools.geometry.Envelope2D;

import eu.esdihumboldt.specification.mediator.context.exceptions.InconsistentContextConstraintException;

/**
 * This class contains a Envelop2D-field, id-field and no-args constractor, to
 * enable Hibernate the persisting of Envelope2D-objects.
 * 
 * 
 * @author Anna Pitaev, Logica CMG
 * @version $Id: Envelope2DImpl.java,v 1.1 2007-11-12 12:12:14 pitaeva Exp $
 */
public class Envelope2DImpl {

	private long id;
	private Envelope2D envelope2D;

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
	 * no-args constructor to enable hibernate-mapping.
	 */
	public Envelope2DImpl() {
	}

	/**
	 * @return 2D-envelope.
	 */
	public Envelope2D getEnvelope2D() {

		return envelope2D;
	}

	/**
	 * @param envelope2D
	 *            .
	 * @throws InconsistentContextConstraintException
	 */
	public synchronized void setEnvelope2D(Envelope2D envelope2D)
			throws InconsistentContextConstraintException {
		// check that the data members needed have been set
		// if so set the method on Evelope2D

		if (this.envelope2D == null)
			throw new InconsistentContextConstraintException(
					"SpatialConstraint:bad Envelope");
		else if (this.envelope2D.height == 0 || this.envelope2D.width == 0)
			throw new InconsistentContextConstraintException(
					"SpatialConstraint:bad Envelope");
		else
			this.envelope2D.setFrame(this.envelope2D.x, this.envelope2D.y,
					this.envelope2D.width, this.envelope2D.height);

	}

}
