/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.groovy;

import eu.esdihumboldt.cst.functions.core.join.IndexJoinHandler;
import eu.esdihumboldt.hale.common.align.model.functions.JoinFunction;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on matching properties. The transformation
 * also applies a Groovy script that can be used to control the target instance
 * creation.
 * 
 * @author Simon Templer
 */
public class GroovyJoin extends GroovyRetype implements JoinFunction {

	/**
	 * the function ID
	 */
	public static final String ID = "eu.esdihumboldt.cst.functions.groovy.join";

	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		return new IndexJoinHandler();
	}

}
