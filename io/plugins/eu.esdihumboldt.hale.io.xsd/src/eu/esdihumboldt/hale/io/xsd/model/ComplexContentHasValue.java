/*
 * Copyright (c) 2018 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.xsd.model;

import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultTypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlAttributeFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlMixedFlag;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;

/**
 * Determines if a complex content type may have a value.
 * 
 * @author Simon Templer
 */
public class ComplexContentHasValue extends HasValueFlag {

	private final XmlTypeDefinition typeDef;

	private boolean initialized = false;

	private boolean hasValue = false;

	/**
	 * Create a constraint for the given type.
	 * 
	 * @param typeDef the type definition
	 */
	public ComplexContentHasValue(XmlTypeDefinition typeDef) {
		this.typeDef = typeDef;
	}

	@Override
	public boolean isEnabled() {
		if (!initialized) {
			// evaluate on first access (because we access other constraints and
			// structural information)

			// check if type is mixed
			boolean mixed = typeDef.getConstraint(XmlMixedFlag.class).isEnabled();
			if (mixed) {
				hasValue = true;
			}
			else {
				DefaultTypeDefinition superType = typeDef.getSuperType();

				if (superType != null && superType.getConstraint(HasValueFlag.class).isEnabled()) {
					// if the parent type allows values (e.g. anyType)

					// check if there are element (or group) children
					boolean nonAttributeChildren = typeDef.getChildren().stream()
							.anyMatch(e -> e.asGroup() != null || !e.asProperty()
									.getConstraint(XmlAttributeFlag.class).isEnabled());
					hasValue = !nonAttributeChildren;
				}
			}

			initialized = true;
		}

		return hasValue;
	}

}
