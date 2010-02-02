/**
 * 
 */
package eu.esdihumboldt.cst.capabilities.impl;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;

/**
 * JUnit 4 Test for the {@link FunctionDescriptionImpl} type.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class FunctionDescriptionImplTest {

	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl#FunctionDescriptionImpl(eu.esdihumboldt.cst.transformer.CstFunction)}.
	 */
	@Test
	public void testFunctionDescriptionImpl() {
		CstFunctionFactory cff = CstFunctionFactory.getInstance();
		cff.registerCstPackage("eu.esdihumboldt.cst.corefunctions");
		Class<? extends CstFunction> functionClass = cff.getRegisteredFunctions().get(
				"eu.esdihumboldt.cst.corefunctions.GenericMathFunction");
		FunctionDescription fd = null;
		try {
			fd = new FunctionDescriptionImpl(functionClass.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		for (String key : fd.getParameterConfiguration().keySet()) {
			System.out.println(key + ": " + fd.getParameterConfiguration().get(key).getName());
		}
		assertTrue(true);
		
	}

}
