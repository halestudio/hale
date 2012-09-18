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

package eu.esdihumboldt.hale.io.xsd.constraint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import eu.esdihumboldt.hale.common.schema.model.Constraint;
import eu.esdihumboldt.hale.common.schema.model.TypeConstraint;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Constraint that states the {@link XmlElement}s associated with a type
 * definition, by default no elements are contained.
 * 
 * @author Simon Templer
 */
@Constraint(mutable = true)
public class XmlElements implements TypeConstraint {

	private Set<XmlElement> elements;

	/**
	 * Create a default XML element constraint w/o any elements
	 */
	public XmlElements() {
		super();
	}

	/**
	 * Get the elements associated with the type
	 * 
	 * @return the XML elements
	 */
	public Collection<? extends XmlElement> getElements() {
		if (elements == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableCollection(elements);
		}
	}

	/**
	 * Add a XML element to the type
	 * 
	 * @param element the element to add
	 */
	public void addElement(XmlElement element) {
		if (elements == null) {
			elements = new HashSet<XmlElement>();
		}
		elements.add(element);
	}

	/**
	 * @see TypeConstraint#isInheritable()
	 */
	@Override
	public boolean isInheritable() {
		// not inheritable, substitutions instead pose a kind of inheritance
		return false;
	}

}
