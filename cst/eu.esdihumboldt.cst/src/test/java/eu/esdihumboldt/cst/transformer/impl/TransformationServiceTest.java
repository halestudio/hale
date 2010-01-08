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

package eu.esdihumboldt.cst.transformer.impl;

import java.util.Iterator;

import junit.framework.TestCase;
import eu.esdihumboldt.cst.transformer.CstServiceCapabilities;
import eu.esdihumboldt.cst.transformer.FunctionDescription;
import eu.esdihumboldt.cst.transformer.service.impl.CstServiceImpl;

public class TransformationServiceTest extends TestCase{

	public void testGetCapabilities(){
	
		CstServiceImpl tsi = new CstServiceImpl();
		CstServiceCapabilities tc = tsi.getCapabilities();
		
		//List<FunctionDescription> tc.getFunctionDescriptions()
		for (Iterator<FunctionDescription> i = tc.getFunctionDescriptions().iterator();i.hasNext(); ){
			FunctionDescription desc = i.next() ;
			assertNotNull(desc.getFunctionId());			
			assertNotNull(desc.getFunctionId().toString()+ " has to configure ParameterConfiguration", 
					(Object)desc.getParameterConfiguration());
			/*if (desc.getParameterConfiguration()==null){
				System.out.println(desc.getFunctionId().toString()+ " has to add get getParameterTypes method");
			}*/
			
		}
		//System.out.println(tc.getFunctionDescriptions().get(1).getFunctionId());
		//AssertTrue.notEmpty(tc.getFunctionDescriptions());
	
		
		
	}
}
