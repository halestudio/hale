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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.WpsException.WpsErrorCode;
import eu.esdihumboldt.hale.server.war.wps.ExceptionReport;
import eu.esdihumboldt.hale.server.war.wps.ExceptionType;
import eu.esdihumboldt.hale.server.war.wps.ProcessFailedType;

/**
 * WPS utilities
 * @author Andreas Burchert
 * @author Simon Templer
 */
public abstract class WpsUtil {
	
	private static final ALogger log = ALoggerFactory.getLogger(WpsUtil.class);
	
	/**
	 * Bundle symbolic name
	 */
	public static final String BUNDLE_ID = "eu.esdihumboldt.hale.server.war";
	
	/**
	 * Name of the system property that may be used to override the service URL
	 */
	public static final String PROPERTY_SERVICE_URL = "service_url";
	
	/**
	 * The template directory for velocity
	 */
	private static File velocityTemplates = null;
	
	/**
	 * This function handles all occurrence of Exceptions
	 * and generates output for the user.
	 * @param e the exception to report, special treatment for 
	 *   {@link WpsException}s
	 * @param customMessage a custom message, may be <code>null</code>
	 * @param response the servlet response
	 */
	public static void printError(Exception e, String customMessage, HttpServletResponse response) {
		log.error(e.getMessage(), (e.getCause() == null)?(e):(e.getCause()));
		
		try {
			JAXBContext context = JAXBContext.newInstance(
					eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
					new NamespacePrefixMapperImpl());
			
			ProcessFailedType failed = new ProcessFailedType();
			ExceptionReport report = new ExceptionReport();
			ExceptionType type = new ExceptionType();
			
			String exceptionCode;
			String locator;
			if (e instanceof WpsException) {
				exceptionCode = ((WpsException) e).getCode().toString();
				locator = ((WpsException) e).getLocator();
			}
			else {
				exceptionCode = WpsErrorCode.NoApplicableCode.toString();
				locator = null;
			}
			
			type.setExceptionCode(exceptionCode);
			if (customMessage != null) {
				type.getExceptionText().add(customMessage);
			}
			type.getExceptionText().add(e.getMessage());
			if (locator != null) {
				type.setLocator(locator);
			}
			
			report.getException().add(type);
			report.setLang("en-GB");
			report.setVersion("1.0.0");
			failed.setExceptionReport(report);
			
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			try {
				marshaller.marshal(report, writer); // using ProcessFailedType does not work
			} finally {
				writer.close();
			}
		} catch (Exception e1) {
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
	
	/**
	 * Merge a template with the given context to a writer
	 * @param templatePath the template path (in the bundle)
	 * @param context the context
	 * @param encoding the template encoding
	 * @param output the writer for the merged output
	 * @throws Exception if an error occurs
	 */
	public static synchronized void mergeTemplate(String templatePath,
			VelocityContext context, String encoding, Writer output)
			throws Exception {
		if (velocityTemplates == null) {
			velocityTemplates = Files.createTempDir();
			velocityTemplates.deleteOnExit();
			
			Velocity.setProperty("file.resource.loader.path", velocityTemplates.getAbsolutePath());
			// initialize Velocity
			Velocity.init(); //TODO use non-singleton instead?
		}
		
		File templateFile = new File(velocityTemplates, templatePath);
		if (!templateFile.exists()) {
			templateFile.getParentFile().mkdirs();
			
			// copy template
			final URL url = findResource(templatePath);
			Files.copy(new InputSupplier<InputStream>() {

				@Override
				public InputStream getInput() throws IOException {
					return url.openStream();
				}
			}, templateFile);
			
			templateFile.deleteOnExit();
		}
		
		Template template = Velocity.getTemplate(templatePath, encoding);
		template.merge(context, output);
	}
	
	/**
	 * Find a resource in the bundle
	 * @param path the resource path in the bundle
	 * @return the resource URL
	 */
	public static URL findResource(String path) {
		Bundle bundle = Platform.getBundle(BUNDLE_ID);
		Path bundlePath = new Path(path);

		return FileLocator.find(bundle, bundlePath, null);
	}

	/**
	 * Get the process version
	 * @return the process version
	 */
	public static String getProcessVersion() {
		return Platform.getBundle(BUNDLE_ID).getVersion().toString();
	}

	/**
	 * Get the service URL from a HTTP request
	 * @param httpRequest the HTTP servlet request
	 * @param includeServletPath if the servlet path shall be included in the
	 *   service URL
	 * @return the service URL, it ends with a slash
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
	
}
