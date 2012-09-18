/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
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
		/*
		 * cst?service=WPS&Request=GetCapabilities&AcceptVersions=1.0.0&language=en-CA
		 */
		
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<?> parameterNames = httpRequest.getParameterNames();
		
		// build a lower case Map
		while (parameterNames.hasMoreElements()) {
			String key = (String) parameterNames.nextElement();
			String val = httpRequest.getParameter(key);
			params.put(key.toLowerCase(), val.toLowerCase());
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
			else if (request.toLowerCase().equals("execute")) {
				// 
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
	 * @param response the response
	 * @param writer the writer
	 */
	public void execute(HttpServletResponse response, PrintWriter writer) {
		/* TODO imlement me */
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
