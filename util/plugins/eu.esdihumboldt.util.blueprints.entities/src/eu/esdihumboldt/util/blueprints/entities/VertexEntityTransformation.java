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

package eu.esdihumboldt.util.blueprints.entities;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.AbstractASTTransformUtil;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * TODO Type description
 * 
 * @author Simon Templer
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class VertexEntityTransformation implements ASTTransformation {

	private static final ClassNode VERTEX_CLASS = ClassHelper.make(Vertex.class);
	private static final ClassNode GRAPH_CLASS = ClassHelper.make(Graph.class);
	private static final ClassNode ORIENT_GRAPH_CLASS = ClassHelper.make(OrientGraph.class);
	private static final ClassNode VERTEX_ENTITY_CLASS = ClassHelper.make(VertexEntity.class);

	@Override
	public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		if (sourceUnit.getAST() == null)
			return;

		List<ClassNode> classes = sourceUnit.getAST().getClasses();
		for (ClassNode clazz : classes) {
			// find all classes annotated with @ODocumentEntity
			List<AnnotationNode> entityAnnotations = clazz.getAnnotations(VERTEX_ENTITY_CLASS);
			if (entityAnnotations != null && !entityAnnotations.isEmpty()) {
				Expression entityName = entityAnnotations.get(0).getMember("value");

				// add the vertex field
				FieldNode vertexField = clazz.addField("v", Modifier.PRIVATE | Modifier.FINAL,
						VERTEX_CLASS, null);

				// add the vertex field
				FieldNode graphField = clazz.addField("g", Modifier.PRIVATE | Modifier.FINAL,
						GRAPH_CLASS, null);

				// add constructor
				clazz.addConstructor(buildVertexConstructor(vertexField, graphField));

				// get all non-static properties
				List<PropertyNode> properties = AbstractASTTransformUtil
						.getInstanceProperties(clazz);
				List<PropertyNode> newProperties = new ArrayList<>();
				for (PropertyNode property : properties) {
					// TODO check for "transient" properties?

					property.setGetterBlock(createGetter(property.getName(), vertexField));
					property.setSetterBlock(createSetter(property.getName(), vertexField));
					newProperties.add(property);

				}
				// readd updated properties
				for (PropertyNode property : newProperties) {
					readdProperty(clazz, property);
				}

				// add the vertex getter
				clazz.addMethod("getV", Modifier.PUBLIC, VERTEX_CLASS, //
						new Parameter[0], new ClassNode[0], new ReturnStatement(
								new FieldExpression(vertexField)));

				// add static create method
				clazz.addMethod(buildCreateMethod(clazz, entityName));
			}
		}
	}

	/**
	 * Create a static method that allows creating a new vertex and adding it to
	 * a graph.
	 * 
	 * @param clazz the entity class
	 * @param entityName the entity name
	 * @return the static create method taking a graph as an argument
	 */
	private MethodNode buildCreateMethod(ClassNode clazz, Expression entityName) {
		clazz = ClassHelper.make(clazz.getName());

		BlockStatement code = new BlockStatement();

		// graph
		VariableExpression graph = new VariableExpression("graph");
		// graph.addVertex(entityName)
		MethodCallExpression vertex = new MethodCallExpression(graph, new ConstantExpression(
				"addVertex"), new ArgumentListExpression(entityName));

		code.addStatement(new ReturnStatement(new ConstructorCallExpression(clazz,
				new ArgumentListExpression(vertex, graph))));

		return new MethodNode("create", Modifier.STATIC | Modifier.PUBLIC, clazz,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph") }, new ClassNode[0], code);
	}

	/**
	 * Create a constructor taking a Vertex and a Graph as an argument,
	 * assigning them to the vertex and graph fields.
	 * 
	 * @param vertexField the vertex field
	 * @param graphField the graph field
	 * @return a constructor taking a Vertex as an argument
	 */
	private ConstructorNode buildVertexConstructor(FieldNode vertexField, FieldNode graphField) {
		BlockStatement block = new BlockStatement();

		// this.v = v
		block.addStatement(AbstractASTTransformUtil.assignStatement(
				new FieldExpression(vertexField), new VariableExpression("v")));
		// this.g = g
		block.addStatement(AbstractASTTransformUtil.assignStatement(
				new FieldExpression(graphField), new VariableExpression("g")));

		return new ConstructorNode(Modifier.PUBLIC, new Parameter[] {
				new Parameter(VERTEX_CLASS, "v"), new Parameter(GRAPH_CLASS, "g") },
				new ClassNode[0], block);
	}

	/**
	 * Add the given property and clean up the old with the same name.
	 * 
	 * @param clazz the class node to add the property to
	 * @param property the property node
	 */
	private void readdProperty(ClassNode clazz, PropertyNode property) {
		clazz.getProperties().remove(property);
		final FieldNode fn = property.getField();
		clazz.getFields().remove(fn);
		clazz.addProperty(property.getName(), property.getModifiers(), property.getType(),
				property.getInitialExpression(), property.getGetterBlock(),
				property.getSetterBlock());
		final FieldNode newfn = clazz.getField(fn.getName());
		clazz.getFields().remove(newfn);
		// the field is not needed
//      cNode.addField(fn);
	}

	private Statement createSetter(String name, FieldNode documentField) {
		BlockStatement block = new BlockStatement();

		/*
		 * Calls document.field(name, value)
		 */
		ArgumentListExpression args = new ArgumentListExpression();

		args.addExpression(new ConstantExpression(name));
		// XXX not sure if value is the parameter name
		args.addExpression(new VariableExpression("value"));

		block.addStatement(new ExpressionStatement(new MethodCallExpression(new FieldExpression(
				documentField), "field", args)));

		return block;
	}

	private Statement createGetter(String name, FieldNode documentField) {
		BlockStatement block = new BlockStatement();

		/*
		 * Calls document.field(name)
		 */
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new ConstantExpression(name));
		block.addStatement(new ExpressionStatement(new MethodCallExpression(new FieldExpression(
				documentField), "field", args)));

		return block;
	}
}
