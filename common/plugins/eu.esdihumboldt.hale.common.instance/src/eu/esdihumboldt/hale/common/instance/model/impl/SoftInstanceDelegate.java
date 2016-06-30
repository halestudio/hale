/*
 * Copyright (c) 2016 Data Harmonisation Panel
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
 *     wetransform GmbH
 */

package eu.esdihumboldt.hale.common.instance.model.impl;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.InstanceReference;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Instance delegating to a soft reference that is refreshed via an
 * InstanceReference.
 * 
 * @author Simon Templer
 */
public class SoftInstanceDelegate implements Instance {

	private volatile SoftReference<Instance> instance = null;
	private final InstanceReference ref;
	private final InstanceCollection collection;

	/**
	 * Constructs the delegating instance.
	 * 
	 * @param ref the instance reference
	 * @param collection the instance collection to resolved the reference from
	 */
	public SoftInstanceDelegate(InstanceReference ref, InstanceCollection collection) {
		this.ref = ref;
		this.collection = collection;
	}

	/**
	 * @return the original instance
	 */
	public Instance getInstance() {
		Instance result = null;
		if (instance != null) {
			result = instance.get();
		}
		if (result == null) {
			result = collection.getInstance(ref);
			instance = new SoftReference<Instance>(result);
		}
		return result;
	}

	@Override
	public Object[] getProperty(QName propertyName) {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getProperty(propertyName);
		}
		return null;
	}

	@Override
	public Iterable<QName> getPropertyNames() {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getPropertyNames();
		}
		return Collections.emptyList();
	}

	@Override
	public TypeDefinition getDefinition() {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getDefinition();
		}
		return null;
	}

	@Override
	public Object getValue() {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getValue();
		}
		return null;
	}

	@Override
	public DataSet getDataSet() {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getDataSet();
		}
		return null;
	}

	@Override
	public List<Object> getMetaData(String key) {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getMetaData(key);
		}
		return Collections.emptyList();
	}

	@Override
	public Set<String> getMetaDataNames() {
		Instance instance = getInstance();
		if (instance != null) {
			return instance.getMetaDataNames();
		}
		return Collections.emptySet();
	}
}
