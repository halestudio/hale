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
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.syntax.Types
import org.eclipse.jface.text.ITextViewer
import org.eclipse.jface.text.contentassist.CompletionProposal
import org.eclipse.jface.text.contentassist.ICompletionProposal
import org.eclipse.swt.graphics.Image

import com.tinkerpop.blueprints.Vertex

import de.fhg.igd.slf4jplus.ALogger
import de.fhg.igd.slf4jplus.ALoggerFactory
import eu.esdihumboldt.cst.functions.groovy.GroovyConstants
import eu.esdihumboldt.cst.functions.groovy.internal.GroovyUtil
import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor
import eu.esdihumboldt.hale.common.schema.model.Definition
import eu.esdihumboldt.hale.common.schema.model.DefinitionUtil
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.ui.common.definition.DefinitionImages
import eu.esdihumboldt.hale.ui.util.groovy.GroovyCompletionProposals
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphConstants
import eu.esdihumboldt.hale.ui.util.groovy.ast.ASTGraphUtil
import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST
import groovy.transform.CompileStatic

/**
 * Completion proposals for instance builders.
 * 
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class InstanceBuilderCompletions implements GroovyCompletionProposals, ASTGraphConstants {

	//	static {
	//		Gremlin.load()
	//	}

	private static final ALogger log = ALoggerFactory.getLogger(InstanceBuilderCompletions)

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
			// println "Vertex: ${v.getProperty(P_AST_TYPE)}"

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
					!isBuilderClosure(it.object)
				}.path.toList()

				// reverse paths so they start with root nodes
				paths = paths.collect{it.reverse().drop(1)}

				//XXX debug
				// println "Paths: $paths"

				if (paths) {
					// analyse path
					// and determine current type
					type = analysePath(paths[0], type)

					if (type != null) {
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
		}

		return null
	}

	@CompileStatic
	protected TypeDefinition analysePath(List<Vertex> path, TypeDefinition parent) {
		for (Vertex v : path) {
			Object node = v.getProperty(P_AST_NODE)
			if (node instanceof MethodCallExpression) {
				MethodCallExpression mce = (MethodCallExpression) node

				/*
				 * Certain conditions have to match for the method call to
				 * identify a builder call.
				 */

				// check variable
				boolean varOk = mce.implicitThis
				if (!varOk) {
					// builder variable is also OK
					if (mce.receiver instanceof VariableExpression) {
						VariableExpression expr = (VariableExpression) mce.receiver
						varOk = expr.accessedVariable.name == GroovyConstants.BINDING_BUILDER
					}
				}

				if (varOk) {
					if (mce.arguments instanceof ArgumentListExpression) {
						ArgumentListExpression args = (ArgumentListExpression) mce.arguments

						if (args.expressions) {
							// only viable with at least the closure as argument

							Expression shouldBeClosure = args.expressions.last()
							if (shouldBeClosure instanceof ClosureExpression) {
								// determine name
								String name = mce.methodAsString
								String namespace = null

								// ... and namespace
								for (Expression arg : args.expressions) {
									if (arg instanceof MapExpression) {
										// map expression contains the named parameters
										String ns = extractNamespace((MapExpression) arg)
										if (ns != null) {
											namespace = ns
										}
									}
								}

								// find property and set parent
								try {
									Definition child = new DefinitionAccessor(parent).
											findChildren(name, namespace).toDefinition()
									if (child instanceof PropertyDefinition) {
										parent = ((PropertyDefinition) child).propertyType
									}
									else {
										log.warn('Invalid definition for builder path', (Throwable)null)
										return null
									}
								} catch (IllegalStateException e) {
									log.debug 'Unable to determine type for non-unique sub-property', e
									return null
								}
							}
						}
					}
				}
			}
		}

		parent
	}

	@CompileStatic
	private String extractNamespace(MapExpression me) {
		for (MapEntryExpression mee : me.mapEntryExpressions) {
			if (mee.keyExpression instanceof ConstantExpression) {
				String name = ((ConstantExpression) mee.keyExpression).text
				if (name == 'ns' || name == 'namespace') {
					// map key represents namespace

					Expression valueExpr = mee.valueExpression

					//XXX for now only constant namespace supported
					if (valueExpr instanceof ConstantExpression) {
						ConstantExpression constantValue = (ConstantExpression) valueExpr
						if (!constantValue.nullExpression) {
							return constantValue.text
						}
					}
				}
			}
		}

		null
	}

	@CompileStatic
	protected Iterable<? extends ICompletionProposal> createProposals(final TypeDefinition type,
			int offset, String baseIndent, String namePrefix) {
		Collection<PropertyDefinition> properties = (Collection<PropertyDefinition>) DefinitionUtil.getAllProperties(type) // Groovy CompileStatic can't deal properly with ? extends ...
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
	 * Checks if the given vertex represents the closure assignment or a closure call of the
	 * builder closure variable.
	 * 
	 * @param v the vertex
	 * @return if the vertex represents the builder closure assignment
	 */
	@CompileStatic
	private boolean isBuilderClosure(Vertex v) {
		if (v.getProperty(P_AST_TYPE) == 'BinaryExpression') {
			return isBuilderClosureAssignment((BinaryExpression) v.getProperty(P_AST_NODE))
		} else if (v.getProperty(P_AST_TYPE) == 'MethodCallExpression') {
			return isBuilderClosureCall((MethodCallExpression) v.getProperty(P_AST_NODE))
		} else {
			return false
		}
	}

	/**
	 * Checks if the given vertex represents the closure assignment of the
	 * builder closure variable.
	 *
	 * @param v the vertex
	 * @return if the vertex represents the builder closure assignment
	 */
	private boolean isBuilderClosureAssignment(BinaryExpression expr) {
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
	 * Checks if the given vertex represents a closure call of the
	 * builder closure variable.
	 *
	 * @param v the vertex
	 * @return if the vertex represents the builder closure assignment
	 */
	private boolean isBuilderClosureCall(MethodCallExpression expr) {
		// _target {} is an implicit call on the script with method name _target
		if (!expr.implicitThis || variableName != expr.methodAsString) {
			return false
		}

		// check if argument is a closure
		TupleExpression arguments = expr.arguments
		if (arguments.expressions.size() != 1) {
			return false
		}
		return arguments.expressions.get(0) instanceof ClosureExpression
	}

	/**
	 * Determine the target type of the instance to build.
	 * 
	 * @return the target type
	 */
	protected abstract TypeDefinition getTargetType()
}
