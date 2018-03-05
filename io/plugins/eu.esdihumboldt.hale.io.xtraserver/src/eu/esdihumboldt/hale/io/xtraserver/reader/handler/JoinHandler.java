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

package eu.esdihumboldt.hale.io.xtraserver.reader.handler;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingTable;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.core.io.Value;

/**
 * Transforms a {@link FeatureTypeMapping} to a {@link JoinFunction}
 * 
 * @author zahnen
 */
class JoinHandler extends AbstractTypeTransformationHandler {

	private final Set<String> seenTables;

	JoinHandler(final TransformationContext mappingContext) {
		super(mappingContext);
		this.seenTables = new HashSet<>();
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String doHandle(final FeatureTypeMapping featureTypeMapping,
			final String primaryTableName) {

		final Optional<MappingTable> primaryTable = featureTypeMapping.getTable(primaryTableName);

		if (!primaryTable.isPresent()) {
			throw new IllegalArgumentException("Primary table not found: " + primaryTableName);
		}

		seenTables.add(primaryTableName);

		final Set<JoinCondition> joinConditions = primaryTable.get().getAllJoiningTablesStream()
				.filter(isNotJoinedYet()).filter(hasValues())
				.flatMap(joinedTable -> joinedTable.getJoinPaths().stream())
				.flatMap(joinPath -> joinPath.getJoinConditions().stream())
				.map(toHaleJoinCondition()).collect(Collectors.toSet());

		final JoinParameter joinParameter = new JoinParameter(
				transformationContext.getCurrentSourceTypeEntityDefinitions(), joinConditions);

		final String validation = joinParameter.validate();
		if (validation != null) {
			throw new IllegalArgumentException("Join parameter invalid: " + validation);
		}

		transformationContext.getCurrentTypeParameters().put("join",
				new ParameterValue(Value.complex(joinParameter)));

		return JoinFunction.ID;
	}

	private Function<MappingJoin.Condition, JoinCondition> toHaleJoinCondition() {
		return condition -> {

			final QName baseTableName = transformationContext.addTable(condition.getSourceTable());
			final QName joinedTableName = transformationContext
					.addTable(condition.getTargetTable());

			final PropertyEntityDefinition baseProperty = transformationContext
					.getEntityDefinition(baseTableName, new QName(condition.getSourceField()));
			final PropertyEntityDefinition joinedProperty = transformationContext
					.getEntityDefinition(joinedTableName, new QName(condition.getTargetField()));

			return new JoinCondition(baseProperty, joinedProperty);
		};
	}

	private Predicate<MappingTable> isNotJoinedYet() {
		return joinedTable -> {
			if (seenTables.contains(joinedTable.getName())) {
				transformationContext.getReporter()
						.warn("Table is already joined for this feature type, skipping: "
								+ joinedTable.getName() + " ["
								+ joinedTable.getJoinPaths().toString() + "]");

				return false;
			}

			seenTables.add(joinedTable.getName());
			return true;
		};
	}

	private Predicate<MappingTable> hasValues() {
		return joinedTable -> !joinedTable.getValues().isEmpty();
	}

}
