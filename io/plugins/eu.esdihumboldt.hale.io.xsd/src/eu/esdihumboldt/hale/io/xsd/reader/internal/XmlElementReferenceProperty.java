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
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Property referencing a XML element
 * 
 * @author Simon Templer
 */
public class XmlElementReferenceProperty extends LazyPropertyDefinition {

	private final QName elementName;

	/**
	 * Create a property that references a XML element
	 * 
	 * @param name the property name
	 * @param declaringGroup the declaring group
	 * @param index the XML index
	 * @param elementName the element name
	 */
	public XmlElementReferenceProperty(QName name, DefinitionGroup declaringGroup, XmlIndex index,
			QName elementName) {
		super(name, declaringGroup, index);

		this.elementName = elementName;
	}

	/**
	 * @see LazyPropertyDefinition#resolvePropertyType(XmlIndex)
	 */
	@Override
	protected TypeDefinition resolvePropertyType(XmlIndex index) {
		XmlElement element = index.getElements().get(elementName);

		if (element == null) {
			throw new IllegalStateException("Referenced element could not be found");
		}

		return element.getType();
	}

}
