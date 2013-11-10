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

package eu.esdihumboldt.hale.ui.util.source;

/**
 * Compiler for source viewer content.
 * 
 * @param <C> the type of the compilation result
 * 
 * @author Simon Templer
 */
public interface SourceCompiler<C> {

	/**
	 * Compile the given source document content.
	 * 
	 * @param content the document content
	 * @return the compilation result, may be <code>null</code> if compilation
	 *         is not possible
	 */
	public C compile(String content);
}