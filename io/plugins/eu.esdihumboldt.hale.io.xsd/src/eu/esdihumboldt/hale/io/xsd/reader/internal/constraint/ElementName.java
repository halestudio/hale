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
