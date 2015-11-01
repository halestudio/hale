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

package eu.esdihumboldt.hale.io.geoserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.ContentType;

/**
 * Interface defining the API for GeoServer REST resources.
 * 
 * @author Stefano Costa, GeoSolutions
 */
public interface Resource {

	/**
	 * @return the resource name
	 */
	public String name();

	/**
	 * @return the resource content type
	 */
	public ContentType contentType();

	/**
	 * @param name the name of the attribute to retrieve
	 * @return the value of the specified attribute, or <code>null</code> if it
	 *         is not found
	 */
	public Object getAttribute(String name);

	/**
	 * @param name the name of the attribute to set
	 * @param value the value of the attribute to set
	 */
	public void setAttribute(String name, Object value);

	/**
	 * Write the resource content to the provided output stream.
	 * 
	 * @param out the output stream to write to
	 * @throws IOException if an I/O error occurs
	 */
	public void write(OutputStream out) throws IOException;

	/**
	 * @return an input stream from which the resource content can be read
	 * @throws IOException if an I/O error occurs
	 */
	public InputStream asStream() throws IOException;

	/**
	 * @return the resource content as byte array
	 * @throws IOException if an I/O error occurs
	 */
	public byte[] asByteArray() throws IOException;

}
