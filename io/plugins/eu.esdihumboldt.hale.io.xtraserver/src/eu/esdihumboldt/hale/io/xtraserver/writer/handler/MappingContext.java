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
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import de.interactive_instruments.xtraserver.config.util.ApplicationSchema;
import de.interactive_instruments.xtraserver.config.util.Namespaces;
import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
import de.interactive_instruments.xtraserver.config.util.api.XtraServerMapping;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * The mapping context provides access to the {@link Alignment}, the
 * {@link Namespaces} and holds all {@link FeatureTypeMapping}s.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class MappingContext {

	/**
	 * ADV Modellart
	 */
	public final static String ADV_MODELLART = "ADV_MODELLART";

	/**
	 * INSPIRE namespace
	 */
	public final static String INSPIRE_NAMESPACE = "INSPIRE_NAMESPACE";

	/**
	 * Project property types that are used by this plugin
	 */
	public final static String[] SUPPORTED_PROJECT_PROPERTY_NAMES = new String[] { ADV_MODELLART,
			INSPIRE_NAMESPACE };

	private final Alignment alignment;
	private final ApplicationSchema applicationSchema;
	private final Deque<FeatureTypeMapping> featureTypeMappings = new LinkedList<>();
	private final Map<String, Value> transformationProperties;

	/**
	 * Constructor Only the first schema is used
	 * 
	 * @param alignment the Alignment with all cells
	 * @param schemaspace the target schema
	 * @param transformationProperties Properties used in transformations
	 * @throws IOException if the schema cannot be read
	 */
	public MappingContext(final Alignment alignment, final SchemaSpace schemaspace,
			final Map<String, Value> transformationProperties) throws IOException {
		this.alignment = Objects.requireNonNull(alignment);
		this.transformationProperties = Objects.requireNonNull(transformationProperties);

		final Iterator<? extends Schema> it = Objects
				.requireNonNull(schemaspace, "Schemaspace not provided").getSchemas().iterator();
		if (!it.hasNext()) {
			throw new IllegalArgumentException("Schemaspace does not contain a schema");
		}
		final Schema schema = it.next();
		this.applicationSchema = new ApplicationSchema(schema.getLocation());
	}

	/**
	 * Add a new FeatureTypeMapping to the mapping context
	 * 
	 * @param featureTypeMapping new FeatureTypeMapping
	 * @return the same FeatureTypeMapping for chaining method calls
	 */
	FeatureTypeMapping addNextFeatureTypeMapping(final FeatureTypeMapping featureTypeMapping) {
		featureTypeMappings.push(Objects.requireNonNull(featureTypeMapping));
		return featureTypeMapping;
	}

	/**
	 * Returns the name of the currently processed Feature Type Mapping
	 * 
	 * @return Feature Type Mapping name
	 */
	String getFeatureTypeName() {
		return featureTypeMappings.peek().getName();
	}

	Value getTransformationProperty(final String name) {
		return this.transformationProperties.get(name);
	}

	/**
	 * Returns the namespaces from the target schema
	 * 
	 * @return namespaces from the target schema
	 */
	Namespaces getNamespaces() {
		return this.applicationSchema.getNamespaces();
	}

	/**
	 * Retrieve table from current FeatureTypeMapping
	 * 
	 * @param tableName Mapping Table name
	 * @return MappingTable
	 */
	MappingTable getTable(String tableName) {
		return featureTypeMappings.peek().getTable(tableName);
	}

	void addValueMappingToTable(final Property target, final MappingValue value,
			final String tableName) {
		final MappingTable table = Objects.requireNonNull(getTable(tableName),
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
	Collection<? extends Cell> getPropertyCells(final Cell typeCell) {
		return this.alignment.getPropertyCells(typeCell);
	}

	/**
	 * Return the XtraServerMapping containing all FeatureTypeMappings that were
	 * propagated
	 * 
	 * @return XtraServerMapping containing all FeatureTypeMappings
	 */
	public XtraServerMapping getMapping() {
		final XtraServerMapping xtraServerMapping = XtraServerMapping
				.create(this.applicationSchema);
		this.featureTypeMappings.forEach(xtraServerMapping::addFeatureTypeMapping);
		return xtraServerMapping;
	}
}
