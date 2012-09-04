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

package eu.esdihumboldt.hale.io.xsd.constraint;

import org.apache.ws.commons.schema.constants.Constants;

import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Unique;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;

/**
 * Unique constraint for XS:ID types.
 * Needed because while loading the property type's supertypes may not be resolved yet.
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
			} while (definition != null) ;

			status = 0;
		}
	}
}
