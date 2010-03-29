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


package eu.esdihumboldt.cst.transformer.service;

import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.CstServiceCapabilitiesImpl;


/**
 * Test of CstServiceCapabilitiesImpl and functions parameter configuration. 
 * @author jezekjan
 * @partner Help Service - Remote Sensing
 * @version $Id$ 
 */
public class WPSTest {

	@Before
	public void addFunctions() {		
		/**
		 * We should add corefunctions jar to classpath before trying to
		 * register it.
		 */
		AddFunctionsToPathUtility.getInstance().add();
	}

	/**
	 * Tests if at least some parameters are returned. It does not test if the parameters are correct.
	 */
	@Test
	public void testWPSInfo() {
		CstFunctionFactory.getInstance().registerCstPackage(
				"eu.esdihumboldt.cst.corefunctions");

		CstServiceCapabilitiesImpl service = new CstServiceCapabilitiesImpl();
		for (Iterator<FunctionDescription> i = service
				.getFunctionDescriptions().iterator(); i.hasNext();) {

			FunctionDescription fd = i.next();
			assertNotNull(fd.getFunctionId());

			for (Iterator<String> j = fd.getParameterConfiguration().keySet()
					.iterator(); j.hasNext();) {
				String name = j.next();

				fd.getParameterConfiguration().get(name);
				assertNotNull(fd.getParameterConfiguration().get(name));
			}

		}
	}
}
