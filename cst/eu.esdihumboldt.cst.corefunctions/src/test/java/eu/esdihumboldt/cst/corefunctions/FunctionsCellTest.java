package eu.esdihumboldt.cst.corefunctions;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import eu.esdihumboldt.cst.corefunctions.inspire.GeographicalNameFunction;
import eu.esdihumboldt.cst.corefunctions.inspire.IdentifierFunction;
import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.service.CstFunctionFactory;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;

/**
 * Checks the compatibility for getParameters and configure methods.
 * @author jezekjan
 *
 */
public class FunctionsCellTest {

	@Test
	public void testCell(){
		/**
		 * we can not use:
		 * CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");
		 * because of test is having same package name... so manual instantiation of each function is required			
		 * 
		 */
		List<CstFunction> func = new ArrayList<CstFunction>();
		CstFunction f1 = new SpatialTypeConversionFunction();	
		func.add(f1);
	//	CstFunction f2 = new GeographicalNameFunction();	
	//	func.add(f2);
		CstFunction f3 = new NilReasonFunction();
		func.add(f3);
		CstFunction f4 = new DateExtractionFunction();
		func.add(f4);
		CstFunction f5 = new BoundingBoxFunction();
		func.add(f5);
		CstFunction f6 = new ClassificationMappingFunction();
		func.add(f6);
		CstFunction f7 = new ClipByRectangleFunction();
		func.add(f7);
		CstFunction f8 = new GenericMathFunction();
		func.add(f8);
		CstFunction f9 = new RenameFeatureFunction();
		func.add(f9);
		CstFunction f10 = new NetworkExpansionFunction();
		func.add(f10);
	//	CstFunction f11 = new IdentifierFunction();
	//	func.add(f11);
		CstFunction f12 = new RenameAttributeFunction();
		func.add(f12);
		CstFunction f13 = new CentroidFunction();
		func.add(f13);
		
		for (Iterator<CstFunction> i = func.iterator(); i.hasNext();) {
			CstFunction f = i.next();
			f.configure(f.getParameters());
		}		
	}
}
