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
package eu.esdihumboldt.hale.quality.mdl;

import java.util.HashMap;
import java.util.Map;

/**
 * Each Mismatch has a context in which it is valid, This type can be used to 
 * describe that context by means of referencing application schema URLs.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class MismatchContext {
	
	private Map<String, ContextOperator> schemas;
	
	// Constructors ............................................................
	
	public MismatchContext() {
		super();
		this.schemas = new HashMap<String, ContextOperator>();
	}
	
	public MismatchContext(String schema, ContextOperator operator) {
		this();
		this.schemas.put(schema, operator);
	}
	
	// Standard methods ........................................................
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MismatchContext: ");
		if (this.schemas.containsValue(ContextOperator.Always)) {
			sb.append("Always");
		}
		else {
			for (String s : this.schemas.keySet()) {
				sb.append(s + ": " + this.schemas.get(s));
			}
		}
		return super.toString();
	}
	
	// MismatchContext methods .................................................

	public void addNamespace(String schema, ContextOperator operator) {
		this.schemas.put(schema, operator);
	}
	
	public void addAlwaysElement() {
		this.schemas.put(null, ContextOperator.Always);
	}
	
	/**
	 * @param namespace a {@link String} containing the namespace URL/URI/URN
	 * to check
	 * @return true if the schema identified by the given namespace would be 
	 * affected by the {@link Mismatch} having this {@link MismatchContext}.
	 */
	public boolean isAffected(String namespace) {
		// check if the given namespace is generally mentioned
		if (this.schemas.containsKey(namespace)) {
			if (this.schemas.get(namespace).equals(ContextOperator.In)) {
				return true;
			}
		}
		// if there is an element that the mismatch occurs outside a given schema, then also return true.
		if (this.schemas.containsValue(ContextOperator.OutOf)) {
			return true;
		}
		
		if (this.schemas.containsValue(ContextOperator.Always)) {
			return true;
		}
		
		return false;
	}
	
	public enum ContextOperator{
		In, /** Mismatch occurs in the given schema. */
		OutOf, /** Mismatch occurs outside the given schema. */
		Always /** Mismatch occurs always, not bound to a schema. */
	}

}
