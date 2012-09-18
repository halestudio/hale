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
