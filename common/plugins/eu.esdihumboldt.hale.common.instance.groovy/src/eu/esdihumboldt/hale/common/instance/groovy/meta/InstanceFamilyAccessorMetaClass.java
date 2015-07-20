/*
 * Copyright (c) 2015 Simon Templer
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

import eu.esdihumboldt.hale.common.instance.groovy.InstanceFamilyAccessor;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * Meta class adding the <code>children()</code> method and <code>links</code>
 * property to {@link FamilyInstance}.
 * 
 * @author Simon Templer
 */
public class InstanceFamilyAccessorMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public InstanceFamilyAccessorMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);

		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public InstanceFamilyAccessorMetaClass(MetaClass delegate) {
		super(delegate);

		initialize();
	}

	@Override
	public Object getProperty(Object object, String property) {
		if (object instanceof FamilyInstance && ("links".equals(property) || "l".equals(property))) {
			return new InstanceFamilyAccessor((FamilyInstance) object);
		}
		return super.getProperty(object, property);
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if ((arguments == null || arguments.length == 0) && "children".equals(methodName)
				&& object instanceof FamilyInstance) {
			return new InstanceFamilyAccessor((FamilyInstance) object);
		}
		return super.invokeMethod(object, methodName, arguments);
	}
}
