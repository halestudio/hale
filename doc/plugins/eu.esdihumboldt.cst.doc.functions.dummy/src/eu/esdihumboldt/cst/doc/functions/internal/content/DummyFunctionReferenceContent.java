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

package eu.esdihumboldt.cst.doc.functions.internal.content;

import java.io.InputStream;
import java.util.Locale;

import org.eclipse.core.runtime.Platform;
import org.eclipse.help.IHelpContentProducer;

import eu.esdihumboldt.cst.doc.functions.FunctionReferenceConstants;

/**
 * Provides dummy content for function documentation.
 * 
 * @author Simon Templer
 */
public class DummyFunctionReferenceContent
		implements IHelpContentProducer, FunctionReferenceConstants {

	/**
	 * @see IHelpContentProducer#getInputStream(String, String, Locale)
	 */
	@Override
	public InputStream getInputStream(String pluginID, String href, Locale locale) {
		if (href.startsWith(FUNCTION_TOPIC_PATH)) {
			// it's a function
			try {
				return Platform.getBundle("eu.esdihumboldt.cst.doc.functions.dummy")
						.getEntry("/html/functions.html").openStream();
			} catch (Exception e) {
				// ignore
			}
		}

		return null;
	}

}
