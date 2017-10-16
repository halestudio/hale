/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Collections;

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Mapping definition between {@link Instance} and {@link IndexedPropertyValue}
 * based on a {@link PropertyEntityDefinition}.
 * 
 * @author Florian Esser
 */
public class PropertyEntityDefinitionMapping
		implements IndexMapping<Instance, IndexedPropertyValue> {

	private final PropertyEntityDefinition definition;

	/**
	 * Create the mapping based on the given property
	 * 
	 * @param definition Property entity definition
	 */
	public PropertyEntityDefinitionMapping(PropertyEntityDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.IndexMapping#map(java.lang.Object)
	 */
	@Override
	public IndexedPropertyValue map(Instance instance) {
		InstanceAccessor accessor = new InstanceAccessor(instance);
		for (ChildContext child : definition.getPropertyPath()) {
			QName name = child.getChild().getName();
			accessor.findChildren(name.getLocalPart(),
					Collections.singletonList(name.getNamespaceURI()));
		}

		return new IndexedPropertyValue(definition.getDefinition().getName(), accessor.list(true));
	}

	/**
	 * @return the property entity definition
	 */
	public PropertyEntityDefinition getDefinition() {
		return definition;
	}
}
