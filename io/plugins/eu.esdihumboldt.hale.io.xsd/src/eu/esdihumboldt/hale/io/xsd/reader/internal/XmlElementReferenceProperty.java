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
