/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to this website:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to : http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.specification.cst;

import java.util.Collection;

import org.opengis.feature.Feature;

/**
 * An extension of the {@link CstFunction} interface that declares a method for
 * the splitting of a single Features into multiple ones. Note that any
 * necessary configuration of how the aggregate has to be performed must happen
 * via the {@link CstFunction#configure(eu.esdihumboldt.cst.align.ICell)}
 * method.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public interface SplitCstFunction extends CstFunction {

	/**
	 * @param source
	 *            the Feature from which multiple Features should be created by
	 *            splitting.
	 * @param target
	 *            the target Collection of Features
	 * @return the target Collection of Features
	 */
	public Feature splitTransform(Feature source, Collection<Feature> target);

}
