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

package eu.esdihumboldt.hale.server.war;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Element;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.iobridge.impl.DefaultCstServiceBridge;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.ows.ExceptionReport;
import eu.esdihumboldt.hale.server.war.ows.ExceptionType;
import eu.esdihumboldt.hale.server.war.ows.LanguageStringType;
import eu.esdihumboldt.hale.server.war.wps.ComplexDataType;
import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.DataType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse.ProcessOutputs;
import eu.esdihumboldt.hale.server.war.wps.InputType;
import eu.esdihumboldt.hale.server.war.wps.OutputDataType;
import eu.esdihumboldt.hale.server.war.wps.ProcessBriefType;
import eu.esdihumboldt.hale.server.war.wps.ProcessFailedType;
import eu.esdihumboldt.hale.server.war.wps.StatusType;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExecuteProcess {
	
	private final ALogger _log = ALoggerFactory.getLogger(CstWps.class);
	
	private String workspace;
	private String requestDataFileName = "xmlRequestData_"+UUID.randomUUID()+".xml";
	
	/**
	 * Name of input field: SourceData
	 */
	public static final String inputSourceData = "SourceData";
	
	/**
	 * Name of input field: SourceXmlSchemaDefinition
	 */
	public static final String inputSourceXml = "SourceXmlSchemaDefinition";
	
	/**
	 * Name of input field: TargetXmlSchemaDefinition
	 */
	public static final String inputTargetXml = "TargetXmlSchemaDefinition";
	
	/**
	 * Name of input field: Mapping
	 */
	public static final String inputMapping = "Mapping";
	
	private Map<String, String> params;
	private HttpServletResponse response;
	private PrintWriter writer;
	
	private File fXmldata;
	private JAXBContext context;
	private Unmarshaller unmarshaller;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> sourceData = new ArrayList<DataType>();
	/**
	 * Count of sourceData elements
	 */
	int iSourceData = 0;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> sourceXmlSchemaDefinition = new ArrayList<DataType>();
	/**
	 * Count of souceSchema elements
	 */
	int iSourceXSD = 0;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> targetXmlSchemaDefinition = new ArrayList<DataType>();
	/**
	 * Count of targetSchema elements
	 */
	int iTargetXSD = 0;
	
	// minOccurs="1" maxOccurs="1"
//	List<DataType> mapping = new ArrayList<DataType>();
	/**
	 * Count of mapping elements
	 */
	int iMapping = 0;
	
	/**
	 * Constructor. Does all processing.
	 * 
	 * @param params all given (http)parameter in lowercase
	 * @param response the response
	 * @param writer the writer
	 */
	public ExecuteProcess(Map<String, String> params, HttpServletResponse response, PrintWriter writer) {
		this.params = params;
		this.response = response;
		this.writer = writer;
		
		try {
			// init jaxb
			this.initJAXB();
			
			// create workspace dir
			this.prepareWorkspace();
			
			// save data from request to file
			this.saveRequestData();
			
			// preprocess the data and check for consistency
			this.preprocessData();
			this.checkData();
			
			// now process the data
			this.processData();
			
			// and clean up workspace
			this.cleanup();
		} catch(Exception e) {
			this.exceptionHandler(e);
		}
	}

	/**
	 * Create workspace.
	 * 
	 * @throws FileNotFoundException if the workspace could not be created
	 */
	private void prepareWorkspace() throws FileNotFoundException {
		workspace = Platform.getLocation().toString() + "/tmp/cst_" + UUID.randomUUID() + "/";
		
		if(!new File(workspace).mkdirs()) {
			throw new FileNotFoundException("Could not create directory: "+workspace);
		}
	}
	
	/**
	 * Initialize the JAXBContext and the Unmarshaller.
	 * 
	 * @throws JAXBException if something is missing in the ObjectFactory
	 */
	private void initJAXB() throws JAXBException {
			context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class, eu.esdihumboldt.hale.server.war.ows.ObjectFactory.class);
			unmarshaller = context.createUnmarshaller();
	}
	
	/**
	 * Save the requestData to disk as JAXB only can unmarshall from a File.
	 * 
	 * @throws IOException if the file can't be written
	 */
	private void saveRequestData() throws IOException {
		String xmlData = params.get("request");
		fXmldata = new File(workspace+requestDataFileName);

		FileWriter wr = new FileWriter(fXmldata);
		wr.write(xmlData);
		wr.flush();
		wr.close();
	}
	
	/**
	 * Analyzes the requestData and saves provided data to disk.
	 *  
	 * @throws Exception if something goes wrong
	 */
	private void preprocessData() throws Exception {
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
			String identifier = t.getIdentifier().getValue();
			
			int fileNumber = 0;
			
			if (identifier.equals(ExecuteProcess.inputSourceData)) {
//				sourceData.add(t.getData());
				iSourceData++;
				fileNumber = iSourceData;
			} else if (identifier.equals(ExecuteProcess.inputSourceXml)) {
//				sourceXmlSchemaDefinition.add(t.getData());
				iSourceXSD++;
				fileNumber = iSourceXSD;
			} else if (identifier.equals(ExecuteProcess.inputTargetXml)) {
//				targetXmlSchemaDefinition.add(t.getData());
				iTargetXSD++;
				fileNumber = iTargetXSD;
			} else if (identifier.equals(ExecuteProcess.inputMapping)) {
//				mapping.add(t.getData());
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
						FileWriter fw = new FileWriter(workspace+identifier+"_"+fileNumber);
						fw.write(str);
						fw.close();
					}
				}
			}
		}
	}
	
	/**
	 * Checks if mandatory elements are provided.
	 * 
	 * @throws Exception if something is missing
	 */
	private void checkData() throws Exception {
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
		}
	}
	
	private void processData() throws Exception {
		DefaultCstServiceBridge cst = new DefaultCstServiceBridge();
		cst.setOutputDir("file:/"+workspace);

		String outputFile = cst.transform("file:/"+workspace+"TargetXmlSchemaDefinition_1", "file:/"+workspace+"Mapping_1", "file:/"+workspace+"SourceData_1", "file:/"+workspace+"SourceXmlSchemaDefinition_1", null);
		
		ExecuteResponse resp = new ExecuteResponse();
		ProcessOutputs pOut = new ProcessOutputs();
		OutputDataType data = new OutputDataType();
		DataType type = new DataType();
		ComplexDataType cData = new ComplexDataType();
		StatusType statusType = new StatusType();
		statusType.setProcessSucceeded("");
//		statusType.setCreationTime(value) TODO add this information
		
		ProcessBriefType pbt = new ProcessBriefType();
//		pbt.getProfile().add("profile");
		
		
		cData.setEncoding("utf-8");
		cData.setMimeType("text/xml");
		FileReader fReader = new FileReader(outputFile.replace("file:/", ""));
		BufferedReader reader = new BufferedReader(fReader);
		String txt;
		String xml = "";
		while ((txt = reader.readLine()) != null) {
			xml += txt+"\n";
		}
		reader.close();
		fReader.close();
		
		cData.getContent().add(xml); // TODO remove with loaded xml/check why < and > are removed with html entities
		
		type.setComplexData(cData);
		data.setData(type);
		
		LanguageStringType lst = new LanguageStringType();
		lst.setValue("translate");
		data.getTitle().add(lst);
//		data.getAbstract().add(lst);

		pOut.getOutput().add(data);
		resp.setProcessOutputs(pOut);
		resp.setLang("en-CA"); // TODO support different languages
		resp.setService("WPS");
		resp.setVersion("1.0.0");
		resp.setProcess(pbt);
		resp.setStatus(statusType);
//		resp.setServiceInstance("url.of.getcapabilities.wps"); // FIXME set to a real URL from GetCapabilities
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
				new NamespacePrefixMapperImpl());
		marshaller.marshal(resp, writer);
	}
	
	/**
	 * Remove the working directory after the transformation has finished.
	 */
	private void cleanup() {
		try {
			FileUtils.deleteDirectory(new File(workspace));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This function handles all occurrence of Exceptions
	 * and generates output for the user.
	 * 
	 * @param e thrown Execption
	 */
	private void exceptionHandler(Exception e) {
		if (e.getCause() != null) {
			_log.error(e.getCause().getMessage(), e);
		} else {
			_log.error(e.getMessage(), e);
		}
		
		try {
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
			/* 
			 * If we get here something really important does not work.
			 * TODO add some kind of static error report instead of showing a blank page
			 */
			try {
				// send "internal server error"
				response.sendError(505);
			} catch (IOException e2) {
				/* if we get here... everything is broken! */
			}
		}
		
	}
}
