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

import org.opengis.feature.Feature;

import eu.esdihumboldt.specification.cst.align.ICell;

/**
 * Interface which defines methods for function configuration, description and
 * simple one-to-one execution.
 * 
 * @author Thorsten Reitz
 */
public interface CstFunction {

	/**
	 * Apply a function to a single source-target Feature combination.
	 * 
	 * @param source
	 *            a source {@link Feature} for the function.
	 * @param target
	 *            a target {@link Feature} to be affected by the function.
	 * @return the transformed {@link Feature} (identical to target).
	 */
	public Feature transform(Feature source, Feature target);

	/**
	 * @param cell
	 *            a full {@link ICell} to be used for configuration. This
	 *            operation is useful if the {@link CstFunction} requires
	 *            information about the entities, relations and other
	 *            structures, but is more complex to implement.
	 * @return false if this mode of configuration is not supported.
	 */
	public boolean configure(ICell cell);

	/**
	 * @return a prototype {@link ICell} that provides information on the
	 *         parameter structure that a function expects, including type
	 *         conditions and value conditions if appropriate. Parameter values
	 *         are left empty.
	 */
	public ICell getParameters();

	/**
	 * @return the description of the function. It can be just a short String,
	 *         or a link to a documented Web page (in this case, the returned
	 *         string must begin with "link;", for example,
	 *         "link:http://community.esdi-humboldt.eu").
	 */
	public String getDescription();

}
