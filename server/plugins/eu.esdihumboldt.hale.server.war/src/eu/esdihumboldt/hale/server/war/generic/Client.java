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

package eu.esdihumboldt.hale.server.war.generic;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.core.internal.jobs.Worker;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.springframework.web.HttpRequestHandler;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.server.war.CstWps;
import eu.esdihumboldt.hale.server.war.ExecuteProcess;

/**
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Client extends HttpServlet implements HttpRequestHandler {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = -5590628346583308498L;
	
	private final ALogger _log = ALoggerFactory.getLogger(Client.class);

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		
		// session should not timeout
		session.setMaxInactiveInterval(-1);
		
		_log.info("Session ID: "+session.getId());
		
		// create a writer
		PrintWriter writer = response.getWriter();
		
		// handle upload data
		if (request.getParameter("upload") != null) {
			// check if the workspace is available
			if (session.getAttribute("workspace") == null) {
				ExecuteProcess.prepareWorkspace(request);
			}
			
			try {
				this.handleUploadData(request, session.getAttribute("workspace").toString());
			} catch (Exception e) {
				_log.error(e.getMessage(), "Error during data processing.");
			}
		}
		// delete workspace
		else if (request.getParameter("deleteAll") != null) {
			// check if a session is available AND a workspace is set
			if (session.getId() != null && session.getAttribute("workspace") != null) {
				// delete workspace
				ExecuteProcess.deleteAll(request);
			}
		}
		// nothing requested, just show the upload form
		else {
			try {
				this.showForm(writer);
			} catch (Exception e) {
				_log.error(e.getMessage(), "Could not load static form.");
			}
		}
		
		// close output stream
		writer.close();
	}
	
	/**
	 * Loads the static form.
	 * 
	 * @param writer outputstream
	 * @throws Exception if something goes wrong
	 */
	private void showForm(PrintWriter writer) throws Exception {
		BufferedReader reader;
		
		Bundle bundle = Platform.getBundle(CstWps.ID);
		Path path = new Path("cst-wps-static/generic/client.html");

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
	
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	private void handleUploadData(HttpServletRequest request, String path) throws Exception {
		if (ServletFileUpload.isMultipartContent(request)) {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			try {
				List<FileItem> items = upload.parseRequest(request);
				
				for (FileItem item : items) {
					// Process a regular form field
					if (item.isFormField()) {
					    String name = item.getFieldName();
					    String value = item.getString();
					} else {
						String fieldName = item.getFieldName();
						String fileName = item.getName();
//						String contentType = item.getContentType();
//						boolean isInMemory = item.isInMemory();
//						long sizeInBytes = item.getSize();
						
						InputStream is = item.getInputStream();
						
						// create file
						FileOutputStream fos = new FileOutputStream(path+fileName);
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
				}
			} catch (FileUploadException e) {
				_log.error(e.getMessage(), "Error during multipart parsing.");
			}
		}
	}
}
