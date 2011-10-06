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

package eu.esdihumboldt.hale.common.instance.helper;

import java.util.ArrayList;

import javax.xml.namespace.QName;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * TODO Type description
 * @author Sebastian Reinhardt
 */
public class QueryDefinitionIndex {

	
	private TypeDefinition def;
	private String query;
	
	/**
	 * @param def
	 * @param query
	 */
	public QueryDefinitionIndex (TypeDefinition def, String query){
		this.def = def;
		this.query = query;
	}
	

	
	

	/**
	 * @return the def
	 */
	public TypeDefinition getDef() {
		return def;
	}

	/**
	 * @param def the def to set
	 */
	public void setDef(TypeDefinition def) {
		this.def = def;
	}

	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}





	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((def == null) ? 0 : def.hashCode());
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		return result;
	}





	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QueryDefinitionIndex other = (QueryDefinitionIndex) obj;
		if (def == null) {
			if (other.def != null)
				return false;
		} else if (!def.equals(other.def))
			return false;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		return true;
	}
	
	
	
}
