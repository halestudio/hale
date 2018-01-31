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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin.Condition;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
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
	public void doHandle(final Collection<Entity> sourceTypes, final Entity targetType,
			final FeatureTypeMapping featureTypeMapping, final Cell typeCell) {

		final MappingTable baseTable = createTableIfAbsent(featureTypeMapping,
				sourceTypes.iterator().next().getDefinition());

		for (final ParameterValue transParam : typeCell.getTransformationParameters()
				.get(PARAMETER_JOIN)) {

			final JoinParameter joinParameter = transParam.as(JoinParameter.class);
			final String validation = joinParameter.validate();
			if (validation != null) {
				throw new IllegalArgumentException("Join parameter invalid: " + validation);
			}

			final List<Condition> sortedConditions = transformSortedConditions(joinParameter,
					featureTypeMapping, sourceTypes);

			final List<MappingJoin> joins = sortedConditions.stream()
					.filter(condition -> condition.getSourceTable().equals(baseTable.getName()))
					.map(condition -> {
						MappingJoin join = MappingJoin.create();
						join.addCondition(condition);

						Optional<Condition> matchingCondition = sortedConditions.stream()
								.filter(condition2 -> condition2.getSourceTable()
										.equals(condition.getTargetTable()))
								.findFirst();

						if (matchingCondition.isPresent()) {
							join.addCondition(matchingCondition.get());
						}

						return join;
					}).collect(Collectors.toList());

			joins.forEach(j -> featureTypeMapping.addJoin(j));
		}
	}

	/**
	 * Transform and sort hale Join Condition Set into Join Conditions that are
	 * attached to multiple XtraServer Join objects
	 * 
	 * @param joinParameter hale conditions
	 * @param featureTypeMapping FeatureTypeMapping to lookup tables or add missing
	 *            ones
	 * @return sorted joins
	 */
	private List<Condition> transformSortedConditions(final JoinParameter joinParameter,
			final FeatureTypeMapping featureTypeMapping, final Collection<Entity> sourceTypes) {

		return joinParameter.conditions.stream().sorted(new Comparator<JoinCondition>() {

			@Override
			public int compare(JoinCondition o1, JoinCondition o2) {
				TypeEntityDefinition o1Type = AlignmentUtil.getTypeEntity(o1.joinProperty);
				TypeEntityDefinition o2Type = AlignmentUtil.getTypeEntity(o2.joinProperty);
				return joinParameter.types.indexOf(o1Type) - joinParameter.types.indexOf(o2Type);
			}
		}).map(condition -> {
			final TypeEntityDefinition baseType = AlignmentUtil
					.getTypeEntity(condition.baseProperty);

			final TypeEntityDefinition joinType = sourceTypes.stream()
					.filter(entity -> entity.getDefinition().getType().getName()
							.equals(condition.joinProperty.getType().getName()))
					.map(entity -> AlignmentUtil.getTypeEntity(entity.getDefinition())).findFirst()
					.orElse(AlignmentUtil.getTypeEntity(condition.joinProperty));

			final MappingTable baseTable = createTableIfAbsent(featureTypeMapping, baseType, true);
			final String baseField = condition.baseProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();
			final MappingTable joinTable = createTableIfAbsent(featureTypeMapping, joinType, true);
			final String joinField = condition.joinProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();

			return Condition.create(baseTable, baseField, joinTable, joinField);
		}).collect(Collectors.toList());
	}

}
