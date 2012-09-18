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

package eu.esdihumboldt.cst.internal;

/**
 * Tests for the transformation tree
 * 
 * @author Kai Schwierczek
 */
public abstract class TransformationTreeTest {

	// XXX commented out as not used, to remove zest dependencies for this
	// bundle
//	/**
//	 * NOT TESTED!
//	 * 
//	 * Compares the two graphs with each other.
//	 * It expects the connections to point from target to source.
//	 * It expects exactly one target with no outgoing connections.
//	 * Source values schould be unique for it to work correctly.
//	 *
//	 * @param a one graph
//	 * @param b the other graph
//	 * @return true, if the two graphs equal each other
//	 */
//	@SuppressWarnings("unchecked")
//	public static boolean graphEquals(Graph a, Graph b) {
//		List<GraphNode> nodes1 = a.getNodes();
//		List<GraphNode> nodes2 = b.getNodes();
//
//		// compare node count
//		if (nodes1.size() != nodes2.size())
//			return false;
//
//		// find target nodes
//		GraphNode target1 = null, target2 = null;
//		for (GraphNode node1 : nodes1)
//			if (node1.getSourceConnections().size() == 0) {
//				target1 = node1;
//				break;
//			}
//		for (GraphNode node2 : nodes2)
//			if (node2.getSourceConnections().size() == 0) {
//				target2 = node2;
//				break;
//			}
//
//		// compare them
//		return nodesEqual(target1, target2);
//	}
//
//	// Compares the two given graph nodes.
//	@SuppressWarnings("unchecked")
//	private static boolean nodesEqual(GraphNode a, GraphNode b) {
//		// compare text
//		if (!a.getText().equals(b.getText()))
//			return false;
//
//		List<GraphConnection> aConnections = a.getSourceConnections();
//		List<GraphConnection> bConnections = b.getSourceConnections();
//		// compare outgoing connection count
//		if (aConnections.size() != bConnections.size())
//			return false;
//
//		// compare outgoing connections
//		for (GraphConnection aConnection : aConnections) {
//			boolean found = false;
//			Iterator<GraphConnection> bIter = bConnections.iterator();
//			while (!found && bIter.hasNext()) {
//				GraphConnection bConnection = bIter.next();
//				if (nodesEqual(aConnection.getDestination(), bConnection.getDestination())) {
//					bIter.remove();
//					found = true;
//				}
//			}
//			if (!found)
//				return false;
//		}
//
//		return true;
//	}
}
