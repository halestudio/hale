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

package eu.esdihumboldt.hale.common.align.tgraph.impl.internal;

import java.util.Set;

import com.google.common.collect.SetMultimap;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.GroupNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.TreeToGraphVisitor;
import eu.esdihumboldt.hale.common.align.service.FunctionService;
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants;
import eu.esdihumboldt.hale.common.schema.SchemaSpaceID;

/**
 * Helper for transformation graphs based on a {@link TransformationTree}.
 * 
 * @author Simon Templer
 */
public class TGraphFactory implements TGraphConstants {

	/**
	 * Create a transformation graph from a transformation tree.
	 * 
	 * @param ttree the transformation tree
	 * @param functionService the function service
	 * @return an in-memory graph created from the transformation tree
	 */
	public static Graph create(TransformationTree ttree, FunctionService functionService) {
		TreeToGraphVisitor graphVisitor = new TreeToGraphVisitor(functionService);
		ttree.accept(graphVisitor);

		SetMultimap<String, String> connections = graphVisitor.getAllConnections();
		Set<String> ids = graphVisitor.getAllIds();

		Graph graph = new TinkerGraph();

		// add nodes to the graph
		for (String key : ids) {
			// create a vertex for each transformation node
			TransformationNode node = graphVisitor.getNode(key);
			Vertex vertex = graph.addVertex(key);
			setVertexProperties(vertex, node);
		}

		for (String key : connections.keySet()) {
			for (String value : connections.get(key)) {
				Vertex targetSide = graph.getVertex(key);
				Vertex sourceSide = graph.getVertex(value);

				TransformationNode targetSideNode = graphVisitor.getNode(key);
				TransformationNode sourceSideNode = graphVisitor.getNode(value);

				String edgeLabel;
				if (sourceSideNode instanceof SourceNode && targetSideNode instanceof SourceNode) {
					edgeLabel = EDGE_CHILD;
				}
				else if (sourceSideNode instanceof SourceNode && targetSideNode instanceof CellNode) {
					edgeLabel = EDGE_VARIABLE;
				}
				else if (sourceSideNode instanceof CellNode && targetSideNode instanceof GroupNode) {
					edgeLabel = EDGE_RESULT;
				}
				else if (sourceSideNode instanceof GroupNode && targetSideNode instanceof GroupNode) {
					edgeLabel = EDGE_PARENT;
				}
				else {
					throw new IllegalStateException("Invalid relation in transformation tree");
				}

				Edge edge = graph.addEdge(null, sourceSide, targetSide, edgeLabel);
				setEdgeProperties(edge, sourceSideNode, targetSideNode);
			}
		}
		return graph;
	}

	/**
	 * Set the edge properties based on the source and target node.
	 * 
	 * @param edge the edge
	 * @param sourceSideNode the source node
	 * @param targetSideNode the target node
	 */
	private static void setEdgeProperties(Edge edge, TransformationNode sourceSideNode,
			TransformationNode targetSideNode) {
		if (sourceSideNode instanceof SourceNode && targetSideNode instanceof SourceNode) {
			setEdgeProperties(edge, (SourceNode) sourceSideNode, (SourceNode) targetSideNode);
		}
		else if (sourceSideNode instanceof SourceNode && targetSideNode instanceof CellNode) {
			setEdgeProperties(edge, (SourceNode) sourceSideNode, (CellNode) targetSideNode);
		}
		else if (sourceSideNode instanceof CellNode && targetSideNode instanceof GroupNode) {
			setEdgeProperties(edge, (CellNode) sourceSideNode, (GroupNode) targetSideNode);
		}
		else if (sourceSideNode instanceof GroupNode && targetSideNode instanceof GroupNode) {
			setEdgeProperties(edge, (GroupNode) sourceSideNode, (GroupNode) targetSideNode);
		}
	}

	@SuppressWarnings("unused")
	private static void setEdgeProperties(Edge edge, SourceNode sourceSideNode,
			SourceNode targetSideNode) {
		// do nothing
	}

	private static void setEdgeProperties(Edge edge, SourceNode sourceSideNode,
			CellNode targetSideNode) {
		// do nothing
		edge.setProperty(P_VAR_NAMES, targetSideNode.getSourceNames(sourceSideNode));
	}

	@SuppressWarnings("unused")
	private static void setEdgeProperties(Edge edge, CellNode sourceSideNode,
			GroupNode targetSideNode) {
		// do nothing
	}

	@SuppressWarnings("unused")
	private static void setEdgeProperties(Edge edge, GroupNode sourceSideNode,
			GroupNode targetSideNode) {
		// do nothing
	}

	/**
	 * Set the vertex properties based on the associated transformation tree
	 * node.
	 * 
	 * @param node the transformation tree node
	 * @param vertex the vertex to update
	 */
	private static void setVertexProperties(Vertex vertex, TransformationNode node) {
		vertex.setProperty(P_ORG_NODE, node);
		if (node instanceof CellNode) {
			setVertexProperties(vertex, (CellNode) node);
		}
		if (node instanceof SourceNode) {
			setVertexProperties(vertex, (SourceNode) node);
		}
		if (node instanceof GroupNode) {
			setVertexProperties(vertex, (GroupNode) node);
		}
	}

	private static void setVertexProperties(Vertex vertex, CellNode node) {
		vertex.setProperty(P_TYPE, NodeType.Cell);
		vertex.setProperty(P_CELL, node.getCell());
	}

	private static void setVertexProperties(Vertex vertex, SourceNode node) {
		vertex.setProperty(P_TYPE, NodeType.Source);
		vertex.setProperty(P_ENTITY, node.getEntityDefinition());
	}

	private static void setVertexProperties(Vertex vertex, GroupNode node) {
		vertex.setProperty(P_TYPE, NodeType.Target);
		if (node instanceof TargetNode) {
			setVertexProperties(vertex, (TargetNode) node);
		}
		if (node instanceof TransformationTree) {
			setVertexProperties(vertex, (TransformationTree) node);
		}
	}

	private static void setVertexProperties(Vertex vertex, TargetNode node) {
		vertex.setProperty(P_ENTITY, node.getEntityDefinition());
	}

	private static void setVertexProperties(Vertex vertex, TransformationTree node) {
		// create a type entity definition
		// TODO also include the filter
		TypeEntityDefinition ted = new TypeEntityDefinition(node.getType(), SchemaSpaceID.TARGET,
				null);
		vertex.setProperty(P_ENTITY, ted);
	}

}
