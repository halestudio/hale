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
package eu.esdihumboldt.specification.mediator.constraints;

import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;

/**
 * A SpatialConstraint is used to express a region of interest. The most
 * commonly used SpatialRequest contains just a bounding box, but this interface
 * also allows for more complex geometry. It corresponds to the OGC Filter
 * Encoding for spatial operators.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public interface SpatialConstraint extends Constraint {

	/**
	 * This operation is used to return a simplified bounding box, as it is used
	 * with most standards. It corresponds to
	 * <code>&lt;xsd:complexType name="BBOXType"/&gt;</code>.
	 * 
	 * @return the {@link Envelope} (BoundingBox) defined as the spatial
	 *         constraint for a request. The Envelope needs to be complete, i.e.
	 *         it needs to have a CoordinateReferenceSystem defined. If
	 *         getGeometry() doesn't return <code>null</code>, this will return
	 *         the {@link Envelope} of this {@link Geometry}. The operation may
	 *         return <code>null</code> only if neither an {@link Envelope} nor
	 *         a {@link Geometry} have been set.
	 * 
	 */
	public Envelope getEnvelope();

	/**
	 * This operation can be used to return a more detailed description of a
	 * spatial constraint's reference area/space.
	 * 
	 * @return the {@link Geometry} defined as the spatial constraint for this a
	 *         request. If only an {@link Envelope} was defined, this will
	 *         return a rectangular polygon {@link Geometry} representing that
	 *         {@link Envelope}. The operation may return <code>null</code> only
	 *         if neither an {@link Envelope} nor a {@link Geometry} have been
	 *         set.
	 */
	public Geometry getGeometry();

	public String getPropertyName();

	/**
	 * Corresponds to &lt;xsd:element name="Distance"
	 * type="ogc:DistanceType"/&gt;
	 * 
	 * @return the distance value (expressed in units of getGeometry()'s SRS) to
	 *         be used if a buffer operation ({@link RelationType} DWithin,
	 *         Beyond) is requested.
	 */
	public double getBufferDistance();

	/**
	 * @return the {@link RelationType} that this constraint has.
	 */
	public RelationType getRelationType();

	/**
	 * The RelationType defines what kind of spatial relation objects need to
	 * fulfill to satisfy this criterion. In all definitions, A is the
	 * constraint's {@link Envelope}/{@link Geometry} and B is the is the
	 * candidate data's {@link Envelope}/{@link Geometry}. It corresponds to the
	 * <code>&lt;xsd:element .*? substitutionGroup="ogc:spatialOps"&gt;</code>
	 * group.
	 */
	public enum RelationType {
		/** A must be equal to B. */
		Equals,
		/** equal to !(Any) */
		Disjoint,
		/**
		 * A must touch B's envelope, either in one or multiple points or in a
		 * (partial) edge or any combination.
		 */
		Touches,
		/** B must contain A's envelope entirely. */
		Within,
		/** A must overlap with B, i.e. FIXME. */
		Overlaps,
		/** A must cross B, i.e. intersect on two sides. */
		Crosses,
		/** A must intersect B. */
		Intersects,
		/** A must contain B entirely. */
		Contains,
		/** Candidate objects must within getDistance() of getGeometry(). */
		DWithin,
		/** equal to !(DWithin) */
		Beyond,
		/**
		 * equal to (Contains || Within || Touches || Equals || Intersects ||
		 * Overlaps || Crosses)
		 */
		Any
	}

}
