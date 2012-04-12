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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;

/**
 * Type transformation function base class
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTypeTransformation<E extends TransformationEngine>
		extends AbstractTransformationFunction<E> implements TypeTransformation<E> {

	private PropertyTransformer propertyTransformer;
	private ListMultimap<String, ? extends Type> target;
	private Collection<Instance> source;

	/**
	 * @see TypeTransformation#setPropertyTransformer(PropertyTransformer)
	 */
	@Override
	public void setPropertyTransformer(PropertyTransformer propertyTransformer) {
		this.propertyTransformer = propertyTransformer;
	}

	/**
	 * Get the property transformer to publish any source/target instance pair 
	 * to
	 * @return the property transformer
	 */
	public PropertyTransformer getPropertyTransformer() {
		return propertyTransformer;
	}
	
	/**
	 * @see TypeTransformation#setTarget(ListMultimap)
	 */
	@Override
	public void setTarget(ListMultimap<String, ? extends Type> target) {
		this.target = Multimaps.unmodifiableListMultimap(target);
	}

	/**
	 * @return the targetTypes
	 */
	public ListMultimap<String, ? extends Type> getTarget() {
		return target;
	}

	/**
	 * Get the instance factory
	 * @return the instance factory
	 */
	protected InstanceFactory getInstanceFactory() {
		return OsgiUtils.getService(InstanceFactory.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation#setSource(java.util.Collection)
	 */
	@Override
	public void setSource(Collection<Instance> sourceInstances) {
		this.source = Collections.unmodifiableCollection(sourceInstances);
	}

	/**
	 * Get the source instances.
	 * 
	 * @return the source instances
	 */
	public Collection<Instance> getSource() {
		return source;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super E> getInstanceHandler() {
		return null;
	}
}
