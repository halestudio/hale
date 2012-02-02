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

package eu.esdihumboldt.hale.common.instance.model.impl;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Default instance implementation backed by a {@link ListMultimap}.
 * @author Simon Templer
 */
public class DefaultInstance extends DefaultGroup implements MutableInstance {

	private DataSet dataSet;
	
	private Object value;

	/**
	 * Create an empty instance.
	 * @param definition the associated type definition
	 * @param dataSet the data set the instance is associated to
	 */
	public DefaultInstance(TypeDefinition definition, DataSet dataSet) {
		super(definition);
		this.dataSet = dataSet;
	}
	
	/**
	 * Copy constructor.
	 * Creates an instance based on the properties and values of the given 
	 * instance.
	 * @param org the instance to copy
	 */
	public DefaultInstance(Instance org) {
		super(org);
		
		setValue(org.getValue());
		setDataSet(org.getDataSet());
	}

	/**
	 * @see Instance#getValue()
	 */
	@Override
	public Object getValue() {
		return value;
	}

	/**
	 * @see Instance#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @see MutableInstance#setValue(Object)
	 */
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @see MutableInstance#setDataSet(DataSet)
	 */
	@Override
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * @see DefaultGroup#getDefinition()
	 */
	@Override
	public TypeDefinition getDefinition() {
		return (TypeDefinition) super.getDefinition();
	}

}
