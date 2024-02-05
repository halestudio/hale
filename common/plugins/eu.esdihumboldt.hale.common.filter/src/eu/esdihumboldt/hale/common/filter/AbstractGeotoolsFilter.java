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

package eu.esdihumboldt.hale.common.filter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.FilterAttributeExtractor;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.util.factory.GeoTools;
import org.opengis.filter.And;
import org.opengis.filter.Filter;
import org.opengis.filter.Or;
import org.opengis.filter.expression.PropertyName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor;
import eu.esdihumboldt.hale.common.align.groovy.accessor.PathElement;
import eu.esdihumboldt.hale.common.align.groovy.accessor.internal.EntityAccessorUtil;
import eu.esdihumboldt.hale.common.align.instance.EntityAwareFilter;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.migrate.EntityMatch;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.filter.internal.EntityReplacementVisitor;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.util.groovy.paths.Path;

/**
 * Geotools based filter. Two filters are seen as equal if they are based on the
 * same filter expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
@SuppressWarnings("restriction")
public abstract class AbstractGeotoolsFilter
		implements eu.esdihumboldt.hale.common.instance.model.Filter, EntityAwareFilter {

	private static enum SplitType {
		AND, OR
	}

	private static final ALogger log = ALoggerFactory.getLogger(AbstractGeotoolsFilter.class);

	private final String filterTerm;
	private final Filter internFilter;

	/**
	 * Create a Geotools based filter.
	 * 
	 * @param filterTerm the filter expression
	 * @throws CQLException if parsing the filter expression fails
	 */
	public AbstractGeotoolsFilter(String filterTerm) throws CQLException {
		this.filterTerm = filterTerm;

		internFilter = createFilter(this.filterTerm);
		if (internFilter == Filter.EXCLUDE) {
			log.warn("Parsed filter will not match any instance");
		}
	}

	/**
	 * Create a filter instance from a filter term.
	 * 
	 * @param filterTerm the filter term
	 * @return the filter
	 * @throws CQLException if an error occurs on filter creation
	 */
	protected abstract AbstractGeotoolsFilter buildFilter(String filterTerm) throws CQLException;

	/**
	 * Create the filter from a filter term.
	 * 
	 * @param filterTerm the filter term
	 * @return the filter
	 * @throws CQLException if an error occurs on filter creation
	 */
	protected abstract Filter createFilter(String filterTerm) throws CQLException;

	/**
	 * Get the filter term from a filter object
	 * 
	 * @param filter the filter
	 * @return the instance filter
	 * @throws CQLException if an error occurs on filter creation
	 */
	protected abstract String toFilterTerm(Filter filter) throws CQLException;

	@Override
	public boolean match(Instance instance) {
		PropertyResolver.isLastQueryPathUnique(); // reset the information on
													// the last query
		try {
			return internFilter.evaluate(instance);
		} finally {
			if (!PropertyResolver.isLastQueryPathUnique()) {
				log.warn("Evaluated filter with non-unique definition path: " + filterTerm);
			}
		}
	}

	/**
	 * Get the ECQL expression the filter is based on.
	 * 
	 * @return the ECQL expression
	 */
	public String getFilterTerm() {
		return filterTerm;
	}

	/**
	 * @return the internal filter
	 */
	public Filter getInternFilter() {
		return internFilter;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterTerm == null) ? 0 : filterTerm.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractGeotoolsFilter other = (AbstractGeotoolsFilter) obj;
		if (filterTerm == null) {
			if (other.filterTerm != null)
				return false;
		}
		else if (!filterTerm.equals(other.filterTerm))
			return false;
		return true;
	}

	@Override
	public boolean supportsMigration() {
		return true;
	}

	@Override
	public List<Optional<EntityDefinition>> getReferencedEntities(EntityDefinition context) {

		FilterAttributeExtractor visitor = new FilterAttributeExtractor();
		Object extraData = null;
		internFilter.accept(visitor, extraData);

		return visitor.getPropertyNameSet().stream()
				.map(p -> resolveProperty(p, context, SimpleLog.NO_LOG))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<eu.esdihumboldt.hale.common.instance.model.Filter> migrateFilter(
			EntityDefinition context, EntityMatch targetMatch, AlignmentMigration migration,
			TypeDefinition preferRoot, SimpleLog log) {
		// determine how to split filter
		boolean join = targetMatch.isMatchPartOfJoin();
		SplitType splitType = join ? SplitType.AND : SplitType.OR;

		// split filter
		List<Filter> filterParts = splitFilter(internFilter, splitType);

		// migrate each filter part
		List<Filter> acceptedParts = new ArrayList<>();
		for (Filter part : filterParts) {
			EntityReplacementVisitor visitor = new EntityReplacementVisitor(migration,
					name -> resolveProperty(name, context, log), preferRoot, log);
			Object extraData = null;
			Filter copy = (Filter) part.accept(visitor, extraData);

			/*
			 * Determine if part is relevant. Only accept filter parts that are
			 * not exclusively updated with other types than `preferRoot`. (This
			 * is used to handle the different types from a Join individually,
			 * also for properties that are mapped in the same context)
			 * 
			 * TODO is usage of preferRoot OK or should we have an additional
			 * parameter to control this behavior?
			 * 
			 * Inform about parts that are dropped
			 */
			TypeDefinition focusType = preferRoot;
			String messagePrefix = (focusType == null) ? "" : focusType.getDisplayName() + ": ";
			if (visitor.isAllMismatches(focusType)) {
				// drop if there were no successful replacements at all
				if (filterParts.size() == 1) {
					try {
						log.warn(
								"{0}The filter \"{1}\" was removed because no matches for the respective properties were found",
								messagePrefix, toFilterTerm(part));
					} catch (CQLException e) {
						log.error(
								"{0}The filter was removed because no matches for the respective properties were found; error converting filter to string",
								messagePrefix, e);
					}
				}
				else {
					try {
						log.warn(
								"{0}The filter operand \"{1}\" part of the filter''s {2} condition was removed because no matches for the respective properties were found",
								messagePrefix, toFilterTerm(part), splitType);
					} catch (CQLException e) {
						log.error(
								"{0}A filter operand part of the filter's {1} condition was removed because no matches for the respective properties were found; error converting filter part to string",
								messagePrefix, splitType, e);
					}
				}
			}
			else {
				acceptedParts.add(copy);

				// log if there are replacements that don't match the focus type
				if (focusType != null) {
					List<EntityDefinition> otherReplacements = visitor.getReplacements().stream()
							.filter(entity -> !focusType.equals(entity.getType())).toList();
					if (!otherReplacements.isEmpty()) {
						try {
							log.warn(
									"{0}The filter operand \"{1}\" part of the filter''s {3} condition contains references related to other types than {2}",
									messagePrefix, toFilterTerm(part), focusType.getDisplayName(),
									splitType);
						} catch (CQLException e) {
							log.error(
									"{0}A filter operand part of the filter's {2} condition contains references related to other types than {1}; error converting filter part to string",
									messagePrefix, focusType.getDisplayName(), splitType, e);
						}
					}
				}
			}
		}

		if (acceptedParts.isEmpty()) {
			return Optional.empty();
		}

		// combine accepted filter parts
		Filter combined;
		switch (splitType) {
		case AND:
			combined = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints())
					.and(acceptedParts);
			break;
		case OR:
			combined = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints())
					.or(acceptedParts);
			break;
		default:
			throw new IllegalStateException("Unsupported filter split type " + splitType);
		}

		try {
			String filterString = toFilterTerm(combined);
			return Optional.of(buildFilter(filterString));
		} catch (CQLException e) {
			log.error("Filter could not be automatically migrated", e);
			return Optional.empty();
		}
	}

	/**
	 * Split a filter into separate AND or OR conditions.
	 * 
	 * @param filter the filter to split
	 * @param splitType on which operation to split
	 * @return the split filters as list
	 */
	private List<Filter> splitFilter(Filter filter, SplitType splitType) {
		List<Filter> result = new ArrayList<>();

		Deque<Filter> toCheck = new LinkedList<>();
		toCheck.add(filter);
		while (!toCheck.isEmpty()) {
			Filter f = toCheck.poll();
			if (SplitType.AND.equals(splitType) && f instanceof And) {
				toCheck.addAll(((And) f).getChildren());
			}
			else if (SplitType.OR.equals(splitType) && f instanceof Or) {
				toCheck.addAll(((Or) f).getChildren());
			}
			else {
				result.add(f);
			}
		}

		return result;
	}

	/**
	 * Resolve a property name based on the given context.
	 * 
	 * @param name the property name
	 * @param context the entity context
	 * @param log the operation log
	 * @return the resolved entity definition if it could be resolved uniquely
	 */
	private Optional<EntityDefinition> resolveProperty(PropertyName name, EntityDefinition context,
			SimpleLog log) {
		List<QName> path = PropertyResolver.getQNamesFromPath(name.getPropertyName());

		EntityAccessor acc = new EntityAccessor(context);
		for (QName element : path) {
			acc = acc.findChildren(element);
		}

		try {
			return Optional.ofNullable(acc.toEntityDefinition());
		} catch (IllegalStateException e) {
			List<? extends Path<PathElement>> candidates = acc.all();
			if (candidates.isEmpty()) {
				log.error("Unable to find reference to {0}", name);
				return Optional.empty();
			}
			else {
				log.warn("Could not find unique reference to {0}, trying first match", name);

				Path<PathElement> selected = candidates.get(0);

				/*
				 * XXX dirty hack to work around conditions in AdV
				 * GeographicalNames alignment not being defined properly
				 */
				if (candidates.size() > 1) {
					// prefer next candidate if this one is name in gml
					// namespace
					List<PathElement> elements = selected.getElements();
					PathElement last = elements.get(elements.size() - 1);
					QName lastName = last.getDefinition().getName();
					if ("name".equals(lastName.getLocalPart()) && "http://www.opengis.net/gml/3.2"
							.equals(lastName.getNamespaceURI())) {
						selected = candidates.get(1);
					}
				}

				return Optional.ofNullable(EntityAccessorUtil.createEntity(selected));
			}
		}
	}

}
