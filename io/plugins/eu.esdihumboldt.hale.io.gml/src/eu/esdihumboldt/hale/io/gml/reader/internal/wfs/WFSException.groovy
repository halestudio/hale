/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.gml.reader.internal.wfs

/**
 * Exception for exceptions during WFS communication
 * 
 * @author Florian Esser
 */
class WFSException extends Exception {

	public WFSException() {
		super();
	}

	public WFSException(String message, Throwable cause, boolean enableSuppression,
	boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public WFSException(String message, Throwable cause) {
		super(message, cause);
	}

	public WFSException(String message) {
		super(message);
	}

	public WFSException(Throwable cause) {
		super(cause);
	}
}
