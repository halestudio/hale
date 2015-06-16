/*
 * Copyright (c) 2015 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import eu.esdihumboldt.cst.MultiValue;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * Adds implicit groovy conversions to {@link MultiValue} via the asType()
 * method.
 * 
 * @author Simon Templer
 */
public class AsMultiValueMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public AsMultiValueMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);
		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public AsMultiValueMetaClass(MetaClass delegate) {
		super(delegate);
		initialize();
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if (object != null && arguments != null && arguments.length == 1
				&& "asType".equals(methodName) && MultiValue.class.equals(arguments[0])) {
			// call to asType(MultiValue.class)
			if (object.getClass().isArray()) {
				return convertArrayToMultiValue(object);
			}
			else if (object instanceof Collection<?>) {
				return new MultiValue((Collection<?>) object);
			}
			else if (object instanceof Iterable<?>) {
				MultiValue result = new MultiValue();
				for (Object value : (Iterable<?>) object) {
					result.add(value);
				}
				return result;
			}
		}
		return super.invokeMethod(object, methodName, arguments);
	}

	/**
	 * Converts an object that is an array to a MultiValue.
	 * 
	 * @param array the array object
	 * @return the MultiValue representing the array
	 */
	private static MultiValue convertArrayToMultiValue(Object array) {
		Class<?> ofArray = array.getClass().getComponentType();
		if (ofArray.isPrimitive()) {
			MultiValue ar = new MultiValue();
			int length = Array.getLength(array);
			for (int i = 0; i < length; i++) {
				ar.add(Array.get(array, i));
			}
			return ar;
		}
		else {
			return new MultiValue(Arrays.asList((Object[]) array));
		}
	}

}
