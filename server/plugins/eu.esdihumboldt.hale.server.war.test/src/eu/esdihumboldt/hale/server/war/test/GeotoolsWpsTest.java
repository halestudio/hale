/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.hale.server.war.test;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import net.opengis.wps10.ProcessDescriptionType;
import net.opengis.wps10.ProcessDescriptionsType;
import net.opengis.wps10.ProcessOfferingsType;
import net.opengis.wps10.WPSCapabilitiesType;

import org.eclipse.emf.common.util.EList;
import org.geotools.data.wps.WPSFactory;
import org.geotools.data.wps.WebProcessingService;
import org.geotools.data.wps.request.DescribeProcessRequest;
import org.geotools.data.wps.response.DescribeProcessResponse;
import org.junit.Test;

/**
 * 
 *
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class GeotoolsWpsTest {
	
	@Test
	public void testWps() throws Exception {
		URL url = new URL("http://localhost:8080/cst?service=WPS&request=GetCapabilities");
		WebProcessingService wps = new WebProcessingService(url);
		WPSCapabilitiesType capabilities = wps.getCapabilities();

		// view a list of processes offered by the server
		ProcessOfferingsType processOfferings = capabilities.getProcessOfferings();
		EList processes = processOfferings.getProcess();
		
		// create a WebProcessingService as shown above, then do a full describeprocess on my process
		DescribeProcessRequest descRequest = wps.createDescribeProcessRequest();
		descRequest.setIdentifier("translate"); // describe the double addition process
	
		// send the request and get the ProcessDescriptionType bean to create a WPSFactory
		DescribeProcessResponse descResponse = wps.issueRequest(descRequest);
		ProcessDescriptionsType processDesc = descResponse.getProcessDesc();
		ProcessDescriptionType pdt = (ProcessDescriptionType) processDesc.getProcessDescription().get(0);
		WPSFactory wpsfactory = new WPSFactory(pdt, url);
	
		// create a WebProcessingService, WPSFactory and WPSProcess as shown above and execute it
		org.geotools.process.Process process = wpsfactory.create();

		// setup the inputs
		Map<String, Object> map = new TreeMap<String, Object>();
		Double d1 = 77.5;
		Double d2 = 22.3;
		map.put("input_a", d1);
		map.put("input_b", d2);

		// you could validate your inputs against what the process expected by checking
		// your map against the Parameters in wpsfactory.getParameterInfo(), but
		// to keep this simple let's just try sending the request without validation
		Map<String, Object> results = process.execute(map, null);

		Double result = (Double) results.get("result");
	}

}
