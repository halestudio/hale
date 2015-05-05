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

package eu.esdihumboldt.hale.common.instance.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;

/**
 * Instance utility functions.
 * 
 * @author Kai Schwierczek
 */
public final class InstanceUtil {

	private InstanceUtil() {
		// static utility class
	}

	/**
	 * Checks whether the two given instances equal each other disregarding
	 * their data set.
	 * 
	 * @param a the first instance
	 * @param b the second instance
	 * @param propertyOrderRelevant whether the order of properties of the same
	 *            name is relevant or not
	 * @return true, iff both instances are equal to each other except for their
	 *         data set
	 */
	public static boolean instanceEqual(Instance a, Instance b, boolean propertyOrderRelevant) {
		if (a == b)
			return true;
		if (a == null || b == null)
			return false;
		// compare value
		// XXX other checks than equals possible?
		if (!equals(a.getValue(), b.getValue()))
			return false;
		// check groups properties
		return groupEqual(a, b, propertyOrderRelevant);
	}

	/**
	 * Equals implementation comparing two objects, with some improvements and
	 * adaptations.
	 * 
	 * @param o1 the first object
	 * @param o2 the second object
	 * @return if the two objects are deemed equal
	 */
	private static boolean equals(Object o1, Object o2) {
		if (o1 != null && o2 != null) {
			// special case: arrays
			if (o1.getClass().isArray() && o2.getClass().isArray()) {
				return arrayToList(o1).equals(arrayToList(o2));
			}
			// special case: geometry properties
			if (o1 instanceof GeometryProperty<?> && o2 instanceof GeometryProperty<?>) {
				GeometryProperty<?> g1 = (GeometryProperty<?>) o1;
				GeometryProperty<?> g2 = (GeometryProperty<?>) o2;

				if (g1.getGeometry() == null && g2.getGeometry() == null) {
					return true;
				}
				else if (g1.getGeometry() != null && g2.getGeometry() != null) {
					boolean crsEquals;
					if (g1.getCRSDefinition() != null && g2.getCRSDefinition() != null) {
						crsEquals = Objects.equal(g1.getCRSDefinition().getCRS(), g2
								.getCRSDefinition().getCRS());
					}
					else {
						crsEquals = Objects.equal(g1.getCRSDefinition(), g2.getCRSDefinition());
					}

					// XXX do conversion of geometry?

					// topological comparison (added 0.005 as tolerance for
					// testing purpose)
					boolean geometryEquals = g1.getGeometry().equalsExact(g2.getGeometry(), 0.005);
					// geometryEquals && crsEquals;
					return geometryEquals || crsEquals;
				}
				else {
					return false;
				}
			}
		}

		return Objects.equal(o1, o2);
	}

	private static List<?> arrayToList(Object array) {
		if (array instanceof byte[]) {
			return Bytes.asList((byte[]) array);
		}
		if (array instanceof int[]) {
			return Ints.asList((int[]) array);
		}
		if (array instanceof short[]) {
			return Shorts.asList((short[]) array);
		}
		if (array instanceof long[]) {
			return Longs.asList((long[]) array);
		}
		if (array instanceof float[]) {
			return Floats.asList((float[]) array);
		}
		if (array instanceof double[]) {
			return Doubles.asList((double[]) array);
		}
		if (array instanceof char[]) {
			return Chars.asList((char[]) array);
		}
		if (array instanceof boolean[]) {
			return Booleans.asList((boolean[]) array);
		}
		return Arrays.asList((Object[]) array);
	}

	/**
	 * Check if an instance is present in the given candidates. If found, will
	 * remove the match from the candidates collection.
	 * 
	 * @param instance the instance to test
	 * @param candidates the candidates to compare the instance against
	 * @return the error message if the check failed, otherwise
	 *         <code>null</code>
	 */
	public static String checkInstance(Instance instance, Collection<Instance> candidates) {
		boolean found = false;
		Iterator<Instance> candidatesIter = candidates.iterator();
		while (!found && candidatesIter.hasNext()) {
			if (InstanceUtil.instanceEqual(instance, candidatesIter.next(), false)) {
				candidatesIter.remove();
				found = true;
			}
		}
		if (!found) {
			StringBuilder sb = new StringBuilder();
			sb.append("Could not find matching instance for: \n");
			sb.append(InstanceUtil.instanceToString(instance));
			sb.append("\n inside the available ones: \n");
			for (Instance candidate : candidates) {
				sb.append(InstanceUtil.instanceToString(candidate));
			}
			String message = sb.toString();
			return message;
		}
		return null;
	}

	/**
	 * Checks whether the two given groups equal each other.
	 * 
	 * @param a the first group
	 * @param b the second group
	 * @param propertyOrderRelevant whether the order of properties of the same
	 *            name is relevant or not
	 * @return true, if both groups are equal to each
	 */
	public static boolean groupEqual(Group a, Group b, boolean propertyOrderRelevant) {
		if (a == b)
			return true;
		// compare definitions
		if (!Objects.equal(a.getDefinition(), b.getDefinition()))
			return false;
		// check property count
		Iterable<QName> aProperties = a.getPropertyNames();
		if (Iterables.size(aProperties) != Iterables.size(b.getPropertyNames()))
			return false;
		// iterate over a properties
		for (QName aPropertyName : aProperties) {
			Object[] aProperty = a.getProperty(aPropertyName);
			Object[] bProperty = b.getProperty(aPropertyName);
			// check whether the property exists (in the same amount)
			if (bProperty == null || bProperty.length != aProperty.length)
				return false;
			if (propertyOrderRelevant) {
				// simply iterate over the property array once
				for (int i = 0; i < aProperty.length; i++)
					if (!propertyValueEquals(aProperty[i], bProperty[i], propertyOrderRelevant))
						return false;
			}
			else {
				// check whether each property value of a has an equal property
				// value in b
				List<Object> bPropertyList = new LinkedList<Object>(Arrays.asList(bProperty));
				for (Object aPropertyValue : aProperty) {
					Iterator<Object> bPropertyValueIterator = bPropertyList.iterator();
					boolean match = false;
					while (!match && bPropertyValueIterator.hasNext()) {
						Object bPropertyValue = bPropertyValueIterator.next();
						match = propertyValueEquals(aPropertyValue, bPropertyValue,
								propertyOrderRelevant);
						if (match)
							bPropertyValueIterator.remove();
					}
					if (!match)
						return false;
				}
			}
		}
		return true;
	}

	private static boolean propertyValueEquals(Object a, Object b, boolean propertyOrderRelevant) {
		if (a == b)
			return true;

		// check if a is an instance or a group for specialized check
		// XXX other method than equals if it is not a group or instance?
		if (a instanceof Instance) {
			if (b instanceof Instance
					&& instanceEqual((Instance) a, (Instance) b, propertyOrderRelevant))
				return true;
		}
		else if (a instanceof Group) {
			if (b instanceof Group && groupEqual((Group) a, (Group) b, propertyOrderRelevant))
				return true;
		}
		// Two BigDecimal objects that are equal in value but have a different
		// scale (like 2.0 and 2.00) should consider as equal.
		else if (a instanceof BigDecimal && b instanceof BigDecimal) {
			BigDecimal x = (BigDecimal) a;
			BigDecimal y = (BigDecimal) b;
			return x.compareTo(y) == 0;
		}
		else if (equals(a, b))
			return true;

		return false;
	}

	// TODO better output for values in case of Collections?
	/**
	 * Returns a string representation of the given instance.
	 * 
	 * @param instance the instance
	 * @return a string representation of the given instance
	 */
	public static String instanceToString(Instance instance) {
		StringBuilder builder = new StringBuilder();
		if (instance.getValue() != null)
			builder.append("value=").append(instance.getValue()).append('\n');
		builder.append("properties=[\n");
		builder.append(indent(groupToString(instance))).append("\n]");

		return builder.toString();
	}

	/**
	 * Returns a string representation of the given group.
	 * 
	 * @param group the group
	 * @return a string representation of the given group
	 */
	public static String groupToString(Group group) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (QName property : group.getPropertyNames()) {
			if (first)
				first = false;
			else
				builder.append('\n');
			builder.append(property.toString()).append("=[\n");
			for (Object propertyValue : group.getProperty(property)) {
				String representation;
				if (propertyValue instanceof Instance)
					representation = instanceToString((Instance) propertyValue);
				else if (propertyValue instanceof Group)
					representation = groupToString((Group) propertyValue);
				else if (propertyValue != null) {
					if (propertyValue.getClass().isArray()) {
						representation = arrayToList(propertyValue).toString();
					}
					else {
						representation = propertyValue.toString();
					}
				}
				else
					representation = "<null>";
				builder.append(indent(representation)).append('\n');
			}
			builder.append("]");
		}

		return builder.toString();
	}

	/**
	 * Indents the given string with one tab.
	 * 
	 * @param string the string to indent
	 * @return the string indented with one tab
	 */
	private static String indent(String string) {
		return "\t" + string.replace("\n", "\n\t");
	}

	/**
	 * Creates a list of instances out of a FamilyInstance
	 * 
	 * @param fi the FamilyInstance, may be <code>null</code>
	 * @return a collection of instances or an empty list
	 */
	public static Collection<Instance> getInstanceOutOfFamily(FamilyInstance fi) {
		Collection<Instance> result = new ArrayList<Instance>();
		if (fi != null) {
			result.add(fi);

			if (!fi.getChildren().isEmpty()) {
				for (FamilyInstance inst : fi.getChildren()) {
					if (!inst.getChildren().isEmpty()) {
						result.addAll(getInstanceOutOfFamily(inst));
					}
					else
						result.add(inst);
				}
			}
		}
		return result;
	}

}
