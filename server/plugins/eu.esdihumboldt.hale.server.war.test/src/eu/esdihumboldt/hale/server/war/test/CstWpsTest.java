/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */

package eu.esdihumboldt.hale.server.war.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CstWpsTest {
	String host = "http://localhost";
	String port = "8080";
	String path = "/cst";
	URL url;

	@Before
	public void setUp() throws Exception {
		url = new URL(host+":"+port+path);
	}

	@Test
	public void testExecute() {
		try {
			// get test data
			InputStream is = CstWpsTest.class.getResourceAsStream("data/execute_simple.xml");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String txt, xml = "";
			while ((txt = reader.readLine()) != null) {
				xml += txt;
			}
			
			// construct data
			String data = "";
			data += "Request="+URLEncoder.encode(xml, "UTF-8");
			data += "&service=WPS";
			
			// open connection
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			
			// send data
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(data);
			writer.flush();
			
			// get the response
			reader.close();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			txt = "";
			String output = "";
			while ((txt = reader.readLine()) != null) {
				output += txt;
			}
			
			// close open streams
			writer.close();
			reader.close();
			
			if (output.contains("Exception")) {
				Assert.assertTrue(false);
			}
			
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testExecuteFail() {
		try {
		// construct data
		String data = "";
		data += "Request="+URLEncoder.encode("", "UTF-8");
		data += "&service=WPS";
		
		// open connection
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		
		// send data
		OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
		writer.write(data);
		writer.flush();
		
		// get the response
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String txt = "";
		String output = "";
		while ((txt = reader.readLine()) != null) {
//			System.out.println(txt);
			output += txt;
		}
		
		// close open streams
		writer.close();
		reader.close();
		
		if (output.equals("")) {
			Assert.assertTrue(true);
		}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testExecuteComplex() {
		try {
			// get test data
			InputStream is = CstWpsTest.class.getResourceAsStream("data/execute_complex.xml");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			String txt, xml = "";
			while ((txt = reader.readLine()) != null) {
				xml += txt;
			}
			
			// construct data
			String data = "";
			data += "Request="+URLEncoder.encode(xml, "UTF-8");
			data += "&service=WPS";
			
			// open connection
			URLConnection con = url.openConnection();
			con.setDoOutput(true);
			
			// send data
			OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
			writer.write(data);
			writer.flush();
			
			// get the response
			reader.close();
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			txt = "";
			String output = "";
			while ((txt = reader.readLine()) != null) {
				output += txt;
			}
			
			// close open streams
			writer.close();
			reader.close();
			
			if (output.contains("Exception")) {
				Assert.assertTrue(false);
			}
			
			Assert.assertTrue(true);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
