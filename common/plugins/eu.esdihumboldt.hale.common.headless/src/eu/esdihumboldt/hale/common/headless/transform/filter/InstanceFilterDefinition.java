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

package eu.esdihumboldt.hale.common.headless.transform.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.common.instance.extension.filter.FilterDefinitionManager;
import eu.esdihumboldt.hale.common.instance.model.ContextAwareFilter;
import eu.esdihumboldt.hale.common.instance.model.Filter;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xsd.model.XmlElement;

/**
 * Filter definition is a group of filters, applied on {@link Instance}s one by
 * one.
 * 
 * @author Arun
 */
public class InstanceFilterDefinition implements Filter, ContextAwareFilter {

	private boolean globalContext = false;

	/**
	 * filters for specific types of source
	 */
	private final Multimap<String, Filter> typeFilters;

	/**
	 * filters without types(generalized to all types) per source
	 */
	private final List<Filter> unconditionalFilters;

	/**
	 * Filters applicable for any instance that can mark instances to be
	 * excluded.
	 */
	private final List<Filter> excludeFilters = new ArrayList<>();

	/**
	 * Excluded types for
	 */
	private final Set<String> excludedTypes;

	/**
	 * Default constructor
	 */
	public InstanceFilterDefinition() {
		this.typeFilters = ArrayListMultimap.create();
		this.unconditionalFilters = new ArrayList<>();
		this.excludedTypes = new HashSet<String>();
	}

	/**
	 * To add type filters
	 * 
	 * @param types types of schema
	 * @param filter {@link Filter}
	 */
	public void addTypeFilter(String types, Filter filter) {
		this.typeFilters.put(types, filter);
	}

	/**
	 * To add type filters based on expression
	 * 
	 * @param types types of schema
	 * @param expression filter expression
	 */
	public void addTypeFilter(String types, String expression) {
		this.typeFilters.put(types, createFilter(expression));
	}

	/**
	 * To add simple filters
	 * 
	 * @param filter {@link Filter}
	 */
	public void addUnconditionalFilter(Filter filter) {
		this.unconditionalFilters.add(filter);
	}

	/**
	 * Add an exclude filter.
	 * 
	 * @param expression the filter expression
	 */
	public void addExcludeFilter(String expression) {
		excludeFilters.add(createFilter(expression));
	}

	/**
	 * To add simple filters
	 * 
	 * @param expression filter expression
	 */
	public void addUnconditionalFilter(String expression) {
		this.unconditionalFilters.add(createFilter(expression));
	}

	/**
	 * to get typeFilters
	 * 
	 * @return multimap of {@link String} and {@link Filter}
	 */
	public Multimap<String, Filter> getTypeFilters() {
		return this.typeFilters;
	}

	/**
	 * to get Unconditional Filters
	 * 
	 * @return List of {@link Filter}
	 */
	public List<Filter> getUnconditionalFilters() {
		return Collections.unmodifiableList(unconditionalFilters);
	}

	/**
	 * Get the defined exclude filters.
	 * 
	 * @return the list of exclude filters
	 */
	public List<Filter> getExcludeFilters() {
		return Collections.unmodifiableList(excludeFilters);
	}

	/**
	 * Add excluded types from source
	 * 
	 * @param value type of Source
	 */
	public void addExcludedType(String value) {
		this.excludedTypes.add(value);
	}

	/**
	 * @return the globalContext
	 */
	public boolean isGlobalContext() {
		return globalContext;
	}

	/**
	 * @param globalContext the globalContext to set
	 */
	public void setGlobalContext(boolean globalContext) {
		this.globalContext = globalContext;
	}

	/**
	 * Create a filter from the given test.
	 * 
	 * @param expression the filter expression
	 * @return the filter or may be <code>null</code>
	 */
	private Filter createFilter(String expression) {
		Filter filter = FilterDefinitionManager.getInstance().parse(expression);
		if (filter == null) // if parsing failed
			filter = FilterDefinitionManager.getInstance().from("CQL", expression);
		return filter;
	}

	@Override
	public boolean match(Instance instance) {
		return applyFilters(instance, null);
	}

	@Override
	public boolean match(Instance instance, Map<Object, Object> context) {
		return applyFilters(instance, context);
	}

	// Filtration using unconditional filters and Typed filters. Instance should
	// match any of the filter if supplied.
	private boolean applyFilters(Instance instance, Map<Object, Object> context) {
		if (this.unconditionalFilters.size() == 0 && this.typeFilters.size() == 0
				&& this.excludedTypes.size() == 0)
			return true;

		Boolean result = null;

		// applying exclude filter first
		if (applyExcludeFilters(instance, context)) {
			if (context == null) {
				// if there is no context we can do an early exit
				return false;
			}
			result = false;
		}

		// if only exclude filters will be supplied then, it should return true
		// for other types.
		if (result == null && this.unconditionalFilters.size() == 0
				&& this.typeFilters.size() == 0) {
			return true;
		}

		if (unconditionalFilters.isEmpty()) {
			// only type filters - anything isn't rejected may pass
			if (context == null) {
				// if there is no context we can do a direct exit
				return applyTypedFilter(instance, context, true);
			}
			else {
				// otherwise we need to make sure that every filter may be
				// called (and respect previous exclusion)
				if (applyTypedFilter(instance, context, true)) {
					if (result == null) { // only set to true if not excluded
						result = true;
					}
				}
				if (result == null) {
					result = false;
				}

				return result;
			}
		}

		// applying remaining filters one by one (also handling lazy evaluation)
		if (context == null) {
			// if there is no context we can do an early exit
			return applyTypedFilter(instance, context)
					|| applyUnconditionalFilter(instance, context);
		}
		else {
			// otherwise we need to make sure that every filter may be called
			// (and respect previous exclusion)
			if (applyTypedFilter(instance, context)) {
				if (result == null) { // only set to true if not excluded
					result = true;
				}
			}
			if (applyUnconditionalFilter(instance, context)) {
				if (result == null) { // only set to true if not excluded
					result = true;
				}
			}
			if (result == null) {
				result = false;
			}

			return result;
		}
	}

	private boolean applyUnconditionalFilter(Instance instance, Map<Object, Object> context) {
		boolean result = false;

		for (Filter filter : this.unconditionalFilters) {
			if (filter instanceof ContextAwareFilter) {
				if (((ContextAwareFilter) filter).match(instance, context)) {
					if (context == null) { // no side effects possible
						return true;
					}
					result = true;
				}
			}
			else if (!result && filter.match(instance)) {
				if (context == null) { // no side effects possible
					return true;
				}
				result = true;
			}
		}

		return result;
	}

	private boolean applyTypedFilter(Instance instance, Map<Object, Object> context) {
		return applyTypedFilter(instance, context, false);
	}

	private boolean applyTypedFilter(Instance instance, Map<Object, Object> context,
			boolean acceptIfNoTypeMatch) {
		Boolean result = null;

		// Loop through all the type filters
		for (String type : this.typeFilters.keySet()) {
			if (checkType(instance.getDefinition(), type)) {
				// instance equals type name
				for (Filter filter : this.typeFilters.get(type)) {
					if (filter instanceof ContextAwareFilter) {
						if (((ContextAwareFilter) filter).match(instance, context)) {
							if (context == null) {
								return true; // no side effects possible
							}
							result = true;
						}
					}
					else if (result == null && filter.match(instance)) {
						if (context == null) {
							return true; // no side effects possible
						}
						result = true;
					}
				}
				if (context == null) { // no side effects possible
					// So, instance matches type and but does not match filter
					return false;
				}
				if (result == null) {
					result = false;
				}
			}
		}
		// it reaches here that means Instance does not match any type of
		// type filters. Method should return false for lazy evaluation.

		if (result == null) {
			result = acceptIfNoTypeMatch;
		}

		return result;
	}

	private boolean applyExcludeFilters(Instance instance, Map<Object, Object> context) {
		Boolean rejected = false;

		for (Filter filter : excludeFilters) {
			if (filter instanceof ContextAwareFilter) {
				if (((ContextAwareFilter) filter).match(instance, context)) {
					if (context == null) {
						return true; // no side effects possible
					}
					rejected = true;
				}
			}
			else if (!rejected && filter.match(instance)) {
				if (context == null) {
					return true; // no side effects possible
				}
				rejected = true;
			}
		}

		if (rejected) {
			return true;
		}

		// for excluded Types
		for (String excludedType : this.excludedTypes) {
			if (checkType(instance.getDefinition(), excludedType)) {
				return true;
			}
		}
		// instance does not match any excluded types.
		return false;
	}

	// Checking type of instance with givenType.
	private boolean checkType(TypeDefinition instanceType, String givenType) {
		// Here givenType is supplied by user

		QName qNameOfGivenType = QName.valueOf(givenType);

		if (instanceType.getName().equals(qNameOfGivenType))
			return true;

		if (instanceType.getDisplayName().equals(givenType))
			return true;

		for (XmlElement element : instanceType.getConstraint(XmlElements.class).getElements()) {
			if (element.getName().equals(qNameOfGivenType))
				return true;
		}
		// does not matched with all the above cases, return false
		return false;
	}

}
