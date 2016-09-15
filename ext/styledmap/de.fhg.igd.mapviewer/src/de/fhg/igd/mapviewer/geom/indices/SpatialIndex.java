/*
 * Copyright (c) 2016 Fraunhofer IGD
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
 *     Fraunhofer IGD <http://www.igd.fraunhofer.de/>
 */

package de.fhg.igd.mapviewer.geom.indices;

import java.util.Set;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Localizable;
import de.fhg.igd.mapviewer.geom.Verifier;

/**
 * This interface describes the access methods that a spatial index has to
 * provide..
 * 
 * @author Thorsten Reitz
 * @param <T> the type of the objects stored in the index
 */
public interface SpatialIndex<T extends Localizable> {

	/**
	 * Return the k nearest neighbors to the given point.
	 * 
	 * @param k The k nearest neighbors to return.
	 * @param loc The Localizable to question.
	 * @param nnc the comparator that compares Localizables
	 * @return the k nearest neighbors
	 */
	public Set<T> nearestNeighbor(int k, Localizable loc, NNComparator nnc);

	/**
	 * Sets the BoundingBox for this index.
	 * 
	 * @param bb the new bounding box
	 */
	public void setBoundingBox(BoundingBox bb);

	/**
	 * Resets this index.
	 */
	public void flush();

	/**
	 * inserts a node into this index.
	 * 
	 * @param data the node to insert
	 * @throws IllegalArgumentException if data cannot be indexed
	 */
	public void insert(final T data);

	/**
	 * removes a node from this index.
	 * 
	 * @param entity the node to remove
	 * @return true if the index was changed
	 */
	public boolean delete(final T entity);

	/**
	 * Performs a spatial query
	 * 
	 * @param <L> the type of the localizable to compare to
	 * @param entity the localizable to compare to
	 * @param verifier the verifier used to check if the candidates found have a
	 *            certain spatial relation to the given entity
	 * @return a set of candidates matching the given entity
	 */
	public <L extends Localizable> Set<T> query(final L entity, Verifier<? super T, L> verifier);

	/**
	 * Performs a spatial query. Ignores the z ordinate during candidate search.
	 * 
	 * @param <L> the type of the localizable to compare to
	 * @param entity the localizable to compare to
	 * @param verifier the verifier used to check if the candidates found have a
	 *            certain spatial relation to the given entity
	 * @return a set of candidates matching the given entity
	 */
	public <L extends Localizable> Set<T> query2D(final L entity, Verifier<? super T, L> verifier);

	/**
	 * @return the number of objects indexed in this index.
	 */
	public int size();

}
