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

package eu.esdihumboldt.hale.ui.model.schema;

import eu.esdihumboldt.hale.schemaprovider.model.Definition;
import eu.esdihumboldt.hale.schemaprovider.model.SchemaElement;
import eu.esdihumboldt.hale.ui.service.schema.SchemaService.SchemaType;

/**
 * Schema item representing an element
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ElementItem extends TreeParent {
	
	private final SchemaElement element;

	/**
	 * Creates a element item
	 * 
	 * @param element the type definition
	 * @param schemaType the schema type
	 */
	public ElementItem(SchemaElement element, SchemaType schemaType) {
		super(
				element.getElementName().getLocalPart(), 
				element.getElementName(), 
				TypeItem.determineType(element.getType()), 
				element.getAttributeType(null),
				schemaType);
		
		this.element = element;
	}

	/**
	 * @return the typeDefinition
	 */
	public SchemaElement getElement() {
		return element;
	}

	/**
	 * @see SchemaItem#getDefinition()
	 */
	@Override
	public Definition getDefinition() {
		return element;
	}

}
