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

package eu.esdihumboldt.hale.ui.cst.debug.metadata.internal;

import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.SetMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.CellNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.SourceNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TargetNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationNode;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTree;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.visitor.TreeToDotVisitor;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Provider for converting TransformationTrees into the DOT-graph-format
 * 
 * @author SebastianReinhardt
 */
public class TreeToDotProvider implements DotProvider {

	TransformationTree tree;
	TreeToDotVisitor dotVisitor;

	/**
	 * @param tree The Tree to get the dot-graph from
	 */
	public TreeToDotProvider(TransformationTree tree) {
		this.tree = tree;
		dotVisitor = new TreeToDotVisitor();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.cst.debug.metadata.internal.DotProvider#generateGraph()
	 */
	@Override
	public String generateGraph() {

		tree.accept(dotVisitor);

		SetMultimap<String, String> connections = dotVisitor.getAllConnections();
		Set<String> ids = dotVisitor.getAllIds();

		String dot = "graph TransformationTree {\n";
		for (String key : ids) {

			TransformationNode node = dotVisitor.getNode(key);
			dot = dot + (key + " " + getNodeParamString(node));
		}

		for (String key : connections.keySet()) {
			for (String value : connections.get(key)) {
				dot = dot + (key + " -- " + value + ";\n");
			}
		}

		dot = dot + ("}");
		return dot;
	}

	/**
	 * get the dot-parameter string of a node from the node-objects
	 * 
	 * @param node the node-object to get the name from
	 * @return the parameter of the node
	 */
	private String getNodeParamString(TransformationNode node) {

		if (node instanceof TransformationTree) {
			return "[label=\"" + ((TransformationTree) node).getType().getDisplayName() + "\"];\n";
		}

		if (node instanceof TargetNode) {
			return "[label=\"" + ((TargetNode) node).getDefinition().getDisplayName() + "\"];\n";

		}

		if (node instanceof SourceNode) {
			SourceNode snode = (SourceNode) node;
			Object value = snode.getValue();
			String name = ((SourceNode) node).getDefinition().getDisplayName();
			String result = "";
			if (value != null) {
				result = result + "[label=\"{" + name + " | " + getLabelValueString(value) + "}";
			}
			else {
				result = result + "[label=\"" + name;
			}
			result = result + "\"" + " shape=record];\n";
			return result;
		}

		if (node instanceof CellNode) {
			return "[label=\""
					+ FunctionUtil.getFunction(
							((CellNode) node).getCell().getTransformationIdentifier())
							.getDisplayName() + "\"" + " shape=hexagon];\n";
		}

		else
			return null;
	}

	/**
	 * method for gathering the dot-string of a node value
	 * 
	 * @param value the node value
	 * @return the dot-format-string
	 */
	private String getLabelValueString(Object value) {

		if (value instanceof Instance) {
			int i = 0;

			for (@SuppressWarnings("unused")
			QName name : ((Instance) value).getPropertyNames()) {
				i++;
			}
			String ivalue = "Instance(" + i + ")";
			if (((Instance) value).getValue() != null) {
				ivalue = ivalue + " | " + fixUpString(((Instance) value).getValue().toString());
			}
			return ivalue;

		}
		if (value instanceof Group) {
			int i = 0;
			for (@SuppressWarnings("unused")
			QName name : ((Group) value).getPropertyNames()) {
				i++;
			}
			return "Group(" + i + ")";
		}

		return fixUpString(value.toString());
	}

	/**
	 * utility method for shortening strings in a dot graph
	 * 
	 * @param string the string to shorten
	 * @return the shortened string
	 */
	private String shortenString(String string) {

		if (string.length() > 40) {
			string = string.substring(0, 37) + "...";
		}
		return string;

	}

	/**
	 * this method checks for special characters for dot label string and
	 * creates the correct string to interpret
	 * 
	 * @param string the original string to fix
	 * @return the fixed up string
	 */
	private String fixUpString(String string) {
		string = string.replaceAll("\\{", "\\\\\\{");
		string = string.replaceAll("\\}", "\\\\\\}");
		string = string.replaceAll("|", "\\\\|");

		return shortenString(string);
	}
}
