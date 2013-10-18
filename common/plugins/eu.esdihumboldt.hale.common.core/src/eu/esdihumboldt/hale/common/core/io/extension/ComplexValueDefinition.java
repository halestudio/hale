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

package eu.esdihumboldt.hale.common.core.io.extension;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import de.cs3d.util.eclipse.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.ComplexValueType;

/**
 * Represents a complex value type registered with the extension point.
 * 
 * @author Simon Templer
 */
public class ComplexValueDefinition implements Identifiable, ComplexValueType<Object, Object> {

	private final String id;

	private final QName elementName;

	@SuppressWarnings("rawtypes")
	private final ComplexValueType descriptor;

	private final Class<?> valueType;

	/**
	 * Create a complex value definition.
	 * 
	 * @param id complex value identifier
	 * @param elementName the qualified name of the XML element representing the
	 *            complex value
	 * @param descriptor the complex value descriptor
	 * @param valueType the complex value type
	 * @throws IllegalAccessException if access to the default constructor of
	 *             the descriptor class is not allowed
	 * @throws InstantiationException if the descriptor object cannot be created
	 */
	public ComplexValueDefinition(String id, QName elementName,
			Class<ComplexValueType<?, ?>> descriptor, Class<?> valueType)
			throws InstantiationException, IllegalAccessException {
		super();
		this.id = id;
		this.elementName = elementName;
		this.valueType = valueType;
		this.descriptor = descriptor.newInstance();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object fromDOM(Element fragment, Object context) {
		if (context != null && descriptor.getContextType().isAssignableFrom(context.getClass())) {
			return descriptor.fromDOM(fragment, context);
		}
		return descriptor.fromDOM(fragment, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Element toDOM(Object annotation) {
		return descriptor.toDOM(annotation);
	}

	@Override
	public String getId() {
		return id;
	}

	/**
	 * Get the name of the element representing the complex value.
	 * 
	 * @return the qualified element name
	 */
	public QName getElementName() {
		return elementName;
	}

	/**
	 * Get the associated complex value type.
	 * 
	 * @return the complex value type
	 */
	public Class<?> getValueType() {
		return valueType;
	}

	@Override
	public Class<Object> getContextType() {
		return Object.class;
	}

}
