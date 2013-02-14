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

package eu.esdihumboldt.hale.server.api.model


/**
 * Configuration for a transformation source.
 * 
 * @author Simon Templer
 */
class SourceConfig extends IOConfig {


	public static final String FILE = 'file'

	/**
	 * Location of the source. The default value {@link #FILE} references an
	 * uploaded file, otherwise an URI. Local file URIs should be blocked for
	 * security reasons.
	 */
	String location = FILE
}
