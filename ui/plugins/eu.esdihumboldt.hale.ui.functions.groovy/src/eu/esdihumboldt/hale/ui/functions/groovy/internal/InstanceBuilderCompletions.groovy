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

import java.util.regex.Matcher

import org.codehaus.groovy.ast.DynamicVariable
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.syntax.Types
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.contentassist.CompletionProposal
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.swt.graphics.Image

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.groovy.Gremlin

import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages
import eu.esdihumboldt.hale.ui.util.groovy.GroovyCompletionProposals
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphConstants
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphUtil
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

/**
 * Completion proposals for instance builders.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class InstanceBuilderCompletions implements GroovyCompletionProposals, ASTGraphConstants {

	static {
		Gremlin.load()
	}

	private final String variableName = GroovyUtil.BINDING_TARGET

	private final DefinitionImages images

	/**
	 * Constructor.
	 * 
	 * @param images the definition images, may be <code>null</code>
	 */
	InstanceBuilderCompletions(DefinitionImages images) {
		this.images = images
	}

	@Override
	public Iterable<? extends ICompletionProposal> computeProposals(ITextViewer viewer,
			GroovyAST ast, int line, int column, int offset) {
		TypeDefinition type = getTargetType()
		if (!type) {
			return null
		}

		Vertex v = ASTGraphUtil.findAt(ast.getRootVertices(), line, column)

		if (v != null) {
			//XXX debug
			println "Vertex: ${v.getProperty(P_AST_TYPE)}"

			// in general only certain vertices are acceptable as start point
			Object node = v.getProperty(P_AST_NODE)
			String prefix = null;
			if (node instanceof ClosureExpression || node instanceof BlockStatement) {
				prefix = ''
			}
			else if (node instanceof VariableExpression) {
				VariableExpression varExpr = (VariableExpression) node
				String var = varExpr.accessedVariable.name
				// determine variable name before cursor
				int cut = varExpr.lastColumnNumber - column
				prefix = var.substring(0, var.length() - cut)
			}

			//XXX debug
			// println "Prefix: $prefix"

			if (prefix != null) {
				/*
				 * Detect if we are inside the closure that will be handed to the
				 * instance builder.
				 */
				def paths = v.out(E_PARENT).loop(1) {
					// loop while...
					!isBuilderClosureAssignment(it.object)
				}.path.toList()

				//XXX debug
				// println "Paths: $paths"

				if (paths) {
					// TODO analyse path
					// TODO determine current type

					// determine current indent
					String indent = ''
					int lineOffset = viewer.getDocument().getLineOffset(line - 1)
					String lineStart = viewer.getDocument().get(lineOffset, column - 1)
					Matcher matcher = lineStart =~ /^(\s+).*/
					if (matcher.find()) {
						indent = matcher.group(1)
					}

					// create proposals
					return createProposals(type, offset, indent, prefix)
				}
			}
		}

		return null
	}

	@CompileStatic
	protected Iterable<? extends ICompletionProposal> createProposals(final TypeDefinition type,
			int offset, String baseIndent, String namePrefix) {
		Collection<? extends PropertyDefinition> properties = DefinitionUtil.getAllProperties(type)
		final DefinitionImages images = this.images

		// filter
		if (namePrefix) {
			properties = properties.findAll { PropertyDefinition property ->
				property.name.localPart.startsWith(namePrefix)
			}
		}

		// sort
		List sorted = properties.sort()

		List<ICompletionProposal> result = []

		// create proposals
		sorted.each { PropertyDefinition property ->
			String name = property.name.localPart

			List proposalAndCursor = createProposal(property, type, baseIndent)

			Image image;
			if (images) {
				image = images.getImage(property)
			}
			else {
				image = null
			}

			String proposal = proposalAndCursor[0] as String
			int cursor = proposalAndCursor[1] as int

			ICompletionProposal prop = new CompletionProposal( //
					proposal, // replacement string
					offset - namePrefix.length(), // replacement position
					namePrefix.length(), // length of the text to be replaced
					// new cursor position relative to replacement position
					cursor, //
					image, // image
					name, // display string
					null, // context information
					'Property') // additional info

			result << prop
		}

		result
	}

	/**
	 * Determine replacement string and cursor offset for a property proposal.
	 * 
	 * @param property the property
	 * @param useNamespace if the namespace should be explicitly used
	 * @return a list with two entries, the replacement string and the cursor offset
	 */
	@CompileStatic
	protected List createProposal(PropertyDefinition property, TypeDefinition parent,
			String baseIndent) {
		def result = []

		StringBuilder code = new StringBuilder()
		int cursor = InstanceBuilderCode.appendBuildProperties(code, baseIndent, PathTree.create(property),
				parent, true, false, false, false)

		result << code.toString()
		result << cursor

		result
	}

	/**
	 * Checks if the given vertex represents the closure assignment of the
	 * builder closure variable.
	 * 
	 * @param v the vertex
	 * @return if the vertex represents the builder closure assignment
	 */
	@CompileStatic
	private boolean isBuilderClosureAssignment(Vertex v) {
		if (v.getProperty(P_AST_TYPE) != 'BinaryExpression') {
			// not a binary expression
			return false
		}

		BinaryExpression expr = (BinaryExpression) v.getProperty(P_AST_NODE)
		if (expr.operation.type != Types.ASSIGN) {
			// not an assignment
			return false
		}

		// check if variable matches
		boolean leftOk = false
		if (expr.leftExpression instanceof VariableExpression) {
			VariableExpression varExpr = (VariableExpression) expr.leftExpression
			Variable var = varExpr.accessedVariable
			leftOk = var.name == variableName && var instanceof DynamicVariable
		}
		if (!leftOk) {
			return false
		}

		// check if assignment is a closure
		return expr.rightExpression instanceof ClosureExpression
	}

	/**
	 * Determine the target type of the instance to build.
	 * 
	 * @return the target type
	 */
	protected abstract TypeDefinition getTargetType()
}
