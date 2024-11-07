/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.cst.functions.core.join.IndexJoinHandler;
import eu.esdihumboldt.cst.functions.core.join.JoinHandler;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter;
import eu.esdihumboldt.hale.common.align.model.functions.join.JoinParameter.JoinCondition;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on matching properties.
 *
 * @author Kai Schwierczek
 */
public class Join extends Retype implements JoinFunction, InstanceIndexContribution {

	/**
	 * The log
	 */
	private static final ALogger LOG = ALoggerFactory.getLogger(Join.class);

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		boolean useIndexJoinHandler = false;

		String setting = System.getProperty("hale.functions.join.use_index_join_handler");

		if (setting == null) {
			setting = System.getenv("HALE_FUNCTIONS_USE_INDEX_JOIN_HANDLER");
		}

		if (setting != null) {
			try {
				useIndexJoinHandler = Boolean.valueOf(setting);
			} catch (Throwable e) {
				LOG.error("Error applying index join handler setting: " + setting, e);
			}
		}

		return useIndexJoinHandler ? new IndexJoinHandler() : new JoinHandler();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution#getIndexContribution(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Collection<List<PropertyEntityDefinition>> getIndexContribution(Cell cell) {
		return getJoinContribution(cell);
	}

	/**
	 * Calculates the instance index contribution for the given cell
	 * 
	 * @param cell Cell
	 * @return The properties to index
	 */
	public static Collection<List<PropertyEntityDefinition>> getJoinContribution(Cell cell) {
		List<List<PropertyEntityDefinition>> result = new ArrayList<>();

		JoinParameter joinParameter = cell.getTransformationParameters()
				.get(JoinFunction.PARAMETER_JOIN).get(0).as(JoinParameter.class);
		for (JoinCondition cond : joinParameter.getConditions()) {
			// Index all base and join properties individually
			result.add(Collections.singletonList(cond.baseProperty));
			result.add(Collections.singletonList(cond.joinProperty));
		}

		return result;
	}
}
