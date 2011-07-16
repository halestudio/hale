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

package eu.esdihumboldt.hale.align.transformation.function;

import com.google.common.collect.Multimap;

import eu.esdihumboldt.hale.align.model.Property;
import eu.esdihumboldt.hale.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;

/**
 * Transformation function between source and target properties.
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface PropertyTransformation<E extends TransformationEngine> extends TransformationFunction<E> {
	
	/**
	 * Set the source properties and instance
	 * @param sourceProperties the source properties
	 * @param sourceInstance the source instance
	 */
	public void setSource(Multimap<String, ? extends Property> sourceProperties,
			Instance sourceInstance);
	
	/**
	 * Set the target properties and instance
	 * @param targetProperties the source properties
	 * @param targetInstance the source instance
	 */
	public void setTarget(Multimap<String, ? extends Property> targetProperties,
			MutableInstance targetInstance);

}
