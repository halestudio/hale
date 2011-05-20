/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.cst.transformer;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;

import eu.esdihumboldt.specification.cst.CstFunction;

/**
 * An interface for dealing with multiple {@link CstFunction}s in a schema 
 * translation. A group is usually built for each target {@link FeatureType}.
 * 
 * @author Thorsten Reitz 
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$ 
 */
public interface CstFunctionGroup {
	
	/**
	 * Adds a {@link CstFunction} to this {@link CstFunctionGroup}.
	 * @param cstf the {@link CstFunction} to add.
	 */
	public void addCstFunction(CstFunction cstf);
	
	/**
	 * execute a group of {@link CstFunction}s.
	 * @param target the target {@link Feature} to use for all embedded 
	 * {@link CstFunction}s
	 * @return the modified target {@link Feature}.
	 */
	public Feature executeFunctionGroup(Feature target);

}
