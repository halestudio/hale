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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;

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
		if (!Objects.equal(a.getValue(), b.getValue()))
			return false;
		// check groups properties
		return groupEqual(a, b, propertyOrderRelevant);
	}

	/**
	 * Checks whether the two given groups equal each other.
	 * 
	 * @param a the first group
	 * @param b the second group
	 * @param propertyOrderRelevant whether the order of properties of the same
	 *            name is relevant or not
	 * @return true, iff both groups are equal to each
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
		else if (Objects.equal(a, b))
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
				else if (propertyValue != null)
					representation = propertyValue.toString();
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
