/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.util.groovy.json

import eu.esdihumboldt.util.groovy.builder.BuilderBase
import groovy.json.JsonOutput
import groovy.json.StringEscapeUtils
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode


/**
 * Alternative to {@link StreamingJsonBuilder} with a slightly different syntax
 * and support concerning arrays.
 * 
 * @author Simon Templer
 */
@CompileStatic
class JsonStreamBuilder extends BuilderBase {

	private static final String INDENT = '\t'

	// represents a builder node
	private static class NodeState {
		int level = 0
		// the node name
		String name
		// if the node is part of an array
		boolean array = false
		// if the node represents a JSON object
		boolean object = false
		// if the node represents a virtual root
		boolean root = false

		// the last child of the node
		NodeState lastChild
	}

	final Writer writer

	final boolean prettyPrint

	/**
	 * Create a new builder streaming JSON to the given writer.
	 * 
	 * @param writer the writer, it's the callers responsibility to close the writer
	 * @param prettyPrint if the output should be pretty printed
	 */
	JsonStreamBuilder(Writer writer, boolean prettyPrint = false) {
		this.writer = writer
		this.prettyPrint = prettyPrint
	}

	/**
	 * Creates a JSON root object. If a parent already exists will just call
	 * the given closure.
	 * 
	 * @param closure the closure defining the object
	 */
	public void call(Closure closure) {
		def parent = current
		if (parent == null) {
			current = new NodeState(object: true, root: false)
			writer.write( '{' )
		}

		closure = (Closure) closure.clone()
		closure.delegate = this
		closure.call()

		if (parent == null) {
			internalNodeWrapup(current)
			current = parent
			reset()
		}
	}

	@Override
	protected Object internalCreateNode(String name, Map attributes, List params, Object parent,
			boolean subClosure) {
		NodeState parentNode = (NodeState) parent
		NodeState node = new NodeState()
		NodeState previous = null
		if (parentNode) {
			// has a parent
			previous = parentNode.lastChild
			parentNode.lastChild = node
			node.level = parentNode.level + 1
		}
		else {
			// has no parent -> create a root object
			node.root = true
			node.level = 1
			writer << '{'
		}

		// name denotes an array?
		//XXX allow other conditions?
		if (name.endsWith('[]')) {
			name = name[0..name.length()-3]
			node.array = true
		}

		// store child name for later reference
		node.name = name

		// closes a previous array?
		if (previous?.array && previous.name != name) {
			writer << ']'
		}

		// need a comma?
		if (previous != null) {
			writer << ','
		}

		if (prettyPrint) {
			writer << '\n'
			node.level.times { writer << INDENT }
		}

		// a named node
		// write name if not a continued array
		if (previous == null || !(previous.array && node.array && previous.name == node.name)) {
			writer << "\"${StringEscapeUtils.escapeJava(name)}\""
			writer << ':'
		}

		// starts an array?
		if (node.array && (previous == null || previous.name != name)) {
			writer << '['
		}

		if (subClosure) {
			// JSON object
			internalWriteStartJsonObject(node, attributes)
		}
		else {
			if (params) {
				// JSON value

				// there should be no named arguments
				if (attributes) {
					throw new IllegalStateException('Not allowed to provide both value and named parameters')
				}

				if (params.size() == 1) {
					// single object as value
					writer << internalToJson(params[0])
				} else {
					// list as value
					writer << internalToJson(params)
				}
			}
			else {
				// JSON object w/ or w/o additional attributes
				internalWriteStartJsonObject(node, attributes)
			}
		}

		node
	}

	private def internalWriteStartJsonObject(NodeState node, Map attributes) {
		// JSON object with or w/o key:value
		writer << '{'
		node.object = true;

		if (attributes) {
			// write attributes
			attributes.eachWithIndex { String key, def value, int index ->
				if (index > 0) {
					writer << ','
				}
				if (prettyPrint) {
					writer << '\n'
					(node.level + 1).times { writer << INDENT }
				}
				writer << internalToJson(key)
				writer << ':'
				writer << internalToJson(value)
			}

			if (prettyPrint) {
				writer << '\n'
			}

			// mark node to already have children (so the comma is written appropriately)
			node.lastChild = new NodeState()
		}
	}

	@CompileStatic(TypeCheckingMode.SKIP)
	private static def internalToJson(object) {
		// not compiled static to allow method selection based on type
		JsonOutput.toJson(object)
	}

	@Override
	protected void internalNodeWrapup(Object node) {
		if (node == null) {
			return
		}

		NodeState state = (NodeState) node

		// close an array (if the last child is in an array)
		if (state.lastChild != null && state.lastChild.array) {
			writer << ']'
		}

		// close an object (if the node is an object)
		if (state.object) {
			if (prettyPrint) {
				writer << '\n'
				state.level.times { writer << INDENT }
			}
			writer << '}'
		}

		// close root (if the node is a root node not created by #call(Closure))
		if (state.root) {
			if (prettyPrint) {
				writer << '\n'
			}
			writer << '}'
		}

		// remove child reference (allow clean-up)
		state.lastChild = null
	}

	@Override
	protected Object internalExtractNode(Object node) {
		// don't expose nodes!
		// return null instead
		null
	}

}
