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

import org.geotools.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.referencing.ReferenceIdentifier;

import eu.esdihumboldt.commons.mediator.contextservice.hibernate.helpers.GeneralEnvelopeImpl;
import eu.esdihumboldt.specification.annotations.concurrency.Immutable;
import eu.esdihumboldt.specification.mediator.constraints.Constraint;
import eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint;
import eu.esdihumboldt.specification.util.IdentifierManager;

/**
 * Prototype Immplementation of the SpatialConstraint that is {@link Immutable}
 * and {@link Serializable}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id: SpatialConstraintImpl.java,v 1.13 2007-12-03 09:02:31 pitaeva
 *          Exp $
 */
public class SpatialConstraintImpl implements SpatialConstraint, Serializable {

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
	 * The Envelope used to test for the RelationType.
	 */
	private Envelope envelope;

	private Geometry geometry;

	/**
	 * Name of the property which the operator should be applied
	 */
	private String propertyName;

	/**
	 * The envelope, that should be stored to the database
	 */
	private GeneralEnvelopeImpl dbEnvelope;

	/**
	 * The Geometry used to test for the RelationType.
	 */
	private RelationType relationType;

	/**
	 * the {@link ConstraintSource} of this {@link SpatialConstraint}.
	 */
	private ConstraintSource constraintSource;

	private double bufferDistance;

	private boolean write;

	private boolean finalized;

	private boolean sharedConstraint = false;

	// Constructors ............................................................
	public double getBufferDistance() {
		return bufferDistance;
	}

	public void setBufferDistance(double bufferDistance) {
		this.bufferDistance = bufferDistance;
	}

	/**
	 * 
	 * no-args constructor need to be public, because of Castor-requirements.
	 */
	public SpatialConstraintImpl() {
		// this.uid = IdentifierManager.next();
		// this.constraintSource = ConstraintSource.parameter;
		// // sets default constraint type to any
		// this.relationType = RelationType.any;
		// //TODO: define default envelope
		// this.envelope = null;
		// this.satisfied = true;
	}

	/**
	 * @param _envelope
	 *            the Envelope being used as a query area.
	 * @param _relation_type
	 *            the RelationType that objects satisfying this Constraint have
	 *            to match in relation to _geometry.
	 */
	public SpatialConstraintImpl(Envelope _envelope, RelationType _relation_type) {
		this.uid = IdentifierManager.next();
		this.envelope = _envelope;
		this.relationType = _relation_type;
		this.constraintSource = ConstraintSource.parameter;
		this.satisfied = false;

		this.dbEnvelope = new GeneralEnvelopeImpl();
		this.dbEnvelope.setMinX(this.envelope.getMinimum(0));
		this.dbEnvelope.setMinY(this.envelope.getMinimum(1));
		this.dbEnvelope.setMaxX(this.envelope.getMaximum(0));
		this.dbEnvelope.setMaxY(this.envelope.getMaximum(1));
	}

	/**
	 * @param _envelope
	 *            the Envelope being used as a query area.
	 * @param _relation_type
	 *            the RelationType that objects satisfying this Constraint have
	 *            to match in relation to _geometry.
	 */
	public SpatialConstraintImpl(GeneralEnvelopeImpl _envelope,
			RelationType _relation_type) {
		this.uid = IdentifierManager.next();
		this.dbEnvelope = _envelope;
		this.relationType = _relation_type;
		this.constraintSource = ConstraintSource.parameter;
		this.satisfied = false;

		double min[] = new double[2];
		min[0] = this.dbEnvelope.getMinX();
		min[1] = this.dbEnvelope.getMinY();

		double max[] = new double[2];
		max[0] = this.dbEnvelope.getMaxX();
		max[1] = this.dbEnvelope.getMaxY();

		this.envelope = new GeneralEnvelope(min, max);
		this.envelope.getClass();
	}

	// SpatialConstraint operations ............................................
	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint#getGeometry()
	 */
	public Envelope getEnvelope() {
		if (envelope == null) {
			double min[] = new double[2];
			min[0] = this.dbEnvelope.getMinX();
			min[1] = this.dbEnvelope.getMinY();

			double max[] = new double[2];
			max[0] = this.dbEnvelope.getMaxX();
			max[1] = this.dbEnvelope.getMaxY();

			envelope = new GeneralEnvelope(min, max);
		}
		return envelope;
	}

	/**
	 * @see eu.esdihumboldt.specification.mediator.constraints.SpatialConstraint#getRelationType()
	 */
	public RelationType getRelationType() {
		return this.relationType;
	}

	// Constraint operations ...................................................
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

	// Other operations ........................................................
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

	@SuppressWarnings("unused")
	public void setRelationType(RelationType relationType) {
		this.relationType = relationType;
	}

	@SuppressWarnings("unused")
	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	@SuppressWarnings("unused")
	public void setConstraintSource(ConstraintSource constraintSource) {
		this.constraintSource = constraintSource;
	}

	/**
	 * @return the dbEnvelope
	 */
	public GeneralEnvelopeImpl getDbEnvelope() {
		return dbEnvelope;
	}

	/**
	 * @param dbEnvelope
	 *            the dbEnvelope to set
	 */
	public void setDbEnvelope(GeneralEnvelopeImpl dbEnvelope) {
		this.dbEnvelope = dbEnvelope;
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID identifier) {
		this.identifier = identifier;
	}

	public boolean isWriteProteted() {
		return this.write;
	}

	/**
	 * 
	 * @param write
	 */
	public void setWriteProtected(boolean write) {
		this.write = write;
	}

	public boolean compatible(Constraint constraint) {
		// @ TODO Re-Implement this

		boolean sameCRS = this
				.getGeometry()
				.getCoordinateReferenceSystem()
				.equals(((SpatialConstraint) constraint).getGeometry()
						.getCoordinateReferenceSystem());
		if (sameCRS) {
			return true;
		} else {
			return false;
		}
	}

	public org.opengis.geometry.Geometry getGeometry() {
		return geometry;
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void setFinalized(boolean write) {
		this.finalized = write;
	}

	/**
	 * This is a convinient method to find a string value of the CRS defined by
	 * this constraint
	 * 
	 * @return the EPSG code of the CRS defined by this constraint
	 */
	public String getReferenceSystem() {
		String refSys = null;
		for (Object object : this.getDbEnvelope()
				.getCoordinateReferenceSystem().getCoordinateSystem()
				.getIdentifiers()) {
			ReferenceIdentifier ref = (ReferenceIdentifier) object;
			System.out.println("The ESPG CODE: " + ref.getCode());
			System.out.println(ref.getCodeSpace());
			refSys = ref.getCode();
		}
		return refSys;
	}

	public void setShared(boolean shared) {
		this.sharedConstraint = shared;
	}

	public boolean isShared() {
		return sharedConstraint;
	}
}
