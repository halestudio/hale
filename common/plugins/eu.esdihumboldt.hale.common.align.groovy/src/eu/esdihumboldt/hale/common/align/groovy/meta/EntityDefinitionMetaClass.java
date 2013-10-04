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

package eu.esdihumboldt.hale.common.align.groovy.meta;

import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import groovy.lang.DelegatingMetaClass;
import groovy.lang.MetaClass;

/**
 * Meta class adding the <code>accessor()</code> method to
 * {@link EntityDefinition}.
 * 
 * @author Simon Templer
 */
public class EntityDefinitionMetaClass extends DelegatingMetaClass {

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(Class)
	 */
	public EntityDefinitionMetaClass(@SuppressWarnings("rawtypes") Class theClass) {
		super(theClass);

		initialize();
	}

	/**
	 * @see DelegatingMetaClass#DelegatingMetaClass(MetaClass)
	 */
	public EntityDefinitionMetaClass(MetaClass delegate) {
		super(delegate);

		initialize();
	}

	@Override
	public Object invokeMethod(Object object, String methodName, Object[] arguments) {
		if ((arguments == null || arguments.length == 0) && "accessor".equals(methodName)
				&& object instanceof EntityDefinition) {
			return new EntityAccessor((EntityDefinition) object);
		}
		return super.invokeMethod(object, methodName, arguments);
	}
}
