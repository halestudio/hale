/*
 * Copyright (c) 2016 wetransform GmbH
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

package eu.esdihumboldt.cst.functions.groovy.helpers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

/**
 * Thread-safe helper for collecting values.
 * 
 * @author Simon Templer
 */
public class Collector extends GroovyObjectSupport {

	private final Map<Object, Collector> properties = new HashMap<>();

	private final List<Object> values = new ArrayList<>();

	@Override
	public Collector getProperty(String property) {
		return getAt(property);
	}

	@Override
	public void setProperty(String property, Object newValue) {
		getProperty(property).set(newValue);
	}

	/**
	 * Set the collector's value. Clears all previous values and sets the given
	 * value as the single value.
	 * 
	 * @param value the value to set
	 */
	public void set(Object value) {
		synchronized (values) {
			values.clear();
			values.add(value);
		}
	}

	/**
	 * Convert the collector to the given type.
	 * 
	 * @param clazz the class to convert to
	 * @return the converted object
	 */
	public Object asType(Class<?> clazz) {
		if (List.class.equals(clazz)) {
			return values();
		}
		else if (Map.class.equals(clazz)) {
			synchronized (properties) {
				return new HashMap<>(properties);
			}
		}

		throw new IllegalArgumentException("Conversion to" + clazz.getName() + " not supported.");
	}

	/**
	 * Add a value to the collector.
	 * 
	 * @param value the value to add
	 */
	public void leftShift(Object value) {
		add(value);
	}

	/**
	 * Add a value to the collector.
	 * 
	 * @param value the value to add
	 */
	public void add(Object value) {
		synchronized (values) {
			values.add(value);
		}
	}

	/**
	 * Get the first value of the collector.
	 * 
	 * @return the first value or <code>null</code>
	 */
	public Object value() {
		synchronized (values) {
			return values.isEmpty() ? null : values.get(0);
		}
	}

	/**
	 * Get the list of values of the collector.
	 * 
	 * @return a copy of the list of values
	 */
	public List<Object> values() {
		synchronized (values) {
			return new ArrayList<>(values);
		}
	}

	/**
	 * Clear the values and return them.
	 * 
	 * @return the value list
	 */
	public List<Object> clear() {
		synchronized (values) {
			List<Object> copy = new ArrayList<>(values);
			values.clear();
			return copy;
		}
	}

	/**
	 * Get the collector with the given name.
	 * 
	 * @param property the collector name
	 * @return the child collector
	 */
	public Collector getAt(Object property) {
		synchronized (properties) {
			Collector child = properties.get(property);

			if (child == null) {
				child = new Collector();
				properties.put(property, child);
			}

			return child;
		}
	}

	/**
	 * Set the value for the collector with the given name.
	 * 
	 * @param property the child collector name
	 * @param value the value to set on the child collector
	 */
	public void putAt(Object property, Object value) {
		getAt(property).set(value);
	}

	/**
	 * Iterate over the values (one argument) or over the child collectors and
	 * values (two arguments).
	 * 
	 * @param closure the closure called for each value or key/values pair
	 */
	public void each(Closure<?> closure) {
		if (closure.getMaximumNumberOfParameters() >= 2) {
			// iterate map
			Map<Object, Collector> props;
			synchronized (properties) {
				props = new HashMap<>(properties);
			}
			props.forEach((key, collector) -> {
				closure.call(key, collector.values());
			});
		}
		else {
			// iterate values
			values().forEach(value -> closure.call(value));
		}
	}

	/**
	 * Iterate over the child collectors.
	 * 
	 * @param closure the closure called for each collector key and collector
	 *            (two arguments) or only the collector (one argument)
	 */
	public void eachCollector(Closure<?> closure) {
		// iterate map
		Map<Object, Collector> props;
		synchronized (properties) {
			props = new HashMap<>(properties);
		}
		props.forEach((key, collector) -> {
			if (closure.getMaximumNumberOfParameters() >= 2) {
				closure.call(key, collector);
			}
			else {
				closure.call(collector);
			}
		});
	}

	/**
	 * Iterate over the values (one argument) or over the child collectors and
	 * values (two arguments) and resets the respective values.
	 * 
	 * @param closure the closure called for each value or key/values pair
	 */
	public void consume(Closure<?> closure) {
		if (closure.getMaximumNumberOfParameters() >= 2) {
			// iterate map
			Map<Object, Collector> props;
			synchronized (properties) {
				props = new HashMap<>(properties);
			}
			props.forEach((key, collector) -> {
				closure.call(key, collector.clear());
			});
		}
		else {
			// iterate values
			List<Object> values = clear();
			values.forEach(value -> closure.call(value));
		}
	}

}
