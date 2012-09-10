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

package eu.esdihumboldt.hale.common.align.transformation.engine.internal;

import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;

/**
 * Transformation engine with no state
 * 
 * @author Simon Templer
 */
public final class NullTransformationEngine implements TransformationEngine {

	/**
	 * @see TransformationEngine#setup()
	 */
	@Override
	public void setup() {
		// do nothing
	}

	/**
	 * @see TransformationEngine#dispose()
	 */
	@Override
	public void dispose() {
		// do nothing
	}

}
