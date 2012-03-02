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

package eu.esdihumboldt.hale.io.xsd.reader.internal.constraint;

import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.AbstractFlagConstraint;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.MappingRelevantFlag;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.reader.internal.XmlTypeDefinition;

/**
 * Mappable constraint that determines if a type is mappable using xsi:type.
 * @author Simon Templer
 */
public class MappableUsingXsiType extends MappingRelevantFlag {

	private XmlTypeDefinition type;

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
		// the type is mappable if one of the super types is mappable and has
		// an associated element
		
		TypeDefinition superType = type.getSuperType();
		while (superType != null) {
			// check elements first to prevent the mappable constraint to be determined unncessarily
			if (!superType.getConstraint(XmlElements.class).getElements().isEmpty() && 
					superType.getConstraint(MappingRelevantFlag.class).isEnabled()) {
				return true;
			}
			
			superType = superType.getSuperType();
		}
		
		return super.isEnabled();
	}

}
