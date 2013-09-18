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

package eu.esdihumboldt.util.orient.entities;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
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

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * AST transformation that adds an {@link ODocument} field and getter to a class
 * and replaces its properties by properties based on the document.
 * 
 * @author Simon Templer
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class ODocumentEntityTransformation implements ASTTransformation {

	private static final ClassNode ODOCUMENT_CLASS = ClassHelper.make(ODocument.class);
	private static final ClassNode ODOCUMENT_ENTITY_CLASS = ClassHelper.make(ODocumentEntity.class);

	@Override
	public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
		if (sourceUnit.getAST() == null)
			return;

		List<ClassNode> classes = sourceUnit.getAST().getClasses();
		for (ClassNode clazz : classes) {
			// find all classes annotated with @ODocumentEntity
			List<AnnotationNode> entityAnnotations = clazz.getAnnotations(ODOCUMENT_ENTITY_CLASS);
			if (entityAnnotations != null && !entityAnnotations.isEmpty()) {
				Expression classname = entityAnnotations.get(0).getMember("value");

				// add the document field
				FieldNode documentField = clazz.addField("document", Modifier.PRIVATE
						| Modifier.FINAL, ODOCUMENT_CLASS, //
						new ConstructorCallExpression(ODOCUMENT_CLASS, //
								new ArgumentListExpression(classname)));

				// get all non-static properties
				List<PropertyNode> properties = AbstractASTTransformUtil
						.getInstanceProperties(clazz);
				List<PropertyNode> newProperties = new ArrayList<>();
				for (PropertyNode property : properties) {
					// TODO check for "transient" properties?

					property.setGetterBlock(createGetter(property.getName(), documentField));
					property.setSetterBlock(createSetter(property.getName(), documentField));
					newProperties.add(property);

				}
				// readd updated properties
				for (PropertyNode property : newProperties) {
					readdProperty(clazz, property);
				}

				// add the document getter
				clazz.addMethod("getODocument", Modifier.PUBLIC, ODOCUMENT_CLASS, //
						new Parameter[0], new ClassNode[0], new ReturnStatement(
								new FieldExpression(documentField)));
			}
		}
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
