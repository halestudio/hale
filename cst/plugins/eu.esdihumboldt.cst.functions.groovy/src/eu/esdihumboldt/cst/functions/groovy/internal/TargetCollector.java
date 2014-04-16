/*
 * Copyright (c) 2014 Data Harmonisation Panel
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

package eu.esdihumboldt.cst.functions.groovy.internal;

import java.util.ArrayList;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import groovy.lang.Closure;

/**
 * Target binding class for {@link GroovyTransformation}.
 * 
 * @author Kai Schwierczek
 */
public class TargetCollector extends ArrayList<Closure<?>> {

	private static final long serialVersionUID = 1L;

	/**
	 * Call method for easy access from Groovy.
	 * 
	 * @param targetClosure the target closure to add to the list
	 */
	public void call(Closure<?> targetClosure) {
		add(targetClosure);
	}

	/**
	 * Transforms the closures added to this collector to a {@link MultiValue}
	 * using the supplied builder.
	 * 
	 * @param builder the instance builder for creating target instances
	 * @param type the type of the instance to create
	 * @return a result value for all closures added to this collector
	 */
	public MultiValue toMultiValue(InstanceBuilder builder, TypeDefinition type) {
		MultiValue value = new MultiValue(size());

		for (Closure<?> closure : this) {
			value.add(builder.createInstance(type, closure));
		}

		return value;
	}
}
