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

package eu.esdihumboldt.hale.align.transformation.function.impl;

import eu.esdihumboldt.hale.align.model.Type;
import eu.esdihumboldt.hale.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.align.transformation.function.SingleTypeTransformation;
import eu.esdihumboldt.hale.instance.model.Instance;

/**
 * Transformation function base class
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public abstract class AbstractSingleTypeTransformation<E extends TransformationEngine> extends
		AbstractTypeTransformation<E> implements SingleTypeTransformation<E> {

	private Type sourceType;
	private Instance sourceInstance;

	/**
	 * @see SingleTypeTransformation#setSource(Type, Instance)
	 */
	@Override
	public void setSource(Type sourceType, Instance sourceInstance) {
		this.sourceType = sourceType;
		this.sourceInstance = sourceInstance;
	}

	/**
	 * Get the source type
	 * @return the source type
	 */
	public Type getSourceType() {
		return sourceType;
	}

	/**
	 * Get the source instance
	 * @return the source instance
	 */
	public Instance getSourceInstance() {
		return sourceInstance;
	}

}
