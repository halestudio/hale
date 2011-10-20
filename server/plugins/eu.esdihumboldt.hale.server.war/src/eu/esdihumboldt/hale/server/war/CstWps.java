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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

import eu.esdihumboldt.cst.iobridge.CstServiceBridge;
import eu.esdihumboldt.cst.iobridge.TransformationException;
import eu.esdihumboldt.cst.iobridge.impl.DefaultCstServiceBridge;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.ows.CodeType;
import eu.esdihumboldt.hale.server.war.ows.DescriptionType;
import eu.esdihumboldt.hale.server.war.ows.ExceptionReport;
import eu.esdihumboldt.hale.server.war.ows.ExceptionType;
import eu.esdihumboldt.hale.server.war.ows.KeywordsType;
import eu.esdihumboldt.hale.server.war.ows.LanguageStringType;
import eu.esdihumboldt.hale.server.war.wps.ComplexDataType;
import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.DataType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse;
import eu.esdihumboldt.hale.server.war.wps.ProcessBriefType;
import eu.esdihumboldt.hale.server.war.wps.ProcessFailedType;
import eu.esdihumboldt.hale.server.war.wps.StatusType;
import eu.esdihumboldt.hale.server.war.wps.WSDL;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse.ProcessOutputs;
import eu.esdihumboldt.hale.server.war.wps.InputType;
import eu.esdihumboldt.hale.server.war.wps.OutputDataType;



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
	
	private final ALogger _log = ALoggerFactory.getLogger(CstWps.class);
	
	/**
	 * Bundlename
	 */
	public static final String ID = "eu.esdihumboldt.hale.server.war";
	
	public static final String inputSourceData = "SourceData";
	
	public static final String inputSourceXml = "SourceXmlSchemaDefinition";
	
	public static final String inputTargetXml = "TargetXmlSchemaDefinition";
	
	public static final String inputMapping = "Mapping";
	
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
			// TODO check if it's a POST request
			else if (httpRequest.getMethod().toLowerCase().equals("post") && 
						( request.toLowerCase().equals("execute") 
								|| request.toLowerCase().contains("execute")
						)
					) {
				// 
				try {
					this.execute(params, response, writer);
				} catch (Exception e) {
//					writer.println(e.getMessage());
					if (e.getCause() != null) {
//						writer.print(e.getCause().getMessage());
						_log.error(e.getCause().getMessage(), e);
					} else {
						_log.error(e.getMessage(), e);
					}
					
					try {
						JAXBContext context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class, eu.esdihumboldt.hale.server.war.ows.ObjectFactory.class);
						Marshaller marshaller = context.createMarshaller();
						
						ProcessFailedType failed = new ProcessFailedType();
						ExceptionReport report = new ExceptionReport();
						ExceptionType type = new ExceptionType();
						
						type.setExceptionCode("OperationNotSupported");
						type.getExceptionText().add(e.getMessage());
						report.getException().add(type);
						report.setLang("en-CA");
						report.setVersion("1.0.0");
						failed.setExceptionReport(report);
						
						marshaller.marshal(report, writer); // using ProcessFailedType does not work
					} catch (JAXBException e1) {
						/* */
					}
					
				}
			}
			else {
				try {
					JAXBContext context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class, eu.esdihumboldt.hale.server.war.ows.ObjectFactory.class);
					Marshaller marshaller = context.createMarshaller();
					
					ProcessFailedType failed = new ProcessFailedType();
					ExceptionReport report = new ExceptionReport();
					ExceptionType type = new ExceptionType();
					
					type.setExceptionCode("OperationNotSupported");
					report.getException().add(type);
					report.setLang("en-CA");
					report.setVersion("1.0.0");
					failed.setExceptionReport(report);
					
					marshaller.marshal(report, writer); // using ProcessFailedType does not work
				} catch (JAXBException e1) {
					/* */
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
	 * @throws Exception 
	 */
	public void execute(Map<String, String> params, HttpServletResponse response, PrintWriter writer) throws Exception {
		// create temp folder
		String temp = Platform.getLocation().toString() + "/tmp/cst_" + System.currentTimeMillis() + "/";
//		String temp = Platform.getLocation().toString() + "/tmp/cst_test/";
		
		if (!new File(temp).mkdirs()) {
			// could not create temp directory
		}
		
		// save request data to disk
		String xmlData = params.get("request");
		File fXmldata = new File(temp+"xmlRequestData.xml");
		FileWriter wr = new FileWriter(fXmldata);
		wr.write(xmlData);
		wr.flush();
		wr.close();
		
		// minOccurs="1" maxOccurs="unbounded"
		List<DataType> sourceData = new ArrayList<DataType>();
		int iSourceData = 0;
		
		// minOccurs="1" maxOccurs="unbounded"
		List<DataType> sourceXmlSchemaDefinition = new ArrayList<DataType>();
		int iSourceXSD = 0;
		
		// minOccurs="1" maxOccurs="unbounded"
		List<DataType> targetXmlSchemaDefinition = new ArrayList<DataType>();
		int iTargetXSD = 0;
		
		// minOccurs="1" maxOccurs="1"
		List<DataType> mapping = new ArrayList<DataType>();
		int iMapping = 0;
		
		// create jaxbcontext and unmarshaller
		JAXBContext context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class, eu.esdihumboldt.hale.server.war.ows.ObjectFactory.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		// umarshall request
		Execute exec = (Execute) unmarshaller.unmarshal(fXmldata);
		
		// check if the right identifier is set
		if (!exec.getIdentifier().getValue().equals("translate")) {
			// not supported identifier!
			throw new Exception("Identifier is not supported!");
		}
		
		DataInputsType dI = exec.getDataInputs();
		
		// iterate through data
		for (InputType t : dI.getInput()) {
			String ident = t.getIdentifier().getValue();
			
			int fileNumber = 0;
			
			if (ident.equals(CstWps.inputSourceData)) {
				sourceData.add(t.getData());
				iSourceData++;
				fileNumber = iSourceData;
			} else if (ident.equals(CstWps.inputSourceXml)) {
				sourceXmlSchemaDefinition.add(t.getData());
				iSourceXSD++;
				fileNumber = iSourceXSD;
			} else if (ident.equals(CstWps.inputTargetXml)) {
				targetXmlSchemaDefinition.add(t.getData());
				iTargetXSD++;
				fileNumber = iTargetXSD;
			} else if (ident.equals(CstWps.inputMapping)) {
				mapping.add(t.getData());
				iMapping++;
				fileNumber = iMapping;
			} else {
				// not allowed input type
				throw new Exception("Not supported InputType is provided!");
			}
			
			// check if ComplexData is available
			if (t.getData() != null && t.getData().getComplexData() != null && t.getData().getComplexData().getContent().size() > 0) {
				List<?> list = t.getData().getComplexData().getContent();
				
				for (Object o : list) {
					if (o instanceof Element) {
						// create a element
						Element element = (Element) o;
						
						TransformerFactory transFactory = TransformerFactory.newInstance();
						Transformer transformer = null;
						
						transformer = transFactory.newTransformer();
						
						StringWriter buffer = new StringWriter();
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
						
						transformer.transform(new DOMSource(element), new StreamResult(buffer));
						
						String str = buffer.toString();
						
						// write to disk
						FileWriter fw = new FileWriter(temp+ident+"_"+fileNumber);
						fw.write(str);
						fw.close();
					}
				}
			}
		}
		
		// check occurrence
		if (iSourceData < 1) {
			/* throw or generate exception */
			throw new Exception("No SourceData provided.");
		} else if (iSourceXSD < 1) {
			/* throw or generate exception */
			throw new Exception("No SourceSchema provided.");
		} else if (iTargetXSD < 1) {
			/* throw or generate exception */
			throw new Exception("No TargetSchema provided.");
		} else if (iMapping != 1) {
			/* throw or generate exception */
			throw new Exception("No Mapping provided.");
		} else {
			/* everything alright -> start transformation */
			DefaultCstServiceBridge cst = new DefaultCstServiceBridge();
			cst.setOutputDir("file:/"+temp);

			String outputFile = cst.transform("file:/"+temp+"TargetXmlSchemaDefinition_1", "file:/"+temp+"Mapping_1", "file:/"+temp+"SourceData_1");
			
			ExecuteResponse resp = new ExecuteResponse();
			ProcessOutputs pOut = new ProcessOutputs();
			OutputDataType data = new OutputDataType();
			DataType type = new DataType();
			ComplexDataType cData = new ComplexDataType();
			StatusType statusType = new StatusType();
			statusType.setProcessSucceeded("");
//			statusType.setCreationTime(value) TODO add this information
			
			ProcessBriefType pbt = new ProcessBriefType();
//			pbt.getProfile().add("profile");
			
			
			cData.setEncoding("utf-8");
			cData.setMimeType("text/xml");
			FileReader fReader = new FileReader(outputFile.replace("file:/", ""));
			BufferedReader reader = new BufferedReader(fReader);
			String txt;
			String xml = "";
			while ((txt = reader.readLine()) != null) {
				xml += txt+"\n";
			}
			cData.getContent().add(xml); // TODO remove with loaded xml/check why < and > are removed with html entities
			
			type.setComplexData(cData);
			data.setData(type);
			
			LanguageStringType lst = new LanguageStringType();
			lst.setValue("translate");
			data.getTitle().add(lst);
//			data.getAbstract().add(lst);

			pOut.getOutput().add(data);
			resp.setProcessOutputs(pOut);
			resp.setLang("en-CA");
			resp.setService("WPS");
			resp.setVersion("1.0.0");
			resp.setProcess(pbt);
			resp.setStatus(statusType);
//			resp.setServiceInstance("url.of.getcapabilities.wps"); // FIXME set to a real URL from GetCapabilities
			
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
					new NamespacePrefixMapperImpl());
			marshaller.marshal(resp, writer);
		}
	}

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
