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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * The mapping context provides access to the {@link Alignment}, the
 * {@link Namespaces} and holds all added {@link MappingTable}s.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public class MappingContext {

	private final Alignment alignment;
	private final ApplicationSchema applicationSchema;
	private static final ALogger logger = ALoggerFactory.getLogger(MappingContext.class);

	private final Map<String, MappingTable> mappingTables = new LinkedHashMap<>();

	/**
	 * Constructor Only the first schema is used
	 * 
	 * @param alignment the Alignment with all cells
	 * @param schemaspace the target schema
	 * @throws IOException if the schema cannot be read
	 */
	public MappingContext(final Alignment alignment, final SchemaSpace schemaspace)
			throws IOException {
		this.alignment = Objects.requireNonNull(alignment);

		final Iterator<? extends Schema> it = Objects
				.requireNonNull(schemaspace, "Schemaspace not provided").getSchemas().iterator();
		if (!it.hasNext()) {
			throw new IllegalArgumentException("Schemaspace does not contain a schema");
		}
		final Schema schema = it.next();
		final URI uri = schema.getLocation();
		this.applicationSchema = new ApplicationSchema(uri.toURL().openStream());
	}

	/**
	 * Returns the namespaces from the target schema
	 * 
	 * @return namespaces from the target schema
	 */
	public Namespaces getNamespaces() {
		return this.applicationSchema.getNamespaces();
	}

	void addTable(final MappingTable table) {
		if (mappingTables.putIfAbsent(Objects.requireNonNull(table.getName(), "Table name is null"),
				table) != null) {
			throw new IllegalArgumentException(
					"Table " + table.getName() + " already added to Mapping Context.");
		}
		logger.debug("Table added: {} ", table.getName());
	}

	MappingTable getTable(String tableName) {
		return mappingTables.get(tableName);
	}

	void addValueMappingToTable(final Property target, final MappingValue value,
			final String tableName) {
		final MappingTable table = Objects.requireNonNull(mappingTables.get(tableName),
				"Table " + tableName + " not found");
		if (!table.hasTarget() && value.getTarget() != null
				&& target.getDefinition().getPropertyPath() != null) {
			// Target is set in value mapping, check if the property is multiple and the
			// target must be added to the table
			for (final Iterator<ChildContext> it = target.getDefinition().getPropertyPath()
					.iterator(); it.hasNext();) {
				final ChildContext segment = it.next();
				final PropertyDefinition property = segment.getChild().asProperty();
				if (property != null) {
					final Cardinality cardinality = property.getConstraint(Cardinality.class);
					if (cardinality.mayOccurMultipleTimes()) {
						table.setTarget(
								this.getNamespaces().getPrefixedName(segment.getChild().getName()));
						break;
					}
				}
			}
		}
		value.setTable(table);
	}

	/**
	 * Return the property cells for a type cell
	 * 
	 * @param typeCell the type cell
	 * @return the property cells associated with type cell
	 */
	public Collection<? extends Cell> getPropertyCells(final Cell typeCell) {
		return this.alignment.getPropertyCells(typeCell);
	}

	/**
	 * Creates a new, empty XtraServerMapping
	 * 
	 * @return new, empty XtraServerMapping
	 */
	public XtraServerMapping createMapping() {
		return XtraServerMapping.create(this.applicationSchema);
	}
}
