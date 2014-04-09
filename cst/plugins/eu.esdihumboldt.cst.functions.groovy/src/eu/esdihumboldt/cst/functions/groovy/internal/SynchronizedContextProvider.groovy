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

package eu.esdihumboldt.cst.functions.groovy.internal




/**
 * Small groovy helper class providing a closure allowing scripts synchronized 
 * access to the {@link ExecutionContext}.
 *
 * @author Kai Schwierczek
 */
class SynchronizedContextProvider {
	/**
	 * Returns a closure, which accepts one closure as an argument.
	 * When called, it will synchronize on the context map and call the closure with that map.
	 *
	 * @param context the context map to synchronize on
	 * @return a closure as described above
	 */
	public static Closure<?> getContextClosure(Map<Object, Object> context) {
		return SynchronizedContextProvider.&accessContextMap.curry(context);
	}

	/**
	 * Synchronizes on the given map and calls the closure with it.
	 *
	 * @param context the map to synchronize on
	 * @param closure the closure to call
	 */
	private static void accessContextMap(Map<Object, Object> context, Closure closure) {
		synchronized (context) {
			closure(context)
		}
	}
}
