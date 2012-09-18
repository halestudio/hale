/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.io.xsd.reader.internal;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.DefinitionGroup;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * XML property definition that doesn't know its property type on construction
 * time.
 * 
 * @author Simon Templer
 */
public abstract class LazyPropertyDefinition extends DefaultPropertyDefinition {

	/**
	 * The XML index that can be used to resolve needed objects
	 */
	protected final XmlIndex index;

	/**
	 * The resolved property type (if resolved yet)
	 */
	private TypeDefinition resolvedType;

	/**
	 * Create a lazy property definiton
	 * 
	 * @param name the property name
	 * @param declaringType the declaring type
	 * @param index the XML index
	 */
	public LazyPropertyDefinition(QName name, DefinitionGroup declaringType, XmlIndex index) {
		super(name, declaringType, null);

		this.index = index;
	}

	/**
	 * @see DefaultPropertyDefinition#getPropertyType()
	 */
	@Override
	public TypeDefinition getPropertyType() {
		if (resolvedType != null) {
			return resolvedType;
		}

		// resolve type
		resolvedType = resolvePropertyType(index);

		return resolvedType;
	}

	/**
	 * Resolve the property type using the XML index
	 * 
	 * @param index the XML index
	 * @return the resolved property type
	 */
	protected abstract TypeDefinition resolvePropertyType(XmlIndex index);

}
