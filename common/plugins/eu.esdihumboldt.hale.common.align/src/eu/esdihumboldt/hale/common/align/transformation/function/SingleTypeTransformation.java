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

import eu.esdihumboldt.hale.common.align.model.Type;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.instance.model.Instance;

/**
 * Type transformation function that processes only one source type.
 * @param <E> the transformation engine type
 * 
 * @author Simon Templer
 */
public interface SingleTypeTransformation<E extends TransformationEngine> extends TypeTransformation<E> {
	
	/**
	 * Set the source type and instance.
	 * @param sourceType the source type
	 * @param sourceInstance the source instance that is to be transformed
	 */
	public void setSource(Type sourceType, Instance sourceInstance);
	
	/**
	 * Get the handler for merging source instances.
	 * @return the merge handler or <code>null</code> if no merge is required
	 */
	public MergeHandler<? super E> getMergeHandler();

}
