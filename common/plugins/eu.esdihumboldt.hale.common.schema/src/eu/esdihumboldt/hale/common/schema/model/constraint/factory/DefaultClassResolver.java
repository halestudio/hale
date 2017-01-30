/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.common.schema.model.constraint.factory;

import org.apache.commons.lang.ClassUtils;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Default class resolver.
 * 
 * @author Simon Templer
 */
public class DefaultClassResolver implements ClassResolver {

	private static final ALogger log = ALoggerFactory.getLogger(DefaultClassResolver.class);

	@Override
	public Class<?> loadClass(String className) {
		Class<?> result = null;

		try {
			// loading via ClassUtils also supports primitive type classes
			// which Class.forName is not able to restore
			result = ClassUtils.getClass(className);
		} catch (ClassNotFoundException e) {
			log.debug("Could not find class " + className, e);
		}

		return result;
	}

	@Override
	public Class<?> loadClass(String className, String module) {
		// no module support
		return loadClass(className);
	}

}
