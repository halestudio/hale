/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.io.geoserver.template;

import java.io.IOException;

/**
 * Exception thrown by {@link Templates} class if an error occurs initializing
 * the template engine or merging a template.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public class TemplateException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6623393454465464325L;

	/**
	 * @see Exception#Exception(String)
	 */
	public TemplateException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public TemplateException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public TemplateException(String message, Throwable cause) {
		super(message, cause);
	}

}
