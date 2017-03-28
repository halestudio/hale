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
package eu.esdihumboldt.hale.common.propertyaccessor;

import java.util.Collection;
import java.util.stream.Collectors;

import org.geotools.factory.Hints;
import org.geotools.filter.expression.PropertyAccessor;
import org.geotools.filter.expression.PropertyAccessorFactory;

import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Factory for property accessor using {@link PropertyResolver}.
 * 
 * @author Sebastian Reinhardt
 */
public class InstancePropertyAccessorFactory implements PropertyAccessorFactory {

	/**
	 * @see PropertyAccessorFactory#createPropertyAccessor(Class, String, Class,
	 *      Hints)
	 */
	@Override
	public PropertyAccessor createPropertyAccessor(@SuppressWarnings("rawtypes") Class type,
			String xpath, @SuppressWarnings("rawtypes") Class target, Hints hints) {

		if (xpath == null)
			return null;

		if (!Instance.class.isAssignableFrom(type)) {
			return null; // we only work with instances
		}

		return new InstancePropertyAccessor();
	}

	static class InstancePropertyAccessor implements PropertyAccessor {

		@Override
		public boolean canHandle(Object object, String xpath,
				@SuppressWarnings("rawtypes") Class target) {
			if (object instanceof Instance) {
				return true;
			}
			else {
				return false;
			}
		}

		protected Object unwrap(Object obj) {
			if (obj instanceof GeometryProperty) {
				/*
				 * Extract geometry from GeometryProperty, as a Geometry is
				 * usually expected in Geotools filters related to geometries.
				 * 
				 * XXX A better alternative might be adding a Geotools converter
				 * factory that supports this conversions (also see Geotools
				 * Converters class).
				 */
				return ((GeometryProperty<?>) obj).getGeometry();
			}

			return obj;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object get(Object object, String xpath, @SuppressWarnings("rawtypes") Class target) {
			if (object instanceof Instance) {
				Collection<Object> values = PropertyResolver.getValues((Instance) object, xpath);
				if (values.size() == 1) {
					/*
					 * Always yield single value if there is only a single
					 * value. This is required for instance for the IS NULL
					 * filter. It does not work on lists.
					 */
					return unwrap(values.iterator().next());
				}
				else if (values.isEmpty()) {
					/*
					 * No values -> return null. This is required for instance
					 * for the IS NULL filter. It treats a list always as not
					 * null.
					 */
					return null;
				}
				else {
					// unwrap values
					values = values.stream().map(this::unwrap).collect(Collectors.toList());
				}

				return values;
			}
			return null;
		}

		@Override
		public void set(Object object, String xpath, Object value,
				@SuppressWarnings("rawtypes") Class target) {
			throw new UnsupportedOperationException();
		}
	}

}
