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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.align.tgraph;

/**
 * Transformation graph constants.
 * 
 * @author Simon Templer
 */
public interface TransformationGraphConstants {

	/**
	 * Node types in the transformation graph.
	 */
	public enum NodeType {
		/** Source nodes */
		Source,
		/** Target nodes */
		Target,
		/** Cells */
		Cell,
		/** Proxy for a target node */
		Proxy
	}

	// node properties

	/**
	 * Property holding the node type. This property is mandatory.
	 */
	public static final String P_TYPE = "type";

	/**
	 * Property holding the associated entity definition. This property is
	 * mandatory for source and target nodes.
	 */
	public static final String P_ENTITY = "entity";

	/**
	 * Property holding the associated cell. This property is mandatory for cell
	 * nodes.
	 */
	public static final String P_CELL = "cell";

	/**
	 * Property holding the original transformation tree node.
	 */
	public static final String P_ORG_NODE = "ttreeNode";

	// edge properties

	/**
	 * Property holding the variable names a source node is associated to for
	 * its connection to the cell.
	 */
	public static final String P_VAR_NAMES = "varNames";

	// edge labels

	/**
	 * Label for edges between source nodes.
	 */
	public static final String EDGE_CHILD = "child";

	/**
	 * Label for edges between source nodes and cell nodes.
	 */
	public static final String EDGE_VARIABLE = "processedBy";

	/**
	 * Label for edges between cell nodes and target nodes.
	 */
	public static final String EDGE_RESULT = "result";

	/**
	 * Label for edges between target nodes.
	 */
	public static final String EDGE_PARENT = "parent";

	/**
	 * Label for edges between target nodes.
	 */
	public static final String EDGE_PROXY = "proxies";

}
