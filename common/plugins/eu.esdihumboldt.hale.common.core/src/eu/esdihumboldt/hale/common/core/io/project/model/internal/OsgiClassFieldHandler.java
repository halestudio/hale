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

package eu.esdihumboldt.hale.common.core.io.project.model.internal;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * Field handler for {@link Class}es in an OSGi context.
 * 
 * @author Simon Templer
 */
public class OsgiClassFieldHandler extends GeneralizedFieldHandler {

	/**
	 * @see GeneralizedFieldHandler#convertUponGet(Object)
	 */
	@Override
	public Object convertUponGet(Object value) {
		if (value == null) {
			return null;
		}
		return ((Class<?>) value).getName();
	}

	/**
	 * @see GeneralizedFieldHandler#convertUponSet(Object)
	 */
	@Override
	public Object convertUponSet(Object value) {
		return OsgiUtils.loadClass((String) value, null);
	}

	/**
	 * @see GeneralizedFieldHandler#getFieldType()
	 */
	@Override
	public Class<?> getFieldType() {
		return Class.class;
	}

}
