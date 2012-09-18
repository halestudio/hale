/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.helper;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Index for hash map used in property resolver.
 * 
 * @author Sebastian Reinhardt
 */
public class QueryDefinitionIndex {

	private TypeDefinition def;
	private DataSet dataSet;
	private String query;

	/**
	 * Constructor.
	 * 
	 * @param def the definition
	 * @param dataSet the data set
	 * @param query the query
	 */
	public QueryDefinitionIndex(TypeDefinition def, DataSet dataSet, String query) {
		this.def = def;
		this.dataSet = dataSet;
		this.query = query;
	}

	/**
	 * Returns the definition
	 * 
	 * @return the definition
	 */
	public TypeDefinition getDef() {
		return def;
	}

	/**
	 * Returns the query
	 * 
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Returns the associated data set which may be null.
	 * 
	 * @return the data set
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((def == null) ? 0 : def.hashCode());
		result = prime * result + ((dataSet == null) ? 0 : dataSet.hashCode());
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
		}
		else if (!def.equals(other.def))
			return false;

		if (dataSet == null) {
			if (other.dataSet != null)
				return false;
		}
		else if (!dataSet.equals(other.dataSet))
			return false;

		if (query == null) {
			if (other.query != null)
				return false;
		}
		else if (!query.equals(other.query))
			return false;
		return true;
	}
}
