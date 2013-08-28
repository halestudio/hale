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

package eu.esdihumboldt.hale.common.align.extension.annotation;

import org.w3c.dom.Element;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.align.model.AnnotationDescriptor;

/**
 * Represents a cell annotation defined in the extension point.
 * 
 * @author Simon Templer
 */
public class DefinedAnnotation implements Identifiable, AnnotationDescriptor<Object> {

	private final String id;

	private final AnnotationDescriptor<Object> descriptor;

	/**
	 * Create a defined annotation.
	 * 
	 * @param id the associated annotation type identifier
	 * @param descriptor the annotation descriptor class
	 * @throws IllegalAccessException if access to the default constructor of
	 *             the descriptor class is not allowed
	 * @throws InstantiationException if the descriptor object cannot be created
	 */
	@SuppressWarnings("unchecked")
	public DefinedAnnotation(String id, Class<AnnotationDescriptor<?>> descriptor)
			throws InstantiationException, IllegalAccessException {
		super();
		this.id = id;
		this.descriptor = (AnnotationDescriptor<Object>) descriptor.newInstance();
	}

	@Override
	public Object create() {
		return descriptor.create();
	}

	@Override
	public Object fromDOM(Element fragment, Void context) {
		return descriptor.fromDOM(fragment, context);
	}

	@Override
	public Element toDOM(Object annotation) {
		return descriptor.toDOM(annotation);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Class<Void> getContextType() {
		return descriptor.getContextType();
	}

}
