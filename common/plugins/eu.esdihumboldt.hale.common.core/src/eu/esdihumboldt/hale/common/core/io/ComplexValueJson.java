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

package eu.esdihumboldt.hale.common.core.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Extend {@link ComplexValueType}s with the ability to store a complex value to
 * JSON or loading it from JSON. The interface may either be implemented by a
 * {@link ComplexValueType}, or you create a separate implementation and
 * register it additionally to the original complex value extension.
 * 
 * @param <T> the type of the complex value
 * @param <C> the type of the context that should be supplied
 * @author Simon Templer
 */
public interface ComplexValueJson<T, C> {

	/**
	 * Load the complex value from a JSON representation.
	 * 
	 * @param json the JSON representation to parse
	 * @param context the complex value context, may be <code>null</code> if
	 *            unavailable
	 * @return the loaded complex value
	 * @throws IOException if there are problems reading the representation
	 */
	public T fromJson(Reader json, C context) throws IOException;

	/**
	 * Write the complex value as JSON.
	 * 
	 * @param value the complex value to save
	 * @param writer the writer to write the JSON representation to
	 * @throws IOException if writing the representation fails
	 */
	public void toJson(T value, Writer writer) throws IOException;

	// convenience methods for Groovy

	/**
	 * Load the complex value from a JSON representation.
	 * 
	 * @param json the JSON object representation as used by Groovy JsonSlurper,
	 *            JsonBuilder and JsonOutput
	 * @param context the complex value context, may be <code>null</code> if
	 *            unavailable
	 * @return the loaded complex value
	 */
	public T fromJson(Object json, C context);

	/**
	 * Write the complex value as JSON.
	 * 
	 * @param value the complex value to save
	 * @return the JSON object representation as used by Groovy JsonSlurper,
	 *         JsonBuilder and JsonOutput
	 */
	public Object toJson(T value);

	/**
	 * @return the type of the complex value context
	 */
	public Class<? extends C> getContextType();

}
