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
			else
				return false;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object get(Object object, String xpath, @SuppressWarnings("rawtypes") Class target) {
			if (object instanceof Instance) {
				return PropertyResolver.getValues((Instance) object, xpath);
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
