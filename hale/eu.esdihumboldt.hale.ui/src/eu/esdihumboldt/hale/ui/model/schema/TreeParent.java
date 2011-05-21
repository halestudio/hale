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

import java.util.SortedSet;
import java.util.TreeSet;

import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyType;

import eu.esdihumboldt.hale.models.SchemaService.SchemaType;
import eu.esdihumboldt.hale.schemaprovider.model.Definition;


/**
 * A special TreeObject which is a parent of other TreeObjects.
 * 
 * @author Thorsten Reitz, Christian Jauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class TreeParent extends TreeObject{
	private SortedSet<TreeObject> children;
	
	/**
	 * Constructor
	 * 
	 * @param label the item label
	 * @param name the item entity name
	 * @param type the item entity type
	 * @param propertyType the property type represented by this item, may be <code>null</code>
	 * @param schemaType the schema type
	 */
	public TreeParent(String label, Name name, TreeObjectType type,
			PropertyType propertyType, SchemaType schemaType) {
		super(label, name, type, propertyType, schemaType);
		children = new TreeSet<TreeObject>();
	}
	
	/**
	 * Add a child item
	 * 
	 * @param child the child item
	 */
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	
	/**
	 * Remove a child item
	 * 
	 * @param child the child item
	 */
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	
	/**
	 * Get the item's children
	 * 
	 * @return an array of the item's children
	 */
	@Override
	public SchemaItem[] getChildren() {
		return children.toArray(new TreeObject[children.size()]);
	}
	
	/**
	 * Determine if the item has any children
	 * 
	 * @return if the item has any children
	 */
	@Override
	public boolean hasChildren() {
		return children.size() >0 ;
	}

	/**
	 * @see SchemaItem#getDefinition()
	 */
	@Override
	public Definition getDefinition() {
		return null;
	}
}