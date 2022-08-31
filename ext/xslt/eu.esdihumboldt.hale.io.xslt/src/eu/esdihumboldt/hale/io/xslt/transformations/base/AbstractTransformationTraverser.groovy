/*
 * Copyright (c) 2012 Fraunhofer IGD
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
 *     Fraunhofer IGD
 */

package eu.esdihumboldt.hale.io.xslt.transformations.base;

import javax.xml.namespace.QName

import org.apache.tinkerpop.gremlin.structure.Vertex

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants
import eu.esdihumboldt.hale.common.align.tgraph.TGraphHelpers
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag

/**
 * Traverser for a transformation graph that visits target nodes in the correct
 * order needed for valid XML output.
 * 
 * @author Simon Templer
 */
abstract class AbstractTransformationTraverser implements TGraphConstants {

	static {
		TGraphHelpers.load()
	}

	/**
	 * Traverse the given transformation graph.
	 * 
	 * @param graph the transformation graph
	 */
	void traverse(TGraph graph) {
		// start traversing with the target type node
		Vertex root = graph.getTarget()
		traverseTargets(root, root.entity().definition);
	}

	/**
	 * Traverse the given target nodes in the correct order.
	 * 
	 * @param parentNode the parent node
	 * @param parent the parent type or group
	 */
	protected void traverseTargets(Vertex parentNode, DefinitionGroup parent) {
		// organize child nodes by name
		Multimap<QName, Vertex> namedNodes = ArrayListMultimap.create()
		for (Vertex child : parentNode.in(EDGE_PARENT)) {
			namedNodes.put(child.entity.definition.name, child);
		}

		//XXX NOTE: this will not include proxy nodes

		List<QName> childNames = new ArrayList<QName>();
		// determine correct order for children
		for (ChildDefinition<?> child : DefinitionUtil.getAllChildren(parent)) {
			if (child.asProperty() != null &&
					child.asProperty().getConstraint(XmlAttributeFlag.class).isEnabled()) {
				// child is an XML attribute
				// and must be handled first
				childNames.add(0, child.getName());
			}
			else {
				// child must be handled in order as defined
				childNames.add(child.getName());
			}
		}

		// handle target nodes in order
		for (QName childName : childNames) {
			def nodes = namedNodes.get(childName)
			// there may be multiple nodes of the same name, e.g. instance contexts
			if (nodes) {
				for (Vertex node in nodes) {
					visitProperty(node);

					// traverse children
					ChildDefinition<?> nd = node.entity().definition
					DefinitionGroup group = (nd.asGroup() == null) ?
							(nd.asProperty().getPropertyType()) :
							(nd.asGroup());
					traverseTargets(node, group);

					leaveProperty(node);
				}
			}
			else {
				//FIXME do this also after treating all nodes?!
				//XXX there must be a possibility to detect whether there were results

				ChildDefinition<?> child = parent.getChild(childName);
				if (DefinitionUtil.getCardinality(child).getMinOccurs() > 0) {
					/*
					 * Allow to handle unmapped mandatory properties, e.g.
					 * elements that are mandatory but nillable.
					 */
					handleUnmappedProperty(child);
				}
			}
		}
	}

	/**
	 * Handle a child for which no target node exists.
	 * 
	 * @param child the child definition
	 */
	protected abstract void handleUnmappedProperty(ChildDefinition<?> child);

	/**
	 * Called when leaving a target node and its children.
	 * 
	 * @param node the target node
	 */
	protected abstract void leaveProperty(Vertex node);

	/**
	 * Visit a target node.
	 * 
	 * @param node the target node
	 */
	protected abstract void visitProperty(Vertex node);
}
