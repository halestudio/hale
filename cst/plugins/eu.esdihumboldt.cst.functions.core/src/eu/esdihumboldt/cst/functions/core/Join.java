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

import eu.esdihumboldt.cst.functions.core.join.JoinHandler;
import eu.esdihumboldt.hale.common.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.common.align.transformation.function.InstanceHandler;

/**
 * Type transformation that joins multiple instances of different source types
 * into one target instance, based on matching properties.
 * 
 * @author Kai Schwierczek
 */
public class Join extends Retype {
	/**
	 * @see eu.esdihumboldt.hale.common.align.transformation.function.impl.AbstractTypeTransformation#getInstanceHandler()
	 */
	@Override
	public InstanceHandler<? super TransformationEngine> getInstanceHandler() {
		return new JoinHandler();
	}
}
