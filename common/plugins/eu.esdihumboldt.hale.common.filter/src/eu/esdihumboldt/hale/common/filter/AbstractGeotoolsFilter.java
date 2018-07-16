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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.geotools.filter.FilterAttributeExtractor;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.PropertyName;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor;
import eu.esdihumboldt.hale.common.align.instance.EntityAwareFilter;
import eu.esdihumboldt.hale.common.align.migrate.AlignmentMigration;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.filter.internal.EntityReplacementVisitor;
import eu.esdihumboldt.hale.common.instance.helper.PropertyResolver;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Geotools based filter. Two filters are seen as equal if they are based on the
 * same filter expression.
 * 
 * @author Sebastian Reinhardt
 * @author Simon Templer
 */
public abstract class AbstractGeotoolsFilter
		implements eu.esdihumboldt.hale.common.instance.model.Filter, EntityAwareFilter {

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
			EntityDefinition context, AlignmentMigration migration, SimpleLog log) {

		EntityReplacementVisitor visitor = new EntityReplacementVisitor(migration,
				name -> resolveProperty(name, context, log), log);
		Object extraData = null;
		Filter copy = (Filter) internFilter.accept(visitor, extraData);

		try {
			return Optional.of(buildFilter(toFilterTerm(copy)));
		} catch (CQLException e) {
			log.error("Filter could not be automatically migrated", e);
			return Optional.empty();
		}
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
			log.error("Unable to find unique reference to " + name, e);
			// TODO instead use one of the found candidates?
			return Optional.empty();
		}
	}

}
