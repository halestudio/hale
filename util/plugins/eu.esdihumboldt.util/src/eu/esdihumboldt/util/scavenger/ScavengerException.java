/*
 * Copyright (c) 2012 Data Harmonisation Panel
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

package eu.esdihumboldt.util.scavenger;

/**
 * {@link ResourceScavenger} exception.
 * 
 * @author Simon Templer
 */
public class ScavengerException extends Exception {

	private static final long serialVersionUID = 3507629132111593971L;

	/**
	 * @see Exception#Exception()
	 */
	public ScavengerException() {
		super();
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public ScavengerException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public ScavengerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public ScavengerException(String message, Throwable cause) {
		super(message, cause);
	}

}
