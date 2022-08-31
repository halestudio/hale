/*
 * Copyright 2003-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.esdihumboldt.hale.ui.util.groovy.ast

import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ConstructorNode
import org.codehaus.groovy.ast.DynamicVariable
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.PropertyNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ArrayExpression
import org.codehaus.groovy.ast.expr.AttributeExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.CastExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ClosureListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.FieldExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.MethodPointerExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.NotExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.RangeExpression
import org.codehaus.groovy.ast.expr.SpreadExpression
import org.codehaus.groovy.ast.expr.SpreadMapExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.AssertStatement
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.BreakStatement
import org.codehaus.groovy.ast.stmt.CaseStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ContinueStatement
import org.codehaus.groovy.ast.stmt.DoWhileStatement
import org.codehaus.groovy.ast.stmt.EmptyStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.SwitchStatement
import org.codehaus.groovy.ast.stmt.SynchronizedStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.ast.stmt.WhileStatement
import org.codehaus.groovy.classgen.BytecodeExpression
import org.codehaus.groovy.control.SourceUnit


/**
 * This AST visitor builds up a tree of nodes.
 * Based on the <code>TreeNodeBuildingVisitor</code> class part of Groovy.
 *
 * @author Hamlet D'Arcy
 */
abstract class AbstractASTTreeVisitor<N> extends ClassCodeVisitorSupport {

	private N currentNode

	/**
	 * Creates the visitor.
	 */
	AbstractASTTreeVisitor() {
		currentNode = null
	}

	/**
	 * This method looks at the AST node and decides how to represent it in a TreeNode, then it
	 * continues walking the tree. If the node and the expectedSubclass are not exactly the same
	 * Class object then the node is not added to the tree. This is to eliminate seeing duplicate
	 * nodes, for instance seeing an ArgumentListExpression and a TupleExpression in the tree, when
	 * an ArgumentList is-a Tuple.
	 */
	protected void addNode(def node, Class expectedSubclass, Closure superMethod) {

		if (expectedSubclass == null || expectedSubclass.getName() == node.getClass().getName()) {
			if (currentNode == null) {
				currentNode = createNode(node)
				if (currentNode != null) {
					addRootNode(currentNode)
					superMethod.call(node)
				}
				currentNode = null
			} else {
				// visitor works off void methods... so we have to
				// perform a swap to get accumulation like behavior.
				def temp = currentNode;
				currentNode = createNode(node)

				if (currentNode != null) {
					setParent(currentNode, temp)

					superMethod.call(node)
				}

				currentNode = temp
			}
		} else {
			superMethod.call(node)
		}
	}

	/**
	 * Called when a root node has been encountered.
	 * 
	 * @param node the root node
	 */
	protected void addRootNode(N node) {
		// override me
	}

	/**
	 * Create a tree node from the given AST node.
	 * 
	 * @param node the AST node, an {@link ASTNode}, a {@link Parameter}
	 *   or {@link DynamicVariable}
	 * @return the tree node, <code>null</code> to reject the AST node
	 */
	public abstract N createNode(def node)

	/**
	 * Set the parent of a tree node.
	 * @param node the node to set the parent of
	 * @param parent the parent to set
	 */
	public abstract void setParent(N node, N parent)

	public void visitBlockStatement(BlockStatement node) {
		addNode(node, BlockStatement, { super.visitBlockStatement(it) });
	}

	public void visitForLoop(ForStatement node) {
		addNode(node, ForStatement, { super.visitForLoop(it) });
	}

	public void visitWhileLoop(WhileStatement node) {
		addNode(node, WhileStatement, { super.visitWhileLoop(it) });
	}

	public void visitDoWhileLoop(DoWhileStatement node) {
		addNode(node, DoWhileStatement, { super.visitDoWhileLoop(it) });
	}

	public void visitIfElse(IfStatement node) {
		addNode(node, IfStatement, { super.visitIfElse(it) });
	}

	public void visitExpressionStatement(ExpressionStatement node) {
		addNode(node, ExpressionStatement, { super.visitExpressionStatement(it) });
	}

	public void visitReturnStatement(ReturnStatement node) {
		addNode(node, ReturnStatement, { super.visitReturnStatement(it) });
	}

	public void visitAssertStatement(AssertStatement node) {
		addNode(node, AssertStatement, { super.visitAssertStatement(it) });
	}

	public void visitTryCatchFinally(TryCatchStatement node) {
		addNode(node, TryCatchStatement, { super.visitTryCatchFinally(it) });
	}

	public void visitEmptyStatement(EmptyStatement node) {
		addNode(node, EmptyStatement, { super.visitEmptyStatement(it) });
	}

	public void visitSwitch(SwitchStatement node) {
		addNode(node, SwitchStatement, { super.visitSwitch(it) });
	}

	public void visitCaseStatement(CaseStatement node) {
		addNode(node, CaseStatement, { super.visitCaseStatement(it) });
	}

	public void visitBreakStatement(BreakStatement node) {
		addNode(node, BreakStatement, { super.visitBreakStatement(it) });
	}

	public void visitContinueStatement(ContinueStatement node) {
		addNode(node, ContinueStatement, { super.visitContinueStatement(it) });
	}

	public void visitSynchronizedStatement(SynchronizedStatement node) {
		addNode(node, SynchronizedStatement, { super.visitSynchronizedStatement(it) });
	}

	public void visitThrowStatement(ThrowStatement node) {
		addNode(node, ThrowStatement, { super.visitThrowStatement(it) });
	}

	public void visitMethodCallExpression(MethodCallExpression node) {
		addNode(node, MethodCallExpression, { super.visitMethodCallExpression(it) });
	}

	public void visitStaticMethodCallExpression(StaticMethodCallExpression node) {
		addNode(node, StaticMethodCallExpression, { super.visitStaticMethodCallExpression(it) });
	}

	public void visitConstructorCallExpression(ConstructorCallExpression node) {
		addNode(node, ConstructorCallExpression, { super.visitConstructorCallExpression(it) });
	}

	public void visitBinaryExpression(BinaryExpression node) {
		addNode(node, BinaryExpression, { super.visitBinaryExpression(it) });
	}

	public void visitTernaryExpression(TernaryExpression node) {
		addNode(node, TernaryExpression, { super.visitTernaryExpression(it) });
	}

	public void visitShortTernaryExpression(ElvisOperatorExpression node) {
		addNode(node, ElvisOperatorExpression, { super.visitShortTernaryExpression(it) });
	}

	public void visitPostfixExpression(PostfixExpression node) {
		addNode(node, PostfixExpression, { super.visitPostfixExpression(it) });
	}

	public void visitPrefixExpression(PrefixExpression node) {
		addNode(node, PrefixExpression, { super.visitPrefixExpression(it) });
	}

	public void visitBooleanExpression(BooleanExpression node) {
		addNode(node, BooleanExpression, { super.visitBooleanExpression(it) });
	}

	public void visitNotExpression(NotExpression node) {
		addNode(node, NotExpression, { super.visitNotExpression(it) });
	}

	public void visitClosureExpression(ClosureExpression node) {
		addNode(node, ClosureExpression, {
			it.parameters?.each { parameter -> visitParameter(parameter) }
			super.visitClosureExpression(it)
		});
	}

	/**
	 * Makes walking parameters look like others in the visitor.
	 */
	public void visitParameter(Parameter node) {
		addNode(node, Parameter, {
			if (node.initialExpression) {
				node.initialExpression?.visit(this)
			}
		});
	}

	public void visitTupleExpression(TupleExpression node) {
		addNode(node, TupleExpression, { super.visitTupleExpression(it) });
	}

	public void visitListExpression(ListExpression node) {
		addNode(node, ListExpression, { super.visitListExpression(it) });
	}

	public void visitArrayExpression(ArrayExpression node) {
		addNode(node, ArrayExpression, { super.visitArrayExpression(it) });
	}

	public void visitMapExpression(MapExpression node) {
		addNode(node, MapExpression, { super.visitMapExpression(it) });
	}

	public void visitMapEntryExpression(MapEntryExpression node) {
		addNode(node, MapEntryExpression, { super.visitMapEntryExpression(it) });
	}

	public void visitRangeExpression(RangeExpression node) {
		addNode(node, RangeExpression, { super.visitRangeExpression(it) });
	}

	public void visitSpreadExpression(SpreadExpression node) {
		addNode(node, SpreadExpression, { super.visitSpreadExpression(it) });
	}

	public void visitSpreadMapExpression(SpreadMapExpression node) {
		addNode(node, SpreadMapExpression, { super.visitSpreadMapExpression(it) });
	}

	public void visitMethodPointerExpression(MethodPointerExpression node) {
		addNode(node, MethodPointerExpression, { super.visitMethodPointerExpression(it) });
	}

	public void visitUnaryMinusExpression(UnaryMinusExpression node) {
		addNode(node, UnaryMinusExpression, { super.visitUnaryMinusExpression(it) });
	}

	public void visitUnaryPlusExpression(UnaryPlusExpression node) {
		addNode(node, UnaryPlusExpression, { super.visitUnaryPlusExpression(it) });
	}

	public void visitBitwiseNegationExpression(BitwiseNegationExpression node) {
		addNode(node, BitwiseNegationExpression, { super.visitBitwiseNegationExpression(it) });
	}

	public void visitCastExpression(CastExpression node) {
		addNode(node, CastExpression, { super.visitCastExpression(it) });
	}

	public void visitConstantExpression(ConstantExpression node) {
		addNode(node, ConstantExpression, { super.visitConstantExpression(it) });
	}

	public void visitClassExpression(ClassExpression node) {
		addNode(node, ClassExpression, { super.visitClassExpression(it) });
	}

	public void visitVariableExpression(VariableExpression node) {
		addNode(node, VariableExpression, { VariableExpression it ->
			if (it.accessedVariable) {
				if (it.accessedVariable instanceof Parameter) {
					visitParameter((Parameter)it.accessedVariable)
				} else if (it.accessedVariable instanceof DynamicVariable) {
					addNode(it.accessedVariable, DynamicVariable,{ it.initialExpression?.visit(this)});
				}
			}
		});
	}

	public void visitDeclarationExpression(DeclarationExpression node) {
		addNode(node, DeclarationExpression, { super.visitDeclarationExpression(it) });
	}

	public void visitPropertyExpression(PropertyExpression node) {
		addNode(node, PropertyExpression, { super.visitPropertyExpression(it) });
	}

	public void visitAttributeExpression(AttributeExpression node) {
		addNode(node, AttributeExpression, { super.visitAttributeExpression(it) });
	}

	public void visitFieldExpression(FieldExpression node) {
		addNode(node, FieldExpression, { super.visitFieldExpression(it) });
	}

	public void visitGStringExpression(GStringExpression node) {
		addNode(node, GStringExpression, { super.visitGStringExpression(it) });
	}

	public void visitCatchStatement(CatchStatement node) {
		addNode(node, CatchStatement, {
			if (it.variable) visitParameter(it.variable)
			super.visitCatchStatement(it)
		});
	}

	public void visitArgumentlistExpression(ArgumentListExpression node) {
		addNode(node, ArgumentListExpression, { super.visitArgumentlistExpression(it) });
	}

	public void visitClosureListExpression(ClosureListExpression node) {
		addNode(node, ClosureListExpression, { super.visitClosureListExpression(it) });
	}

	public void visitBytecodeExpression(BytecodeExpression node) {
		addNode(node, BytecodeExpression, { super.visitBytecodeExpression(it) });
	}

	public void visitListOfExpressions(List<? extends Expression> list) {
		list.each { Expression node ->
			if (node instanceof NamedArgumentListExpression ) {
				addNode(node, NamedArgumentListExpression, { it.visit(this) });
			} else {
				node.visit(this)
			}
		}
	}

	public void visitClass(ClassNode node) {
		addNode(node, ClassNode, { super.visitClass(it) });
	}

	public void visitConstructor(ConstructorNode node) {
		// do not include constructors of script class
		if (!node.declaringClass.name.startsWith('script'))
			addNode(node, ConstructorNode, { super.visitConstructor(it) });
	}

	public void visitMethod(MethodNode node) {
		// do not include this$dist$(invoke|get|set)-methods
		// do not include static main-method of script class
		if (!node.name.startsWith('this$dist$') && (!node.name.equals('main') || !node.static || !node.declaringClass.name.startsWith('script')))
			addNode(node, MethodNode, { super.visitMethod(it) });
	}

	public void visitField(FieldNode node) {
		addNode(node, FieldNode, { super.visitField(it) });
	}

	public void visitProperty(PropertyNode node) {
		addNode(node, PropertyNode, { super.visitProperty(it) });
	}

	@Override
	protected SourceUnit getSourceUnit() {
		return null;
	}
}
