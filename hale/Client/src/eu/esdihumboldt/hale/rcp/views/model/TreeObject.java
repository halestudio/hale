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

/**
 * A TreeObject for TreeViewers. 
 * @author cjauss
 * @version $Id$
 */
public class TreeObject {
	
	private String name;
	private TreeParent parent;
	private TreeObjectType type;
	private Object data;
	
	public TreeObject(String name, TreeObjectType type, Object data) {
		this.name = name;
		this.type = type;
		this.data = data;
	}
	/**
	 * @return the type, either ROOT, 
	 */
	public TreeObjectType getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	public void setParent(TreeParent parent) {
		this.parent = parent;
	}
	public TreeParent getParent() {
		return parent;
	}
	public String toString() {
		return getName();
	}
	
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	
	public enum TreeObjectType {
		ROOT,
		ABSTRACT_FT,
		CONCRETE_FT,
		NUMERIC_ATTRIBUTE,
		STRING_ATTRIBUTE,
		COMPLEX_ATTRIBUTE,
		GEOMETRIC_ATTRIBUTE
	}

}
