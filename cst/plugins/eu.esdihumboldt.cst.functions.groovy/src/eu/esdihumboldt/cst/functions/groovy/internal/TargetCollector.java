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

import com.vividsolutions.jts.geom.Geometry;

import eu.esdihumboldt.cst.MultiValue;
import eu.esdihumboldt.cst.functions.groovy.GroovyTransformation;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationException;
import eu.esdihumboldt.hale.common.core.report.SimpleLog;
import eu.esdihumboldt.hale.common.instance.groovy.InstanceBuilder;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;
import eu.esdihumboldt.hale.common.schema.geometry.GeometryProperty;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.AugmentedValueFlag;
import eu.esdihumboldt.hale.common.schema.model.constraint.type.HasValueFlag;
import groovy.lang.Closure;

/**
 * Target binding class for {@link GroovyTransformation}.
 * 
 * @author Kai Schwierczek
 */
public class TargetCollector {

	private class TargetData {

		private final Object value;
		private final Instance instance;

		private TargetData(Object value, Closure<?> closure) {
			this.value = value;

			if (closure != null) {
				// the closure must be evaluated in the moment of the call
				// otherwise the bindings may have changed
				instance = builder.createInstance(typeDef, closure);
			}
			else {
				instance = null;
			}
		}
	}

	private final ArrayList<TargetData> targetData = new ArrayList<>();
	private boolean containsValues = false;
	private boolean containsGeometries = false;
	private boolean containsClosures = false;
	private final InstanceBuilder builder;
	private final TypeDefinition typeDef;

	/**
	 * Create a new target collector.
	 * 
	 * @param builder the builder used to create target instances
	 * @param typeDef the target type definition
	 */
	public TargetCollector(InstanceBuilder builder, TypeDefinition typeDef) {
		this.builder = builder;
		this.typeDef = typeDef;
	}

	/**
	 * Call method for easy access from Groovy.
	 * 
	 * @param targetClosure the target closure
	 */
	public void call(Closure<?> targetClosure) {
		targetData.add(new TargetData(null, targetClosure));

		containsClosures = true;
	}

	/**
	 * Call method for easy access from Groovy.
	 * 
	 * @param value the property value
	 */
	public void call(Object value) {
		targetData.add(new TargetData(value, null));
		containsValues = true;
		if (value instanceof Geometry || value instanceof GeometryProperty) {
			containsGeometries = true;
		}
	}

	/**
	 * Call method for easy access from Groovy.
	 * 
	 * @param value the property value
	 * @param targetClosure the target closure
	 */
	public void call(Object value, Closure<?> targetClosure) {
		targetData.add(new TargetData(value, targetClosure));
		if (targetClosure != null) {
			containsClosures = true;
		}
		if (value != null) {
			containsValues = true;
			if (value instanceof Geometry || value instanceof GeometryProperty) {
				containsGeometries = true;
			}
		}
	}

	/**
	 * Transforms the closures added to this collector to a {@link MultiValue}
	 * using the supplied builder.
	 * 
	 * @param builder the instance builder for creating target instances
	 * @param type the type of the instance to create
	 * @param log the log
	 * @return a result value for all closures added to this collector
	 * @throws TransformationException if some of the collected targets do not
	 *             match the specified type
	 */
	public MultiValue toMultiValue(InstanceBuilder builder, TypeDefinition type, SimpleLog log)
			throws TransformationException {
		MultiValue result = new MultiValue(size());

		// a) closures not allowed if the target is no instance
		if (containsClosures && type.getChildren().isEmpty()) {
			throw new TransformationException("An instance is not applicable for the target.");
		}
		// b) values not allowed if the target may not have a value
		if (containsValues && !type.getConstraint(HasValueFlag.class).isEnabled()
				&& !type.getConstraint(AugmentedValueFlag.class).isEnabled()) {
			// throw new TransformationException("A value is not applicable for
			// the target.");

			// this may be desired, e.g. when producing geometries for GML
			if (containsGeometries) {
				// only warning message for geometries
				log.warn(
						"Value provided for target that does not allow a value according to the schema, contains geometries");
			}
			else {
				// instead of a hard error, we just log an error
				log.error(
						"Value provided for target that does not allow a value according to the schema");
			}
		}

		for (TargetData data : targetData) {
			Object value;
			if (data.instance != null) {
				Instance instance = data.instance;
				// value as instance value
				if (data.value != null && instance instanceof MutableInstance) {
					((MutableInstance) instance).setValue(data.value);
				}
				value = instance;
			}
			else {
				value = data.value;
			}
			result.add(value);
		}

		return result;
	}

	/**
	 * Returns the number of collected targets.
	 * 
	 * @return the number of collected targets
	 */
	public int size() {
		return targetData.size();
	}
}
