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

package eu.esdihumboldt.hale.common.instance.index;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.util.StructuredEquals;

/**
 * Tuple of a property {@link QName} and its associated values
 * 
 * @author Florian Esser
 */
public class IndexedPropertyValue {

	private static final StructuredEquals se = new StructuredEquals() {

		@Override
		protected Iterable<?> asIterable(Object object) {
			if (object instanceof Group) {
				Group o = (Group) object;
				List<Object> objects = new LinkedList<Object>();
				objects.add(o.getDefinition());
				if (o instanceof Instance)
					objects.add(((Instance) o).getValue());
				List<QName> propertyNames = Lists.newArrayList(o.getPropertyNames());
				Collections.sort(propertyNames, new Comparator<QName>() {

					@Override
					public int compare(QName o1, QName o2) {
						return o1.toString().compareTo(o2.toString());
					}
				});
				for (QName propertyName : propertyNames) {
					objects.add(propertyName);
					objects.addAll(Arrays.asList(o.getProperty(propertyName)));
				}
				return objects;
			}
			return super.asIterable(object);
		}
	};

	private final List<QName> property;
	private final List<?> values;

	/**
	 * Create the value
	 * 
	 * @param property Property {@link QName}
	 * @param values Property values
	 */
	public IndexedPropertyValue(List<QName> property, List<?> values) {
		this.property = property;
		this.values = values;
	}

	/**
	 * @return the property
	 */
	public List<QName> getProperty() {
		return property;
	}

	/**
	 * @return the values
	 */
	public List<?> getValues() {
		return values;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IndexedPropertyValue) {
			IndexedPropertyValue other = (IndexedPropertyValue) obj;
			return this.property.equals(other.property)
					&& se.deepIterableEquals(this.values, other.values);
		}
		return false;
	}

	@Override
	public int hashCode() {
		int result = 31 + this.property.hashCode();
		result = 31 * result + se.deepIterableHashCode(values);

		return result;
	}
}
