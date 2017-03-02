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

package de.fhg.igd.geom.indices;

import java.util.ArrayList;
import java.util.List;

import de.fhg.igd.geom.BoundingBox;
import de.fhg.igd.geom.Localizable;

/**
 * Represents a node (or leaf) in a R-Tree. The splitting techniques used in
 * this Node implements the Quadratic Algorithm described in Guttman - R-Trees:
 * A dynamic index structure for spatial searching
 * 
 * @author Michel Kraemer
 * @param <T> the type of the objects stored in the RTree
 */
public class Node<T extends Localizable> implements Localizable {

	/**
	 * The bounding box of this node
	 */
	private BoundingBox _boundingBox = new BoundingBox();

	/**
	 * This node's children
	 */
	private List<Localizable> _children;

	/**
	 * True, if this node is a leaf. All nodes are leafs until child nodes are
	 * added
	 */
	private boolean _isLeaf = true;

	/**
	 * The node's parent or null if the node is the root
	 */
	private Node<T> _parent;

	/**
	 * The page size (number of children/locs that can be attached to this node
	 * before it gets splitted). Must be even and greater than or equal to 4.
	 */
	private final int _pageSize;

	/**
	 * The parent R-Tree
	 */
	private final RTree<T> _tree;

	/**
	 * Default constructor
	 * 
	 * @param pageSize the page size (number of children/locs that can be
	 *            attached to this node before it gets splitted). Must be even
	 *            and greater than or equal to 4.
	 * @param parent the node's parent or null if the node is the root
	 * @param tree the parent R-Tree
	 */
	public Node(int pageSize, Node<T> parent, RTree<T> tree) {
		if (pageSize < 4) {
			throw new IllegalArgumentException("pageSize must be " + "greater than or equal to 4");
		}

		if ((pageSize & 1) == 1) {
			throw new IllegalArgumentException("pageSize must be even");
		}

		_pageSize = pageSize;
		_parent = parent;
		_tree = tree;
		_children = new ArrayList<Localizable>(pageSize);
	}

	/**
	 * Calculates the minimum size of a Node
	 * 
	 * @return the number of children a Node must have at least
	 */
	private int calculateMinSize() {
		return _pageSize / 2;
	}

	/**
	 * In the RTree we can't use @link{BoundingBox#getVolume} as we want to
	 * index n-dimensional stuff. We make sure irregular stuff won't get in
	 * anyway, so be relaxed about BBs.
	 * 
	 * @param bb the boundingBox
	 * @return the ersatz volume
	 */
	protected double calcPseudoVolume(BoundingBox bb) {
		double nonDimensionSize = _tree.getNonDimensionSize();
		double d = bb.getWidth();
		double v = d > 0 ? d : nonDimensionSize;
		d = bb.getHeight();
		v *= d > 0 ? d : nonDimensionSize;
		d = bb.getDepth();
		v *= d > 0 ? d : nonDimensionSize;
		return v;
	}

	/**
	 * In the RTree we can't use @link{BoundingBox#getVolume} as we want to
	 * index n-dimensional stuff. We make sure irregular stuff won't get in
	 * anyway, so be relaxed about BBs.
	 * 
	 * @param bb the boundingBox
	 * @return the ersatz volume
	 */
	protected double calcPseudoVolume(Localizable bb) {
		return calcPseudoVolume(bb.getBoundingBox());
	}

	/**
	 * Relates one bounding box to another
	 * 
	 * @param b1 the first bounding box
	 * @param b2 the second bounding box
	 * @param useExtent true if the extents of the bounding boxes should be
	 *            compared and not the bounding boxes themselves
	 * @return true if the first bounding box has any relation to the other one,
	 *         false otherwise
	 */
	private static boolean relate(BoundingBox b1, BoundingBox b2, boolean useExtent) {
		if (useExtent) {
			return b1.toExtent().any(b2.toExtent());
		}
		return b1.any(b2);
	}

	/**
	 * Searches this Node and all children and returns the leaf that contains
	 * the given Localizable
	 * 
	 * @param loc the Localizable to find
	 * @return the leaf that contains loc or null if loc is not contained by any
	 *         leaf
	 */
	@SuppressWarnings("unchecked")
	private Node<T> findLeaf(Localizable loc) {
		if (!isLeaf()) {
			// check all child nodes and invoke findLeaf() on them
			// if they intersect with loc
			for (Localizable l : _children) {
				assert l instanceof Node;
				if (relate(l.getBoundingBox(), loc.getBoundingBox(), false)) {
					Node<T> n = ((Node<T>) l).findLeaf(loc);
					if (n != null) {
						return n;
					}
				}
			}
		}
		else {
			// check all children, if one of them matches
			// loc return this leaf
			for (Localizable l : _children) {
				if (l == loc) {
					return this;
				}
			}
		}

		return null;
	}

	/**
	 * Select a leaf node in which to place a new index entry.
	 * 
	 * @param loc the localizable to find a leaf for
	 * @return the leaf node
	 */
	@SuppressWarnings({ "unchecked", "null" })
	private Node<T> chooseLeaf(final Localizable loc) {
		// if this is a leaf, return this
		if (this.isLeaf()) {
			return this;
		}

		// we're a node, so there must be children
		assert _children != null;

		// Guttman: find the smallest enlargement and find the
		// bounding box with the smallest volume

		double max = Double.POSITIVE_INFINITY;
		Node<T> child = null;

		for (Localizable l : _children) {
			// l must be a Node, because this is no leaf (see above)
			assert l instanceof Node;

			Node<T> n = (Node<T>) l;

			// calculate enlargement
			BoundingBox larger = new BoundingBox(n.getBoundingBox());
			larger.add(loc.getBoundingBox());
			double nvolume = calcPseudoVolume(n);
			double enlargement = calcPseudoVolume(larger) - nvolume;

			if (enlargement < max) {
				// use the one with the smallest enlargement
				max = enlargement;
				child = n;
			}
			else if (enlargement < max + 0.000001) { // roughly equal
														// enlargement
				// child cannot be null, because in the first loop it will
				// always be set to n
				assert child != null;

				// use the one with the smallest volume
				if (nvolume < calcPseudoVolume(child)) {
					child = n;
				}
			}
		}

		// leaf cannot be null at this point,
		// because there's always at least one child
		assert child != null;

		// descend
		return child.chooseLeaf(loc);
	}

	/**
	 * Finds the first entries of two splitted groups (Quadratic Split)
	 * 
	 * @return the two entries
	 */
	private Localizable[] pickSeeds() {
		Localizable[] result = new Localizable[2];
		int ii = -1, jj = -1;
		double min = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < _children.size() - 1; ++i) {
			for (int j = i + 1; j < _children.size(); ++j) {
				Localizable e1 = _children.get(i);
				Localizable e2 = _children.get(j);

				// compose a new bounding box
				BoundingBox bb = new BoundingBox(e1.getBoundingBox());
				bb.add(e2.getBoundingBox());

				// calculate waste
				double d = calcPseudoVolume(bb) - calcPseudoVolume(e1) - calcPseudoVolume(e2);
				if (d > min) {
					result[0] = e1;
					ii = i;
					result[1] = e2;
					jj = j;
					min = d;
				}
			}
		}

		// result must not be empty
		assert result[0] != null;
		assert result[1] != null;

		// remove entries from the list of Localizables
		// make sure that we remove the correct element jj if
		// ii has been removed before
		_children.remove(ii);
		_children.remove(ii < jj ? jj - 1 : jj);

		return result;
	}

	@SuppressWarnings("unchecked")
	private void plainAdd(Localizable loc) {
		_children.add(loc);
		_boundingBox.add(loc.getBoundingBox());
		if (loc instanceof Node) {
			((Node<T>) loc)._parent = this;
		}
	}

	/**
	 * Splits this node into two nodes and adds them to the parent node.
	 * (Quadratic Split)
	 * 
	 * @return the new second Node
	 */
	private Node<T> split() {
		// choose two entries to be the first
		// elements of the groups
		Localizable[] seeds = pickSeeds();

		// calculate minimum fill factor
		int minSize = calculateMinSize();

		// save this._children
		List<Localizable> children = _children;

		// assign each entry to a new Node
		// this node will be group 0
		Node<T> group0 = this;
		_boundingBox = new BoundingBox();
		_children = new ArrayList<Localizable>(_pageSize);
		group0.plainAdd(seeds[0]);

		Node<T> group1 = new Node<T>(_pageSize, this, _tree);
		group1._isLeaf = this.isLeaf();
		group1.plainAdd(seeds[1]);

		// add next entries until nothing is left
		while (children.size() > 0) {
			// if one group has two few entries,
			// add all remaining entries to it
			if (minSize - group0._children.size() == children.size()) {
				for (Localizable c : children) {
					group0.plainAdd(c);
				}
				break;
			}
			else if (minSize - group1._children.size() == children.size()) {
				for (Localizable c : children) {
					group1.plainAdd(c);
				}
				break;
			}

			Localizable next = null;
			double min = Double.NEGATIVE_INFINITY;
			double dd0 = 0.0, dd1 = 0.0;
			int ii = -1;

			// iterate through children and get the one that causes
			// the least cost
			for (int i = 0; i < children.size(); ++i) {
				Localizable l = children.get(i);
				BoundingBox eb = l.getBoundingBox();

				// calculate how much group 0 had to be extended if
				// we would add the child to it
				BoundingBox bb = new BoundingBox(group0.getBoundingBox());
				bb.add(eb);
				double d0 = calcPseudoVolume(bb) - calcPseudoVolume(group0);

				// calculate the same for group 2
				bb = new BoundingBox(group1.getBoundingBox());
				bb.add(eb);
				double d1 = calcPseudoVolume(bb) - calcPseudoVolume(group1);

				// calculate the least cost
				double diff = Math.abs(d1 - d0);
				if (diff > min) {
					next = l;
					ii = i;
					min = diff;

					// remember the cost
					dd0 = d0;
					dd1 = d1;
				}
			}

			// we must have a next entity now
			assert next != null;

			// remove entry from the list of Localizables
			children.remove(ii);

			// add result to one of the groups
			if (dd0 < dd1) {
				group0.plainAdd(next);
			}
			else if (dd1 < dd0) {
				group1.plainAdd(next);
			}
			else {
				double a0 = calcPseudoVolume(group0);
				double a1 = calcPseudoVolume(group1);
				if (a0 < a1) {
					group0.plainAdd(next);
				}
				else if (a1 < a0) {
					group1.plainAdd(next);
				}
				else {
					if (group1._children.size() < group0._children.size()) {
						group1.plainAdd(next);
					}
					else {
						group0.plainAdd(next);
					}
				}
			}
		}

		return group1;
	}

	/**
	 * Inserts a Localizable to the Node. Splits the Node if there are too many
	 * children.
	 * 
	 * @param loc the Localizable to insert
	 */
	private void internalInsert(final Localizable loc) {
		if (!isLeaf() && !(loc instanceof Node<?>)) {
			throw new IllegalArgumentException("You may not add a " + "Localizable to a Node");
		}

		// check if this is a leaf or a node
		if (_children.size() == 0 && (loc instanceof Node<?>)) {
			_isLeaf = false;
		}

		// if there is enough space, add loc
		if (_children.size() < _pageSize) {
			plainAdd(loc);

			// propagate changes upward
			adjustTree(null);
		}
		else {
			plainAdd(loc);
			Node<T> second = split();

			// propagate changes upward
			adjustTree(second);

			// add group 1 to parent
			if (_parent == null) {
				// node is obviously the root
				// create a new root
				Node<T> newroot = new Node<T>(_pageSize, null, _tree);

				// set new root
				_tree.setRoot(newroot);

				// insert child elements
				newroot.internalInsert(this);
				newroot.internalInsert(second);

				// the following line is not needed, because
				// the insert method sets the parent
				// _parent = newroot;
			}
		}
	}

	/**
	 * Finds the Leaf L in which to insert the given Localizable loc and inserts
	 * loc to L. Splits L if there are too many children.
	 * 
	 * @param loc the Localizable to insert
	 */
	public void insert(final Localizable loc) {
		// find the leaf in which to place loc
		Node<T> leaf = chooseLeaf(loc);
		leaf.internalInsert(loc);
	}

	/**
	 * Adds the BoundingBox of this Node to all parent nodes and adds the
	 * Localizable nn, which resulted from a split operation, to the parent.
	 * 
	 * @param nn the Localizable produced by a split operation
	 */
	private void adjustTree(Node<T> nn) {
		if (_parent == null) {
			return;
		}

		_parent._boundingBox.add(_boundingBox);

		if (nn != null) {
			_parent.internalInsert(nn);
		}
		else {
			_parent.adjustTree(null);
		}
	}

	/**
	 * Traverses the given Node "no" and all its children. If a leaf is found
	 * its children (the actual Localizables) will be inserted into the given
	 * RTree as usual.
	 * 
	 * @param <T> the type of the objects stored in the RTree
	 * @param no the Node to traverse
	 * @param tree the RTree to insert the Localizables into
	 */
	@SuppressWarnings("unchecked")
	private static <T extends Localizable> void insertChildrenOfAllLeafs(Node<T> no,
			RTree<T> tree) {
		if (no.isLeaf()) {
			// re-insert the leaf's children as usual
			for (Localizable c : no.getChildren()) {
				tree.insert((T) c);
			}
		}
		else {
			// no is no leaf, so descend...
			for (Localizable c : no.getChildren()) {
				assert c instanceof Node;
				insertChildrenOfAllLeafs((Node<T>) c, tree);
			}
		}
	}

	/**
	 * Eliminate l if it has too few entries. Propagate elimination upwards.
	 * Adjust BoundingBoxes.
	 * 
	 * @param l the Leaf from which an entry has been deleted
	 */
	@SuppressWarnings("unchecked")
	private void condenseTree(Node<T> l) {
		// the list of deleted nodes
		List<Node<T>> q = new ArrayList<Node<T>>();

		Node<T> n = l;
		while (n._parent != null) {
			// eliminate under-full node
			int minSize = calculateMinSize();
			if (n._children.size() < minSize) {
				n._parent._children.remove(n);
				q.add(n);
			}
			else {
				// adjust BoundingBox of n (the element from which an
				// entry has been deleted)
				n._boundingBox = new BoundingBox();
				for (Localizable c : n._children) {
					n._boundingBox.add(c.getBoundingBox());
				}
			}

			n = n._parent;
		}

		// adjust the bounding box of the root
		n._boundingBox = new BoundingBox();
		for (Localizable c : n._children) {
			n._boundingBox.add(c.getBoundingBox());
		}

		// re-insert orphaned entries
		for (Node<T> no : q) {
			if (no.isLeaf()) {
				// re-insert the leaf's children as usual
				for (Localizable c : no.getChildren()) {
					_tree.insert((T) c);
				}
			}
			else {
				// insert the children of all leafs we can
				// find in "no"
				insertChildrenOfAllLeafs(no, _tree);
			}
		}
	}

	/**
	 * Removes a Localizable from the Node
	 * 
	 * @param loc the Localizable to remove
	 * @return true if the Node has been changed, false otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean delete(Localizable loc) {
		// find the leaf containing loc
		Node<T> l = findLeaf(loc);

		if (l == null) {
			return false;
		}

		// remove loc from the leaf
		l._children.remove(loc);

		// propagate changes
		condenseTree(l);

		// shorten tree (if the root has only one child element,
		// make this element the new root)
		if (_tree.getRoot()._children.size() == 1 && !_tree.getRoot().isLeaf()) {
			_tree.setRoot((Node<T>) _tree.getRoot()._children.get(0));
			_tree.getRoot()._parent = null;
		}

		return true;
	}

	/**
	 * Returns a list of all Localizables that have any relation to the given
	 * Localizable loc.
	 * 
	 * @param loc the Localizable to match
	 * @param ignoreZ true if the z coordinate should be ignored during
	 *            candidate search
	 * @return a list of Localizables (containing only leafs of this tree and no
	 *         nodes)
	 */
	@SuppressWarnings("unchecked")
	private List<T> find(final Localizable loc, boolean ignoreZ) {
		List<T> result = new ArrayList<T>();

		BoundingBox theirs = loc.getBoundingBox();
		for (Localizable l : _children) {
			BoundingBox ours = l.getBoundingBox();
			if (relate(ours, theirs, ignoreZ)) {
				if (l instanceof Node) {
					// descend
					result.addAll(((Node<T>) l).find(loc, ignoreZ));
				}
				else {
					// add leaf
					result.add((T) l);
				}
			}
		}

		return result;
	}

	/**
	 * Returns a list of all Localizables that have any relation to the given
	 * Localizable loc.
	 * 
	 * @param loc the Localizable to match
	 * @return a list of Localizables (containing only leafs of this tree and no
	 *         nodes)
	 */
	public List<T> find(final Localizable loc) {
		return find(loc, false);
	}

	/**
	 * Returns a list of all Localizables that have any relation to the given
	 * Localizable loc. Ignores the z ordinate.
	 * 
	 * @param loc the Localizable to match
	 * @return a list of Localizables (containing only leafs of this tree and no
	 *         nodes)
	 */
	public List<T> find2D(final Localizable loc) {
		return find(loc, true);
	}

	/**
	 * This method will return the "Neighborhood" of a given Localizable.
	 * 
	 * @param k the number of neighbor candidates to retrieve
	 * @param loc the given Localizable
	 * @param stepsize the size of each step in which the neighborhood is
	 *            enlarged. If the original Localizable had no volume (because
	 *            it's a point type), this value is first used as an absolute
	 *            value and then as a successive relative increase
	 * @return a list with all neighbors
	 */
	public ArrayList<T> findNeighborhood(int k, Localizable loc, double stepsize) {
		ArrayList<T> result = new ArrayList<T>();

		result.addAll(this.find(loc));

		BoundingBox bb = loc.getBoundingBox();
		while (result.size() < k && this.getBoundingBox().intersectsOrCovers(bb)) {
			bb.expand(stepsize);
			result.addAll(this.find(bb));
		}
		return result;
	}

	/**
	 * @see Localizable#getBoundingBox()
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return _boundingBox;
	}

	/**
	 * @return true if this node is a leaf (has no children), false otherwise
	 */
	public boolean isLeaf() {
		return _isLeaf;
	}

	/**
	 * @return this node's children or null if this node is a leaf
	 */
	public List<Localizable> getChildren() {
		return _children;
	}
}
