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

import eu.esdihumboldt.cst.functions.core.join.IndexJoinHandler;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on matching properties.
 * 
 * @author Kai Schwierczek
 */
public class Join extends Retype implements JoinFunction {

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		return new IndexJoinHandler();
	}
}
