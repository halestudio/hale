/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import java.util.HashMap;
import java.util.Map;

import de.fhg.igd.osgi.util.OsgiUtils;

/**
 * Resolves class names to classes by inspecting OSGi bundle class loaders.
 * Caches classes loaded through {@link #loadClass(String)}, not thread safe.
 * 
 * @author Simon Templer
 */
public class OsgiClassResolver extends DefaultClassResolver {

	private final Map<String, Class<?>> classCache = new HashMap<>();

	@Override
	public Class<?> loadClass(String className) {
		Class<?> clazz = classCache.get(className);

		if (clazz == null) {
			// first try default mechanism for loading classes
			clazz = super.loadClass(className);
		}

		if (clazz == null) {
			clazz = OsgiUtils.loadClass(className, null);
			if (clazz != null) {
				classCache.put(className, clazz);
			}
		}
		return clazz;
	}

	@Override
	public Class<?> loadClass(String className, String module) {
		if (module == null) {
			return loadClass(className);
		}

		// try to load class from module
		Class<?> clazz = OsgiUtils.loadClass(className, module);
		if (clazz != null) {
			return clazz;
		}

		return loadClass(className);
	}

}