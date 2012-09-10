/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                  01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2010.
 */
package eu.esdihumboldt.hale.ui.util;

import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * This utility class can be used to transform an Exception to an Alert that is
 * displayed to the use.
 * 
 * @author Michel Kraemer, Thorsten Reitz
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @version $Id$
 */
public class ExceptionHelper {

	/**
	 * Handles exceptions and displays an error dialog
	 * 
	 * @param message the message displayed in the error dialog
	 * @param pluginId the identifier of the relevant plugin
	 * @param t the exception
	 */
	public static void handleException(String message, String pluginId, Throwable t) {
		if (message == null || message.isEmpty()) {
			message = t.getMessage();
		}
		Status status = new Status(Status.ERROR, pluginId, message, t);
		StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.BLOCK);
	}
}