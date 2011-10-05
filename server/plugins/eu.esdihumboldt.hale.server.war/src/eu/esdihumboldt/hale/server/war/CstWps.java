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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;

import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.InputType;



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
			else if (request.toLowerCase().equals("execute") || request.toLowerCase().contains("execute")) {
				// 
				try {
					this.execute(response, writer);
				} catch (Exception e) {
					writer.print(e.getLocalizedMessage());
				}
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
	 * 
	 * @throws JAXBException if unmarshaling fails this is thrown
	 */
	public void execute(HttpServletResponse response, PrintWriter writer) throws JAXBException {
		// test data
		URL url = null;
		try {
			url = new URL("http://schemas.opengis.net/wps/1.0.0/examples/53_wpsExecute_request_ComplexValue.xml");
		} catch (MalformedURLException e1) {
			/* */
		}
		
		JAXBContext context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class, eu.esdihumboldt.hale.server.war.ows.ObjectFactory.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		Execute e = (Execute) unmarshaller.unmarshal(url);
		writer.println(e.getService());
		writer.println(e.getIdentifier().getValue());
		writer.println(System.currentTimeMillis());
		
		DataInputsType dI = e.getDataInputs();
		
		for (InputType t : dI.getInput()) {
			writer.println(t.getIdentifier() + " / "+t.getData());
		}
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
