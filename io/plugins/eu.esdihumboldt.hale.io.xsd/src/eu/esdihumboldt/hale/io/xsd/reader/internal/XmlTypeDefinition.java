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

import java.util.Collection;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.schema.model.ChildDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.RestrictionFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * XML type definition
 * 
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
		if (getConstraint(RestrictionFlag.class).isEnabled()
				&& !getConstraint(HasValueFlag.class).isEnabled()) {
			/*
			 * XXX For restrictions (on complex types) assume that all
			 * properties are redefined if needed. FIXME is this correct?
			 */
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

		// if no description is present, try the description of the associated
		// element
		XmlElements elements = getConstraint(XmlElements.class);
		if (elements.getElements().size() == 1) {
			// only use element description if it's unique
			XmlElement element = elements.getElements().iterator().next();
			return element.getDescription();
		}

		return desc;
	}

}
