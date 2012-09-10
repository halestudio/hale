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

package eu.esdihumboldt.hale.common.align.model.transformation;

import eu.esdihumboldt.hale.common.align.model.Alignment;

/**
 * A transformation is an {@link Alignment} that has been processed to a set of
 * transformation cells that can be converted to instructions to be executed to
 * perform that transformation on instances.
 * 
 * @author Simon Templer
 */
public interface Transformation {

	// TODO add method to generate specific transformation instructions given a
	// source instance
//	public X y(Collection<? extends Type> sourceTypes, Instance source);

//	/**
//	 * 
//	 * @param tree
//	 * @param sourceTypes
//	 * @param source the source instance XXX eventually multiple?
//	 * @return
//	 */
//	public X prepare(TransformationTree tree, 
//			Collection<? extends Type> sourceTypes, Instance source);

}
