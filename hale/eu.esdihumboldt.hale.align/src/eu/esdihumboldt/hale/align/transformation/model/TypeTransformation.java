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

package eu.esdihumboldt.hale.align.transformation.model;

import java.util.Collection;

import eu.esdihumboldt.hale.align.transformation.report.TransformationReporter;
import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.MutableInstance;

/**
 * Transformation function between source and target types.
 * @author Simon Templer
 */
public interface TypeTransformation extends TransformationFunction {
	
	/**
	 * Set the property transformer to publish the source/target instance pairs
	 * to. Type transformations have no result, instead they must publish the 
	 * instance pairs created during {@link #execute(TransformationReporter)}ion 
	 * to the property transformer using
	 * {@link PropertyTransformer#publish(Collection, Instance, MutableInstance)}.
	 * @param propertyTransformer the property transformer
	 */
	public void setPropertyTransformer(PropertyTransformer propertyTransformer);

}
