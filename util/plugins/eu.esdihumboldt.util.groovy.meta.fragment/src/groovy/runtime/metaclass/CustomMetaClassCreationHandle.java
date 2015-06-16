/*
 * Copyright (c) 2013 Simon Templer
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
 *     Simon Templer - initial version
 */

package groovy.runtime.metaclass;

import java.lang.reflect.Constructor;

import eu.esdihumboldt.util.groovy.meta.extension.MetaClassDescriptor;
import eu.esdihumboldt.util.groovy.meta.extension.MetaClassExtension;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MetaClassRegistry.MetaClassCreationHandle;

/**
 * Adapts created meta classes with delegating meta classes registered in the
 * {@link MetaClassExtension}.
 * 
 * @author Simon Templer
 */
public class CustomMetaClassCreationHandle extends MetaClassCreationHandle {

	private final MetaClassExtension ext = new MetaClassExtension();

	@Override
	protected MetaClass createNormalMetaClass(@SuppressWarnings("rawtypes") Class theClass,
			MetaClassRegistry registry) {
		MetaClass metaClass = super.createNormalMetaClass(theClass, registry);

		for (MetaClassDescriptor descriptor : ext.getElements()) {
			if (descriptorApplies(descriptor, theClass)) {
				// create meta class
				Class<?> delegatingMetaClass = descriptor.getMetaClass();
				try {
					Constructor<?> constructor = delegatingMetaClass
							.getConstructor(MetaClass.class);
					metaClass = (MetaClass) constructor.newInstance(metaClass);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return metaClass;
	}

	/**
	 * Check if a meta class descriptor applies to a given class.
	 * 
	 * @param descriptor the meta class descriptor
	 * @param theClass the class for which should be determined if the
	 *            descriptor applies
	 * @return <code>true</code> if the descriptor applies to the class,
	 *         <code>false</code> otherwise
	 */
	private boolean descriptorApplies(MetaClassDescriptor descriptor,
			@SuppressWarnings("rawtypes") Class theClass) {
		Class<?> forClass = descriptor.getForClass();
		if (descriptor.isForArray()) {
			if (theClass.isArray()) {
				Class<?> componentClass = theClass.getComponentType();
				if (componentClass != null) {
					return forClass.equals(componentClass)
							|| forClass.isAssignableFrom(componentClass);
				}
				else {
					// should actually not happen
					return false;
				}
			}
			else {
				// no array
				return false;
			}
		}
		else {
			return forClass.equals(theClass) || forClass.isAssignableFrom(theClass);
		}
	}

}
