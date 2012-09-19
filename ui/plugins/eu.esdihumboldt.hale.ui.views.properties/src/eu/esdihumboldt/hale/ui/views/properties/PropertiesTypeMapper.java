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

package eu.esdihumboldt.hale.ui.views.properties;

import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;
import org.eclipse.ui.views.properties.tabbed.ITypeMapper;

import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.align.model.transformation.tree.TransformationTreeUtil;

/**
 * Type mapper for objects containing definitions
 * 
 * @author Simon Templer
 */
public class PropertiesTypeMapper extends AbstractTypeMapper {

	/**
	 * @see ITypeMapper#mapType(Object)
	 */
	@Override
	public Class<?> mapType(Object object) {
		object = TransformationTreeUtil.extractObject(object);

		if (object instanceof EntityDefinition) {
			return ((EntityDefinition) object).getDefinition().getClass();
		}

		return super.mapType(object);
	}

}
