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

package eu.esdihumboldt.hale.ui.util.groovy.ast;

import org.codehaus.groovy.ast.builder.AstBuilder;

import eu.esdihumboldt.hale.ui.util.source.SourceCompiler;

/**
 * Groovy AST source compiler using the {@link AstBuilder}.
 * 
 * @author Simon Templer
 */
public class GroovyASTSourceCompiler implements SourceCompiler<GroovyAST> {

	private final AstBuilder builder = new AstBuilder();

	@Override
	public GroovyAST compile(String content) {
		return new GroovyAST(builder.buildFromString(content));
	}

}
