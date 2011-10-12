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

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.HttpRequestHandler;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;

/**
 * TODO Type description
 * @author sitemple
 */
public class WMSProxy implements HttpRequestHandler {
	
	private static final ALogger log = ALoggerFactory.getLogger(WMSProxy.class);

	/**
	 * @see org.springframework.web.HttpRequestHandler#handleRequest(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = request.getParameterMap();
		
		final String target = "http://pvfrm.alta4cloud.com/regfnp";
		
		StringBuffer build = new StringBuffer(target);
		
		boolean first = true;
		for (Entry<String, String[]> entry : parameters.entrySet()) {
			if (first) {
				build.append('?');
				first = false;
			}
			else {
				build.append('&');
			}
			
			build.append(entry.getKey());
			build.append('=');
			String value = entry.getValue()[0];
			
			if (entry.getKey().toLowerCase().equals("bbox")) {
				log.info("Switching X and Y in BBOX");
				String[] ords = value.split(",");
				
				String tmp = ords[0];
				ords[0] = ords[1];
				ords[1] = tmp;
				
				tmp = ords[2];
				ords[2] = ords[3];
				ords[3] = tmp;
				
				StringBuffer buff = new StringBuffer();
				boolean f = true;
				for (String ord: ords) {
					if (f) {
						f = false;
					}
					else {
						buff.append(',');
					}
					buff.append(ord);
				}
				value = buff.toString();
			}
			
			build.append(value); // TODO iterate?
			
			if (entry.getValue().length > 1) {
				log.warn("Multiple values for parameter " + entry.getKey());
			}
		}
		
		URL url = new URL(build.toString());
		
		log.info("New request: " + url.toString());
		
		if (request.getParameter("request").toLowerCase().equals("getmap")) {
			response.setContentType("image/png");
		}
		ServletOutputStream out = response.getOutputStream();
		IOUtils.copy(url.openStream(), out);
		out.flush();
		out.close();
	}

}
