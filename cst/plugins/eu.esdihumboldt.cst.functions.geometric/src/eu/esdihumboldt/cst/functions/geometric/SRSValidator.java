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

package eu.esdihumboldt.cst.functions.geometric;

import com.google.common.collect.ListMultimap;

import eu.esdihumboldt.hale.common.align.extension.function.Validator;
import eu.esdihumboldt.hale.common.instance.geometry.impl.CodeDefinition;

/**
 * Validator for the referenceSystem parameter of the ordinates to point function.
 *
 * @author Kai Schwierczek
 */
public class SRSValidator implements Validator {

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#validate(java.lang.String)
	 */
	@Override
	public String validate(String value) {
		CodeDefinition codeDef = new CodeDefinition(value, null);
		try {
			codeDef.getCRS();
		} catch (IllegalStateException ise) {
			return "Invalid CRS code. Must be a valid EPSG:... code.";
		}
		return null;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.align.extension.function.Validator#setParameters(com.google.common.collect.ListMultimap)
	 */
	@Override
	public void setParameters(ListMultimap<String, String> parameters) {
		// no parameter for this validator
	}

}
