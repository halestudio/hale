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

/**
 * Resolves class names to classes.
 * 
 * @author Simon Templer
 */
public interface ClassResolver {

	/**
	 * Try to load the class with the given name.
	 * 
	 * @param className the name of the class to load
	 * @return the loaded class or <code>null</code>
	 */
	public Class<?> loadClass(String className);

	/**
	 * Try to load the class with the given name.
	 * 
	 * @param className the name of the class to load
	 * @param module the name of the module that provides the class
	 * @return the loaded class or <code>null</code>
	 */
	public Class<?> loadClass(String className, String module);

}
