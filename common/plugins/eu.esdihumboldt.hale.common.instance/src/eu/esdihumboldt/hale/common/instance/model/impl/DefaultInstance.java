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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
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
	
	private ListMultimap<String, Object> metaData;

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

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaData(java.lang.String)
	 */
	@Override
	public List<Object> getMetaData(String key) {
		if(metaData == null || metaData.isEmpty()){
			return Collections.emptyList();
		}
		else return metaData.get(key);
		
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.MutableInstance#puttMetaData(java.lang.String, java.lang.Object)
	 */
	@Override
	public void puttMetaData(String key, Object obj) {
		if(metaData == null){
			metaData = ArrayListMultimap.create();
		}
		metaData.put(key, obj);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaDataNames()
	 */
	@Override
	public Set<String> getMetaDataNames() {		
		if(metaData == null){
			return Collections.emptySet();		
		}	
		
		else return Collections.unmodifiableSet(metaData.keySet());

	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.MutableInstance#setMetaData(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void setMetaData(String key, Object... values) {
		if(metaData == null){
			metaData = ArrayListMultimap.create();
		}
		
		if (values == null || values.length == 0) {
			metaData.removeAll(key);
			return;
		}
		
		else{
			List<Object> valueList = new ArrayList<Object>();
			for (Object value : values) {
				valueList.add(value);
			}
			metaData.putAll(key, valueList);		}
	}

}
