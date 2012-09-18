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
import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.io.xsd.model.XmlAttribute;
import eu.esdihumboldt.hale.io.xsd.model.XmlIndex;

/**
 * Property referencing a XML attribute
 * 
 * @author Simon Templer
 */
public class XmlAttributeReferenceProperty extends LazyPropertyDefinition {

	private final QName attributeName;

	private XmlAttribute referencedAttribute;

	/**
	 * Create a property that references a XML attribute
	 * 
	 * @param name the property name
	 * @param declaringType the declaring type
	 * @param index the XML index
	 * @param attributeName the attribute name
	 */
	public XmlAttributeReferenceProperty(QName name, DefinitionGroup declaringType, XmlIndex index,
			QName attributeName) {
		super(name, declaringType, index);

		this.attributeName = attributeName;
	}

	/**
	 * @see LazyPropertyDefinition#resolvePropertyType(XmlIndex)
	 */
	@Override
	protected TypeDefinition resolvePropertyType(XmlIndex index) {
		XmlAttribute attribute = resolveAttribute();

		if (attribute == null) {
			throw new IllegalStateException("Referenced attribute could not be found: "
					+ attributeName.toString());
		}

		return attribute.getType();
	}

	private XmlAttribute resolveAttribute() {
		if (referencedAttribute == null) {
			referencedAttribute = index.getAttributes().get(attributeName);
		}

		return referencedAttribute;
	}

	/**
	 * @see AbstractDefinition#getConstraint(Class)
	 */
	@Override
	public <T extends PropertyConstraint> T getConstraint(Class<T> constraintType) {
		if (!hasConstraint(constraintType)) {
			// if constraint is not defined look in referenced attribute for the
			// constraint
			XmlAttribute attribute = resolveAttribute();
			if (attribute != null) {
				return attribute.getConstraint(constraintType);
			}
		}

		return super.getConstraint(constraintType);
	}

}
