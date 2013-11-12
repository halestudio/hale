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

package eu.esdihumboldt.hale.ui.util.groovy;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import eu.esdihumboldt.hale.ui.util.groovy.ast.GroovyAST;

/**
 * Computes completion proposals based on a Groovy AST.
 * 
 * @author Simon Templer
 */
public interface GroovyCompletionProposals {

	/**
	 * Compute proposals for the given position.
	 * 
	 * @param ast the Groovy AST
	 * @param line the line number (1-based)
	 * @param column the line column (1-based)
	 * @param offset the document offset (0-based)
	 * @return the computed proposals or <code>null</code>
	 */
	public Iterable<? extends ICompletionProposal> computeProposals(GroovyAST ast, int line,
			int column, int offset);

}
