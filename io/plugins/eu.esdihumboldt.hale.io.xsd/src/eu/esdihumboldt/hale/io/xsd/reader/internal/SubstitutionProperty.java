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

import eu.esdihumboldt.hale.common.schema.model.PropertyConstraint;
import eu.esdihumboldt.hale.common.schema.model.impl.AbstractDefinition;
import eu.esdihumboldt.hale.common.schema.model.impl.DefaultPropertyDefinition;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Property that represents a substitution in an XML element substitution group.
 * 
 * @author Simon Templer
 */
public class SubstitutionProperty extends DefaultPropertyDefinition {

	/**
	 * original property that is substituted
	 */
	public final DefaultPropertyDefinition originialProperty;

	/**
	 * Constructor
	 * 
	 * @param substitution the element that represents the substitution
	 * @param originialProperty the original property that is substituted
	 * @param substitutionGroup the parent group
	 */
	public SubstitutionProperty(XmlElement substitution,
			DefaultPropertyDefinition originialProperty, SubstitutionGroupProperty substitutionGroup) {
		super(substitution.getName(), substitutionGroup, substitution.getType());

		this.originialProperty = originialProperty;
	}

	/**
	 * @see AbstractDefinition#getConstraint(Class)
	 */
	@Override
	public <T extends PropertyConstraint> T getConstraint(Class<T> constraintType) {
		// return the constraints of the original property if possible
		if (originialProperty.hasConstraint(constraintType)) {
			return originialProperty.getConstraint(constraintType);
		}
		return super.getConstraint(constraintType);
	}

}
