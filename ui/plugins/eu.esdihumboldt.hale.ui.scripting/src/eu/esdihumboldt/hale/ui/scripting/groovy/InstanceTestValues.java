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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.ui.scripting.groovy;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;
import eu.esdihumboldt.hale.common.instance.model.DataSet;
import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.common.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.common.instance.model.TypeFilter;
import eu.esdihumboldt.hale.ui.service.instance.InstanceService;

/**
 * Test values provided based on the instance service.
 * 
 * @author Simon Templer
 */
public class InstanceTestValues implements TestValues {

	/**
	 * @see TestValues#get(PropertyEntityDefinition)
	 */
	@Override
	public Object get(PropertyEntityDefinition property) {
		Instance instance = getInstance(property);

		if (instance == null) {
			return getDefaultValue(property);
		}

		return extractValue(instance, property);
	}

	/**
	 * Extract the value represented by the given property from the given
	 * instance.
	 * 
	 * @param instance the instance
	 * @param property the property
	 * @return the first property value in the instance, or <code>null</code>
	 */
	protected Object extractValue(Instance instance, PropertyEntityDefinition property) {
		List<ChildContext> path = property.getPropertyPath();

		Object current = instance;
		// step down for each path element
		for (ChildContext element : path) {
			if (current instanceof Group) {
				Group group = (Group) current;
				Object[] vals = group.getProperty(element.getChild().getName());
				if (vals != null && vals.length > 0) {
					// TODO match filter?
					current = vals[0]; // child
				}
				else {
					// property not present
					return null;
				}
			}
			else {
				// property not present
				return null;
			}
		}
		/*
		 * Do not extract value if it is an instance as we might want to work on
		 * the instance itself.
		 */

		return current;
	}

	/**
	 * Get a default test value for the given property.
	 * 
	 * @param property the property
	 * @return the test value
	 */
	protected Object getDefaultValue(PropertyEntityDefinition property) {
		return null;
	}

	/**
	 * Get an instance that may hold a value for the given property.
	 * 
	 * @param entity the property
	 * @return an instance or <code>null</code>
	 */
	protected Instance getInstance(EntityDefinition entity) {
		// TODO cache instance?
		InstanceService is = PlatformUI.getWorkbench().getService(InstanceService.class);
		InstanceCollection instances = is.getInstances(DataSet.SOURCE)
				.select(new TypeFilter(entity.getType()));
		if (entity.getFilter() != null) {
			instances = instances.select(entity.getFilter());
		}

		ResourceIterator<Instance> it = instances.iterator();
		try {
			// TODO use a random instance?
			if (it.hasNext()) {
				return it.next();
			}
		} finally {
			it.close();
		}

		return null;
	}

	@Override
	public Instance get(TypeEntityDefinition type) {
		return getInstance(type);
	}

}
