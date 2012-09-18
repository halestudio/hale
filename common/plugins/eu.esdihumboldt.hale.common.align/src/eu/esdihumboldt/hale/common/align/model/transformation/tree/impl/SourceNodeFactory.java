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

import java.util.HashMap;
import java.util.Map;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;

/**
 * Factory for creating {@link SourceNode}s.
 * 
 * @author Simon Templer
 */
public class SourceNodeFactory {

	private Map<EntityDefinition, SourceNode> nodes = new HashMap<EntityDefinition, SourceNode>();

	/**
	 * Get or create source node for the given entity definition. Source nodes
	 * created from this factory are ensured to exist only once for the same
	 * entity definition.
	 * 
	 * @param entityDefinition the entity definition the source node is
	 *            associated to
	 * @return the source node
	 */
	public SourceNode getSourceNode(EntityDefinition entityDefinition) {
		SourceNode node = nodes.get(entityDefinition);
		if (node == null) {
			node = new SourceNodeImpl(entityDefinition, this);
			nodes.put(entityDefinition, node);
		}

		return node;
	}

	/**
	 * Get the existing source node associated to the given entity definition.
	 * 
	 * @param entityDefinition the entity definition
	 * @return the source node or <code>null</code>
	 */
	public SourceNode getExistingSourceNode(EntityDefinition entityDefinition) {
		return nodes.get(entityDefinition);
	}

	/**
	 * Get the existing source nodes.
	 * 
	 * @return the source nodes
	 */
	public Iterable<SourceNode> getNodes() {
		return nodes.values();
	}

}
