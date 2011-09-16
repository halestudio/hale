/*
 * LICENSE: This program is being made available under the LGPL 3.0 license.
 * For more information on the license, please read the following:
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * For additional information on the Model behind Mismatches, please refer to
 * the following publication(s):
 * Thorsten Reitz (2010): A Mismatch Description Language for Conceptual Schema 
 * Mapping and Its Cartographic Representation, Geographic Information Science,
 * http://www.springerlink.com/content/um2082120r51232u/
 */
package eu.xsdi.mdl.model.consequence;

import java.util.HashMap;
import java.util.Map;

import eu.xsdi.mdl.model.Consequence;
import eu.xsdi.mdl.model.Mismatch;

/**
 * Each {@link Consequence} has a context in which it is valid. This type can 
 * be used to describe that context by means of referencing application 
 * schema URLs.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class MismatchContext {
	
	private Map<String, ContextOperator> schemas;
	
	// Constructors ............................................................
	
	/**
	 * default constructor.
	 */
	public MismatchContext() {
		super();
		this.schemas = new HashMap<String, ContextOperator>();
	}
	
	/**
	 * @param schema the qualified namespace of the schema for which to define a
	 * {@link MismatchContext}.
	 * @param operator the {@link ContextOperator} indicating the extent of defined 
	 * {@link MismatchContext}.
	 */
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
		/** Mismatch occurs in the given schema. */
		In, 
		/** Mismatch occurs outside the given schema. */
		OutOf, 
		/** Mismatch occurs always, not bound to a schema. */
		Always 
	}

}
