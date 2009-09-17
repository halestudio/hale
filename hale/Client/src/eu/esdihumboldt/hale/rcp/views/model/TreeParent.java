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

import java.util.ArrayList;

import org.opengis.feature.type.Name;


/**
 * A special TreeObject which is a parent of other TreeObjects.
 * 
 * @author Thorsten Reitz, Christian Jauss, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class TreeParent extends TreeObject{
	private ArrayList<TreeObject> children;
	
	/**
	 * Constructor
	 * 
	 * @param label the item label
	 * @param name the item entity name
	 * @param type the item entity type
	 */
	public TreeParent(String label, Name name, TreeObjectType type) {
		super(label, name, type);
		children = new ArrayList<TreeObject>();
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
	public SchemaItem[] getChildren() {
		return (SchemaItem[]) children.toArray(new TreeObject[children.size()]);
	}
	
	/**
	 * Determine if the item has any children
	 * 
	 * @return if the item has any children
	 */
	public boolean hasChildren() {
		return children.size()>0;
	}
}