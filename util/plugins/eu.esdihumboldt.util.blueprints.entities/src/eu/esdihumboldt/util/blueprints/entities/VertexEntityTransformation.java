/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.util.blueprints.entities;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * Groovy AST transformation that converts POGOs to vertex entities.
 * 
 * @author Simon Templer
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
@SuppressWarnings("deprecation")
public class VertexEntityTransformation implements ASTTransformation {

	private static final ClassNode VERTEX_CLASS = ClassHelper.make(Vertex.class);
	private static final ClassNode GRAPH_CLASS = ClassHelper.make(Graph.class);
	private static final ClassNode ORIENT_GRAPH_CLASS = ClassHelper.make(OrientGraph.class);
	private static final ClassNode VERTEX_ENTITY_CLASS = ClassHelper.make(VertexEntity.class);
	private static final ClassNode ITERABLE_CLASS = ClassHelper.make(Iterable.class);

	private static final ClassNode VE_DELEGATES_CLASS = ClassHelper
			.make(VertexEntityDelegates.class);
	private static final ClassNode VE_ITERABLE_DELEGATE_CLASS = ClassHelper
			.make(IterableDelegate.class);
	private static final ClassNode VE_NON_UNIQUE_EXCEPTION = ClassHelper
			.make(NonUniqueResultException.class);

	private static final Token TOKEN_PLUS = Token.newSymbol(Types.PLUS, -1, -1);

	@Override
	public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		if (sourceUnit.getAST() == null)
			return;

		List<ClassNode> classes = sourceUnit.getAST().getClasses();
		for (ClassNode clazz : classes) {
			processClass(clazz);
		}
	}

	/**
	 * Process a class and make it a vertex entity class.
	 * 
	 * @param clazz the class node
	 */
	private void processClass(ClassNode clazz) {
		// check if class already was processed (has field v)
		if (clazz.getField("v") != null) {
			return;
		}

		// find all classes annotated with @ODocumentEntity
		List<AnnotationNode> entityAnnotations = clazz.getAnnotations(VERTEX_ENTITY_CLASS);
		if (entityAnnotations != null && !entityAnnotations.isEmpty()) {
			Expression entityName = entityAnnotations.get(0).getMember("value");
			Expression typeProperty = entityAnnotations.get(0).getMember("typeProperty");
			if (typeProperty == null) {
				// default value if none given
				typeProperty = new ConstantExpression("_type");
			}

			Expression superEntityName = null;
			FieldNode vertexField = null;
			FieldNode graphField = null;
			ClassNode superClass = clazz.getSuperClass();
			if (superClass != null) {
				List<AnnotationNode> superAnnotations = superClass
						.getAnnotations(VERTEX_ENTITY_CLASS);
				if (superAnnotations != null && !superAnnotations.isEmpty()) {
					// super class is also a vertex entity
					superEntityName = superAnnotations.get(0).getMember("value");

					// super class must be processed first
					processClass(superClass);

					// use fields from super class
					vertexField = clazz.getField("v");
					graphField = clazz.getField("g");
				}
				else {
					superClass = null;
				}
			}

			// add the vertex field
			if (vertexField == null) {
				vertexField = clazz.addField("v", Modifier.PROTECTED, VERTEX_CLASS, null);
			}

			// add the graph field
			if (graphField == null) {
				graphField = clazz.addField("g", Modifier.PROTECTED, GRAPH_CLASS, null);
			}

			// add constructor
			clazz.addConstructor(buildVertexGraphConstructor(vertexField, graphField, superClass,
					typeProperty, entityName));

			Map<String, Expression> initialExpressions = new HashMap<>();

			// get all non-static properties
			List<PropertyNode> properties = GeneralUtils.getInstanceProperties(clazz);
			List<PropertyNode> newProperties = new ArrayList<>();
			for (PropertyNode property : properties) {
				// TODO check for "transient" properties?
				// TODO check not allowed property names, e.g. id, v, g
				// TODO decide on kind of property

				// collect initial expressions for create function
				if (property.getField().getInitialExpression() != null) {
					initialExpressions.put(property.getName(), property.getInitialExpression());
				}

				// add static findByX method
				clazz.addMethod(buildFindByMethod(clazz, entityName, typeProperty,
						property.getName(), property.getType()));

				// add static findUniqueByX method
				clazz.addMethod(buildGetByMethod(clazz, entityName, typeProperty,
						property.getName(), property.getType()));

				// update property
				property.setGetterBlock(createGetter(property.getName(), vertexField,
						property.getType(), property.getField().getInitialExpression()));
				property.setSetterBlock(createSetter(property.getName(), vertexField));
				newProperties.add(property);

			}
			// readd updated properties
			for (PropertyNode property : newProperties) {
				readdProperty(clazz, property);
			}

			// add the vertex getter
			clazz.addMethod("getV", Modifier.PUBLIC, VERTEX_CLASS, //
					new Parameter[0], new ClassNode[0],
					new ReturnStatement(new FieldExpression(vertexField)));

			// add the graph getter
			clazz.addMethod("getG", Modifier.PUBLIC, GRAPH_CLASS, //
					new Parameter[0], new ClassNode[0],
					new ReturnStatement(new FieldExpression(graphField)));

			// add the id getter
			clazz.addMethod("getId", Modifier.PUBLIC, ClassHelper.OBJECT_TYPE, new Parameter[0],
					new ClassNode[0],
					new ReturnStatement(new MethodCallExpression(new FieldExpression(vertexField),
							"getId", new ArgumentListExpression())));

			// add delete method
			clazz.addMethod(buildDeleteMethod(vertexField, graphField));

			// add static create method
			clazz.addMethod(buildCreateMethod(clazz, entityName, initialExpressions));

			// add static findAll method
			clazz.addMethod(buildFindAllMethod(clazz, entityName, typeProperty));

			// add static getById method
			clazz.addMethod(buildGetByIdMethod(clazz));

			// add static initGraph method
			clazz.addMethod(buildInitGraphMethod(entityName, superEntityName));
		}
	}

	/**
	 * Create a static method that initializes a graph. For an
	 * {@link OrientGraph} it registers the entity class as a schema type.
	 * 
	 * @param entityName the entity name
	 * @param superEntityName the super entity name, may be <code>null</code>
	 * @return the method
	 */
	private MethodNode buildInitGraphMethod(Expression entityName, Expression superEntityName) {
		// graph (parameter)
		VariableExpression graph = new VariableExpression("graph");

		if (superEntityName == null) {
			superEntityName = new ConstantExpression(null);
		}

		BlockStatement code = new BlockStatement();

		// register class
		code.addStatement(new ExpressionStatement(new StaticMethodCallExpression(VE_DELEGATES_CLASS,
				VertexEntityDelegates.METHOD_REGISTER_CLASS,
				new ArgumentListExpression(graph, entityName, superEntityName))));

		return new MethodNode("initGraph", Modifier.PUBLIC | Modifier.STATIC, ClassHelper.VOID_TYPE,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph") }, new ClassNode[0], code);
	}

	/**
	 * Create a call to initGraph(Graph).
	 * 
	 * @param clazz the entity class
	 * @param graph the graph to initialize
	 * @return the method call statement
	 */
	private Statement callInitGraph(ClassNode clazz, Expression graph) {
		return new ExpressionStatement(new StaticMethodCallExpression(clazz, "initGraph",
				new ArgumentListExpression(graph)));
	}

	/**
	 * Create a method the removes the entity from the graph.
	 * 
	 * @param vertexField the vertex field
	 * @param graphField the graph field
	 * @return the delete method
	 */
	private MethodNode buildDeleteMethod(FieldNode vertexField, FieldNode graphField) {
		BlockStatement code = new BlockStatement();

		// > g.removeVertex(v)
		code.addStatement(new ExpressionStatement(
				new MethodCallExpression(new FieldExpression(graphField), "removeVertex",
						new ArgumentListExpression(new FieldExpression(vertexField)))));

		// reset graph field
		// > g = null
		code.addStatement(GeneralUtils.assignS(new FieldExpression(graphField),
				new ConstantExpression(null)));

		return new MethodNode("delete", Modifier.PUBLIC, ClassHelper.VOID_TYPE, new Parameter[0],
				new ClassNode[0], code);
	}

	/**
	 * Create a static method that allows creating a new vertex and adding it to
	 * a graph.
	 * 
	 * @param clazz the entity class
	 * @param entityName the entity name
	 * @param initialExpressions the initial expressions per property
	 * @return the static create method taking a graph as an argument
	 */
	private MethodNode buildCreateMethod(ClassNode clazz, Expression entityName,
			Map<String, Expression> initialExpressions) {
		clazz = ClassHelper.make(clazz.getName());

		BlockStatement code = new BlockStatement();

		// graph (parameter)
		VariableExpression graph = new VariableExpression("graph");
		// initialize graph
		code.addStatement(callInitGraph(clazz, graph));

		// vertex (local variable)
		VariableExpression vertex = new VariableExpression("vertex");
		// id (local variable)
		VariableExpression id = new VariableExpression("id");
		code.addStatement(GeneralUtils.declS(id, new ConstantExpression(null)));

		// id = OrientGraph.CLASS_PREFIX + entityName
		Statement orientBlock = GeneralUtils.assignS(id, new BinaryExpression(
				new ConstantExpression(OrientGraph.CLASS_PREFIX), TOKEN_PLUS, entityName));
		// > if (graph instanceof OrientGraph) {
		// > id = OrientGraph.CLASS_PREFIX + entityName
		// > }
		code.addStatement(new IfStatement(GeneralUtils.isInstanceOfX(graph, ORIENT_GRAPH_CLASS),
				orientBlock, new EmptyStatement()));

		// > vertex = graph.addVertex(id)
		Statement assignVertex = GeneralUtils.declS(vertex, new MethodCallExpression(graph,
				new ConstantExpression("addVertex"), new ArgumentListExpression(id)));
		code.addStatement(assignVertex);

		// set initial values on vertex
		for (Entry<String, Expression> propertyInitial : initialExpressions.entrySet()) {
			// > vertex.setProperty(name, initialValue)
			code.addStatement(
					new ExpressionStatement(new MethodCallExpression(vertex, "setProperty",
							new ArgumentListExpression(
									new ConstantExpression(propertyInitial.getKey()),
									propertyInitial.getValue()))));
		}

		// > return new Entity(vertex, graph)
		code.addStatement(new ReturnStatement(
				new ConstructorCallExpression(clazz, new ArgumentListExpression(vertex, graph))));

		return new MethodNode("create", Modifier.STATIC | Modifier.PUBLIC, clazz,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph") }, new ClassNode[0], code);
	}

	/**
	 * Create a static method to retrieve all objects in a graph.
	 * 
	 * @param clazz the entity class node
	 * @param entityName the entity name
	 * @param typeProperty the name of the property holding the entity name in a
	 *            vertex
	 * @return the method
	 */
	private MethodNode buildFindAllMethod(ClassNode clazz, Expression entityName,
			Expression typeProperty) {
		clazz = ClassHelper.make(clazz.getName());

		ClassNode returnType = ITERABLE_CLASS.getPlainNodeReference();
		// add generic type argument
		returnType.setGenericsTypes(new GenericsType[] { new GenericsType(clazz) });

		BlockStatement code = new BlockStatement();
		// initialize graph
		code.addStatement(callInitGraph(clazz, new VariableExpression("graph")));

		/*
		 * def vertices = VertexEntityDelegates.findAllDelegate(graph,
		 * entityName, typeProperty)
		 */

		VariableExpression vertices = new VariableExpression("vertices");
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new VariableExpression("graph"));
		args.addExpression(entityName);
		args.addExpression(typeProperty);
		code.addStatement(GeneralUtils.declS(vertices, new StaticMethodCallExpression(
				VE_DELEGATES_CLASS, VertexEntityDelegates.METHOD_FIND_ALL, args)));

		/*
		 * return new IterableDelegate(vertices, EntityClass, graph)
		 */

		ArgumentListExpression createDelegateArgs = new ArgumentListExpression();
		createDelegateArgs.addExpression(vertices);
		createDelegateArgs.addExpression(new ClassExpression(clazz));
		createDelegateArgs.addExpression(new VariableExpression("graph"));
		code.addStatement(new ReturnStatement(
				new ConstructorCallExpression(VE_ITERABLE_DELEGATE_CLASS, createDelegateArgs)));

		return new MethodNode("findAll", Modifier.STATIC | Modifier.PUBLIC, returnType,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph") }, new ClassNode[0], code);
	}

	/**
	 * Create a static method to retrieve objects by property value.
	 * 
	 * @param clazz the entity class node
	 * @param entityName the entity name
	 * @param typeProperty the name of the property holding the entity name in a
	 *            vertex
	 * @param propertyName the property name
	 * @param propertyType the property type
	 * @return the method
	 */
	private MethodNode buildFindByMethod(ClassNode clazz, Expression entityName,
			Expression typeProperty, String propertyName, ClassNode propertyType) {
		clazz = ClassHelper.make(clazz.getName());
		propertyType = ClassHelper.make(propertyType.getName());

		ClassNode returnType = ITERABLE_CLASS.getPlainNodeReference();
		// add generic type argument
		returnType.setGenericsTypes(new GenericsType[] { new GenericsType(clazz) });

		String methodName = "findBy" + Character.toUpperCase(propertyName.charAt(0))
				+ propertyName.substring(1);

		BlockStatement code = new BlockStatement();
		// initialize graph
		code.addStatement(callInitGraph(clazz, new VariableExpression("graph")));

		/*
		 * def vertices = VertexEntityDelegates.findByDelegate(graph,
		 * entityName, typeProperty, propertyName, value)
		 */

		VariableExpression vertices = new VariableExpression("vertices");
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new VariableExpression("graph"));
		args.addExpression(entityName);
		args.addExpression(typeProperty);
		args.addExpression(new ConstantExpression(propertyName));
		args.addExpression(new VariableExpression("value"));
		code.addStatement(GeneralUtils.declS(vertices, new StaticMethodCallExpression(
				VE_DELEGATES_CLASS, VertexEntityDelegates.METHOD_FIND_BY, args)));
		/*
		 * return new IterableDelegate(vertices, EntityClass, graph)
		 */

		ArgumentListExpression createDelegateArgs = new ArgumentListExpression();
		createDelegateArgs.addExpression(vertices);
		createDelegateArgs.addExpression(new ClassExpression(clazz));
		createDelegateArgs.addExpression(new VariableExpression("graph"));
		code.addStatement(new ReturnStatement(
				new ConstructorCallExpression(VE_ITERABLE_DELEGATE_CLASS, createDelegateArgs)));

		return new MethodNode(
				methodName, Modifier.STATIC | Modifier.PUBLIC, returnType, new Parameter[] {
						new Parameter(GRAPH_CLASS, "graph"), new Parameter(propertyType, "value") },
				new ClassNode[0], code);
	}

	/**
	 * Create a static method to get an entity by its ID.
	 * 
	 * @param clazz the entity class node
	 * @return the method
	 */
	private MethodNode buildGetByIdMethod(ClassNode clazz) {
		clazz = ClassHelper.make(clazz.getName());

		BlockStatement code = new BlockStatement();
		// initialize graph
		code.addStatement(callInitGraph(clazz, new VariableExpression("graph")));

		// def vertex = graph.getVertex(id)
		VariableExpression vertex = new VariableExpression("vertex");
		code.addStatement(
				GeneralUtils.declS(vertex, new MethodCallExpression(new VariableExpression("graph"),
						"getVertex", new ArgumentListExpression(new VariableExpression("id")))));

		/*
		 * return new EntityClass(vertex, graph)
		 */
		Statement returnEntity = new ReturnStatement(new ConstructorCallExpression(clazz,
				new ArgumentListExpression(vertex, new VariableExpression("graph"))));

		// return null
		Statement returnNull = new ReturnStatement(new ConstantExpression(null));

		// if (vertex == null) ... else ...
		code.addStatement(
				new IfStatement(GeneralUtils.equalsNullX(vertex), returnNull, returnEntity));

		return new MethodNode("getById", Modifier.STATIC | Modifier.PUBLIC, clazz,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph"),
						new Parameter(ClassHelper.OBJECT_TYPE, "id") },
				new ClassNode[] { VE_NON_UNIQUE_EXCEPTION }, code);
	}

	/**
	 * Create a static method to retrieve an object by property value.
	 * 
	 * @param clazz the entity class node
	 * @param entityName the entity name
	 * @param typeProperty the name of the property holding the entity name in a
	 *            vertex
	 * @param propertyName the property name
	 * @param propertyType the property type
	 * @return the method
	 */
	private MethodNode buildGetByMethod(ClassNode clazz, Expression entityName,
			Expression typeProperty, String propertyName, ClassNode propertyType) {
		clazz = ClassHelper.make(clazz.getName());
		propertyType = ClassHelper.make(propertyType.getName());

		String methodName = "getBy" + Character.toUpperCase(propertyName.charAt(0))
				+ propertyName.substring(1);

		BlockStatement code = new BlockStatement();
		// initialize graph
		code.addStatement(callInitGraph(clazz, new VariableExpression("graph")));

		/*
		 * def vertex = VertexEntityDelegates.findUniqueByDelegate(graph,
		 * entityName, typeProperty, propertyName, value)
		 */

		VariableExpression vertex = new VariableExpression("vertex");
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new VariableExpression("graph"));
		args.addExpression(entityName);
		args.addExpression(typeProperty);
		args.addExpression(new ConstantExpression(propertyName));
		args.addExpression(new VariableExpression("value"));
		code.addStatement(GeneralUtils.declS(vertex, new StaticMethodCallExpression(
				VE_DELEGATES_CLASS, VertexEntityDelegates.METHOD_GET_BY, args)));
		/*
		 * return new EntityClass(vertex, graph)
		 */
		Statement returnEntity = new ReturnStatement(new ConstructorCallExpression(clazz,
				new ArgumentListExpression(vertex, new VariableExpression("graph"))));

		// return null
		Statement returnNull = new ReturnStatement(new ConstantExpression(null));

		// if (vertex == null) ... else ...
		code.addStatement(
				new IfStatement(GeneralUtils.equalsNullX(vertex), returnNull, returnEntity));

		return new MethodNode(methodName, Modifier.STATIC | Modifier.PUBLIC, clazz,
				new Parameter[] { new Parameter(GRAPH_CLASS, "graph"),
						new Parameter(propertyType, "value") },
				new ClassNode[] { VE_NON_UNIQUE_EXCEPTION }, code);
	}

	/**
	 * Create a constructor taking a Vertex and a Graph as an argument,
	 * assigning them to the vertex and graph fields.
	 * 
	 * @param vertexField the vertex field
	 * @param graphField the graph field
	 * @param superClass the vertex entity super class or <code>null</code>
	 * @param typeProperty the expression specifying the name of the type
	 *            property
	 * @param entityName the expression specifying the entity name
	 * @return a constructor taking a Vertex as an argument
	 */
	private ConstructorNode buildVertexGraphConstructor(FieldNode vertexField, FieldNode graphField,
			ClassNode superClass, Expression typeProperty, Expression entityName) {
		BlockStatement block = new BlockStatement();

		// parameter vertex
		VariableExpression vertex = new VariableExpression("vertex");
		// parameter graph
		VariableExpression graph = new VariableExpression("graph");

		if (superClass != null) {
			// super(vertex, graph)
			block.addStatement(new ExpressionStatement(new ConstructorCallExpression(
					ClassNode.SUPER, new ArgumentListExpression(vertex, graph))));
		}
		else {
			// > this.v = vertex
			block.addStatement(GeneralUtils.assignS(new FieldExpression(vertexField),
					new VariableExpression("vertex")));
			// > this.g = graph
			block.addStatement(GeneralUtils.assignS(new FieldExpression(graphField),
					new VariableExpression("graph")));
		}

		// vertex.setProperty(typeProperty, entityName)
		Statement notOrientStatement = new ExpressionStatement(
				new MethodCallExpression(vertex, "setProperty",
						new ArgumentListExpression(new Expression[] { typeProperty, entityName })));
		// > if (!(graph instanceof OrientGraph))
		// > this.v.setProperty(typeProperty, entityName)
		block.addStatement(new IfStatement(
				new NotExpression(GeneralUtils.isInstanceOfX(graph, ORIENT_GRAPH_CLASS)),
				notOrientStatement, new EmptyStatement()));

		return new ConstructorNode(Modifier.PUBLIC, new Parameter[] {
				new Parameter(VERTEX_CLASS, "vertex"), new Parameter(GRAPH_CLASS, "graph") },
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
		// the original field is not needed
//      cNode.addField(fn);
	}

	private Statement createSetter(String name, FieldNode vertexField) {
		BlockStatement block = new BlockStatement();

		VariableExpression value = new VariableExpression("value");

		/*
		 * v.setProperty(name, value)
		 */
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new ConstantExpression(name));
		args.addExpression(value);
		Statement ifNotNull = new ExpressionStatement(
				new MethodCallExpression(new FieldExpression(vertexField), "setProperty", args));

		/*
		 * v.removeProperty(name)
		 */
		Statement ifNull = new ExpressionStatement(
				new MethodCallExpression(new FieldExpression(vertexField), "removeProperty",
						new ArgumentListExpression(new ConstantExpression(name))));

		block.addStatement(new IfStatement(GeneralUtils.equalsNullX(value), ifNull, ifNotNull));

		return block;
	}

	private Statement createGetter(String name, FieldNode vertexField, ClassNode propertyType,
			Expression initialExpression) {
		BlockStatement block = new BlockStatement();

		// def tmp
		VariableExpression tmpValue = new VariableExpression("tmp");

		/*
		 * > tmp = v.getProperty(name)
		 */
		ArgumentListExpression args = new ArgumentListExpression();
		args.addExpression(new ConstantExpression(name));
		block.addStatement(GeneralUtils.declS(tmpValue,
				new MethodCallExpression(new FieldExpression(vertexField), "getProperty", args)));

		if (ClassHelper.isPrimitiveType(propertyType) && initialExpression != null) {
			// if the class is a primitive, we must do a null check here

			// if (tmp == null) return <initial-value>
			block.addStatement(new IfStatement(GeneralUtils.equalsNullX(tmpValue),
					new ReturnStatement(initialExpression), new EmptyStatement()));
		}

		block.addStatement(new ReturnStatement(tmpValue));

		return block;
	}
}
