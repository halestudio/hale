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

import org.apache.ws.commons.schema.constants.Constants;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Unique constraint for XS:ID types. Needed because while loading the property
 * type's supertypes may not be resolved yet.
 * 
 * @author Kai Schwierczek
 */
public class XmlIdUnique extends Unique {

	private static final String IDENTIFIER = "xs:id";
	private final PropertyDefinition property;
	private int status = -1; // -1 not resolved, 0 no id, 1 id

	/**
	 * Default constructor.
	 * 
	 * @param property the property definition
	 */
	public XmlIdUnique(PropertyDefinition property) {
		this.property = property;
	}

	/**
	 * @see Unique#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		if (status == -1)
			resolve();
		return status == 1;
	}

	/**
	 * @see Unique#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		if (status == -1)
			resolve();
		return status == 0 ? null : IDENTIFIER;
	}

	/**
	 * Resolve whether the property type is a sub type of xs:id.
	 */
	private void resolve() {
		TypeDefinition definition = property.getPropertyType();

		// return directly if it has no value
		if (!definition.getConstraint(HasValueFlag.class).isEnabled())
			status = 0;
		else {
			do {
				if (definition.getName().equals(Constants.XSD_ID)) {
					status = 1;
					return;
				}
				definition = definition.getSuperType();
			} while (definition != null);

			status = 0;
		}
	}
}
