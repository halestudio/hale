/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.common.align.transformation.function;

import java.util.Collection;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Transformation function between source and target types.
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface TypeTransformation<E extends TransformationEngine> extends TransformationFunction<E> {
	
	/**
	 * Set the property transformer to publish the source/target instance pairs
	 * to. Type transformations have no result, instead they must publish the 
	 * instance pairs created during
	 * {@link #execute(String, TransformationEngine, java.util.Map, TransformationLog)}ion 
	 * to the property transformer using
	 * {@link PropertyTransformer#publish(Collection, MutableInstance, TransformationLog)}.
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
	public void setSource(Collection<Instance> sourceInstances);

	/**
	 * Get the handler to partition the source instances (e.g. merge or join).
	 * 
	 * @return the instance handler or <code>null</code> if none is required
	 */
	public InstanceHandler<? super E> getInstanceHandler();
}
