package eu.esdihumboldt.web;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

import eu.esdihumboldt.cst.iobridge.IoBridgeFactory;
import eu.esdihumboldt.cst.iobridge.IoBridgeFactory.BridgeType;

public class TestBridge {

	/**
	 * @param args
	 */
	@Test
	public void testBridge() throws Exception{
		
		
		try {
		File oml = new File("./src/main/webapp/xsds/HY/testproject.xml.goml");		
		File gml = new File("./src/main/webapp/xsds/wfs_va.gml");		
		File xsd = new File("./src/main/webapp/xsds/HY/Hydrography.xsd");			
	
		System.out.println(xsd.toURI());
		System.out.println(
				IoBridgeFactory.getIoBridge(BridgeType.preloaded)
				        .transform(xsd.toURI().toString(),
				        		   oml.getPath(),
				        		   gml.getPath()					           
						          ));
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
