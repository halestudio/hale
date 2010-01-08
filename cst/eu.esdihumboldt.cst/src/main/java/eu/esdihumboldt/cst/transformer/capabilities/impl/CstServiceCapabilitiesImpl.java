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

import java.util.List;

import eu.esdihumboldt.cst.transformer.capabilities.CstServiceCapabilities;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;

public class CstServiceCapabilitiesImpl implements
		CstServiceCapabilities {

	private final List<FunctionDescription> opd;
	
	public List<FunctionDescription> getFunctionDescriptions() {
		// TODO Auto-generated method stub
		return opd;
	}

	public CstServiceCapabilitiesImpl(List<FunctionDescription> opd) {
		super();
		this.opd = opd;
	}

}
