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

package eu.esdihumboldt.hale.doc.user.examples.internal.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;

/**
 * Extension point for example projects.
 * 
 * @author Simon Templer
 */
public class ExampleProjectExtension extends IdentifiableExtension<ExampleProject> {

	private static final ALogger log = ALoggerFactory.getLogger(ExampleProjectExtension.class);

	private static ExampleProjectExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension instance
	 */
	public static ExampleProjectExtension getInstance() {
		if (instance == null) {
			instance = new ExampleProjectExtension();
		}
		return instance;
	}

	/**
	 * Extension point ID
	 */
	private static final String EXTENSION_ID = "eu.esdihumboldt.hale.doc.user.examples";

	/**
	 * Default constructor
	 */
	public ExampleProjectExtension() {
		super(EXTENSION_ID);
	}

	/**
	 * @see IdentifiableExtension#getIdAttributeName()
	 */
	@Override
	protected String getIdAttributeName() {
		return "id";
	}

	/**
	 * @see IdentifiableExtension#create(String, IConfigurationElement)
	 */
	@Override
	protected ExampleProject create(String elementId, IConfigurationElement element) {
		if ("example-project".equals(element.getName())) {
			try {
				return new ExampleProject(elementId, element);
			} catch (Exception e) {
				log.error("Error initializing example project", e);
				return null;
			}
		}

		return null;
	}

}
