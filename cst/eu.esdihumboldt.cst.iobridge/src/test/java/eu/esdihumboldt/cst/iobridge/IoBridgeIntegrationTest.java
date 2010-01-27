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

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

/**
 * This class contains tests that test the integration of the different 
 * CST components.
 * 
 * @author Thorsten Reitz
 * @version $Id$
 */
public class IoBridgeIntegrationTest {
	
	@Test
	public void testCstGetRegisteredTransfomers(){
		CstFunctionFactory tf = CstFunctionFactory.getInstance();
		tf.registerCstPackage("eu.esdihumboldt.cst.corefunctions");
		Map<String, Class<? extends CstFunction>> functions = tf
				.getRegisteredFunctions();
		functions.clear();
		functions = tf.getRegisteredFunctions();
		Assert.assertTrue(functions.size() > 0);
	}

}
