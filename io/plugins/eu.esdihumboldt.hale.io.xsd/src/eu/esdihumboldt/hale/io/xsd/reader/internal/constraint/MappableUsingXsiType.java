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

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappableFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;

/**
 * Mappable constraint that determines if a type is mappable using xsi:type.
 * 
 * @author Simon Templer
 */
public class MappableUsingXsiType extends MappableFlag {

	private final XmlTypeDefinition type;

	/**
	 * Create a mapping constraint that checks if a type is mappable using
	 * xsi:type.
	 * 
	 * @param type the type defintion
	 */
	public MappableUsingXsiType(XmlTypeDefinition type) {
		super();

		this.type = type;
	}

	/**
	 * @see AbstractFlagConstraint#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		if (type.getConstraint(HasValueFlag.class).isEnabled())
			return false;
		if (!type.getConstraint(XmlElements.class).getElements().isEmpty())
			return true;

		// the type is mappable if one of the super types is mappable and has
		// an associated element
		TypeDefinition superType = type.getSuperType();
		while (superType != null) {
			// check elements first to prevent the mappable constraint to be
			// determined unnecessarily
			if (!superType.getConstraint(XmlElements.class).getElements().isEmpty()
					&& superType.getConstraint(MappableFlag.class).isEnabled()) {
				return true;
			}

			superType = superType.getSuperType();
		}

		return super.isEnabled();
	}

}
