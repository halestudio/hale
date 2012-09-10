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

	private final DefaultPropertyDefinition originialProperty;

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
