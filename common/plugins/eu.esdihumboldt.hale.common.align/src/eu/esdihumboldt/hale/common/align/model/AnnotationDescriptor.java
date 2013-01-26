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

package eu.esdihumboldt.hale.common.align.model;

import org.w3c.dom.Element;

/**
 * Descriptor for cell annotations.
 * 
 * The interface provides methods for serialization and deserialization to/from
 * DOM (and therefore XML), as the annotations are stored in the alignment file.
 * 
 * @param <T> the type of the annoation object
 * @author Simon Templer
 */
public interface AnnotationDescriptor<T> {

	/**
	 * Create a new annotation object.
	 * 
	 * @return the new annotation object
	 */
	public T create();

	/**
	 * Load the annotation from a document object model.
	 * 
	 * @param fragment the annotation fragment root element
	 * @return the annotation object
	 */
	public T fromDOM(Element fragment);

	/**
	 * Store the annotation to a document object model.
	 * 
	 * @param annotation the annotation object to save
	 * @return the annotation fragment root element
	 */
	public Element toDOM(T annotation);

}
