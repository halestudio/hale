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

package eu.esdihumboldt.hale.server.war;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CstWps extends HttpServlet implements HttpRequestHandler {

	/**
	 * SerialVersion
	 */
	private static final long serialVersionUID = -8128494354035680094L;
	
	/**
	 * Bundlename
	 */
	public static final String ID = "eu.esdihumboldt.hale.server.war";
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet
	 */
	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {		
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<?> parameterNames = httpRequest.getParameterNames();
		
		// create session
		HttpSession session = httpRequest.getSession(true);
		
		// build a lower case Map
		while (parameterNames.hasMoreElements()) {
			String key = (String) parameterNames.nextElement();
			String val = httpRequest.getParameter(key);
			
			// save request data not as lower case
			if (key.toLowerCase().equals("request")) {
				params.put(key.toLowerCase(), val);
			} else {
				params.put(key.toLowerCase(), val.toLowerCase());
			}
		}
		
		// create a writer
		PrintWriter writer = response.getWriter();
		
		if (params.get("service") != null && params.get("service").equals("wps")) {
			String request = params.get("request");
			
			// no request, maybe display manpage?
			if (request == null) {
				writer.println("CstWps Service. Not enough parameter!");
			}
			// call getCapabilities
			else if (request.toLowerCase().equals("getcapabilities")) {
				this.getCapabilities(response, writer);
			}
			// call describeProcess
			else if (request.toLowerCase().equals("describeprocess")) {
				this.describeProcess(response, writer);
			}
			// do the transformation
			else if (httpRequest.getMethod().toLowerCase().equals("post") && 
						request.toLowerCase().contains("execute")) {
				this.execute(params, response, httpRequest, writer);
			}
		} else {
			// give some sample output?
		}
		
		// close the writer
		writer.close();
	}

	/**
	 * The mandatory GetCapabilities operation allows clients to retrieve service metadata
	 * from a server. The response to a GetCapabilities request shall be a XML
	 * document containing service metadata about the server, including brief
	 * metadata describing all the processes implemented. This clause specifies
	 * the XML document that a WPS server must return to describe its capabilities.
	 * 
	 * @param response the response
	 * @param writer the writer
	 * @throws IOException will be thrown if the static file can't be found
	 */
	public void getCapabilities(HttpServletResponse response, PrintWriter writer) throws IOException {
		BufferedReader reader;
		
		Bundle bundle = Platform.getBundle(CstWps.ID);
		Path path = new Path("cst-wps-static/cst-wps_GetCapabilities_response.xml");

		URL url = FileLocator.find(bundle, path, null);
		InputStream in = url.openStream();
		reader = new BufferedReader(new InputStreamReader(in));
		
		String txt;
		while ((txt = reader.readLine()) != null) {
			writer.println(txt);
		}
		
		// close streams
		reader.close();
		in.close();
	}
	
	/**
	 * The mandatory DescribeProcess operation allows WPS clients to request
	 * a full description of one or more processes that can be executed by the Execute operation.
	 * This description includes the input and output parameters and formats.
	 * This description can be used to automatically build a user interface to capture
	 * the parameter values to be used to execute a process instance.
	 * 
	 * @param response the response
	 * @param writer the writer
	 * @throws IOException will be thrown if the static file can't be found
	 */
	public void describeProcess(HttpServletResponse response, PrintWriter writer) throws IOException {
		BufferedReader reader;
		
		Bundle bundle = Platform.getBundle(CstWps.ID);
		Path path = new Path("cst-wps-static/cst-wps_DescribeProcess_response.xml");

		URL url = FileLocator.find(bundle, path, null);
		InputStream in = url.openStream();
		reader = new BufferedReader(new InputStreamReader(in));

		
		String txt;
		while ((txt = reader.readLine()) != null) {
			writer.println(txt);
		}
		
		// close streams
		reader.close();
		in.close();
	}
	
	/**
	 * The mandatory Execute operation allows WPS clients to run a specified process implemented
	 * by a server, using the input parameter values provided and returning the output values produced.
	 * Inputs can be included directly in the Execute request, or reference web accessible resources.
	 * The outputs can be returned in the form of an XML response document, either embedded within
	 * the response document or stored as web accessible resources. If the outputs are stored,
	 * the Execute response shall consist of a XML document that includes a URL for each stored
	 * output, which the client can use to retrieve those outputs. Alternatively, for a single output,
	 * the server can be directed to return that output in its raw form without
	 * being wrapped in an XML response document.
	 * 
	 * @param params all given parameter in lowercase
	 * @param response the response
	 * @param writer the writer
	 */
	public void execute(Map<String, String> params, HttpServletResponse response, HttpServletRequest request, PrintWriter writer) {
		new ExecuteProcess(params, response, request, writer);
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
