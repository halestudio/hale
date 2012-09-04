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

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance decorator class.
 *
 * @author Kai Schwierczek
 */
public class InstanceDecorator implements Instance {
	private final Instance instance;

	/**
	 * Constructs the decorator with the given instance.
	 *
	 * @param instance the instance to decorate
	 */
	public InstanceDecorator(Instance instance) {
		this.instance = instance;
	}

	/**
	 * Returns the original instance.
	 *
	 * @return the original instance
	 */
	public Instance getOriginalInstance() {
		return instance;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Group#getProperty(javax.xml.namespace.QName)
	 */
	@Override
	public Object[] getProperty(QName propertyName) {
		return instance.getProperty(propertyName);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Group#getPropertyNames()
	 */
	@Override
	public Iterable<QName> getPropertyNames() {
		return instance.getPropertyNames();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getDefinition()
	 */
	@Override
	public TypeDefinition getDefinition() {
		return instance.getDefinition();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getValue()
	 */
	@Override
	public Object getValue() {
		return instance.getValue();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getDataSet()
	 */
	@Override
	public DataSet getDataSet() {
		return instance.getDataSet();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaData(java.lang.String)
	 */
	@Override
	public List<Object> getMetaData(String key) {
		return instance.getMetaData(key);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaDataNames()
	 */
	@Override
	public Set<String> getMetaDataNames() {
		return instance.getMetaDataNames();
	}
}
