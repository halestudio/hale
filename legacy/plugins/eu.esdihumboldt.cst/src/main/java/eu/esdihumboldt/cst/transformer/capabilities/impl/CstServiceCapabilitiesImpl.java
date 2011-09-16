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

package eu.esdihumboldt.cst.transformer.capabilities.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.specification.cst.CstFunction;

/**
 * Basic implementation of CST service capabilities
 * 
 * @author Thorsten Reitz, Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class CstServiceCapabilitiesImpl implements CstServiceCapabilities {

	/**
	 * @see CstServiceCapabilities#getFunctionDescriptions()
	 */
	public List<FunctionDescription> getFunctionDescriptions() {
		List<FunctionDescription> result = new ArrayList<FunctionDescription>();

		for (Entry<String, Class<? extends CstFunction>> entry : CstFunctionFactory
				.getInstance().getRegisteredFunctions().entrySet()) {
			result.add(this.createDescription(entry.getValue()));
		}

		return result;
	}

	/**
	 * Create a function description
	 * 
	 * @param type
	 *            the implementing type
	 * 
	 * @return the function description
	 */
	protected FunctionDescription createDescription(
			Class<? extends CstFunction> type) {
		try {
			return new FunctionDescriptionImpl(type.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
