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

package eu.esdihumboldt.hale.rcp.views.model;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Schema item representing a feature type
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TypeItem extends TreeParent {
	
	private final TypeDefinition typeDefinition;

	/**
	 * Creates a feature type item
	 * 
	 * @param type the type definition
	 */
	public TypeItem(TypeDefinition type) {
		super(
				type.getName().getLocalPart(), 
				type.getName(), 
				determineType(type), 
				type.getType());
		
		this.typeDefinition = type;
	}

	/**
	 * Determine the {@link TreeObject.TreeObjectType} for a feature type
	 * 
	 * @param type the type definition
	 * 
	 * @return the tree object type
	 */
	private static TreeObjectType determineType(TypeDefinition type) {
		if (type.isFeatureType()) {
			if (type.isAbstract()) {
				return TreeObjectType.ABSTRACT_FT;
			}
			else {
				return TreeObjectType.CONCRETE_FT;
			}
		}
		else {
			return TreeObjectType.PROPERTY_TYPE;
		}
	}

	/**
	 * @return the typeDefinition
	 */
	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	/**
	 * @see SchemaItem#getDefinition()
	 */
	@Override
	public Definition getDefinition() {
		return typeDefinition;
	}

}
