/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.model.transformation.tree.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.Leftovers;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.util.Pair;

/**
 * Default {@link Leftovers} implementation.
 * 
 * @author Simon Templer
 */
public class LeftoversImpl implements Leftovers {

	/**
	 * Pairs source nodes for left over values with the cells it has been
	 * consumed for. If the cell set is <code>null</code> this means a leftover
	 * has been consumed completely.
	 */
	private final List<Pair<SourceNode, Set<Cell>>> values = new ArrayList<Pair<SourceNode, Set<Cell>>>();

	private final SourceNode originalSource;

	private int firstNotConsumed = 0;

	/**
	 * Constructor
	 * 
	 * @param originalSource the original source node to be duplicated
	 * @param leftovers the left over values
	 */
	public LeftoversImpl(SourceNode originalSource, Object... leftovers) {
		this.originalSource = originalSource;
		// for each leftover create a source node duplicate
		for (Object value : leftovers)
			addLeftover(value, originalSource.getAnnotatedParent());
	}

	/**
	 * Adds the given value to the leftovers.
	 * 
	 * @param value the leftover value
	 * @param annotatedParent the value for the annotated parent field of the
	 *            duplicate nodes
	 */
	public void addLeftover(Object value, SourceNode annotatedParent) {
		SourceNode duplicate = new SourceNodeImpl(originalSource.getEntityDefinition(),
				originalSource.getParent(), false);
		duplicate.setAnnotatedParent(annotatedParent);

		// assign context
		duplicate.setContext(originalSource.getContext());

		// add as annotated child to original parent
		if (originalSource.getParent() != null)
			originalSource.getParent().addAnnotatedChild(duplicate);

		// set the value
		duplicate.setValue(value);
		// XXX where should eventual children be created?

		// store the leftover
		values.add(new Pair<SourceNode, Set<Cell>>(duplicate, new HashSet<Cell>()));
	}

	/**
	 * @see Leftovers#consumeValue()
	 */
	@Override
	public Pair<SourceNode, Set<Cell>> consumeValue() {
		if (firstNotConsumed < values.size()) {
			return unmodifiablePair(values.get(firstNotConsumed++));
		}
		return null;
	}

	private Pair<SourceNode, Set<Cell>> unmodifiablePair(Pair<SourceNode, Set<Cell>> pair) {
		return new Pair<SourceNode, Set<Cell>>(pair.getFirst(), Collections.unmodifiableSet(pair
				.getSecond()));
	}

	/**
	 * @see Leftovers#consumeValue(Cell)
	 */
	@Override
	public SourceNode consumeValue(Cell cell) {
		for (int i = firstNotConsumed; i < values.size(); i++) {
			Pair<SourceNode, Set<Cell>> leftover = values.get(i);
			Set<Cell> consumedCells = leftover.getSecond();
			if (consumedCells != null && !consumedCells.contains(cell)) {
				// cell has not been consumed for the node
				consumedCells.add(cell);
				return leftover.getFirst();
			}
		}
		return null;
	}

}
