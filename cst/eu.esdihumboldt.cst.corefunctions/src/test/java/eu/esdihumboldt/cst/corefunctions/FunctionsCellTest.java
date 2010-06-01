package eu.esdihumboldt.cst.corefunctions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;

/**
 * Checks the compatibility for getParameters and configure methods.
 * 
 * @author jezekjan
 * @version $Id$
 */
public class FunctionsCellTest {

	@Test
	public void testCell(){
		/**
		 * we can not use:
		 * CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");
		 * because of test is having same package name... so manual instantiation of each function is required
		 */
		for (CstFunction f : FunctionsCellTest.getTestFunctions()) {
			f.configure(f.getParameters());
		}		
	}
	
	public static List<CstFunction> getTestFunctions() {
		List<CstFunction> func = new ArrayList<CstFunction>();	
		func.add(new NilReasonFunction());
		func.add(new DateExtractionFunction());
		func.add(new BoundingBoxFunction());
		func.add(new ClassificationMappingFunction());
		func.add(new ClipByRectangleFunction());
		func.add(new GenericMathFunction());
		func.add(new NetworkExpansionFunction());
		func.add(new RenameAttributeFunction());
		func.add(new CentroidFunction());
		//FIXME func.add(new GeographicalNameFunction());
		//FIXME func.add(new IdentifierFunction());
		return func;
	}
}
