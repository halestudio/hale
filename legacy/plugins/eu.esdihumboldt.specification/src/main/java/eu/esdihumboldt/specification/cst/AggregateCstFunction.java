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
 * An extension of the {@link CstFunction} interface that declare a method for
 * the aggregation of multiple Features into one. Note that any necessary
 * configuration of how the aggregate has to be performed must happen via the
 * {@link CstFunction#configure(eu.esdihumboldt.cst.align.ICell)} method.
 * 
 * @author Thorsten Reitz
 * @version {$Id}
 */
public interface AggregateCstFunction extends CstFunction {

	/**
	 * @param source
	 *            the Collection of Features from one or multiple FeatureTypes
	 *            that should be aggregated.
	 * @param target
	 *            the target Feature
	 * @return the target Feature
	 */
	public Feature aggregateTransform(Collection<Feature> source, Feature target);

}
