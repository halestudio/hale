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

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.Property;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.PropertyTransformation;
import eu.esdihumboldt.hale.common.instance.model.Instance;
import eu.esdihumboldt.hale.common.instance.model.MutableInstance;

/**
 * Property transformation base class
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractPropertyTransformation<E extends TransformationEngine>
		extends AbstractTransformationFunction<E>  implements
		PropertyTransformation<E> {

	private ListMultimap<String, ? extends Property> sourceProperties;
	private Instance sourceInstance;
	
	private ListMultimap<String, ? extends Property> targetProperties;
	private MutableInstance targetInstance;

	/**
	 * @see PropertyTransformation#setSource(ListMultimap, Instance)
	 */
	@Override
	public void setSource(
			ListMultimap<String, ? extends Property> sourceProperties,
			Instance sourceInstance) {
		this.sourceProperties = Multimaps.unmodifiableListMultimap(sourceProperties);
		this.sourceInstance = sourceInstance;
	}

	/**
	 * @see PropertyTransformation#setTarget(ListMultimap, MutableInstance)
	 */
	@Override
	public void setTarget(
			ListMultimap<String, ? extends Property> targetProperties,
			MutableInstance targetInstance) {
		this.targetProperties = Multimaps.unmodifiableListMultimap(targetProperties);
		this.targetInstance = targetInstance;
	}

	/**
	 * Get the source properties
	 * @return the source properties
	 */
	public ListMultimap<String, ? extends Property> getSourceProperties() {
		return sourceProperties;
	}

	/**
	 * Get the source instance
	 * @return the source instance
	 */
	public Instance getSourceInstance() {
		return sourceInstance;
	}

	/**
	 * Get the target properties
	 * @return the target properties
	 */
	public ListMultimap<String, ? extends Property> getTargetProperties() {
		return targetProperties;
	}

	/**
	 * Get the target instance
	 * @return the target instance
	 */
	public MutableInstance getTargetInstance() {
		return targetInstance;
	}

}
