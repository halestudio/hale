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

import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.TypeDefinition;

/**
 * Schema item representing an element
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public class TypeItem extends TreeParent {
	
	private final TypeDefinition type;

	/**
	 * Creates a element item
	 * 
	 * @param type the type definition
	 * @param schemaType the schema type
	 */
	public TypeItem(TypeDefinition type, SchemaType schemaType) {
		super(
				type.getDisplayName(), 
				type.getName(), 
				determineType(type), 
				type.getType(null),
				schemaType);
		
		this.type = type;
	}

	/**
	 * Determine the {@link TreeObject.TreeObjectType} for a feature type
	 * 
	 * @param type the type definition
	 * 
	 * @return the tree object type
	 */
	public static TreeObjectType determineType(TypeDefinition type) {
		// special case: treat AbstractGMLType as abstract FeatureType (if it has an AbstractFeatureType sub-type)
		if (type.getName().getLocalPart().equals("AbstractGMLType")) { //$NON-NLS-1$
			for (TypeDefinition subtype : type.getSubTypes()) {
				if (subtype.getName().getLocalPart().equals("AbstractFeatureType")) { //$NON-NLS-1$
					return TreeObjectType.ABSTRACT_FT;
				}
			}
		}
		
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
		return type;
	}

	/**
	 * @see SchemaItem#getDefinition()
	 */
	@Override
	public Definition getDefinition() {
		return type;
	}

}
