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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.prefixmapper.NamespacePrefixMapperImpl;
import eu.esdihumboldt.hale.server.war.wps.ExceptionReport;
import eu.esdihumboldt.hale.server.war.wps.ExceptionType;
import eu.esdihumboldt.hale.server.war.wps.ProcessFailedType;

/**
 * WPS utilities
 * @author Simon Templer
 */
public abstract class WpsUtil implements WpsConstants {
	
	private static final ALogger log = ALoggerFactory.getLogger(WpsUtil.class);
	
	/**
	 * This function handles all occurrence of Exceptions
	 * and generates output for the user.
	 * @param exceptionCode the exception code
	 * @param message the error message, may be <code>null</code>
	 * @param locator the locator value, may be <code>null</code>
	 * @param e thrown Exception, may be <code>null</code>
	 * @param response the servlet response
	 */
	public static void printError(String exceptionCode, String message,
			String locator,	Exception e, HttpServletResponse response) {
		log.error((message == null)?("Error during request handling"):(message), e);
		
		try {
			JAXBContext context = JAXBContext.newInstance(
					eu.esdihumboldt.hale.server.war.wps.ObjectFactory.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", //$NON-NLS-1$
					new NamespacePrefixMapperImpl());
			
			ProcessFailedType failed = new ProcessFailedType();
			ExceptionReport report = new ExceptionReport();
			ExceptionType type = new ExceptionType();
			
			type.setExceptionCode(exceptionCode);
			if (message != null) {
				type.getExceptionText().add(message);
			}
			if (e != null) {
				type.getExceptionText().add(e.getMessage());
			}
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

}
