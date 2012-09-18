/*
 * Copyright (c) 2012 Data Harmonisation Panel
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     HUMBOLDT EU Integrated Project #030962
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
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
