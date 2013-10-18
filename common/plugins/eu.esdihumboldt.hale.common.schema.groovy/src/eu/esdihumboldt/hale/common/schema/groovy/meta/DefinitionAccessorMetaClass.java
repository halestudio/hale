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

package eu.esdihumboldt.hale.common.schema.groovy.meta;

import eu.esdihumboldt.hale.common.schema.groovy.DefinitionAccessor;
import eu.esdihumboldt.hale.common.schema.model.Definition;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * Meta class adding the <code>accessor()</code> method to {@link Definition}.
 * 
 * @author Simon Templer
 */
public class DefinitionAccessorMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public DefinitionAccessorMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);

		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public DefinitionAccessorMetaClass(MetaClass delegate) {
		super(delegate);

		initialize();
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if ((arguments == null || arguments.length == 0) && "accessor".equals(methodName)
				&& object instanceof Definition<?>) {
			return new DefinitionAccessor((Definition<?>) object);
		}
		return super.invokeMethod(object, methodName, arguments);
	}
}
