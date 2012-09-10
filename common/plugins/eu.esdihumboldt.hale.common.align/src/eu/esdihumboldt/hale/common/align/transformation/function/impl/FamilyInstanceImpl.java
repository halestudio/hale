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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Decorate a given instance with family function.
 * 
 * @author Kai Schwierczek
 */
public class FamilyInstanceImpl implements FamilyInstance {

	private final Instance instance;
	private Collection<FamilyInstance> children;

	/**
	 * Decorate the given instance with family function.
	 * 
	 * @param instance the instance to decorate
	 */
	public FamilyInstanceImpl(Instance instance) {
		this.instance = instance;
		children = new ArrayList<FamilyInstance>();
	}

	/**
	 * @see FamilyInstance#getChildren()
	 */
	@Override
	public Collection<FamilyInstance> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	/**
	 * 
	 * @see FamilyInstance#addChild(FamilyInstance)
	 */
	@Override
	public void addChild(FamilyInstance child) {
		children.add(child);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Group#getProperty(javax.xml.namespace.QName)
	 */
	@Override
	public Object[] getProperty(QName propertyName) {
		return instance.getProperty(propertyName);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaData(java.lang.String)
	 */
	@Override
	public List<Object> getMetaData(String key) {
		return instance.getMetaData(key);
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
	 * @see eu.esdihumboldt.hale.common.instance.model.Instance#getMetaDataNames()
	 */
	@Override
	public Set<String> getMetaDataNames() {
		return instance.getMetaDataNames();
	}
}
