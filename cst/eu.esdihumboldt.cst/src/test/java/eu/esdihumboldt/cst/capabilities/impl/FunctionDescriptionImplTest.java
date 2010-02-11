/**
 * 
 */
package eu.esdihumboldt.cst.capabilities.impl;

import static org.junit.Assert.*;

import java.net.URISyntaxException;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.AddFunctionsToPathUtility;

/**
 * JUnit 4 Test for the {@link FunctionDescriptionImpl} type.
 * 
 * @author Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class FunctionDescriptionImplTest {

	@BeforeClass
	public static void addCstFunctionsTopath(){
		AddFunctionsToPathUtility.getInstance().add();
	}
	/**
	 * Test method for {@link eu.esdihumboldt.cst.transformer.capabilities.impl.FunctionDescriptionImpl#FunctionDescriptionImpl(eu.esdihumboldt.cst.transformer.CstFunction)}.
	 */
	@Test
	public void testFunctionDescriptionImpl() {
		CstFunctionFactory cff = CstFunctionFactory.getInstance();
		cff.registerCstPackage("eu.esdihumboldt.cst.corefunctions");
		cff.registerCstPackage("eu.esdihumboldt.cst.corefunctions.inspire");

		for (Class<? extends CstFunction> functionClass : cff.getRegisteredFunctions().values()) {
			System.out.println("=== Test for class: " + functionClass.getName() + " ===");
			FunctionDescription fd = null;
			try {
				fd = new FunctionDescriptionImpl(functionClass.newInstance());
			} catch (Exception e) {
				fail(e.getMessage());
			}
			for (String key : fd.getParameterConfiguration().keySet()) {
				System.out.println("    " + key + ": "
						+ fd.getParameterConfiguration().get(key).getName());
			}
			assertTrue(fd.getFunctionId() != null);
			assertTrue(fd.getParameterConfiguration().keySet().size() > 0);
		}
	}

}
