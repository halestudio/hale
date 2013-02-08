/*
 * Copyright (c) 2013 Fraunhofer IGD
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

package eu.esdihumboldt.hale.io.xslt.transformations.type.retype

import javax.xml.stream.XMLStreamException

import com.google.common.collect.ArrayListMultimap
import com.tinkerpop.blueprints.Vertex

import de.cs3d.util.logging.ALogger
import de.cs3d.util.logging.ALoggerFactory
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.Priority
import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants.NodeType
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation
import eu.esdihumboldt.hale.io.xslt.XsltConstants
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction
import eu.esdihumboldt.hale.io.xslt.functions.impl.XslVariableImpl
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractTransformationTraverser

/**
 * Traverses the transformation graph to create a XSL fragment to populate the
 * target properties.
 * 
 * @author Simon Templer
 */
class RetypeTraverser extends AbstractTransformationTraverser implements XsltConstants {

	private static final ALogger log = ALoggerFactory.getLogger(RetypeTraverser);

	private final Writer writer

	private final LinkedList tagsToClose = new LinkedList()

	private final LinkedList contexts = new LinkedList()

	private final XsltGenerationContext xsltContext

	private Vertex rootContext

	/**
	 * @param writer
	 */
	RetypeTraverser(XsltGenerationContext xsltContext, Writer writer) {
		/*
		 * NOTE: Uses to be a XMLStreamWriter, but it didn't support writing
		 * unescaped XML fragments which is needed to include the function
		 * sequences.
		 */
		this.writer = writer
		this.xsltContext = xsltContext
	}

	@Override
	public void traverse(TGraph graph) {
		tagsToClose.clear()
		contexts.clear()

		/*
		 * XXX Currently no context for the target type, so we have to set
		 * it manually.
		 *
		 * XXX this only is valid for Retype!
		 */
		// select any source node from the graph
		def node = graph.graph.V(P_TYPE, NodeType.Source).next()
		// find the source root
		def ctxs = node.in(EDGE_CHILD).loop(1){it.object.inE(EDGE_CHILD).hasNext()}.toList()
		assert ctxs.size() <= 1
		if (ctxs)
			rootContext = ctxs[0]
		else
			rootContext = node

		super.traverse(graph);
	}

	@Override
	protected void visitProperty(Vertex node) {
		StringBuilder closeTags = new StringBuilder()

		// determine node context & parent context
		Vertex context = node.context()
		Vertex parentContext = node.parentContext()

		if (!parentContext) {
			parentContext = rootContext
		}
		if (!context) {
			// if no context is set, assume it's the same as the parent context
			context = parentContext
		}

		// determine context path in relation to parent context for use in for-each
		String selectContext = selectNode(context, parentContext)

		// specifies if an attribute or element should be written
		final boolean attribute;
		// specifies the maximum number of target occurrences
		final int targetMaxNum;

		if (node.definition().asProperty() &&
		node.definition().asProperty().getConstraint(XmlAttributeFlag).enabled) {
			// attribute
			attribute = true
			targetMaxNum = 1
		}
		else {
			// group or element
			attribute = false
			targetMaxNum = node.cardinality().maxOccurs
		}

		if (context != parentContext) {
			// restrict selection according to targetMaxNum
			//XXX for now only if target may occur only once
			//TODO compare with source cardinality
			if (targetMaxNum == 1 && context.cardinality().mayOccurMultipleTimes()) {
				// select only the first
				selectContext += '[1]'
			}

			// select the context and loop if applicable
			writer << """<xsl:for-each select="$selectContext">"""
			closeTags.insert(0, '</xsl:for-each>')
		}

		// start the element/attribute
		def local = node.definition().name.localPart
		def namespace = node.definition().name.namespaceURI
		if (namespace) {
			def prefix = xsltContext.namespaceContext.getPrefix(namespace)
			if (prefix) {
				// add prefix
				local = "${prefix}:${local}"
			}
		}
		writer << "<xsl:${attribute ? 'attribute' : 'element'} name=\"$local\""
		if (namespace)
			writer << " namespace=\"$namespace\""
		writer << '>'
		closeTags.insert(0, "</xsl:${attribute ? 'attribute' : 'element'}>")
		//XXX do this always? For now assumption is this is needed further down

		// push information for leaveProperty
		tagsToClose.push(closeTags.toString())
		contexts.push(context)
	}

	@Override
	protected void leaveProperty(Vertex node) {
		/*
		 * Determine property value when leaving the property, so eventual
		 * child attributes are created before the property content. 
		 */
		Vertex context = contexts.pop()

		// retrieve cell
		def cells = node.in(EDGE_RESULT).toList()
		if (cells) {
			if (cells.size() == 1) {
				// write the content directly to the element
				Vertex cellNode = cells[0]
				writer << createResultFragment(cellNode, context)
			}
			else {
				// create variable with different results
				writer << '<xsl:variable name="results">'

				// order by priority
				Collections.sort(cells, Collections.reverseOrder(new Comparator<Vertex>() {
							public int compare(Vertex o1, Vertex o2) {
								Cell c1 = o1.cell()
								Cell c2 = o2.cell()
								return Priority.compare(c1.getPriority(), c2.getPriority());
							}
						}))
				for (Vertex cellNode in cells) {
					// create result tag for each cell
					writer << '<result>'
					writer << createResultFragment(cellNode, context)
					writer << '</result>'
				}

				writer << '</xsl:variable>'

				// select non-empty result
				/*
				 * XXX how to know if to use value-of or copy-of? from schema?
				 * No - we could e.g. have a structural rename and a rename. Do both?
				 */
				/*
				 * TODO create example where a structural rename and a rename
				 * or assign compete 
				 */
				// copy all child elements of the first non-empty result node
				//TODO verify if this actually works
				writer << '<xsl:copy-of select="$results/result[node()][1]/*" />'
				// copy value of the first non-empty result node
				writer << '<xsl:value-of select="$results/result[node()][1]" />'
			}

			//XXX what about proxies? not handled yet anywhere in traverser
		}
		else {
			// XXX what can be done if there is no context?
		}

		String closeTags = tagsToClose.pop()
		writer << closeTags
	}

	private String createResultFragment(Vertex cellNode, Vertex context) {
		Cell cell = cellNode.cell()

		// get associated property transformation
		XslPropertyTransformation xpt = xsltContext.getPropertyTransformation(
				cell.transformationIdentifier)

		// ...and the function to apply
		XslFunction function = xpt.selectFunction(cell)
		def variables = ArrayListMultimap.create()
		def varPaths = cellNode.inE(EDGE_VARIABLE).outV.path.toList()
		for (varPath in varPaths) {
			assert varPath.size() == 3
			def names = varPath[1].getProperty(P_VAR_NAMES)
			def sourceNode = varPath[2]

			def sourceXPath = selectNode(sourceNode, context)
			for (name in names) {
				variables.put(name, new XslVariableImpl(sourceNode.entity(), sourceXPath))
			}
		}

		return function.getSequence(cell, variables, xsltContext)
	}

	/**
	 * Select a (source) node in relation to a context node.
	 *  
	 * @param node the node to select
	 * @param context the context node
	 * @return
	 */
	private String selectNode(Vertex node, Vertex context) {
		if (node == context)
			'.'
		else {
			/*
			 * Determine path from context to node.
			 * 
			 * For this determine a path from node to context, as on this path
			 * there are no other routes.
			 */
			def paths = node.in(EDGE_CHILD).loop(1) {
				it.object != context && it.object.inE(EDGE_CHILD).hasNext()
			}.path.toList()
			assert paths.size() == 1
			// get and reverse the path
			def path = paths.first().reverse()
			assert path.last() == node
			assert path.first() == context

			// remove context item from path
			path = path.subList(1, path.size())

			// build XPath expression from path
			StringBuilder xpath = new StringBuilder()
			for (Vertex element in path) {
				if (xpath.length() > 0) {
					// separator
					xpath.append('/')
				}

				if (element.definition().getConstraint(XmlAttributeFlag).enabled) {
					// reference to an attribute
					xpath.append('@')
				}

				// name
				String ns = element.definition().name.namespaceURI;
				if (ns != null && !ns.isEmpty()) {
					String prefix = xsltContext.namespaceContext.getPrefix(ns);
					if (prefix) {
						xpath.append("$prefix:");
					}
				}
				xpath.append(element.definition().name.localPart);

				//XXX what about contexts? conditions, index etc
			}
			xpath.toString()
		}
	}

	@Override
	protected void handleUnmappedProperty(ChildDefinition<?> child) {
		if (DefinitionUtil.getCardinality(child).minOccurs > 0) {
			if (child.asProperty() != null) {
				if (child.asProperty().getConstraint(XmlAttributeFlag).enabled) {
					// a mandatory attribute
					log.warn("No mapping for a mandatory attribute");
				}
				else {
					// a mandatory element
					if (child.asProperty().getConstraint(NillableFlag).enabled) {
						// a nillable mandatory element
						try {
							def name = child.name.localPart
							def prefix = xsltContext.namespaceContext.getPrefix(
									child.name.namespaceURI)
							if (prefix) {
								prefix << ':'
							}

							writer << "<$prefix$name xsi:nil=\"true\" />"
						} catch (XMLStreamException e) {
							throw new IllegalStateException(e);
						}
					}
					else {
						// a mandatory element
						log.warn("No mapping for a mandatory element");
					}
				}
			}
			else {
				// a mandatory group
				log.warn("No mapping for a mandatory group");
			}
		}
	}
}
