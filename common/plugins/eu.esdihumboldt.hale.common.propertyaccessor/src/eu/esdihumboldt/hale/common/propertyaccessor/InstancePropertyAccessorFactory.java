/**
 * 
 */
package eu.esdihumboldt.hale.common.propertyaccessor;

import org.geotools.factory.Hints;
import org.geotools.filter.expression.PropertyAccessor;
import org.geotools.filter.expression.PropertyAccessorFactory;

import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * @author Sebastian Reinhardt
 * 
 */
public class InstancePropertyAccessorFactory implements PropertyAccessorFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.geotools.filter.expression.PropertyAccessorFactory#createPropertyAccessor
	 * (java.lang.Class, java.lang.String, java.lang.Class,
	 * org.geotools.factory.Hints)
	 */
	@Override
	public PropertyAccessor createPropertyAccessor(Class type, String xpath, Class target,
			Hints hints) {

		if (xpath == null)
			return null;

		if (!Instance.class.isAssignableFrom(type)) {
			return null; // we only work with instances
		}

		return new InstancePropertyAccessor();
	}

	static class InstancePropertyAccessor implements PropertyAccessor {

		public boolean canHandle(Object object, String xpath, Class target) {

			if (object instanceof Instance) {

				return true;

			}

			else
				return false;
		}

		public Object get(Object object, String xpath, Class target) {

			if (object instanceof Instance) {

				return PropertyResolver.getValues((Instance) object, xpath);

			}

			return null;
		}

		public void set(Object object, String xpath, Object value, Class target) {
			throw new UnsupportedOperationException();

		}
	}

}
