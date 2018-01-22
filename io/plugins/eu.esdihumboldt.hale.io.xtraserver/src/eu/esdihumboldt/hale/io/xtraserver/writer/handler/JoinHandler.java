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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
	public void doHandle(final Entity sourceType, final Entity targetType,
			final FeatureTypeMapping featureTypeMapping, final Cell typeCell) {

		createTableIfAbsent(featureTypeMapping, sourceType.getDefinition());

		for (final ParameterValue transParam : typeCell.getTransformationParameters()
				.get(PARAMETER_JOIN)) {

			final JoinParameter joinParameter = transParam.as(JoinParameter.class);
			final String validation = joinParameter.validate();
			if (validation != null) {
				throw new IllegalArgumentException("Join parameter invalid: " + validation);
			}

			final SortedSet<Condition> sortedConditions = transformSortedConditions(
					joinParameter.conditions, featureTypeMapping);

			final List<MappingJoin> joins = new ArrayList<MappingJoin>();
			MappingJoin join = MappingJoin.create();
			joins.add(join);
			Condition previousCondition = null;
			final Iterator<Condition> it = sortedConditions.iterator();
			while (it.hasNext()) {
				final Condition condition = it.next();
				if (previousCondition != null
						&& previousCondition.getSourceTable().equals(condition.getSourceTable())) {
					// new Join is required when source tables match
					join = MappingJoin.create();
					joins.add(join);
				}
				join.addCondition(condition);
				previousCondition = condition;
			}
			joins.forEach(j -> featureTypeMapping.addJoin(j));
		}
	}

	/**
	 * Transform and sort hale Join Condition Set into Join Conditions that are
	 * attached to multiple XtraServer Join objects
	 * 
	 * A hale Join with: [ city.id = city_river.cid, city.id = alternativename.cid,
	 * city_river.rid = river.id ]
	 * 
	 * will be sorted into: [ city.id = alternativename.cid, city.id =
	 * city_river.cid, city_river.rid = river.id]
	 * 
	 * @param conditions hale conditions
	 * @param featureTypeMapping FeatureTypeMapping to lookup tables or add missing
	 *            ones
	 * @return sorted joins
	 */
	private SortedSet<Condition> transformSortedConditions(final Set<JoinCondition> conditions,
			final FeatureTypeMapping featureTypeMapping) {
		final SortedSet<Condition> sortedConditions = new TreeSet<>(new Comparator<Condition>() {

			@Override
			public int compare(Condition c1, Condition c2) {

				final int cmpSrcTables = c1.getSourceTable().compareTo(c2.getSourceTable());
				if (cmpSrcTables == 0) {
					// source tables are equal, sort by target tables
					return c1.getTargetTable().compareTo(c2.getTargetTable());
				}
				// Sources tables are not equal
				final int cmpTrgtSrc = c2.getTargetTable().compareTo(c1.getSourceTable());
				if (cmpTrgtSrc == 0) {
					// Target table and source table match
					return 1;
				}
				return cmpSrcTables;
			}

		});
		for (final JoinCondition condition : conditions) {

			final TypeEntityDefinition joinType = AlignmentUtil
					.getTypeEntity(condition.joinProperty);
			final TypeEntityDefinition baseType = AlignmentUtil
					.getTypeEntity(condition.baseProperty);

			final MappingTable baseTable = createTableIfAbsent(featureTypeMapping, baseType);
			final String baseField = condition.joinProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();
			final MappingTable joinTable = createTableIfAbsent(featureTypeMapping, joinType);
			final String joinField = condition.baseProperty.getPropertyPath().iterator().next()
					.getChild().getDisplayName();

			final Condition xsJoinCondition = Condition.create(baseTable, baseField, joinTable,
					joinField);
			sortedConditions.add(xsJoinCondition);
		}
		return sortedConditions;
	}

}
