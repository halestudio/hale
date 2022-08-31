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

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

/**
 * Transformation graph interface.
 * 
 * @author Simon Templer
 */
public interface TGraph extends TGraphConstants {

	/**
	 * Get the graph.
	 * 
	 * @return the internal graph
	 */
	public Graph getGraph();

	/**
	 * Get the graph vertex representing the target type.
	 * 
	 * @return the target type vertex
	 */
	public Vertex getTarget();

	/**
	 * Create proxy nodes for target nodes that have multiple cells assigning
	 * results to it - for each incoming edge from a cell a proxy node is
	 * created.
	 * 
	 * @return this transformation graph
	 */
	public TGraph proxyMultiResultNodes();

	/**
	 * Perform context matching on the transformation graph.
	 * 
	 * @return this transformation graph
	 */
	public TGraph performContextMatching();

}
