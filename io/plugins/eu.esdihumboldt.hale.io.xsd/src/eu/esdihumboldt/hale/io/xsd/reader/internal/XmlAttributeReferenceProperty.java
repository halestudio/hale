/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
