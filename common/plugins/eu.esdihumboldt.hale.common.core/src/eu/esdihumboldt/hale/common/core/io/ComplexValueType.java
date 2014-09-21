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

import org.w3c.dom.Element;

/**
 * Handles storing a complex value to a DOM or loading it from a DOM.
 * 
 * @param <T> the type of the complex value
 * @param <C> the type of the context that should be supplied
 * @author Simon Templer
 */
public interface ComplexValueType<T, C> {

	/**
	 * Load the complex value from a document object model.
	 * 
	 * @param fragment the complex value fragment root element
	 * @param context the complex value context, may be <code>null</code> if
	 *            unavailable
	 * @return the loaded complex value
	 */
	public T fromDOM(Element fragment, C context);

	/**
	 * Store the complex value to a document object model.
	 * 
	 * @param value the complex value to save
	 * @return the complex value fragment root element
	 */
	public Element toDOM(T value);

	/**
	 * @return the type of the complex value context
	 */
	public Class<? extends C> getContextType();

}
