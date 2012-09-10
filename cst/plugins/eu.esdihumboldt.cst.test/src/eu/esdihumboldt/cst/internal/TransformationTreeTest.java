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
