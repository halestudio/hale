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

package eu.esdihumboldt.hale.server.war.handler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
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
import eu.esdihumboldt.hale.server.war.WpsException;
import eu.esdihumboldt.hale.server.war.WpsUtil;
import eu.esdihumboldt.hale.server.war.WpsException.WpsErrorCode;
import eu.esdihumboldt.hale.server.war.wps.CodeType;
import eu.esdihumboldt.hale.server.war.wps.ComplexDataType;
import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.DataType;
import eu.esdihumboldt.hale.server.war.wps.DocumentOutputDefinitionType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse;
import eu.esdihumboldt.hale.server.war.wps.ExecuteResponse.ProcessOutputs;
import eu.esdihumboldt.hale.server.war.wps.InputType;
import eu.esdihumboldt.hale.server.war.wps.LanguageStringType;
import eu.esdihumboldt.hale.server.war.wps.OutputDataType;
import eu.esdihumboldt.hale.server.war.wps.OutputReferenceType;
import eu.esdihumboldt.hale.server.war.wps.ProcessBriefType;
import eu.esdihumboldt.hale.server.war.wps.ReferenceType;
import eu.esdihumboldt.hale.server.war.wps.ResponseDocumentType;
import eu.esdihumboldt.hale.server.war.wps.ResponseFormType;
import eu.esdihumboldt.hale.server.war.wps.StatusType;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class ExecuteProcess {
	
	private final static ALogger _log = ALoggerFactory.getLogger(CstWps.class);
	
	private final File workspace;
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
	private File outputFile;
	
	/**
	 * Constructor. Does all processing.
	 * 
	 * @param params all given (http)parameter in lowercase
	 * @param response the response
	 * @param request the request
	 * @throws WpsException if an error occurs handling the execute request
	 */
	public ExecuteProcess(Map<String, String> params, HttpServletResponse response, HttpServletRequest request) throws WpsException {
		this.params = params;
		this.response = response;
		this.request = request;
		
		// create workspace dir
		File work = null;
		try {
			work = ExecuteProcess.prepareWorkspace(request);
		} catch (Exception e) {
			throw new WpsException("Error creating process workspace.",
					WpsErrorCode.NoApplicableCode, e, null);
		}
		
		this.workspace = work;
		
		if (workspace != null) {
			try {
				// init jaxb
				initJAXB();
				
				// save data from request to file
				saveRequestData();
				
				// preprocess the data and check for consistency
				// unmarshall request
				Execute exec = (Execute) unmarshaller.unmarshal(fXmldata);
				
				// check if the right identifier is set
				if (!exec.getIdentifier().getValue().equals("translate")) {
					// translate is the only supported process
					throw new WpsException("Invalid process identifier: "
							+ exec.getIdentifier().getValue(),
							WpsErrorCode.InvalidParameterValue, null,
							"Identifier");
				}
				
				preprocessData(exec);
				
				checkData();
				
				// now process the data
				processData();
			} catch (WpsException e) {
				throw e;
			} catch(Exception e) {
				throw new WpsException("Error executing process.",
						WpsErrorCode.NoApplicableCode, e, null);
			} finally {
				// and do a cleanup
				cleanup();
			}
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
	public static File prepareWorkspace(HttpServletRequest request) throws FileNotFoundException {
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
			throw new FileNotFoundException("Could not create directory: " + workspace);
		}
		
		return work;
	}
	
	/**
	 * Initialize the JAXBContext and the Unmarshaller.
	 * @throws WpsException if initializing the the context fails 
	 */
	private void initJAXB() throws WpsException {
		try {
			context = JAXBContext
					.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class);
			unmarshaller = context.createUnmarshaller();
		} catch (Exception e) {
			throw new WpsException("Error initializing JAXB context",
					WpsErrorCode.NoApplicableCode, e, null);
		}
	}
	
	/**
	 * Save the requestData to disk as JAXB only can unmarshall from a File.
	 * 
	 * @throws WpsException if the file can't be written
	 */
	private void saveRequestData() throws WpsException {
		try {
			String xmlData = params.get("request");
			fXmldata = new File(workspace, requestDataFileName);
	
			FileWriter wr = new FileWriter(fXmldata);
			wr.write(xmlData);
			wr.flush();
			wr.close();
		} catch (Exception e) {
			throw new WpsException("Error saving request body.",
					WpsErrorCode.NoApplicableCode, e, null);
		}
	}
	
	/**
	 * Analyzes the requestData and saves provided data to disk.
	 * @param exec the execution request
	 *  
	 * @throws WpsException if saving the request data fails
	 */
	private void preprocessData(Execute exec) throws WpsException {
		try {
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
				
				//TODO other types of response form?!, e.g. raw data
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
					saveArchive(t, ExecuteProcess.inputSourceArchive);
					continue;
				} else if (identifier.equals(ExecuteProcess.inputTargetArchive)) {
					iTargetArchive++;
					fileNumber = iTargetArchive;
					saveArchive(t, ExecuteProcess.inputTargetArchive);
					continue;
				} else {
					// not allowed input type
					throw new WpsException(
							"Unknown input: " + identifier,
							WpsErrorCode.NoApplicableCode, null, null);
				}
				
				// save files that are no archives
				File file = new File(workspace, identifier+"_"+fileNumber);
				
				// check if ComplexData is available
				if (t.getData() != null && t.getData().getComplexData() != null && t.getData().getComplexData().getContent().size() > 0) {
					// save inline complex content
					List<?> list = t.getData().getComplexData().getContent();
					
					for (Object o : list) {
						if (o instanceof Element) {
							// create a element
							Element element = (Element) o;
							String str;
							
							TransformerFactory transFactory = TransformerFactory.newInstance();
							Transformer transformer = null;
							
							transformer = transFactory.newTransformer();
							
							StringWriter buffer = new StringWriter();
							transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
							
							transformer.transform(new DOMSource(element), new StreamResult(buffer));
							
							str = buffer.toString();
							
							// write to disk
							FileWriter fw = new FileWriter(file);
							fw.write(str);
							fw.close();
						}
					}
				}
				else if (t.getReference() != null) {
					// save referenced file
					String href = t.getReference().getHref();
					URL url = new URL(href);
					
					InputStream in = url.openStream();
					FileOutputStream out = new FileOutputStream(file);
					IOUtils.copy(in, out);
					in.close();
					out.close();
				}
				else {
					// not supported
					throw new WpsException(
							"Input must be given either as reference or as complex inline data.",
							WpsErrorCode.InvalidParameterValue, null,
							identifier);
				}
			}
		} catch (WpsException e) {
			throw e;
		} catch (Exception e) {
			throw new WpsException("Error saving the request inputs.",
					WpsErrorCode.NoApplicableCode, e, null);
		}
	}
	
	/**
	 * Checks if mandatory elements are provided.
	 * 
	 * @throws WpsException if a required input is missing
	 */
	private void checkData() throws WpsException {
		String locator = null;
		if (iSourceData < 1) {
			/* throw or generate exception */
			locator = inputSourceData;
		} else if (iSourceXSD < 1) {
			/* throw or generate exception */
			locator = inputSourceXml;
		} else if (iTargetXSD < 1) {
			/* throw or generate exception */
			locator = inputTargetXml;
		} else if (iMapping != 1) {
			/* throw or generate exception */
			locator = inputMapping;
		}
		
		if (locator != null) {
			throw new WpsException("Missing required input.",
					WpsErrorCode.MissingParameterValue, null, locator);
		}
	}
	
	/**
	 * Extracts information from request data and saves given
	 * archive data (either from url or from data).
	 * 
	 * @param t contain information (xlink, data, etc.)
	 * @param ident identifier for the archive
	 * @throws Exception if something goes wrong
	 */
	private void saveArchive(InputType t, String ident) throws Exception {
		File zipFile = new File(workspace, ident + ".zip");
		
		if (t.getData() != null && t.getData().getComplexData() != null) {
			// archive is present as complex data
			ComplexDataType complexData = t.getData().getComplexData();
			
//			String mimeType = complexData.getMimeType();
//			String encoding = complexData.getEncoding();
			
//			if (mimeType != null && mimeType.equals("text/plain") && encoding.equalsIgnoreCase("base64")) {
				/*
				 * TODO this hasn't been tested! (bas64 encoded zip archive)
				 */
				
				// create file and output stream
				FileOutputStream zos = new FileOutputStream(zipFile);
				
				for (Object line : complexData.getContent()) {
					// get binary content
					byte[] content = Base64.decodeBase64(line.toString().trim());
					
					// write content
					zos.write(content);
				}
				
				// flush and close stream
				zos.flush();
				zos.close();
//			}
		}
		else if (t.getReference() != null) {
			URL url = new URL(t.getReference().getHref());
			
			// create inputStream
			InputStream is = url.openStream();
			
			// create file
			FileOutputStream fos = new FileOutputStream(zipFile);
			IOUtils.copy(is, fos);
			
			// flush and close
			is.close();
			fos.flush();
			fos.close();
		}
		else {
			// not supported
			throw new WpsException(
					"Input must be given either as reference or as complex inline data.",
					WpsErrorCode.InvalidParameterValue, null,
					t.getIdentifier().getValue());
		}
		
		// check the existence of the file and unzip it
		if (zipFile.exists()) {
			this.extractArchive(zipFile);
		} else {
			//TODO throw Exception
		}
	}
	
	/**
	 * Extracts all data from an archive into current workspace folder.
	 * 
	 * @param zipFile of archive
	 * @throws IOException if something goes wrong
	 */
	private void extractArchive(File zipFile) throws IOException {
		ZipFile file = new ZipFile(zipFile);
		
		for (Enumeration<?> e = file.getEntries(); e.hasMoreElements(); ) {
			ZipArchiveEntry entry = (ZipArchiveEntry) e.nextElement();
			
			if (entry.isDirectory()) {
				// create new directory
				if (!new File(workspace, entry.getName()).mkdirs()) {
					throw new FileNotFoundException("Could not create directory: "+workspace+"/"+entry.getName());
				}
				continue;
			}
			
			// create new file
			File outFile = new File(workspace, entry.getName());
			
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
				inputStream.close();
				outputStream.close();
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
		
		this.outputFile = outputFile;
		
		ExecuteResponse resp = new ExecuteResponse();
		resp.setProcess(new ProcessBriefType());
		resp.getProcess().setProcessVersion(WpsUtil.getProcessVersion());
		CodeType identifier = new CodeType();
		identifier.setValue("translate");
		resp.getProcess().setIdentifier(identifier);
		LanguageStringType title = new LanguageStringType();
		title.setValue("Execute Schema Transformation.");
		resp.getProcess().setTitle(title);
		ProcessOutputs pOut = new ProcessOutputs();
		OutputDataType data = new OutputDataType();
		StatusType statusType = new StatusType();
		statusType.setProcessSucceeded("Successfully transformed source data");
		GregorianCalendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		statusType.setCreationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
		
		CodeType outputIdentifier = new CodeType();
		outputIdentifier.setValue("TargetData");
		data.setIdentifier(outputIdentifier);
		LanguageStringType lst = new LanguageStringType();
		lst.setValue("Transformed Data in Target Schema");
		data.setTitle(lst);

		if (request.getSession().getAttribute("save").equals("link")) {
			File result = outputFile;
			String href = WpsUtil.getServiceURL(request, false) + "download?id="
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
		resp.setServiceInstance(WpsUtil.getServiceURL(request, false) + "cst?");
		
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
		if (workspace == null) {
			return;
		}
		
		File[] files = workspace.listFiles(new GMLFileFilter(outputFile));
		
		for (File f : files) {
				FileUtils.deleteQuietly(f);
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
		
		private final File toKeep;
		
		/**
		 * @param toKeep the file to keep
		 */
		public GMLFileFilter(File toKeep) {
			super();
			this.toKeep = toKeep;
		}

		/**
		 * @see FilenameFilter#accept(File, String)
		 */
		@Override
		public boolean accept(File dir, String name) {
			if (toKeep == null) {
				return true;
			}
			
			if (name.equals(toKeep.getName())) {
				return false;
			}
			
			return true;
		}
	}
}
