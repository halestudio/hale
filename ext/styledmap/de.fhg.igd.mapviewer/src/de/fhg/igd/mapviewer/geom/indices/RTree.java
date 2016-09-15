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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;

import de.fhg.igd.mapviewer.geom.BoundingBox;
import de.fhg.igd.mapviewer.geom.Localizable;
import de.fhg.igd.mapviewer.geom.Verifier;

/**
 * Implements an R-Tree
 * 
 * @author Michel Kraemer
 * @param <T> the type of the objects stored in the tree
 */
public class RTree<T extends Localizable> implements SpatialIndex<T> {

	/**
	 * 
	 */
	private static final double DEFAULT_NON_DIMEMSION_SIZE = 0.0001;

	/**
	 * The root node
	 */
	private Node<T> _root;

	/**
	 * The maximum number of entries a child node can contain
	 */
	private int _pageSize;

	/**
	 * The number of Localizables indexed in this R-Tree
	 */
	private int _size;

	/**
	 * the size assumed for non-present dimensions - only important for
	 * optimization of mixed dimensionality, should be > 0
	 */
	private final double _nonDimensionSize;

	/**
	 * Default constructor
	 * 
	 * @param pageSize the page size (number of children that can be attached to
	 *            this node before it gets splitted). Must be even and greater
	 *            than or equal to 4.
	 */
	public RTree(int pageSize) {
		this(pageSize, DEFAULT_NON_DIMEMSION_SIZE);
	}

	/**
	 * Default constructor
	 * 
	 * @param pageSize the page size (number of children that can be attached to
	 *            this node before it gets splitted). Must be even and greater
	 *            than or equal to 4.
	 * @param nonDimemsionSize the size assumed for non-present dimensions -
	 *            only important for optimization of mixed dimensionality,
	 *            should be > 0
	 */
	public RTree(int pageSize, double nonDimemsionSize) {
		Preconditions.checkArgument(pageSize >= 4 && pageSize % 2 == 0 && nonDimemsionSize > 0);
		_nonDimensionSize = nonDimemsionSize;
		_pageSize = pageSize;
		flush();
	}

	/**
	 * @return the root node
	 */
	public Node<T> getRoot() {
		return _root;
	}

	/**
	 * Sets the new root node. Package visible, because only Node shall access
	 * it.
	 * 
	 * @param root the new root node
	 */
	void setRoot(Node<T> root) {
		_root = root;
	}

	/**
	 * @see SpatialIndex#setBoundingBox(BoundingBox)
	 */
	@Override
	public void setBoundingBox(BoundingBox bb) {
		// this method is not needed, since the R-Tree grows dynamically
	}

	/**
	 * @see SpatialIndex#flush()
	 */
	@Override
	public void flush() {
		_root = new Node<T>(_pageSize, null, this);
		_size = 0;
	}

	/**
	 * @see SpatialIndex#insert(Localizable)
	 */
	@Override
	public void insert(T loc) {
		BoundingBox box = loc.getBoundingBox();
		if (!box.checkIntegrity()) {
			throw new IllegalArgumentException(
					"You may not insert a " + "Localizable object with a invalid BoundingBox");
		}
		_root.insert(loc);
		++_size;
	}

	/**
	 * @see SpatialIndex#delete(Localizable)
	 */
	@Override
	public boolean delete(T loc) {
		if (_root.delete(loc)) {
			--_size;
			return true;
		}
		return false;
	}

	/**
	 * @see SpatialIndex#size()
	 */
	@Override
	public int size() {
		return _size;
	}

	/**
	 * @see SpatialIndex#query(Localizable, Verifier)
	 */
	@Override
	public <L extends Localizable> Set<T> query(L loc, Verifier<? super T, L> verifier) {
		List<T> candidates = _root.find(loc);
		return processQuery(loc, verifier, candidates);
	}

	/**
	 * @see SpatialIndex#query2D(Localizable, Verifier)
	 */
	@Override
	public <L extends Localizable> Set<T> query2D(L loc, Verifier<? super T, L> verifier) {
		List<T> candidates = _root.find2D(loc);
		return processQuery(loc, verifier, candidates);
	}

	/**
	 * Traverses through the given list of candidates and finds those that match
	 * the given verifier
	 * 
	 * @param <L> the type of localizables to search
	 * @param loc the localizable to match against
	 * @param verifier the verifier
	 * @param candidates the candidates to search
	 * @return the candidates that match the given verifier
	 */
	private <L extends Localizable> Set<T> processQuery(L loc, Verifier<? super T, L> verifier,
			List<T> candidates) {
		if (candidates.size() > 0) {
			Set<T> verified = new HashSet<T>();

			for (T cand : candidates) {
				if (verifier.verify(cand, loc)) {
					verified.add(cand);
				}
			}

			return verified;
		}

		return Collections.emptySet();
	}

	/**
	 * @see SpatialIndex#nearestNeighbor(int, Localizable, NNComparator)
	 */
	@Override
	public Set<T> nearestNeighbor(int k, Localizable loc, NNComparator nnc) {
		nnc.setLoc(loc);

		Set<T> ordered_by_nnc = new TreeSet<T>(nnc);
		ordered_by_nnc.addAll(_root.findNeighborhood(k * 2, loc, 0.5));

		return ordered_by_nnc;
	}

	/**
	 * @return the size assumed for non-present dimensions - only important for
	 *         optimization of mixed dimensionality, should be > 0
	 */
	public double getNonDimensionSize() {
		return _nonDimensionSize;
	}
}
