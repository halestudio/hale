/*
 * Copyright (c) 2013 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.ui.functions.groovy.internal

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.contentassist.CompletionProposal
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.swt.graphics.Image

import com.google.common.base.Splitter
import com.google.common.collect.Lists
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.groovy.Gremlin

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.cst.functions.groovy.helper.Category
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionOrCategory
import eu.esdihumboldt.cst.functions.groovy.helper.HelperFunctionsService
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil
import eu.esdihumboldt.hale.ui.common.CommonSharedImages
import eu.esdihumboldt.hale.ui.common.CommonSharedImagesConstants
import eu.esdihumboldt.hale.ui.util.groovy.GroovyCompletionProposals
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphConstants
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphUtil
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST
import groovy.transform.CompileStatic

/**
 * Completion proposals for helper functions.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public class HelperFunctionsCompletions implements GroovyCompletionProposals, ASTGraphConstants {

	static {
		Gremlin.load()
	}

	private static final ALogger log = ALoggerFactory.getLogger(HelperFunctionsCompletions)

	private final String variableName = GroovyUtil.BINDING_HELPER_FUNCTIONS

	private final HelperFunctionsService helperFunctions

	/**
	 * Constructor.
	 * 
	 * @param images the definition images, may be <code>null</code>
	 */
	HelperFunctionsCompletions(HelperFunctionsService helperFunctions) {
		this.helperFunctions = helperFunctions
	}

	@Override
	public Iterable<? extends ICompletionProposal> computeProposals(ITextViewer viewer,
			GroovyAST ast, int line, int column, int offset) {
		Vertex v = ASTGraphUtil.findAt(ast.getRootVertices(), line, column)

		if (v != null) {
			//XXX debug
			//println "Vertex: ${v.getProperty(P_AST_TYPE)}"

			// in general only certain vertices are acceptable as start point
			Object node = v.getProperty(P_AST_NODE)
			String prefix = null;
			if (node instanceof PropertyExpression) {
				prefix = ''
			}
			else if (node instanceof ConstantExpression) {
				ConstantExpression constExpr = (ConstantExpression) node
				String var = constExpr.text
				// determine variable name before cursor
				int cut = constExpr.lastColumnNumber - column
				prefix = var.substring(0, var.length() - cut)
			}

			//XXX debug
			//println "Prefix: $prefix"

			if (prefix != null) {
				/*
				 * Detect the node that seems to be the helper functions root.
				 */
				def root = v.out(E_PARENT).loop(1) {
					// loop while...
					!isFunctionsRoot(it.object)
				}.toList()

				if (root) {
					root = root[0]

					// XXX debug
					//println "Root type: ${root.getProperty(P_AST_TYPE)}"
					//println "Root node: ${root.getProperty(P_AST_NODE).text}"

					def rootNode = root.getProperty(P_AST_NODE)

					// extract from a BinaryExpression
					if (rootNode instanceof BinaryExpression) {
						rootNode = ((BinaryExpression) rootNode).leftExpression
					}

					if (rootNode instanceof PropertyExpression) {
						String path = rootNode.text

						// remove last dot to determine parent
						int index = path.lastIndexOf('.')
						if (index >= variableName.length()) {
							path = path.substring(variableName.length(), index)

							def pathList = Splitter.on('.').omitEmptyStrings().splitToList(path)

							// XXX debug
							//println "Path: $pathList"
							//println "Prefix: $prefix"

							return createProposals(new Category(pathList), offset, prefix)
						}
					}

				}
			}
		}

		return null
	}

	@CompileStatic
	protected Iterable<? extends ICompletionProposal> createProposals(final Category cat,
			int offset, String namePrefix) {
		def catsOrFuns = Lists.newArrayList(helperFunctions.getChildren(cat))

		// filter
		if (namePrefix) {
			catsOrFuns = catsOrFuns.findAll { HelperFunctionOrCategory catOrFun ->
				catOrFun.name.startsWith(namePrefix)
			}
		}

		// TODO sort?
		def sorted = catsOrFuns

		List<ICompletionProposal> result = []

		// create proposals
		sorted.each { HelperFunctionOrCategory catOrFun ->
			String name = catOrFun.name

			Image image = null;
			if (catOrFun.asCategory()) {
				image = CommonSharedImages.getImageRegistry().get(CommonSharedImagesConstants.IMG_DEFINITION_GROUP)
			}
			else {
				image = CommonSharedImages.getImageRegistry().get(CommonSharedImagesConstants.IMG_FUNCTION)
			}

			String proposal = name
			int cursor = name.length()

			ICompletionProposal prop = new CompletionProposal( //
					proposal, // replacement string
					offset - namePrefix.length(), // replacement position
					namePrefix.length(), // length of the text to be replaced
					// new cursor position relative to replacement position
					cursor, //
					image, // image
					name, // display string
					null, // context information - how to display?
					null) // additional info - how to display?

			result << prop
		}

		result
	}

	/**
	 * Checks if the given vertex represents the closure assignment or a closure call of the
	 * builder closure variable.
	 * 
	 * @param v the vertex
	 * @return if the vertex represents the builder closure assignment
	 */
	@CompileStatic
	private boolean isFunctionsRoot(Vertex v) {
		def node = v.getProperty(P_AST_NODE)

		if (node instanceof BinaryExpression) {
			node = ((BinaryExpression) node).leftExpression
		}

		if (node instanceof ASTNode) {
			ASTNode n = (ASTNode) node
			return n.text.startsWith("${variableName}.")
		} else {
			return false
		}
	}

}
