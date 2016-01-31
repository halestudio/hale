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

package eu.esdihumboldt.hale.common.align.transformation.function.impl;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation;
import eu.esdihumboldt.hale.common.align.transformation.service.PropertyTransformer;
import eu.esdihumboldt.hale.common.core.HalePlatform;
import eu.esdihumboldt.hale.common.instance.model.FamilyInstance;
import eu.esdihumboldt.hale.common.instance.model.InstanceFactory;

/**
 * Type transformation function base class
 * 
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractTypeTransformation<E extends TransformationEngine> extends
		AbstractTransformationFunction<E> implements TypeTransformation<E> {

	private PropertyTransformer propertyTransformer;
	private ListMultimap<String, ? extends Type> target;
	private FamilyInstance source;

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
	 * 
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
	 * 
	 * @return the instance factory
	 */
	protected InstanceFactory getInstanceFactory() {
		return HalePlatform.getService(InstanceFactory.class);
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.TypeTransformation#setSource(FamilyInstance)
	 */
	@Override
	public void setSource(FamilyInstance sourceInstances) {
		this.source = sourceInstances;
	}

	/**
	 * Get the source instances.
	 * 
	 * @return the source instances
	 */
	public FamilyInstance getSource() {
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
