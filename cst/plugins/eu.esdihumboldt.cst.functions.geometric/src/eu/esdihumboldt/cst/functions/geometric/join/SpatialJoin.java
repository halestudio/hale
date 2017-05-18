/*
 * Copyright (c) 2017 wetransform GmbH
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
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.cst.functions.geometric.join;

import eu.esdihumboldt.cst.functions.core.Retype;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on spatial relations between geometry
 * properties.
 * 
 * @author Florian Esser
 */
public class SpatialJoin extends Retype implements SpatialJoinFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		return new SpatialJoinHandler();
	}
}
