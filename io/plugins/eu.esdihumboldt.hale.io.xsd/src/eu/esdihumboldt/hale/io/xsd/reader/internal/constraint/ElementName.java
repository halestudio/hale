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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import java.util.Collection;

import eu.esdihumboldt.hale.common.schema.model.constraint.DisplayName;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Custom display name based on XML elements
 * 
 * @author Simon Templer
 */
public class ElementName extends DisplayName {

	private final XmlElements xmlElements;

	/**
	 * Create a display name constraint based on the XML elements of a type
	 * 
	 * @param xmlElements the XML elements
	 */
	public ElementName(XmlElements xmlElements) {
		this.xmlElements = xmlElements;
	}

	/**
	 * @see DisplayName#getCustomName()
	 */
	@Override
	public String getCustomName() {
		Collection<? extends XmlElement> elements = xmlElements.getElements();
		if (elements != null && !elements.isEmpty()) {
			// choose first element
			return elements.iterator().next().getDisplayName();
			// FIXME what to do if there are multiple elements? prefer ones that
			// are flagged Mappable? (not done currently)
		}

		return super.getCustomName();
	}

}
