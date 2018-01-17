/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

/**
 * Exception to indicate that a type or a property transformation is not
 * supported
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
@SuppressWarnings("serial")
public class UnsupportedTransformationException extends Exception {

	private final String transformationIdentifier;

	/**
	 * Constructor
	 * 
	 * @param transformationIdentifier the hale type or property transformation
	 *            identifier
	 */
	public UnsupportedTransformationException(final String transformationIdentifier) {
		super("The transformation is not supported: " + transformationIdentifier);
		this.transformationIdentifier = transformationIdentifier;
	}

	/**
	 * Returns the unsupported transformation
	 * 
	 * @return the transformation identifier
	 */
	public String getTransformationIdentifier() {
		return transformationIdentifier;
	}

}
