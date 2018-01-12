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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import eu.esdihumboldt.cst.functions.core.merge.IndexMergeHandler;
import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.functions.MergeFunction;
import eu.esdihumboldt.hale.common.align.model.functions.merge.MergeUtil;
import eu.esdihumboldt.hale.common.align.model.impl.PropertyEntityDefinition;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution;

/**
 * Type transformation that merges multiple instances of the same source type
 * into one target instance, based on matching properties.
 * 
 * @author Simon Templer
 */
public class Merge extends Retype implements MergeFunction, InstanceIndexContribution {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		return new IndexMergeHandler();
	}

	/**
	 * @see eu.esdihumboldt.hale.common.instance.index.InstanceIndexContribution#getIndexContribution(eu.esdihumboldt.hale.common.align.model.Cell)
	 */
	@Override
	public Collection<List<PropertyEntityDefinition>> getIndexContribution(Cell cell) {
		return getMergeContribution(cell);
	}

	/**
	 * Calculates the instance index contribution for the given cell
	 * 
	 * @param cell Cell
	 * @return The properties to index
	 */
	public static Collection<List<PropertyEntityDefinition>> getMergeContribution(Cell cell) {
		return Collections.singletonList(MergeUtil.getKeyPropertyDefinitions(cell));
	}
}
