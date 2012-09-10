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
