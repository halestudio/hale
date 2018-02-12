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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import de.interactive_instruments.xtraserver.config.util.api.FeatureTypeMapping;
import de.interactive_instruments.xtraserver.config.util.api.MappingJoin;
import de.interactive_instruments.xtraserver.config.util.api.MappingTable;
import de.interactive_instruments.xtraserver.config.util.api.MappingValue;
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

	JoinHandler(final TransformationContext mappingContext) {
		super(mappingContext);
	}

	/**
	 * @see eu.esdihumboldt.hale.io.xtraserver.writer.handler.TypeTransformationHandler#handle(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public String doHandle(final FeatureTypeMapping featureTypeMapping,
			final String primaryTableName) {

		// TODO: only get joined tables with source primaryTableName (should be realized
		// with api redesign)

		final Set<JoinCondition> joinConditions = new LinkedHashSet<>();

		List<String> allTableNames = ImmutableList.<String> builder()
				.addAll(featureTypeMapping.getJoinedTableNames())
				.addAll(featureTypeMapping.getReferenceTableNames()).build();

		for (String joinTableName : allTableNames) {
			MappingTable joinedTable = featureTypeMapping.getTable(joinTableName).get();

			if (joinedTable.hasJoinPath()) {

				for (MappingJoin mappingJoin : joinedTable.getJoinPaths()) {
					for (MappingJoin.Condition condition : mappingJoin.getJoinConditions()) {
						QName baseTableName = transformationContext
								.addTable(condition.getSourceTable());
						QName joinedTableName = transformationContext
								.addTable(condition.getTargetTable());

						if (baseTableName.getLocalPart().equals(joinedTable.getName())) {
							transformationContext.getReporter().warn("SELF JOIN: "
									+ condition.toString() + " [" + mappingJoin.toString() + "]");
						}

						PropertyEntityDefinition baseProperty = transformationContext
								.getEntityDefinition(baseTableName,
										new QName(condition.getSourceField()));
						PropertyEntityDefinition joinedProperty = transformationContext
								.getEntityDefinition(joinedTableName,
										new QName(condition.getTargetField()));

						joinConditions.add(new JoinCondition(baseProperty, joinedProperty));
					}
				}

			}
		}

		// TODO: api redesign
		filterSelfJoins(featureTypeMapping, primaryTableName);

		JoinParameter joinParameter = new JoinParameter(
				transformationContext.getCurrentSourceTypeEntityDefinitions(), joinConditions);

		final String validation = joinParameter.validate();
		if (validation != null) {
			throw new IllegalArgumentException("Join parameter invalid: " + validation);
		}

		transformationContext.getCurrentTypeParameters().put("join",
				new ParameterValue(Value.complex(joinParameter)));

		return JoinFunction.ID;
	}

	private void filterSelfJoins(FeatureTypeMapping featureTypeMapping, String primaryTableName) {
		// add self joins for root table
		MappingTable primaryTable = featureTypeMapping.getTable(primaryTableName).get();

		if (!primaryTable.getJoinPaths().isEmpty()) {

			for (MappingJoin mappingJoin : primaryTable.getJoinPaths()) {
				for (MappingJoin.Condition condition : mappingJoin.getJoinConditions()) {
					// QName baseTableName = getTableQName(condition.getSourceTable(),
					// sourceTypes);
					QName joinedTableName = transformationContext
							.findSourceType(condition.getTargetTable());

					if (!joinedTableName.getLocalPart().equals(primaryTable.getName())) {
						/*
						 * reporter.warn("Valid self join on root table of " + ftm.getName() + ": "
						 * + condition.toString() + "\ncomplete join: " + mappingJoin.toString() +
						 * "\njoin target: " + mappingJoin.getTarget() + "\ntable + target: " +
						 * joinedTable.getName() + "[" + joinedTable.getTarget() + "]");
						 * 
						 * joinedTables.add(getNamedEntity(joinedTableName, "types"));
						 * joinedSourceTypesQN.add(baseTableName);
						 * joinedSourceTypesQN.add(joinedTableName);
						 * 
						 * PropertyEntityDefinition baseProperty = getEntityDefinition(
						 * baseTableName, new QName(condition.getSourceField()));
						 * PropertyEntityDefinition joinedProperty = getEntityDefinition(
						 * joinedTableName, new QName(condition.getTargetField()));
						 * 
						 * conditions.add(new JoinCondition(baseProperty, joinedProperty));
						 */
					}
					else {
						if (!mappingJoin.isSuppressJoin()) {
							transformationContext.getReporter().warn("Self join on root table of "
									+ featureTypeMapping.getName() + ": " + condition.toString()
									+ "\ncomplete join: " + mappingJoin.toString()
									+ "\njoin target: " + mappingJoin.getTarget()
									+ "\ntable + target: " + primaryTable.getName() + "["
									+ primaryTable.getTarget() + "]");
						}

						Collection<MappingValue> ignoreValues = Collections2.filter(
								featureTypeMapping.getValues(), new Predicate<MappingValue>() {

									@Override
									public boolean apply(MappingValue value) {
										return value.getTarget().startsWith(mappingJoin.getTarget())
												&& value.getTable().equals(primaryTable.getName());
									}
								});
						if (!mappingJoin.isSuppressJoin()) {
							for (MappingValue ignoreValue : ignoreValues) {
								transformationContext.getReporter()
										.warn("Ignored joined value: " + ignoreValue.getTable()
												+ " " + ignoreValue.getValue() + " "
												+ ignoreValue.getTarget());
							}
						}
						featureTypeMapping.getValues().removeAll(ignoreValues);
					}
				}
			}

		}

	}

}
