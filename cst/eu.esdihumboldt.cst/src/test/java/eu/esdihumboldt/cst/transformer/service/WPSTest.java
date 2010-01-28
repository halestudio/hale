package eu.esdihumboldt.cst.transformer.service;

import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;

import junit.framework.TestCase;
import eu.esdihumboldt.cst.transformer.AbstractCstFunction;
import eu.esdihumboldt.cst.transformer.CstFunction;
import eu.esdihumboldt.cst.transformer.capabilities.FunctionDescription;
import eu.esdihumboldt.cst.transformer.capabilities.impl.CstServiceCapabilitiesImpl;
import eu.esdihumboldt.cst.transformer.service.rename.RenameFeatureFunction;

public class WPSTest extends TestCase {

	
  protected void setUp() throws Exception {		
		super.setUp();						
	/**
	 * We should add corefunctions jar to classpath before trying to register it.
	 */
	URL functions = getClass().getResource("corefunctions-SNAPSHOT.jar");		
	CstFunctionFactoryTest.addURL(functions);
    }
  
      
    /**
     * Tests if getParameters Method is compatible with configure method 
     */
    public void testCellConfiguration() throws Exception{
    	
    	CstFunctionFactory.getInstance().registerCstPackage("eu.esdihumboldt.cst.corefunctions");			
		
		
    	for (Iterator<String> i = CstFunctionFactory.getInstance().getRegisteredFunctions().keySet().iterator(); i.hasNext();) {
    		CstFunction f = null;
    		try {
				f = CstFunctionFactory.getInstance().getRegisteredFunctions().get(i.next()).newInstance();
				f.configure(f.getParameters());
			} catch (Exception e) {
				assertTrue(f.getClass().getName()+" - incompatible getParameters and configure method", false);
				e.printStackTrace();
			}
		}
	
    }
    
	public void ttestWPSInfo(){
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
