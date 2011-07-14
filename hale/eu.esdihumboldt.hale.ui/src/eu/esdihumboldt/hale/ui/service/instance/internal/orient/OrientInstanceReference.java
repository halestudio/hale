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

import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.ui.service.instance.DataSet;
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

	/**
	 * @return the dataSet
	 */
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @return the typeDefinition
	 */
	public TypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

}
