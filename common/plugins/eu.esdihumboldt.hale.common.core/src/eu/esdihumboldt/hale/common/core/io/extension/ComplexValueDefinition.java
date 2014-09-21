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

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension.Identifiable;
import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;
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

	@SuppressWarnings("rawtypes")
	private final ComplexValueJson jsonConverter;

	/**
	 * Create a complex value definition.
	 * 
	 * @param id complex value identifier
	 * @param elementName the qualified name of the XML element representing the
	 *            complex value
	 * @param descriptor the complex value descriptor class
	 * @param jsonConverter the complex value JSON converter class, may be
	 *            <code>null</code>
	 * @param valueType the complex value type
	 * @throws IllegalAccessException if access to the default constructor of
	 *             the descriptor class is not allowed
	 * @throws InstantiationException if the descriptor object cannot be created
	 */
	@SuppressWarnings("rawtypes")
	public ComplexValueDefinition(String id, QName elementName,
			Class<ComplexValueType<?, ?>> descriptor,
			@Nullable Class<ComplexValueJson<?, ?>> jsonConverter, Class<?> valueType)
			throws InstantiationException, IllegalAccessException {
		super();
		this.id = id;
		this.elementName = elementName;
		this.valueType = valueType;
		this.descriptor = descriptor.newInstance();
		if (jsonConverter == null) {
			// test if descriptor is also applicable as JSON converter
			if (this.descriptor instanceof ComplexValueJson) {
				this.jsonConverter = (ComplexValueJson) this.descriptor;
			}
			else {
				this.jsonConverter = null;
			}
		}
		else {
			this.jsonConverter = jsonConverter.newInstance();
		}
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

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends Object> getContextType() {
		return descriptor.getContextType();
	}

	/**
	 * @return the JSON converter or <code>null</code>
	 */
	@SuppressWarnings("unchecked")
	public @Nullable
	ComplexValueJson<Object, Object> getJsonConverter() {
		return jsonConverter;
	}

}
