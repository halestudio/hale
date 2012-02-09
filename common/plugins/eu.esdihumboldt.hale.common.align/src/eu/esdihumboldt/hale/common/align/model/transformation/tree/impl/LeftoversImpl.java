/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
 * @author Simon Templer
 */
public class LeftoversImpl implements Leftovers {

	/**
	 * Pairs source nodes for left over values with the cells it has been
	 * consumed for.
	 * If the cell set is <code>null</code> this means a leftover has been
	 * consumed completely. 
	 */
	private final List<Pair<SourceNode, Set<Cell>>> values = new ArrayList<Pair<SourceNode,Set<Cell>>>();
	
	private int firstNotConsumed = 0;
	
	/**
	 * Constructor
	 * @param leftovers the left over values
	 * @param originalSource the original source node to be duplicated
	 */
	public LeftoversImpl(Object[] leftovers, SourceNode originalSource) {
		// for each leftover create a source node duplicate
		for (Object value : leftovers) {
			SourceNode duplicate = new SourceNodeImpl(
					originalSource.getEntityDefinition(), 
					originalSource.getParent(), false);
			
			// assign context
			duplicate.setContext(originalSource.getContext());
			
			// add as annotated child to original parent
			originalSource.getParent().addAnnotatedChild(duplicate);
			
			// set the value
			duplicate.setValue(value);
			//XXX where should eventual children be created?
			
			// store the leftover
			values.add(new Pair<SourceNode, Set<Cell>>(duplicate, new HashSet<Cell>()));
		}
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

	private Pair<SourceNode, Set<Cell>> unmodifiablePair(
			Pair<SourceNode, Set<Cell>> pair) {
		return new Pair<SourceNode, Set<Cell>>(
				pair.getFirst(), Collections.unmodifiableSet(pair.getSecond()));
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
