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
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class CstWps extends HttpServlet implements HttpRequestHandler, WpsConstants {

	/**
	 * Name of the system property that may be used to override the service URL
	 */
	public static final String PROPERTY_SERVICE_URL = "service_url";

	/**
	 * SerialVersion
	 */
	private static final long serialVersionUID = -8128494354035680094L;
	
	/**
	 * Bundlename
	 */
	public static final String ID = "eu.esdihumboldt.hale.server.war";
	
	private static final ALogger log = ALoggerFactory.getLogger(CstWps.class);

//	/**
//	 * Service url.
//	 */
//	public static final String SERVICEURL = "http://localhost:8080/";
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet
	 */
	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
		log.debug("Handling Get request: " + httpRequest.getRequestURI());
		
		Map<String, String> params = initRequest(httpRequest);
		
		if (params.get("service") != null && params.get("service").equals("wps")) {
			String version = params.get("version");
			if (version != null && !version.equals("1.0.0")) {
				WpsUtil.printError(EXCEPTION_CODE_INVALID_VERSION,
						"Only WPS version 1.0.0 is supported.", null, null,
						response);
				return;
			}
			
			String request = params.get("request");
			
			if (request == null) {
				WpsUtil.printError(EXCEPTION_CODE_MISSING_PARAM,
						"Parameter request is missing.", "request", null,
						response);
			}
			else if (request.toLowerCase().equals("getcapabilities")) {
				// call getCapabilities
				this.getCapabilities(httpRequest, response);
			}
			else if (request.toLowerCase().equals("describeprocess")) {
				// call describeProcess
				this.describeProcess(response);
			}
			else if (httpRequest.getMethod().toLowerCase().equals("post") && 
						request.toLowerCase().contains("execute")) {
				// do the transformation
				this.execute(params, response, httpRequest);
			}
			else {
				WpsUtil.printError(EXCEPTION_CODE_INVALID_PARAM,
						"Parameter request is invalid: " + request, "request", null,
						response);
			}
		} else {
			WpsUtil.printError(EXCEPTION_CODE_MISSING_PARAM,
					"Parameter service is missing.", "service", null,
					response);
			return;
		}
	}

	private Map<String, String> initRequest(HttpServletRequest httpRequest) {
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<?> parameterNames = httpRequest.getParameterNames();

		// create session
		HttpSession session = httpRequest.getSession(true);

		// set session time to 5 minutes
		session.setMaxInactiveInterval(300);

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

		return params;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		log.debug("Handling Post request: " + req.getRequestURI());
		
		StringWriter writer = new StringWriter();
		Reader reader = req.getReader();
		IOUtils.copy(reader, writer);
		writer.flush();
		writer.close();
		reader.close();
		
		Map<String, String> params = initRequest(req);
		//XXX execute thinks the XML request comes as request parameter
		params.put("request", writer.toString());
		
		this.execute(params, resp, req);
	}

	/**
	 * The mandatory GetCapabilities operation allows clients to retrieve service metadata
	 * from a server. The response to a GetCapabilities request shall be a XML
	 * document containing service metadata about the server, including brief
	 * metadata describing all the processes implemented. This clause specifies
	 * the XML document that a WPS server must return to describe its capabilities.
	 * 
	 * @param httpRequest the request 
	 * @param response the response
	 * @throws IOException will be thrown if the static file can't be found
	 */
	public void getCapabilities(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
		BufferedReader reader;
		
		Bundle bundle = Platform.getBundle(CstWps.ID);
		Path path = new Path("cst-wps-static/cst-wps_GetCapabilities_response.xml");

		URL url = FileLocator.find(bundle, path, null);
		InputStream in = url.openStream();
		reader = new BufferedReader(new InputStreamReader(in));
		
		String txt;
		StringBuilder sb = new StringBuilder();
		while ((txt = reader.readLine()) != null) {
			sb.append(txt+"\n");
		}
		
		// determine service URL from request
		String serviceURL = getServiceURL(httpRequest, true);
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		try {
			writer.print(sb.toString().replace("___HREF___", serviceURL));
			
			// close streams
			reader.close();
			in.close();
		} finally {
			// close the writer
			writer.close();
		}
	}
	
	/**
	 * Get the service URL from a HTTP request
	 * @param httpRequest the HTTP servlet request
	 * @param includeServletPath if the servlet path shall be included in the
	 *   service URL
	 * @return the service URL
	 */
	public static String getServiceURL(HttpServletRequest httpRequest, 
			boolean includeServletPath) {
		String serviceURL = System.getProperty(PROPERTY_SERVICE_URL);
		if (serviceURL != null && !serviceURL.isEmpty()) {
			// system property overrides the request information
			if (serviceURL.endsWith("/")) {
				// remove / from end
				serviceURL = serviceURL.substring(0, serviceURL.length() - 1);
			}
		}
		else {
			serviceURL = httpRequest.getScheme() + "://"
					+ httpRequest.getServerName() + ":"
					+ httpRequest.getServerPort();
		}
		
		String servletPath = (includeServletPath) ? (httpRequest
				.getServletPath()) : ("");
		if (servletPath.isEmpty()) {
			servletPath = "/";
		}
		return serviceURL + servletPath;
	}

	/**
	 * The mandatory DescribeProcess operation allows WPS clients to request
	 * a full description of one or more processes that can be executed by the Execute operation.
	 * This description includes the input and output parameters and formats.
	 * This description can be used to automatically build a user interface to capture
	 * the parameter values to be used to execute a process instance.
	 * 
	 * @param response the response
	 * @throws IOException will be thrown if the static file can't be found
	 */
	public void describeProcess(HttpServletResponse response) throws IOException {
		BufferedReader reader;
		
		Bundle bundle = Platform.getBundle(CstWps.ID);
		Path path = new Path("cst-wps-static/cst-wps_DescribeProcess_response.xml");

		URL url = FileLocator.find(bundle, path, null);
		InputStream in = url.openStream();
		reader = new BufferedReader(new InputStreamReader(in));

		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		
		try {
			String txt;
			while ((txt = reader.readLine()) != null) {
				writer.println(txt);
			}
			
			// close streams
			reader.close();
			in.close();
		} finally {
			// close the writer
			writer.close();
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
	 * @param params all given parameter in lowercase
	 * @param response the response
	 * @param request the request
	 */
	public void execute(Map<String, String> params, HttpServletResponse response, HttpServletRequest request) {
		new ExecuteProcess(params, response, request);
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		service(request, response);
//		this.doGet(request, response);
	}
}
