/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.core.merge;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import net.jcip.annotations.Immutable;

import com.google.common.collect.Lists;

import eu.esdihumboldt.hale.common.instance.model.Group;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.util.StructuredEquals;

/**
 * Key that uses {@link StructuredEquals#deepIterableEquals(Object, Object)} in
 * {@link #equals(Object)} to compare the internal objects.
 * 
 * @author Simon Templer
 */
@Immutable
public class DeepIterableKey {

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

	@Override
	public int hashCode() {
		return se.deepIterableHashCode(key);
	}

}
