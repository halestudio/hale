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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.Platform;
import org.springframework.web.HttpRequestHandler;

/**
 * Offers result files for download
 * @author Andreas Burchert
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 */
public class Download implements HttpRequestHandler{

	/**
	 * @see HttpRequestHandler#handleRequest(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// get parameter
		String id = request.getParameter("id");
		String fileName = request.getParameter("file");
		
		// check if both is set
		if (id != null && fileName != null) {
			//response.setContentType(...)
			
			// create the filepath
			String filePath = Platform.getLocation().toString() + 
					"/tmp/cst_" +id + "/" + fileName;
			
			File file = new File(filePath);
			
			// check if it exists and is readable 
			if (file.exists() && file.canRead()) {
				// create the printwriter
				response.setContentType("text/xml");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer = response.getWriter();
				
				// read file
				FileReader fReader = new FileReader(file);
				BufferedReader reader = new BufferedReader(fReader);
				
				String txt;
				while ((txt = reader.readLine()) != null) {
					// and write to screen
					writer.println(txt);
				}
				
				// close stream
				reader.close();
				fReader.close();
				writer.close();
			}
		} else {
			// the file is not found so set http header to 404
			response.sendError(404);
		}
	}
}
