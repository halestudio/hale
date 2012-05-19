/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.schemaprovider.model;

import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.Name;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
@Deprecated
public class AnonymousType extends TypeDefinition {
	
	/**
	 * Create an anonymous type
	 * 
	 * @param name the type name
	 * @param type the attribute type
	 * @param superType the super type
	 * @param location the location of the anonymous type definition
	 */
	public AnonymousType(Name name, AttributeType type, TypeDefinition superType,
			String location) {
		super(name, type, superType);
		
		setLocation(location);
	}

	/**
	 * @see TypeDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		if (getSuperType() == null) {
			return "?"; //$NON-NLS-1$
		}
		else {
			return "? extends " + getSuperType().getDisplayName(); //$NON-NLS-1$
		}
	}

}
