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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

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
	 * Key used for properties when serializing collector.
	 */
	public static final String KEY_PROPERTIES = "+";

	/**
	 * Key used for values when serializing collector.
	 */
	public static final String KEY_VALUES = "-";

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

	/**
	 * Increments the collector's value if it is a number (to an integer). If no
	 * value is set it will be set to one. Otherwise an exception is thrown.
	 * 
	 * @return the collector
	 */
	@SuppressWarnings("unchecked")
	public C next() {
		synchronized (values) {
			Object value = value();
			if (value == null) {
				set(1L);
			}
			else if (value instanceof Number) {
				set(((Number) value).longValue() + 1);
			}
			else {
				throw new IllegalStateException("Incrementing only supported for number values");
			}
		}
		return (C) this;
	}

	// convenience methods for calling from java

	/**
	 * Get a sub collector with the given name.
	 * 
	 * @param property the sub collector name
	 * @return the sub collector
	 */
	public C at(String property) {
		return getProperty(property);
	}

	// serialization

	/**
	 * Get the collector as map/list structure. This method is not thread safe
	 * and should not be used while the collector is modified.
	 * 
	 * @param compact if a compact representation should be used
	 * 
	 * @return the map/list representation of the collector or <code>null</code>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object saveToMapListStructure(boolean compact) {
		if (compact && properties.isEmpty()) {
			// compact & only values
			if (values.isEmpty()) {
				return null;
			}
			else if (values.size() == 1) {
				return values.get(0);
			}
			else {
				return new ArrayList<>(values);
			}
		}
		else {
			Map children = new HashMap<>();

			for (Entry<K, C> entry : properties.entrySet()) {
				Object child = entry.getValue().saveToMapListStructure(compact);
				if (child != null) {
					children.put(entry.getKey(), child);
				}
			}

			if (compact && values.isEmpty()) {
				// compact & only children
				return children;
			}
			else {
				// children and values

				if (values.isEmpty() && children.isEmpty()) {
					return null;
				}

				Map res = new HashMap<>();
				if (!values.isEmpty()) {
					if (values.size() == 1) {
						res.put(KEY_VALUES, values.get(0));
					}
					else {
						res.put(KEY_VALUES, new ArrayList<>(values));
					}
				}
				if (!children.isEmpty()) {
					res.put(KEY_PROPERTIES, children);
				}
				return res;
			}
		}
	}

	/**
	 * Get the collector as Json string. This method is not thread safe and
	 * should not be used while the collector is modified.
	 * 
	 * @param compact if a compact representation should be used
	 * 
	 * @return the Json representation of the collector
	 */
	public String saveToJson(boolean compact) {
		return JsonOutput.toJson(saveToMapListStructure(compact));
	}

	/**
	 * Load the collector from a map/list structure. This method is not thread
	 * safe and should not be used while the collector is modified.
	 * 
	 * @param from the list or map to load the collector from
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadFromMapListStructure(Object from) {
		properties.clear();
		values.clear();

		if (from instanceof List) {
			// only values
			values.addAll((List) from);
		}
		else if (from instanceof Map) {
			Map<String, ?> map = (Map<String, ?>) from;
			Map<String, ?> children = null;
			if (map.containsKey(KEY_PROPERTIES) || map.containsKey(KEY_VALUES)) {
				// assume full notation
				children = (Map<String, ?>) map.get(KEY_PROPERTIES);

				Object vals = map.get(KEY_VALUES);
				if (vals instanceof List) {
					// only values
					values.addAll((List) vals);
				}
				else if (vals != null) {
					values.add(vals);
				}
			}
			else {
				// assume collectors only
				children = map;
			}

			// load children
			if (children != null) {
				for (Entry<String, ?> child : children.entrySet()) {
					at(child.getKey()).loadFromMapListStructure(child.getValue());
				}
			}
		}
		else if (values != null) {
			values.add(from);
		}
	}

	/**
	 * Load the collector from a Json string. This method is not thread safe and
	 * should not be used while the collector is modified.
	 * 
	 * @param json the collector Json representation
	 */
	public void loadFromJson(String json) {
		loadFromMapListStructure(new JsonSlurper().parseText(json));
	}

}
