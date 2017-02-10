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

package eu.esdihumboldt.hale.common.instance.index.spatial;

import java.util.Collection;

import de.fhg.igd.geom.Localizable;
import de.fhg.igd.geom.Verifier;

/**
 * Service for maintaining a spatial index.
 * 
 * @param <L> Indexed type
 * @param <K> Spatial query type (e.g. BoundingBox)
 * @author Florian Esser
 */
public interface SpatialIndexService<L extends Localizable, K extends Localizable> {

	/**
	 * Inserts a {@link Localizable} into the spatial index
	 * 
	 * @param localizable object to insert
	 */
	void insert(L localizable);

	/**
	 * Retrieves objects that match the given query
	 * 
	 * @param spatialQuery spatial query, e.g. a bounding box
	 * @return Objects that match the query or an empty Collection if there are
	 *         no matches
	 */
	Collection<L> retrieve(K spatialQuery);

	/**
	 * Retrieves objects that match the given query according to the given
	 * {@link Verifier}.
	 * 
	 * @param spatialQuery spatial query
	 * @param verifier Verifier that decides if indexed objects match the
	 *            spatial query
	 * @return Objects that match the query or an empty Collection if there are
	 *         no matches
	 */
	Collection<L> retrieve(K spatialQuery, Verifier<? super L, K> verifier);
}
