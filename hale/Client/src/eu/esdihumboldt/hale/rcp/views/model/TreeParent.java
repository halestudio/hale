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


/**
 * A special TreeObject which is a parent of other TreeObjects.
 * @author cjauss
 * @version $Id$
 */
public class TreeParent extends TreeObject{
	private ArrayList<TreeObject> children;
	
	public TreeParent(String name, TreeObjectType type) {
		super(name, type);
		children = new ArrayList<TreeObject>();
	}
	
	
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
	}
	
	
	public void removeChild(TreeObject child) {
		children.remove(child);
		child.setParent(null);
	}
	
	
	public TreeObject[] getChildren() {
		return (TreeObject[]) children.toArray(new TreeObject[children.size()]);
	}
	
	
	public boolean hasChildren() {
		return children.size()>0;
	}
}