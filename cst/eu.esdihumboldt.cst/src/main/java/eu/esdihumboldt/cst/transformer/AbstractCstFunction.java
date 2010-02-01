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

import java.util.List;

import org.geotools.feature.FeatureCollection;
import org.opengis.feature.Feature;

/**
 * A default implementation of the {@link CstFunction} interface that implements
 * all methods except {@link CstFunction#transform(FeatureCollection)} and
 * {@link CstFunction}{@link #transform(Feature)}.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public abstract class AbstractCstFunction 
	implements CstFunction {

	/**
	 * @see CstFunction#configure(List)
	 */

	public AbstractCstFunction() {

	}


}
