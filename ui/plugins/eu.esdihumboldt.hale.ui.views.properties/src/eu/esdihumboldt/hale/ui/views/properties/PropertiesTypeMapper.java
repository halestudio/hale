/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
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
