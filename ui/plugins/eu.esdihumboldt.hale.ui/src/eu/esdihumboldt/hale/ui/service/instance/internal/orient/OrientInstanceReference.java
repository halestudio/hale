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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import net.jcip.annotations.Immutable;

import com.orientechnologies.orient.core.id.ORID;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.instance.InstanceReference;

/**
 * Instance reference for an {@link OrientInstanceService}
 * @author Simon Templer
 */
@Immutable
public class OrientInstanceReference implements InstanceReference {

	private final ORID id;
	private final DataSet dataSet;
	private final TypeDefinition typeDefinition;

	/**
	 * Create a reference to an instance
	 * @param id the record ID
	 * @param dataSet the data set
	 * @param typeDefinition the associated type definition
	 */
	public OrientInstanceReference(ORID id, DataSet dataSet, TypeDefinition typeDefinition) {
		this.id = id;
		this.dataSet = dataSet;
		this.typeDefinition = typeDefinition;
	}

	/**
	 * @return the id
	 */
	public ORID getId() {
		return id;
	}

	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @return the typeDefinition
	 */
	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSet == null) ? 0 : dataSet.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		OrientInstanceReference other = (OrientInstanceReference) obj;
		if (dataSet != other.dataSet)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
