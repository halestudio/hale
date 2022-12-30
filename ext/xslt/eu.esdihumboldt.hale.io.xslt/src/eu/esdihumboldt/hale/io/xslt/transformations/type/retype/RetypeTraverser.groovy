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

import javax.xml.namespace.QName
import javax.xml.stream.XMLStreamException

import org.apache.commons.lang.StringEscapeUtils
import org.apache.tinkerpop.gremlin.structure.Vertex

import com.google.common.collect.ArrayListMultimap

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.hale.common.align.model.Cell
import eu.esdihumboldt.hale.common.align.model.ChildContext
import eu.esdihumboldt.hale.common.align.model.Priority
import eu.esdihumboldt.hale.common.align.tgraph.TGraph
import eu.esdihumboldt.hale.common.align.tgraph.TGraphConstants
import eu.esdihumboldt.hale.common.schema.model.ChildDefinition
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.constraint.property.NillableFlag
import eu.esdihumboldt.hale.io.gml.writer.internal.GmlWriterUtil
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag
import eu.esdihumboldt.hale.io.xslt.XslPropertyTransformation
import eu.esdihumboldt.hale.io.xslt.XsltConstants
import eu.esdihumboldt.hale.io.xslt.XsltGenerationContext
import eu.esdihumboldt.hale.io.xslt.functions.XslFunction
import eu.esdihumboldt.hale.io.xslt.functions.impl.XslVariableImpl
import eu.esdihumboldt.hale.io.xslt.internal.CellXslInfo
import eu.esdihumboldt.hale.io.xslt.transformations.base.AbstractTransformationTraverser
import eu.esdihumboldt.hale.io.xslt.xpath.FilterToXPath

/**
 * Traverses the transformation graph to create a XSL fragment to populate the
 * target properties.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
class RetypeTraverser extends AbstractTransformationTraverser implements XsltConstants {

	private static final ALogger log = ALoggerFactory.getLogger(RetypeTraverser);

	private final Writer writer

	private final LinkedList tagsToClose = new LinkedList()

	private final LinkedList contexts = new LinkedList()

	private final XsltGenerationContext xsltContext

	private Vertex rootContext

	private final Cell typeCell

	/**
	 * @param writer
	 */
	RetypeTraverser(XsltGenerationContext xsltContext, Writer writer, Cell typeCell) {
		/*
		 * NOTE: Used to be a XMLStreamWriter, but it didn't support writing
		 * unescaped XML fragments which is needed to include the function
		 * sequences.
		 */
		this.writer = writer
		this.xsltContext = xsltContext
		this.typeCell = typeCell
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
		def node
		try {
			node = graph.graph.V(P_TYPE, TGraphConstants.NodeType.Source).next()
		} catch (NoSuchElementException e) {
			log.warn('Empty transformation graph, skipping type transformation')
			return
		}
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

		if (selectContext && context != parentContext) {
			// restrict selection according to targetMaxNum
			//XXX for now only if target may occur only once
			//TODO compare with source cardinality
			if (targetMaxNum == 1 && context.cardinality().mayOccurMultipleTimes()) {
				// select only the first
				selectContext += '[1]'
				/*
				 * XXX This may cause problems in cases where source has to
				 * match certain conditions and the first element is not the
				 * one meeting them. Then there is no result, because the wrong
				 * source element was selected.
				 */
			}

			// select the context and loop if applicable
			writer << """<xsl:for-each select="$selectContext">"""
			closeTags.insert(0, '</xsl:for-each>')
		}

		boolean isGroup = node.definition().asGroup()
		boolean hasChildren = DefinitionUtil.hasChildren(node.definition())

		if (!isGroup && hasChildren) {
			// begin variable to store children and value in
			// the variable is used to allow null values
			writer << '<xsl:variable name="children"><children>'
		}

		// push information for leaveProperty
		tagsToClose.push(closeTags.toString())
		contexts.push(context)
	}

	/**
	 * Write the start tag of a XSL attribute or element for the given
	 * qualified name.
	 * 
	 * @param name the element or attribute name
	 * @param attribute if an attribute should be created
	 */
	private void writeXslAttElStart(QName name, boolean attribute) {
		def local = name.localPart
		def namespace = name.namespaceURI
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
	}

	@Override
	protected void leaveProperty(Vertex node) {
		/*
		 * Determine property value when leaving the property, so eventual
		 * child attributes are created before the property content. 
		 */
		Vertex context = contexts.pop()

		boolean isGroup = node.definition().asGroup()
		boolean hasChildren = DefinitionUtil.hasChildren(node.definition())
		boolean isAttribute = node.definition().asProperty() &&
				node.definition().asProperty().getConstraint(XmlAttributeFlag).enabled

		if (!isGroup) {
			if (hasChildren) {
				// a variable was created to store the children

				// close the children variable
				writer << '</children></xsl:variable>'
			}

			// retrieve cell
			def cells = node.in(EDGE_RESULT).toList()
			if (cells) {
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
					writer << CellXslInfo.getInfo(cellNode.cell())
					writer << createResultFragment(cellNode, context)
					writer << '</result>'
				}

				writer << '</xsl:variable>'

				//XXX what about proxies? not handled yet anywhere in traverser
			}

			/*
			 * The element or attribute may only be written if there are
			 * children and/or a value. 
			 */
			// test if there are child elements or attributes in the children variable
			String childrenTest = '$children/children[node() or @*]'
			// test if the special element def:null is not contained, but a value or child elements or attributes
			String resultTest = '$results/result[not(def:null) and (node() or @* or . = \'\')]'
			String test
			if (hasChildren && cells) {
				// children and possible values
				test = "$childrenTest or $resultTest"
			}
			else if (hasChildren) {
				// only children
				test = childrenTest
			}
			else if (cells) {
				// only values or structural copy
				test = resultTest
			}
			else {
				// no children and no values
				test = null
			}

			if (test) {
				writer << "<xsl:if test=\"$test\">"

				// start element/attribute
				writeXslAttElStart(node.definition().name, isAttribute)

				// copy children
				if (hasChildren) {
					writer << """
					<xsl:if test="$childrenTest">
						<!-- Copy children -->
						<xsl:copy-of select="\$children/children/@*, \$children/children/*" />
					</xsl:if>
					"""
				}

				// copy results (only available if there were cells)
				if (cells) {
					writer << """
					<xsl:if test="$resultTest">
						<!-- Copy cell result with highest priority -->
						<xsl:variable name="firstNonNullResult" select="${resultTest}[1]" />
						<xsl:copy-of select="\$firstNonNullResult/@*, \$firstNonNullResult/child::node()" />
					</xsl:if>
					"""
				}

				// end element/attribute
				writer << "</xsl:${isAttribute ? 'attribute' : 'element'}>"

				writer << '</xsl:if>'
			}
			else {
				//TODO create element with xsi:nil if element is nillable?
			}
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

		return function.getSequence(cell, variables, xsltContext, typeCell)
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
				// skip groups
				if (element.definition().asGroup()) {
					// groups are not represented physically
					continue;
				}

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

				// get child context XPath string
				ChildContext childctx = element.entity().propertyPath.last()
				xpath << childContextXPath(childctx)
			}
			xpath.toString()
		}
	}

	private String childContextXPath(ChildContext ctx) {
		if (ctx.index != null) {
			// index context
			return "[${ctx.index + 1}]"
		}

		if (ctx.condition) {
			// condition context
			def filter = ctx.condition.filter
			assert ctx.child.asProperty() // only working on properties
			String xpathFilter = FilterToXPath.toXPath(
					ctx.child.asProperty(),
					xsltContext.namespaceContext,
					filter)
			xpathFilter = StringEscapeUtils.escapeXml(xpathFilter)
			return "[$xpathFilter]"
		}

		''
	}

	@Override
	protected void handleUnmappedProperty(ChildDefinition<?> child) {
		if (DefinitionUtil.getCardinality(child).minOccurs > 0) {
			if (child.asProperty()) {
				if (child.asProperty().getConstraint(XmlAttributeFlag).enabled) {
					// a mandatory attribute
					log.warn("No mapping for a mandatory attribute");

					/*
					 * Special treatment for ID attributes:
					 * Generate a unique identifier
					 */
					boolean isID = GmlWriterUtil.isID(child.asProperty().propertyType)
					if (isID) {
						writeXslAttElStart(child.name, true) // start attribute

						// combine UUID and context ID
						String uid = UUID.randomUUID().toString()
						writer << "<xsl:value-of select=\"concat(generate-id(), '_${uid}')\" />"
						//XXX possible collisions with parent being mapped with structural rename?

						writer << '</xsl:attribute>' // end attribute
					}
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
