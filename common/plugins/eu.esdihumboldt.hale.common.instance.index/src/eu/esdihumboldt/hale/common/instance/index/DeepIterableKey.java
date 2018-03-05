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
import net.jcip.annotations.Immutable;

/**
 * Key that uses {@link StructuredEquals#deepIterableEquals(Object, Object)} in
 * {@link #equals(Object)} to compare the internal objects.
 * 
 * @author Simon Templer
 */
@Immutable
public class DeepIterableKey {

	/**
	 * Key instance that stands for merging of all instances.
	 */
	public static final DeepIterableKey KEY_ALL = new DeepIterableKey(Long.valueOf(1));

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
	private final Object key;

	/**
	 * Create a
	 * 
	 * @param key the key object
	 */
	public DeepIterableKey(Object key) {
		super();
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DeepIterableKey) {
			return se.deepIterableEquals(key, ((DeepIterableKey) obj).key);
		}
		return false;
	}

	/**
	 * @return the internal object represented by the key
	 */
	public Object getObject() {
		return key;
	}

	@Override
	public int hashCode() {
		return se.deepIterableHashCode(key);
	}

}
