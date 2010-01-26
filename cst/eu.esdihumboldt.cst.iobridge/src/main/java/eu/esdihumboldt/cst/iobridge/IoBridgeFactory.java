/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.cst.iobridge;

import eu.esdihumboldt.cst.iobridge.impl.DefaultCstServiceBridge;
import eu.esdihumboldt.cst.iobridge.impl.DynamicWfsCstServiceBridge;

/**
 * This factory allows to load static (preloaded data) or dynamic IoBridges 
 * for the CST.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class IoBridgeFactory {
	
	public static CstServiceBridge getIoBridge(BridgeType bridgeType) {
		if (BridgeType.dynamic.equals(bridgeType)) {
			return new DynamicWfsCstServiceBridge();
		}
		else if (BridgeType.preloaded.equals(bridgeType)) {
			return new DefaultCstServiceBridge();
		}
		else {
			return null;
		}
	}
	
	public enum BridgeType {
		dynamic,
		preloaded
	}

}
