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

package eu.esdihumboldt.hale.common.align.transformation.function;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Transformation function between source and target types.
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface TypeTransformation<E extends TransformationEngine> extends
		TransformationFunction<E> {

	/**
	 * Set the property transformer to publish the source/target instance pairs
	 * to. Type transformations have no result, instead they must publish the
	 * instance pairs created during
	 * {@link #execute(String, TransformationEngine, java.util.Map, TransformationLog, eu.esdihumboldt.hale.common.align.model.Cell)}
	 * ion to the property transformer using
	 * {@link PropertyTransformer#publish(FamilyInstance, MutableInstance, TransformationLog, eu.esdihumboldt.hale.common.align.model.Cell)}
	 * .
	 * 
	 * @param propertyTransformer the property transformer
	 */
	public void setPropertyTransformer(PropertyTransformer propertyTransformer);

	/**
	 * Set the target types.
	 * 
	 * @param targetTypes the source properties
	 */
	public void setTarget(ListMultimap<String, ? extends Type> targetTypes);

	/**
	 * Set the source instances.
	 * 
	 * @param sourceInstances the source instances
	 */
	public void setSource(FamilyInstance sourceInstances);

	/**
	 * Get the handler to partition the source instances (e.g. merge or join).
	 * 
	 * @return the instance handler or <code>null</code> if none is required
	 */
	public InstanceHandler<? super E> getInstanceHandler();
}
