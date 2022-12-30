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

package eu.esdihumboldt.hale.ui.util.groovy.ast

import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement

import com.tinkerpop.blueprints.Direction
import com.tinkerpop.blueprints.Vertex

import groovy.transform.CompileStatic



/**
 * Tests for {@link ASTGraphUtil}
 * 
 * @author Simon Templer
 */
@CompileStatic
class ASTGraphUtilTest extends GroovyTestCase {

	/**
	 * Test findAt with a position inside a closure.
	 */
	void testFindAtClosure() {
		String code = '''
_target = {
		
}
'''
		int line = 3;
		int col = 2;

		GroovyAST ast = new GroovyASTSourceCompiler().compile(code);

		Vertex found = ASTGraphUtil.findAt(ast.rootVertices, line, col);

		assertNotNull found

		Object node = found.getProperty(ASTGraphConstants.P_AST_NODE);
		assertTrue 'Node doesn\'t have the correct type', node instanceof BlockStatement

		Vertex parent = found.getVertices(Direction.OUT, ASTGraphConstants.E_PARENT).iterator().next()

		assertNotNull parent

		node = parent.getProperty(ASTGraphConstants.P_AST_NODE);
		assertTrue 'Parent node doesn\'t have the correct type', node instanceof ClosureExpression
	}
}
