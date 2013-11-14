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

package eu.esdihumboldt.hale.common.instance.groovy.meta;

import eu.esdihumboldt.hale.common.instance.groovy.InstanceAccessor;
import eu.esdihumboldt.hale.common.instance.model.Group;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * Meta class adding the <code>accessor()</code> method and
 * <code>properties</code> property to {@link Group}.
 * 
 * @author Simon Templer
 */
public class InstanceAccessorMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public InstanceAccessorMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);

		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public InstanceAccessorMetaClass(MetaClass delegate) {
		super(delegate);

		initialize();
	}

	@Override
	public Object getProperty(Object object, String property) {
		if (object instanceof Group && ("properties".equals(property) || "p".equals(property))) {
			return new InstanceAccessor(object);
		}
		return super.getProperty(object, property);
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if ((arguments == null || arguments.length == 0) && "accessor".equals(methodName)
				&& object instanceof Group) {
			return new InstanceAccessor(object);
		}
		return super.invokeMethod(object, methodName, arguments);
	}
}
