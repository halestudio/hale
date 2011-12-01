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

package eu.esdihumboldt.hale.server.war.client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.velocity.VelocityContext;
import org.springframework.web.HttpRequestHandler;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.WpsUtil;
import eu.esdihumboldt.hale.server.war.handler.ExecuteProcess;
import eu.esdihumboldt.hale.server.war.wps.CodeType;
import eu.esdihumboldt.hale.server.war.wps.DataInputsType;
import eu.esdihumboldt.hale.server.war.wps.DocumentOutputDefinitionType;
import eu.esdihumboldt.hale.server.war.wps.Execute;
import eu.esdihumboldt.hale.server.war.wps.InputReferenceType;
import eu.esdihumboldt.hale.server.war.wps.InputType;
import eu.esdihumboldt.hale.server.war.wps.ResponseDocumentType;
import eu.esdihumboldt.hale.server.war.wps.ResponseFormType;

/**
 * This implements a generic web client for CST-WPS.
 * 
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Client extends HttpServlet implements HttpRequestHandler {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = -5590628346583308498L;
	
	private final ALogger _log = ALoggerFactory.getLogger(Client.class);
	
	private JAXBContext context;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		
		// session should not timeout
		session.setMaxInactiveInterval(-1);
		
		_log.info("Session ID: "+session.getId());
		if (context == null) {
			try {
				context = JAXBContext.newInstance(eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class);
			} catch (JAXBException e1) {
				/* FIXME! */
			}
		}
		
		// create a writer
		
		// handle upload data
		if (request.getParameter("upload") != null) {
			// check if the workspace is available
			ExecuteProcess.prepareWorkspace(request);
			
			try {
				Execute exec = this.handleUploadData(request, session.getAttribute("workspace").toString());
				
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
						new NamespacePrefixMapperImpl());
				
				StringWriter sw = new StringWriter();
				marshaller.marshal(exec, sw);
				
				// generate "virtual" request
				Map<String, String> params = new HashMap<String, String>();
				params.put("request", sw.toString());
				
				// execute process
				new ExecuteProcess(params, response, request);
			} catch (Exception e) {
				_log.error("Error during data processing.", e);
			}
		}
		// delete workspace
		else if (request.getParameter("deleteAll") != null) {
			// check if a session is available AND a workspace is set
			if (session.getId() != null && session.getAttribute("workspace") != null) {
				// delete workspace
				ExecuteProcess.deleteAll(request);
			} else if (request.getParameter("id") != null) {
				// set the given session id
				session.setAttribute("id", request.getParameter("id"));
				
				// create the workspace
				// this is needed as we can't guess where the workspace is
				ExecuteProcess.prepareWorkspace(request);
				
				// and delete it
				ExecuteProcess.deleteAll(request);
			}
		}
		// nothing requested, just show the upload form
		else {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			try {
				this.showForm(request, writer);
			} catch (Exception e) {
				_log.error(e.getMessage(), "Could not load static form.");
			} finally {
				// close output stream
				writer.close();
			}
		}
	}
	
	/**
	 * Loads the static form.
	 * 
	 * @param request the request
	 * @param writer outputstream
	 * @throws Exception if something goes wrong
	 */
	private void showForm(HttpServletRequest request, PrintWriter writer) throws Exception {
		HttpSession session = request.getSession();
		
		VelocityContext context = new VelocityContext();
		context.put("session_id", session.getId());
		
		WpsUtil.mergeTemplate(
				"cst-wps-static/client/client.html",
				context, "UTF-8", writer);
	}
	
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	private Execute handleUploadData(HttpServletRequest request, String path) throws Exception {
		if (ServletFileUpload.isMultipartContent(request)) {
			Execute exec = new Execute();
			
			// set identifier
			CodeType codeType = new CodeType();
			codeType.setValue("translate");
			exec.setIdentifier(codeType);
			exec.setService("WPS");
			exec.setVersion("1.0.0");
			
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			//
			DataInputsType dataInputType = new DataInputsType();
			
			try {
				@SuppressWarnings("unchecked")
				List<FileItem> items = upload.parseRequest(request);
				
				for (FileItem item : items) {
					File file = null;
					InputType input = new InputType();
					
					// remove chars so we can use the fieldname as element data
					String fieldName = item.getFieldName().replace("[]", "");
					fieldName = fieldName.replace("URL", "");
					
					// display check
					if (fieldName.equals("save")) {
						// save the value to session
						request.getSession().setAttribute("save", item.getString());
						continue;
					}
					
					// Process a regular form field
					if (item.isFormField()) {
						String filePath = item.getString();
						
						// skip if no url is given
						if (filePath.equals("")) {
							continue;
						}
					} else {
						String fileName = item.getName();
						
						if (fileName.equals("")) {
							continue;
						}
						
						file = new File(path + fileName);
						
						InputStream is = item.getInputStream();
						
						// create file
						FileOutputStream fos = new FileOutputStream(file);
						BufferedOutputStream os = new BufferedOutputStream(fos);
						
						int avail = is.available();
						byte[] data = new byte[avail];
						while (is.read(data, 0, avail)>0) {
							os.write(data);
							os.flush();
							avail = is.available();
							data = new byte[avail];
						}
						
						// flush and close
						os.flush();
						os.close();
						fos.close();
						is.close();
					}
					
					if (file != null) {
						// create data identifier
						CodeType ct = new CodeType();
						ct.setValue(fieldName);
						
						// set identifier
						input.setIdentifier(ct);
						
						InputReferenceType inputReference = new InputReferenceType();
						inputReference.setHref(file.toURI().toURL().toExternalForm());
						
						input.setReference(inputReference);
						
						// add to <wps:DataInputs>
						dataInputType.getInput().add(input);
					}
				}
			} catch (FileUploadException e) {
				_log.error(e.getMessage(), "Error during multipart parsing.");
			}
			
			//
			exec.setDataInputs(dataInputType);
			
			ResponseFormType formType = new ResponseFormType();
			ResponseDocumentType documentType = new ResponseDocumentType();
			DocumentOutputDefinitionType type = new DocumentOutputDefinitionType();
			CodeType targetData = new CodeType();
			targetData.setValue("TargetData");
			type.setIdentifier(targetData);
			
			if (request.getSession().getAttribute("save").equals("link")) {
				// display as reference
				type.setAsReference(true);
				documentType.setStoreExecuteResponse(true);
			} else {
				// display on screen
				type.setAsReference(false);
				documentType.setStoreExecuteResponse(false);
			}
			
			documentType.getOutput().add(type);
			formType.setResponseDocument(documentType);
			exec.setResponseForm(formType);
			
			return exec;
		}
		
		return null;
	}
}
