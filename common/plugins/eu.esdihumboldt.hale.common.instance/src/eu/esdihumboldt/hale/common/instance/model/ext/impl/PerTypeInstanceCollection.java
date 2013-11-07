/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.instance.model.ext.impl;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ext.InstanceCollection2;
import eu.esdihumboldt.hale.common.instance.model.impl.MultiInstanceCollection;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;

/**
 * Multi instance collection that consists of one instance collection per type.
 * 
 * @author Simon Templer
 */
public class PerTypeInstanceCollection extends MultiInstanceCollection implements
		InstanceCollection2 {

	private final Map<TypeDefinition, InstanceCollection> collections;

	/**
	 * Create an instance collection consisting of the given instance
	 * collections.
	 * 
	 * @param collections the instance collections mapped to the type associated
	 *            to the instances they contain
	 */
	public PerTypeInstanceCollection(Map<TypeDefinition, InstanceCollection> collections) {
		super(new ArrayList<>(collections.values()));
		this.collections = ImmutableMap.copyOf(collections);
	}

	@Override
	public boolean supportsFanout() {
		return true;
	}

	@Override
	public Map<TypeDefinition, InstanceCollection> fanout() {
		return collections;
	}

}
