/*
 * Copyright (c) 2017 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.util.geometry.interpolation;

import javax.annotation.Nullable;

import org.locationtech.jts.geom.Coordinate;

/**
 * Segment of an Arc used for interpolation algorithms.
 * 
 * @author Simon Templer
 */
public interface ArcSegment {

	/**
	 * @return if the segment is atomic, i.e. it cannot be split in two parts
	 */
	boolean isAtomic();

	/**
	 * Split the segment and return the second part. Should only be called for
	 * segments that are not {@link #isAtomic()}.
	 * 
	 * @return the second part of the segment
	 */
	ArcSegment getSecondPart();

	/**
	 * Split the segment and return the first part. Should only be called for
	 * segments that are not {@link #isAtomic()}.
	 * 
	 * @return the first part of the segment
	 */
	ArcSegment getFirstPart();

	/**
	 * @return the start point of the segment
	 */
	Coordinate getStartPoint();

	/**
	 * @return the middle point of the segment
	 */
	@Nullable
	Coordinate getMiddlePoint();

	/**
	 * @return the end point of the segment
	 */
	Coordinate getEndPoint();

}
