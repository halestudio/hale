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

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * XML type definition
 * @author Simon Templer
 */
public class XmlTypeDefinition extends DefaultTypeDefinition {

	/**
	 * @see DefaultTypeDefinition#DefaultTypeDefinition(QName)
	 */
	public XmlTypeDefinition(QName name) {
		super(name);
	}

	/**
	 * @see DefaultTypeDefinition#getChildren()
	 */
	@Override
	public Collection<? extends ChildDefinition<?>> getChildren() {
		if (getConstraint(RestrictionFlag.class).isEnabled()) {
			//XXX for restrictions assume that all properties are redefined if needed
			// only return declared properties
			return getDeclaredChildren();
		}
		
		return super.getChildren();
	}

	@Override
	public String getDescription() {
		String desc = super.getDescription();
		if (desc != null && !desc.isEmpty()) {
			return desc;
		}
		
		// if no description is present, try the description of the associated element
		XmlElements elements = getConstraint(XmlElements.class);
		if (elements.getElements().size() == 1) {
			// only use element description if it's unique
			XmlElement element = elements.getElements().iterator().next();
			return element.getDescription();
		}
		
		return desc;
	}

}
