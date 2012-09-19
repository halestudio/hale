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

package eu.esdihumboldt.hale.common.align.model.transformation.tree;

import java.util.Collection;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Represents the root in a transformation tree.
 * 
 * @author Simon Templer
 */
public interface TransformationTree extends GroupNode {

	/**
	 * Get the associated type definition
	 * 
	 * @return the type definition
	 */
	public TypeDefinition getType();

	/**
	 * Get the source node representing the given type.
	 * 
	 * @param type the type entity definition
	 * @return the source node associated to the type or <code>null</code>
	 */
	public SourceNode getSourceNode(TypeEntityDefinition type);

	/**
	 * Gets all root source nodes of the given type.
	 * 
	 * @param type the type definition
	 * @return root source nodes associated to the type
	 */
	public Collection<SourceNode> getRootSourceNodes(TypeDefinition type);

	/**
	 * Gets all root source nodes.
	 * 
	 * @return all root source nodes
	 */
	public Collection<SourceNode> getRootSourceNodes();
}
