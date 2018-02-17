/*
 * Copyright (c) 2018 wetransform GmbH
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

package eu.esdihumboldt.util.groovy.collector;

/**
 * Collector that supports Groovy property access. Values may be any object.
 * 
 * @author Simon Templer
 * @param <K> the key type
 * @param <C> the collector type
 */
public abstract class PropertyCollector<K, C extends PropertyCollector<K, ?>>
		extends GenericCollector<K, Object, C> {

	/**
	 * Convert a property name to a key.
	 * 
	 * @param property the property name
	 * @return the key
	 */
	protected abstract K getPropertyKey(String property);

	@Override
	public C getProperty(String property) {
		return getAt(getPropertyKey(property));
	}

	@Override
	public void setProperty(String property, Object newValue) {
		getProperty(property).set(newValue);
	}

}
