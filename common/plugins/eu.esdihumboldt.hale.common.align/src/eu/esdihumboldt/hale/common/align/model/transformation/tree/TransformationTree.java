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
