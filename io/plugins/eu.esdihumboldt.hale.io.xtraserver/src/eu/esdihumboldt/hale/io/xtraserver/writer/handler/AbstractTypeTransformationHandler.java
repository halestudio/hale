/*
 * Copyright (c) 2017 interactive instruments GmbH
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
 *     interactive instruments GmbH <http://www.interactive-instruments.de>
 */

package eu.esdihumboldt.hale.io.xtraserver.writer.handler;

import java.util.Collection;

import javax.xml.namespace.QName;

import com.google.common.collect.ListMultimap;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition;
import eu.esdihumboldt.hale.common.filter.AbstractGeotoolsFilter;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.PrimaryKey;
import eu.esdihumboldt.hale.io.jdbc.constraints.DatabaseTable;
import eu.esdihumboldt.hale.io.xsd.constraint.XmlElements;
import eu.esdihumboldt.hale.io.xtraserver.compatibility.XtraServerCompatibilityMode;

/**
 * Abstract Type Transformation Handler
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
abstract class AbstractTypeTransformationHandler implements TypeTransformationHandler {

	protected final MappingContext mappingContext;

	protected AbstractTypeTransformationHandler(final MappingContext mappingContext) {
		this.mappingContext = mappingContext;
	}

	protected QName getFeatureTypeName(final Cell cell) {
		final ListMultimap<String, ? extends Entity> targetEntities = cell.getTarget();
		if (targetEntities == null || targetEntities.size() == 0) {
			throw new IllegalStateException("No target type has been specified.");
		}
		final Entity targetType = targetEntities.values().iterator().next();
		final TypeDefinition targetTypeDefinition = targetType.getDefinition().getType();
		final XmlElements constraints = targetTypeDefinition.getConstraint(XmlElements.class);
		if (constraints == null || constraints.getElements().size() == 0) {
			throw new IllegalStateException("No constraint has been specified.");
		}
		else if (constraints.getElements().size() > 1) {
			throw new IllegalStateException("More than one constraint has been specified.");
		}
		return constraints.getElements().iterator().next().getName();
	}

	protected String getPrimaryKey(final TypeDefinition definition) {
		final PrimaryKey primaryKey = definition.getConstraint(PrimaryKey.class);
		if (primaryKey == null || primaryKey.getPrimaryKeyPath() == null
				|| primaryKey.getPrimaryKeyPath().isEmpty()) {
			return null;
		}
		return primaryKey.getPrimaryKeyPath().iterator().next().getLocalPart();
	}

	protected MappingTableBuilder createTableIfAbsent(final EntityDefinition sourceType) {
		final TypeDefinition sourceTypeDefinition = sourceType.getType();
		final String tableName = sourceTypeDefinition.getDisplayName();

		return this.mappingContext.getTable(tableName).orElseGet(() -> {
			MappingTableBuilder table = new MappingTableBuilder();
			final DatabaseTable dbTable = sourceTypeDefinition.getConstraint(DatabaseTable.class);
			if (dbTable != null && dbTable.getTableName() != null) {
				table.name(dbTable.getTableName());
			}
			else {
				table.name(tableName);
			}

			final String primaryKey = getPrimaryKey(sourceTypeDefinition);
			if (primaryKey != null) {
				table.primaryKey(primaryKey);
			}
			else {
				table.primaryKey("id");
				mappingContext.getReporter().warn(
						"No primary key for table \"{0}\" found, assuming \"id\". (context: oid_col in FeatureType \"{1}\")",
						tableName, mappingContext.getFeatureTypeName());
			}

			if (sourceType.getFilter() != null) {
				try {
					AbstractGeotoolsFilter filter = (AbstractGeotoolsFilter) sourceType.getFilter();
					table.predicate(filter.getFilterTerm());
				} catch (ClassCastException e) {
					// ignore
				}
			}

			mappingContext.addCurrentMappingTable(tableName, table);

			return table;
		});
	}

	@Override
	public final FeatureTypeMapping handle(final Cell cell) {
		mappingContext.addNextFeatureTypeMapping(getFeatureTypeName(cell));

		final ListMultimap<String, ? extends Entity> sourceEntities = cell.getSource();
		if (sourceEntities == null || sourceEntities.size() == 0) {
			throw new IllegalStateException("No source type has been specified.");
		}
		if (XtraServerCompatibilityMode.hasFilters(cell.getSource())) {
			mappingContext.getReporter().warn(
					"Filters are not supported and are ignored during type transformation of Feature Type \"{0}\"",
					mappingContext.getFeatureTypeName());
		}

		final ListMultimap<String, ? extends Entity> targetEntities = cell.getTarget();
		if (targetEntities == null || targetEntities.size() == 0) {
			throw new IllegalStateException("No target type has been specified.");
		}
		final Entity targetType = targetEntities.values().iterator().next();
		final Collection<? extends Entity> sourceTypes = sourceEntities.values();
		doHandle(sourceTypes, targetType, cell);

		return null;
	}

	public abstract void doHandle(final Collection<? extends Entity> sourceTypes,
			final Entity targetType, final Cell typeCell);
}
