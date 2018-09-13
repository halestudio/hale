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

import static eu.esdihumboldt.hale.common.align.model.functions.JoinFunction.PARAMETER_JOIN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.interactive_instruments.xtraserver.config.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.api.MappingJoin.Condition;
import de.interactive_instruments.xtraserver.config.api.MappingJoinBuilder;
import de.interactive_instruments.xtraserver.config.api.MappingTableBuilder;
import eu.esdihumboldt.hale.common.align.model.AlignmentUtil;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.ParameterValue;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.TypeEntityDefinition;

/**
 * Transforms the {@link JoinFunction} to a {@link FeatureTypeMapping}
 * 
 * @author Jon Herrmann ( herrmann aT interactive-instruments doT de )
 */
class JoinHandler extends AbstractTypeTransformationHandler {

	JoinHandler(final MappingContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public void doHandle(final Collection<? extends Entity> sourceTypes, final Entity targetType,
			final Cell typeCell) {

		for (final ParameterValue transParam : typeCell.getTransformationParameters()
				.get(PARAMETER_JOIN)) {

			final JoinParameter joinParameter = transParam.as(JoinParameter.class);
			final String validation = joinParameter.validate();
			if (validation != null) {
				throw new IllegalArgumentException("Join parameter invalid: " + validation);
			}

			final MappingTableBuilder baseTable = createTableIfAbsent(
					joinParameter.getTypes().iterator().next());

			final List<Condition> sortedConditions = transformSortedConditions(joinParameter,
					sourceTypes);

			// TODO: add nested joined tables to merged tables

			List<String> sourceTables = new ArrayList<>();

			sourceTables.add(baseTable.buildDraft().getName());

			while (!sourceTables.isEmpty()) {
				List<String> nextSourceTables = new ArrayList<>();
				for (String tableName : sourceTables) {
					final List<MappingJoin> joins = sortedConditions.stream()
							.filter(condition -> condition.getSourceTable().equals(tableName))
							.map(condition -> {
								final MappingJoinBuilder join = new MappingJoinBuilder();
								join.joinCondition(condition);
								join.targetPath("TODO");
								return join.build();
							}).collect(Collectors.toList());

					joins.forEach(joinPath -> {
						mappingContext.getTable(joinPath.getTargetTable())
								.ifPresent(targetTable -> targetTable.joinPath(joinPath));
					});

					nextSourceTables.addAll(joins.stream().map(st -> st.getTargetTable())
							.collect(Collectors.toList()));
				}
				sourceTables = nextSourceTables;
			}

			/*
			 * final List<MappingJoin> joins =
			 * sortedConditions.stream().filter(condition -> condition
			 * .getSourceTable().equals(baseTable.buildDraft().getName())).map(
			 * condition -> { final MappingJoinBuilder join = new
			 * MappingJoinBuilder(); join.joinCondition(condition);
			 * 
			 * Optional<MappingTableBuilder> t = mappingContext
			 * .getTable(condition.getTargetTable());
			 * 
			 * // join with connecting table // TODO cant have values yet, check
			 * name for now if (t.isPresent() &&
			 * t.get().buildDraft().getName().contains("__")) {
			 * Optional<Condition> matchingCondition = sortedConditions.stream()
			 * .filter(condition2 -> condition2.getSourceTable()
			 * .equals(condition.getTargetTable())) .findFirst();
			 * 
			 * if (matchingCondition.isPresent()) {
			 * join.joinCondition(matchingCondition.get()); } }
			 * 
			 * join.targetPath("TODO"); return join.build();
			 * }).collect(Collectors.toList());
			 * 
			 * joins.forEach(joinPath -> {
			 * mappingContext.getTable(joinPath.getTargetTable())
			 * .ifPresent(targetTable -> targetTable.joinPath(joinPath)); });
			 */
		}
	}

	/**
	 * Transform and sort hale Join Condition Set into Join Conditions that are
	 * attached to multiple XtraServer Join objects
	 * 
	 * @param joinParameter hale conditions
	 * @param sourceTypes source types from cell with primary keys
	 * @return sorted joins
	 */
	private List<Condition> transformSortedConditions(final JoinParameter joinParameter,
			final Collection<? extends Entity> sourceTypes) {

		return joinParameter.getConditions().stream().sorted(new Comparator<JoinCondition>() {

			@Override
			public int compare(JoinCondition o1, JoinCondition o2) {
				TypeEntityDefinition o1Type = AlignmentUtil.getTypeEntity(o1.joinProperty);
				TypeEntityDefinition o2Type = AlignmentUtil.getTypeEntity(o2.joinProperty);
				return joinParameter.getTypes().indexOf(o1Type)
						- joinParameter.getTypes().indexOf(o2Type);
			}
		}).map(condition -> {
			final TypeEntityDefinition baseType = AlignmentUtil
					.getTypeEntity(condition.baseProperty);

			final TypeEntityDefinition joinType = sourceTypes.stream()
					.filter(entity -> entity.getDefinition().getType().getName()
							.equals(condition.joinProperty.getType().getName()))
					.map(entity -> AlignmentUtil.getTypeEntity(entity.getDefinition())).findFirst()
					.orElse(AlignmentUtil.getTypeEntity(condition.joinProperty));

			final MappingTableBuilder baseTable = createTableIfAbsent(baseType);
			final String baseField = condition.baseProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();
			final MappingTableBuilder joinTable = createTableIfAbsent(joinType);
			final String joinField = condition.joinProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();

			final MappingJoin.Condition mappingJoinCondition = new MappingJoinBuilder.ConditionBuilder()
					.sourceTable(baseTable.buildDraft().getName()).sourceField(baseField)
					.targetTable(joinTable.buildDraft().getName()).targetField(joinField).build();

			return mappingJoinCondition;
		}).collect(Collectors.toList());
	}

}
