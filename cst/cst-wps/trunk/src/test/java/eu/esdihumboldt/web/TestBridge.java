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
	public void testBridge() {
		try{
		URL oml = this.getClass().getResource("testproject_hydro_withmapping.xml.goml");
		URL gml = this.getClass().getResource("wfs_va.gml");
		URL xsd = this.getClass().getResource("Hydrography.xsd");		
	
		System.out.println(
				IoBridgeFactory.getIoBridge(BridgeType.preloaded)
				        .transform(gml.getPath(), 
						           oml.getPath(), 
						           xsd.getPath()));
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
