package eu.esdihumboldt.cst.transformer.service;

import java.net.URL;
import java.util.Iterator;

import junit.framework.TestCase;
import eu.esdihumboldt.cst.CstFunction;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.CstServiceCapabilitiesImpl;

public class WPSTest extends TestCase {

	
  protected void setUp() throws Exception {		
		super.setUp();						
	/**
	 * We should add corefunctions jar to classpath before trying to register it.
	 */
		AddFunctionsToPathUtility.getInstance().add();
    }
    
	public void testWPSInfo(){
		CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");			
		
		CstServiceCapabilitiesImpl service = new CstServiceCapabilitiesImpl();
		for (Iterator<FunctionDescription> i = service.getFunctionDescriptions().iterator(); i.hasNext();){
			
			FunctionDescription fd = i.next();
			System.out.println(fd.getFunctionId());
			
			
			for (Iterator<String> j = fd.getParameterConfiguration().keySet().iterator();j.hasNext();){
				String name = j.next();
				
				fd.getParameterConfiguration().get(name);
				System.out.println(name+" --- " +fd.getParameterConfiguration().get(name));
			}
			
		}
	}
}
