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

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
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
	public void doHandle(final Entity sourceType, final Entity targetType,
			final FeatureTypeMapping mapping, final Cell typeCell) {

		mapping.addTable(createTableIfAbsent(sourceType.getDefinition()));

		for (final ParameterValue transParam : typeCell.getTransformationParameters()
				.get(PARAMETER_JOIN)) {

			final JoinParameter joinParameter = transParam.as(JoinParameter.class);
			final String validation = joinParameter.validate();
			if (validation != null) {
				throw new IllegalArgumentException("Join parameter invalid: " + validation);
			}

			final MappingJoin mappingJoin = MappingJoin.create();
			for (final JoinCondition condition : joinParameter.conditions) {

				final TypeEntityDefinition joinType = AlignmentUtil
						.getTypeEntity(condition.joinProperty);
				final TypeEntityDefinition baseType = AlignmentUtil
						.getTypeEntity(condition.baseProperty);

				final MappingJoin.Condition xsJoinCondition = MappingJoin.Condition.create();

				xsJoinCondition.setTargetField(condition.joinProperty.getPropertyPath().iterator()
						.next().getChild().getDisplayName());
				final MappingTable joinTable = createTableIfAbsent(joinType);
				mapping.addTable(joinTable);
				xsJoinCondition.setTargetTable(joinTable);

				xsJoinCondition.setSourceField(condition.baseProperty.getPropertyPath().iterator()
						.next().getChild().getDisplayName());
				final MappingTable baseTable = createTableIfAbsent(baseType);
				mapping.addTable(baseTable);
				xsJoinCondition.setSourceTable(baseTable);
				mappingJoin.getJoinConditions().add(xsJoinCondition);
			}

			// TODO REMOVE
			mappingJoin.setTarget("ci:passingRiver");
			mapping.addJoin(mappingJoin);
		}
	}

}
