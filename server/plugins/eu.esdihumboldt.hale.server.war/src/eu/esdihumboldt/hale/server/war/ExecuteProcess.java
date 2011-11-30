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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Element;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.cst.iobridge.impl.CstTransformation;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.ows.LanguageStringType;
import eu.esdihumboldt.hale.server.war.ows.ReferenceType;
import eu.esdihumboldt.hale.server.war.wps.ComplexDataType;
import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.DataType;
import eu.esdihumboldt.hale.server.war.wps.DocumentOutputDefinitionType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse.ProcessOutputs;
import eu.esdihumboldt.hale.server.war.wps.InputType;
import eu.esdihumboldt.hale.server.war.wps.OutputDataType;
import eu.esdihumboldt.hale.server.war.wps.OutputReferenceType;
import eu.esdihumboldt.hale.server.war.wps.ProcessBriefType;
import eu.esdihumboldt.hale.server.war.wps.ResponseDocumentType;
import eu.esdihumboldt.hale.server.war.wps.ResponseFormType;
import eu.esdihumboldt.hale.server.war.wps.StatusType;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExecuteProcess implements WpsConstants {
	
	private final static ALogger _log = ALoggerFactory.getLogger(CstWps.class);
	
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
	 * Name of input field: AuxiliarySourceXmlSchemaArchive
	 */
	public static final String inputSourceArchive = "AuxiliarySourceXmlSchemaArchive";
	
	/**
	 * Name of input field: AuxiliaryTargetXmlSchemaArchive
	 */
	public static final String inputTargetArchive = "AuxiliaryTargetXmlSchemaArchive";
	
	/**
	 * Name of input field: Mapping
	 */
	public static final String inputMapping = "Mapping";
	
	private Map<String, String> params;
	private HttpServletResponse response;
	private HttpServletRequest request;
	
	private File fXmldata;
	private JAXBContext context;
	private Unmarshaller unmarshaller;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> sourceData = new ArrayList<DataType>();
	/**
	 * Count of sourceData elements
	 */
	private int iSourceData = 0;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> sourceXmlSchemaDefinition = new ArrayList<DataType>();
	/**
	 * Count of souceSchema elements
	 */
	private int iSourceXSD = 0;
	
	// minOccurs="1" maxOccurs="unbounded"
//	List<DataType> targetXmlSchemaDefinition = new ArrayList<DataType>();
	/**
	 * Count of targetSchema elements
	 */
	private int iTargetXSD = 0;
	
	// minOccurs="1" maxOccurs="1"
//	List<DataType> mapping = new ArrayList<DataType>();
	/**
	 * Count of mapping elements
	 */
	private int iMapping = 0;
	
	private int iSourceArchive = 0;
	
	private int iTargetArchive = 0;
	
	/**
	 * Contains the outputFile, which is needed for {@link GMLFileFilter}
	 */
	public static File outputFile;
	
	/**
	 * Constructor. Does all processing.
	 * 
	 * @param params all given (http)parameter in lowercase
	 * @param response the response
	 * @param request the request
	 */
	public ExecuteProcess(Map<String, String> params, HttpServletResponse response, HttpServletRequest request) {
		this.params = params;
		this.response = response;
		this.request = request;
//		this.writer = writer;
		
		try {
			// create session
//			HttpSession session = request.getSession(true);
			
			// init jaxb
			this.initJAXB();
			
			// create workspace dir
			this.workspace = ExecuteProcess.prepareWorkspace(request);
			
			// save data from request to file
			this.saveRequestData();
			
			// preprocess the data and check for consistency
			// unmarshall request
			Execute exec = (Execute) unmarshaller.unmarshal(fXmldata);
			
			// check if the right identifier is set
			if (!exec.getIdentifier().getValue().equals("translate")) {
				// translate is the only supported process
				WpsUtil.printError(EXCEPTION_CODE_INVALID_PARAM,
						"Invalid process identifier: "
								+ exec.getIdentifier().getValue(),
						"Identifier", null, response);
			}
			else {
				this.preprocessData(exec);
				this.checkData();
				
				// now process the data
				this.processData();
				
				// and clean up workspace
				this.cleanup();
			}
		} catch(Exception e) {
			// handle exceptions
			WpsUtil.printError(EXCEPTION_CODE_OTHER, null, null, e, response);
			
			// and do a cleanup
			this.cleanup();
		}
	}

	/**
	 * Create workspace.
	 * 
	 * @param request the request
	 * 
	 * @return a String with current path to workspace
	 * 
	 * @throws FileNotFoundException if the workspace could not be created
	 */
	public static String prepareWorkspace(HttpServletRequest request) throws FileNotFoundException {
		HttpSession session = request.getSession();
		String workspace;
		
		_log.info("Session ID: "+session.getId());
		
		if (!session.getId().isEmpty()) {
			workspace = session.getId();
			
			// check if a url provided id is given
			if (session.getAttribute("id") != null) {
				workspace = session.getAttribute("id").toString();
			}
		} else {
			// this should not happen as this might cause trouble
			workspace = UUID.randomUUID().toString();
		}
		
		workspace = Platform.getLocation().toString() + "/tmp/cst_" +workspace + "/";
		
		// save path in session
		session.setAttribute("workspace", workspace);
		
		// try to create all dirs
		File work = new File(workspace);
		if(!work.mkdirs() && !work.exists()) {
			throw new FileNotFoundException("Could not create directory: "+workspace);
		}
		
		return workspace;
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
	 * @param exec the execution request
	 *  
	 * @throws Exception if something goes wrong
	 */
	private void preprocessData(Execute exec) throws Exception {
		// data inputs
		DataInputsType dI = exec.getDataInputs();
		
		// check for ResponseForm
		ResponseFormType responseFormType = exec.getResponseForm();
		if (responseFormType != null) {
			boolean dataAsReference = false;
			ResponseDocumentType documentType = responseFormType.getResponseDocument();
			dataAsReference = dataAsReference | documentType.isStoreExecuteResponse();
			
			for (DocumentOutputDefinitionType t : documentType.getOutput()) {
				dataAsReference = dataAsReference & t.isAsReference();
			}
			
			if (dataAsReference) {
				request.getSession().setAttribute("save", "link");
			}
		}
		
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
			} else if (identifier.equals(ExecuteProcess.inputSourceArchive)) {
				iSourceArchive++;
				fileNumber = iSourceArchive;
				this.saveArchive(t.getData().getComplexData(), ExecuteProcess.inputSourceArchive);
			} else if (identifier.equals(ExecuteProcess.inputTargetArchive)) {
				iTargetArchive++;
				fileNumber = iTargetArchive;
				this.saveArchive(t.getData().getComplexData(), ExecuteProcess.inputTargetArchive);
			} else {
				// not allowed input type
				throw new Exception("Not supported InputType is provided! Identifier: "+identifier);
			}
			
			// check if ComplexData is available
			if (t.getData() != null && t.getData().getComplexData() != null && t.getData().getComplexData().getContent().size() > 0) {
				List<?> list = t.getData().getComplexData().getContent();
				
				for (Object o : list) {
					if (o instanceof Element) {
						// create a element
						Element element = (Element) o;
						String str;
						if (!element.getAttribute("xlink:href").equals("") && element.getAttribute("xlink:href").startsWith("file://")) {
							FileInputStream in = new FileInputStream(element.getAttribute("xlink:href").replace("file://", ""));
							BufferedReader reader = new BufferedReader(new InputStreamReader(in));
							String txt;
							StringBuilder sb = new StringBuilder();
							while ((txt = reader.readLine()) != null) {
								sb.append(txt); 
							}
							
							str = sb.toString();
							
							// close streams
							reader.close();
							in.close();
						} else {
							TransformerFactory transFactory = TransformerFactory.newInstance();
							Transformer transformer = null;
							
							transformer = transFactory.newTransformer();
							
							StringWriter buffer = new StringWriter();
							transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
							
							transformer.transform(new DOMSource(element), new StreamResult(buffer));
							
							str = buffer.toString();
						}
						
						// write to disk
						FileWriter fw = new FileWriter(workspace+identifier+"_"+fileNumber);
						fw.write(str);
						fw.close();
					}
				}
			}
			
			//
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
	
	/**
	 * Extracts information from request data and saves given
	 * archive data (either from url or from data).
	 * 
	 * @param data contain information (xlink, data, etc.)
	 * @param ident identifier for the archive
	 * @throws Exception if something goes wrong
	 */
	private void saveArchive(ComplexDataType data, String ident) throws Exception {
		String fileName = workspace+ident+".zip";
		String mimeType = data.getMimeType();
		String encoding = data.getEncoding();
		if (mimeType != null && mimeType.equals("text/plain") && encoding.equalsIgnoreCase("base64")) {
			/*
			 * TODO this hasn't been tested! (bas64 encoded zip archive)
			 */
			// get binary content
			byte[] content = Base64.decodeBase64(data.getContent().get(1).toString());
			
			// create file and output stream
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName));
			
			// write content
			zos.write(content);
			
			// flush and close stream
			zos.flush();
			zos.close();
		} else if(mimeType != null && mimeType.equals("application/zip")) {
			String uri = "";
			
			for (Object o : data.getContent()) {
				// check if it's a wps:reference
				if (o instanceof Element) {
					Element e = (Element) o;
					uri = e.getAttribute("xlink:href");
					break;
				} else if(o instanceof String) {
					// try to convert a String to a URL
					try {
						URL tmp = new URL(o.toString());
						uri = tmp.toExternalForm();
					} catch(Exception e) {
						/* do nothing */
					}
				}
			}
			
			/*
			 * check if this file already exists in workspace
			 * else process data, download file and check again
			 */
			if (new File(fileName).exists() || uri.startsWith("file://") && uri.endsWith("zip")) {
				this.extractArchive(uri.replace("file://", ""));
				return;
			}
			
			// create url
			URL url = new URL(uri);
			
			// create inputStream
			InputStream is = url.openStream();
			
			// create file
			FileOutputStream fos = new FileOutputStream(fileName);
			BufferedOutputStream os = new BufferedOutputStream(fos);
			
			// read only available byte
			/* NOTE: setting this to a fix value does somehow NOT work
			 * 		 they resulted in broken archives
			 */
			int avail = is.available();
			byte[] zipData = new byte[avail];
			while (is.read(zipData, 0, avail)>=0) {
				os.write(zipData);
				os.flush();
				avail = is.available();
				zipData = new byte[avail];
			}
			
			// flush and close
			os.flush();
			os.close();
			fos.close();
			is.close();
		}
		
		// check the existence of the file and unzip it
		if (new File(fileName).exists()) {
			this.extractArchive(fileName);
		} else {
			//TODO throw Exception
		}
	}
	
	/**
	 * Extracts all data from an archive into current workspace folder.
	 * 
	 * @param name of archive
	 * @throws IOException if something goes wrong
	 */
	private void extractArchive(String name) throws IOException {
		ZipFile file = new ZipFile(name);
		File workFile = new File(workspace);
		
		for (Enumeration<?> e = file.getEntries(); e.hasMoreElements(); ) {
			ZipArchiveEntry entry = (ZipArchiveEntry) e.nextElement();
			
			if (entry.isDirectory()) {
				// create new directory
				if (!new File(workFile, entry.getName()).mkdirs()) {
					throw new FileNotFoundException("Could not create directory: "+workFile+"/"+entry.getName());
				}
				continue;
			}
			
			// create new file
			File outFile = new File(workFile, entry.getName());
			
			// check if parent directory exists
			if (!outFile.getParentFile().exists()){
				if (!outFile.getParentFile().mkdirs()) {
					// TODO maybe an Exception should be thrown: could not create parent dir... hope it will work anyway
					continue;
				}
			}
			
			// create streams
			BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));
			
			try {
				// copy data
				IOUtils.copy(inputStream, outputStream);
			} finally {
				// close streams
				outputStream.close();
				inputStream.close();
	        }
		}
		
		// close the file
		file.close();
	}
	
	/**
	 * This proccesses all data and creates the transformation.
	 * 
	 * @throws Exception if something goes wrong during execution
	 */
	private void processData() throws Exception {
//		DefaultCstServiceBridge cst = new DefaultCstServiceBridge();
		File outputFile = new File(workspace, UUID.randomUUID() + ".gml");
		
		// currently supports only one of each: source schema, target schema, source data and mapping
		CstTransformation.transform(
				new File(workspace, "SourceData_1").toURI(), 
				new File(workspace, "SourceXmlSchemaDefinition_1").toURI(), 
				new File(workspace, "Mapping_1").toURI(), 
				new File(workspace, "TargetXmlSchemaDefinition_1").toURI(), 
				outputFile, 
				null);
		
		ExecuteProcess.outputFile = outputFile;
		
		ExecuteResponse resp = new ExecuteResponse();
		resp.setProcess(new ProcessBriefType());
		resp.getProcess().setProcessVersion("2.1.2"); // must match version in DescribeProcess,GetCapabilities TODO determine both from application version
		//FIXME process identifier! (not included in generated classes)
		ProcessOutputs pOut = new ProcessOutputs();
		OutputDataType data = new OutputDataType();
		StatusType statusType = new StatusType();
		statusType.setProcessSucceeded("Successfully transformed source data");
		GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		statusType.setCreationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
		
		LanguageStringType lst = new LanguageStringType();
		lst.setValue("translate");
		data.getTitle().add(lst);
//		data.getAbstract().add(lst);

		if (request.getSession().getAttribute("save").equals("link")) {
			File result = outputFile;
			String href = CstWps.getServiceURL(request, false) + "download?id="
					+ request.getSession().getId() + "&amp;file="
					+ result.getName();
			
			ReferenceType ref = new ReferenceType();
			ref.setHref(href);
			
			OutputReferenceType outputReferenceType = new OutputReferenceType();
			outputReferenceType.setHref(href);
			
			data.setReference(outputReferenceType);
		}
		else {
			DataType type = new DataType();
			ComplexDataType cData = new ComplexDataType();
			
			cData.setEncoding("utf-8");
			cData.setMimeType("text/xml");
			FileReader fReader = new FileReader(outputFile);
			BufferedReader reader = new BufferedReader(fReader);
			String txt;
			String xml = "";
			StringBuilder sb = new StringBuilder();
			while ((txt = reader.readLine()) != null) {
				sb.append(txt+"\n");
			}
			xml = sb.toString();
			reader.close();
			fReader.close();
			
			cData.getContent().add(xml); // TODO remove with loaded xml/check why < and > are removed with html entities
			
			type.setComplexData(cData);
			data.setData(type);
		}
		
		pOut.getOutput().add(data);
		resp.setProcessOutputs(pOut);
		resp.setLang("en-GB"); // TODO support different languages
		resp.setService("WPS");
		resp.setVersion("1.0.0");
		resp.setStatus(statusType);
		resp.setServiceInstance(CstWps.getServiceURL(request, false) + "cst?");
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
				new NamespacePrefixMapperImpl());
		
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();
		try {
			marshaller.marshal(resp, writer);
		} finally {
			writer.close();
		}
	}
	
	/**
	 * Remove all files, except the result, from working directory.
	 */
	private void cleanup() {
		try {
			File home = new File(workspace);
			
			File[] files = home.listFiles(new GMLFileFilter());
			
			for (File f : files) {
				if (f.isDirectory()) {
					FileUtils.deleteDirectory(f);
				} else {
					FileUtils.deleteQuietly(f);
				}
			}
		} catch (IOException e) {
			_log.error("IOException during cleanup: ", e);
		}
	}
	
	/**
	 * Remove the working directory after the transformation has finished.
	 * @param request the request
	 */
	public static void deleteAll(HttpServletRequest request) {
		String workspace = request.getSession().getAttribute("workspace").toString();
		
		try {
			FileUtils.deleteDirectory(new File(workspace));
		} catch (IOException e) {
			_log.error("IOException during full cleanup: ", e);
		}
	}
	
	/**
	 * This implements a FilenameFilter, which is used in {@link ExecuteProcess#cleanup()}
	 * 
	 * @author Andreas Burchert
	 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
	 */
	public static class GMLFileFilter implements FilenameFilter {
		/**
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			if (name.equals(ExecuteProcess.outputFile.getName())) {
				return false;
			}
			
			return true;
		}
	}
}
