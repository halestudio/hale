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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.FeatureTypeMappingBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder.MappingTableDraft;
import de.interactive_instruments.xtraserver.config.api.MappingValue;
import de.interactive_instruments.xtraserver.config.api.VirtualTableBuilder;
import de.interactive_instruments.xtraserver.config.api.XtraServerMapping;
import de.interactive_instruments.xtraserver.config.api.XtraServerMappingBuilder;
import de.interactive_instruments.xtraserver.config.transformer.XtraServerMappingTransformer;
import eu.esdihumboldt.hale.common.align.model.Alignment;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.ChildContext;
import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.project.ProjectInfo;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.schema.model.PropertyDefinition;
import eu.esdihumboldt.hale.common.schema.model.Schema;
import eu.esdihumboldt.hale.common.schema.model.SchemaSpace;
import eu.esdihumboldt.hale.common.schema.model.constraint.property.Cardinality;

/**
 * The mapping context provides access to the {@link Alignment} and holds all
 * {@link FeatureTypeMapping}s.
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
public final class MappingContext {

	/**
	 * ADV Modellart
	 */
	public final static String PROPERTY_ADV_MODELLART = "ADV_MODELLART";

	/**
	 * INSPIRE namespace
	 */
	public final static String PROPERTY_INSPIRE_NAMESPACE = "INSPIRE_NAMESPACE";

	private final Alignment alignment;
	// private final ApplicationSchema applicationSchema;
	private final Map<String, Value> transformationProperties;
	private final static Pattern projectVarPattern = Pattern.compile("\\{\\{project:([^}]+)\\}\\}");

	private final Map<String, FeatureTypeMappingBuilder> featureTypeMappings = new LinkedHashMap<>();
	private final Map<String, VirtualTableBuilder> virtualTables = new LinkedHashMap<>();
	private FeatureTypeMappingBuilder currentFeatureTypeMapping;
	private String currentFeatureTypeMappingName;
	private final Map<String, MappingTableBuilder> currentMappingTables = new LinkedHashMap<>();
	private final Map<String, VirtualTableBuilder> currentVirtualTables = new LinkedHashMap<>();
	private final Set<String> missingAssociationTargets = new TreeSet<String>();
	private final URI applicationSchemaUri;
	private final ProjectInfo projectInfo;
	private final URI projectLocation;
	private final IOReporter reporter;

	/**
	 * Constructor Only the first schema is used
	 * 
	 * @param alignment the Alignment with all cells
	 * @param schemaspace the target schema
	 * @param transformationProperties Properties used in transformations
	 * @param projectInfo project info
	 * @param projectLocation project file
	 * @param reporter reporter
	 */
	public MappingContext(final Alignment alignment, final SchemaSpace schemaspace,
			final Map<String, Value> transformationProperties, final ProjectInfo projectInfo,
			final URI projectLocation, final IOReporter reporter) {
		this.alignment = Objects.requireNonNull(alignment);
		this.transformationProperties = Objects.requireNonNull(transformationProperties);

		final Iterator<? extends Schema> it = Objects
				.requireNonNull(schemaspace, "Schemaspace not provided").getSchemas().iterator();
		if (!it.hasNext()) {
			throw new IllegalArgumentException("Schemaspace does not contain a schema");
		}
		final Schema schema = it.next();
		this.applicationSchemaUri = schema.getLocation();
		this.projectInfo = projectInfo;
		this.projectLocation = projectLocation;
		this.reporter = reporter;
	}

	/**
	 * Add a new FeatureTypeMapping to the mapping context
	 * 
	 * @param featureTypeName feature type name
	 * @return the same FeatureTypeMapping for chaining method calls
	 */
	FeatureTypeMappingBuilder addNextFeatureTypeMapping(final QName featureTypeName) {
		buildAndClearCurrentTables();

		final String key = Objects.requireNonNull(featureTypeName, "Feature Type name is null")
				.toString();
		currentFeatureTypeMapping = featureTypeMappings.get(key);
		if (currentFeatureTypeMapping == null) {
			currentFeatureTypeMapping = new FeatureTypeMappingBuilder()
					.qualifiedName(featureTypeName);
			featureTypeMappings.put(key, currentFeatureTypeMapping);
		}

		this.currentFeatureTypeMappingName = featureTypeName.getLocalPart();

		return currentFeatureTypeMapping;
	}

	void addCurrentMappingTable(final String tableName, final MappingTableBuilder mappingTable) {
		this.currentMappingTables.put(tableName, mappingTable);
	}

	Collection<MappingTableBuilder> getCurrentMappingTables() {
		return this.currentMappingTables.values();
	}

	void buildAndClearCurrentTables() {
		if (this.currentFeatureTypeMapping == null) {
			return;
		}

		Lists.reverse(Lists.newArrayList(this.currentMappingTables.values())).stream()
				.filter(isJoinTable()).map(MappingTableBuilder::build).forEach(table -> {
					if (this.currentMappingTables
							.containsKey(table.getJoinPaths().iterator().next().getSourceTable())) {
						this.currentMappingTables
								.get(table.getJoinPaths().iterator().next().getSourceTable())
								.joiningTable(table);
					}
				});

		/*
		 * this.currentMappingTables.values().stream().filter(
		 * isInvalidJoinTableWithoutTarget())
		 * .map(MappingTableBuilder::buildDraft).forEach(table -> { String
		 * primaryName =
		 * table.getJoinPaths().iterator().next().getSourceTable(); if
		 * (this.currentMappingTables.containsKey(primaryName)) {
		 * MappingTableBuilder primaryTable =
		 * currentMappingTables.get(primaryName); String virtualName =
		 * primaryTable.buildDraft().getName(); boolean virtualExists =
		 * currentVirtualTables.containsKey(primaryName);
		 * 
		 * VirtualTableBuilder virtualTable =
		 * currentVirtualTables.get(primaryName); if (!virtualExists) {
		 * virtualTable = new VirtualTableBuilder();
		 * virtualTable.originalTable(primaryTable.buildDraft());
		 * this.currentVirtualTables.put(primaryName, virtualTable); }
		 * 
		 * if (!virtualExists) { virtualName = "vrt_" + virtualName; }
		 * 
		 * if (!virtualName.contains(table.getName())) { virtualName += "_" +
		 * table.getName(); this.currentMappingTables.get(primaryName) .name("$"
		 * + virtualName + "$"); }
		 * 
		 * virtualTable.name(virtualName); virtualTable.originalTable(table);
		 * 
		 * primaryTable
		 * .values(table.getAllValuesStream().collect(Collectors.toList())); }
		 * });
		 */

		final Optional<MappingTable> primaryTable = this.currentMappingTables.values().stream()
				.map(MappingTableBuilder::build).filter(table -> table.isPrimary()).findFirst();

		if (primaryTable.isPresent()) {
			this.currentFeatureTypeMapping.primaryTable(primaryTable.get());
		}

		this.currentVirtualTables.values()
				.forEach(vtable -> this.virtualTables.put(vtable.build().getName(), vtable));

		this.currentMappingTables.clear();
		this.currentVirtualTables.clear();
	}

	private Predicate<MappingTableBuilder> isValidJoinTable() {
		return tableBuilder -> tableBuilder.buildDraft().isJoined()
				&& tableBuilder.buildDraft().getAllValuesStream().findFirst().isPresent()
				&& tableBuilder.buildDraft().getAllValuesStream()
						.allMatch(value -> value.getQualifiedTargetPath().get(0)
								.equals(tableBuilder.buildDraft().getQualifiedTargetPath().get(0)));
	}

	private Predicate<MappingTableBuilder> isInvalidJoinTableWithoutTarget() {
		return tableBuilder -> !tableBuilder.buildDraft().getJoinPaths().isEmpty()
				&& tableBuilder.buildDraft().getAllValuesStream().findFirst().isPresent()
				&& (tableBuilder.buildDraft().getQualifiedTargetPath().isEmpty() || tableBuilder
						.buildDraft().getAllValuesStream()
						.anyMatch(value -> !value.getQualifiedTargetPath().get(0).equals(
								tableBuilder.buildDraft().getQualifiedTargetPath().get(0))));
	}

	private Predicate<MappingTableBuilder> isJoinTable() {
		return tableBuilder -> (tableBuilder.buildDraft().isJoined()
				&& tableBuilder.buildDraft().getAllValuesStream().findFirst().isPresent()
				&& tableBuilder.buildDraft().getAllValuesStream()
						.allMatch(value -> value.getQualifiedTargetPath().get(0)
								.equals(tableBuilder.buildDraft().getQualifiedTargetPath().get(0))))
				|| (!tableBuilder.buildDraft().getJoinPaths().isEmpty()
						&& tableBuilder.buildDraft().getAllValuesStream().findFirst().isPresent()
						&& (tableBuilder.buildDraft().getQualifiedTargetPath().isEmpty()
								|| tableBuilder.buildDraft().getAllValuesStream()
										.anyMatch(value -> !value.getQualifiedTargetPath().get(0)
												.equals(tableBuilder.buildDraft()
														.getQualifiedTargetPath().get(0)))));
	}

	/**
	 * Returns the name of the currently processed Feature Type Mapping
	 * 
	 * @return Feature Type Mapping name
	 */
	public String getFeatureTypeName() {
		return currentFeatureTypeMappingName;
	}

	/**
	 * Return all property paths for which no association target could be found
	 * in the schema.
	 * 
	 * @return list of properties with missing association targets
	 */
	public Set<String> getMissingAssociationTargets() {
		return this.missingAssociationTargets;
	}

	void addMissingAssociationTarget(final String associationTarget) {
		this.missingAssociationTargets.add(associationTarget);
	}

	Value getTransformationProperty(final String name) {
		final Value val = this.transformationProperties.get(name);
		if (val != null) {
			return val;
		}
		return Value.NULL;
	}

	/**
	 * Retrieve table from current FeatureTypeMapping
	 * 
	 * @param tableName Mapping Table name
	 * @return MappingTable
	 */
	Optional<MappingTableBuilder> getTable(String tableName) {
		return Optional.ofNullable(currentMappingTables.get(tableName));
	}

	void addValueMappingToTable(final Property target, final MappingValue value,
			final String tableName) {

		final MappingTableBuilder tableBuilder = getTable(tableName).orElseThrow(
				() -> new IllegalArgumentException("Table " + tableName + " not found"));
		final MappingTableDraft tableDraft = tableBuilder.buildDraft();

		// TODO if joinPaths and no target path and multiple, set target path ->
		// joined table
		// if joinPaths and target path and not multiple, clear target path ->
		// merged table
		// ignore merged tables in JaxbWriter, transform to virtual tables in
		// transformer
		if (!tableDraft.getJoinPaths().isEmpty()
				&& target.getDefinition().getPropertyPath() != null) {
			// Target is set in value mapping, check if the property is multiple
			// and the
			// target must be added to the table
			List<QName> targetPath = new ArrayList<>();
			boolean multiple = false;
			for (final Iterator<ChildContext> it = target.getDefinition().getPropertyPath()
					.iterator(); it.hasNext();) {
				final ChildContext segment = it.next();
				final PropertyDefinition property = segment.getChild().asProperty();
				targetPath.add(segment.getChild().getName());
				if (property != null) {
					final Cardinality cardinality = property.getConstraint(Cardinality.class);
					if (cardinality.mayOccurMultipleTimes()) {
						if (tableDraft.getQualifiedTargetPath().isEmpty()
								&& tableDraft.getValues().isEmpty()) {
							tableBuilder.qualifiedTargetPath(targetPath);
						}
						multiple = true;
						break;
					}
				}
			}
			if (!multiple && !tableDraft.getQualifiedTargetPath().isEmpty()) {
				tableBuilder.qualifiedTargetPath(ImmutableList.of());
			}
		}

		tableBuilder.value(value);
	}

	IOReporter getReporter() {
		return reporter;
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
		buildAndClearCurrentTables();

		final XtraServerMappingBuilder xtraServerMappingBuilder = new XtraServerMappingBuilder();

		xtraServerMappingBuilder.description("\n  Source:\n    - hale "
				+ projectInfo.getHaleVersion().toString() + "\n    - "
				+ (projectLocation != null ? projectLocation.toString() : projectInfo.getName())
				+ "\n");

		featureTypeMappings.values().stream().map(FeatureTypeMappingBuilder::build)
				.forEach(xtraServerMappingBuilder::featureTypeMapping);

		virtualTables.values().stream().map(VirtualTableBuilder::build)
				.forEach(xtraServerMappingBuilder::virtualTable);

		XtraServerMapping fannedOutmapping = XtraServerMappingTransformer
				.forMapping(xtraServerMappingBuilder.build())
				.applySchemaInfo(this.applicationSchemaUri).fanOutInheritance()
				.ensureRelationNavigability().fixMultiplicity().virtualTables().transform();

		/*
		 * fannedOutmapping =
		 * XtraServerMappingTransformer.forMapping(fannedOutmapping)
		 * .applySchemaInfo(this.applicationSchemaUri).
		 * ensureRelationNavigability() .transform();
		 */
		return fannedOutmapping;
	}

	/**
	 * Replace project variables in a string
	 * 
	 * @param str input string
	 * @return string with replaced project variables, unresolved variables are
	 *         replaced with 'PROJECT_VARIABLE_<VARIABLE_NAME>_NOT_SET'
	 */
	public String resolveProjectVars(final String str) {
		final Matcher m = projectVarPattern.matcher(str);
		String repStr = str;
		while (m.find()) {
			final String varName = m.group(1);
			final Value val = transformationProperties.get(varName);
			final String replacement;
			if (val != null && !val.isEmpty()) {
				replacement = val.as(String.class);
			}
			else {
				replacement = "PROJECT_VARIABLE_" + varName + "_NOT_SET";
			}
			repStr = repStr.replaceAll("\\{\\{project:" + varName + "\\}\\}",
					Matcher.quoteReplacement(replacement));
		}
		return repStr;
	}

}
