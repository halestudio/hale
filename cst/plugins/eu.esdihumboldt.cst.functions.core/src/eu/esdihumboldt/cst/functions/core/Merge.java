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

package eu.esdihumboldt.cst.functions.core;

import eu.esdihumboldt.cst.functions.core.merge.PropertiesMergeHandler;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.MergeHandler;
import eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractSingleTypeTransformation;

/**
 * Type transformation that merges multiple instances of the same source type
 * into one target instance, based on matching properties.
 * @author Simon Templer
 */
public class Merge extends Retype {

	/**
	 * Creates and returns a {@link PropertiesMergeHandler}.
	 * @see AbstractSingleTypeTransformation#getMergeHandler()
	 */
	@Override
	public MergeHandler<? super TransformationEngine> getMergeHandler() {
		return new PropertiesMergeHandler();
	}

}
