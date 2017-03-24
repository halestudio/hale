/*
 * Copyright (c) 2013 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.common.core.io.extension;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.ExtensionUtil;
import de.fhg.igd.eclipse.util.extension.simple.IdentifiableExtension;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.core.io.ComplexValueJson;

/**
 * Extension for JSON converters for complex parameter types.
 * 
 * @author Simon Templer
 */
public class ComplexValueJsonExtension extends IdentifiableExtension<ComplexValueJsonDescriptor> {

	private static final ALogger log = ALoggerFactory.getLogger(ComplexValueJsonExtension.class);

	private static ComplexValueJsonExtension instance;

	/**
	 * Get the extension instance.
	 * 
	 * @return the extension singleton
	 */
	public static ComplexValueJsonExtension getInstance() {
		synchronized (ComplexValueJsonExtension.class) {
			if (instance == null)
				instance = new ComplexValueJsonExtension();
		}
		return instance;
	}

	/**
	 * Default constructor
	 */
	protected ComplexValueJsonExtension() {
		super(ComplexValueExtension.EXTENSION_ID);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ComplexValueJsonDescriptor create(String id, IConfigurationElement conf) {
		if (!conf.getName().equals("complexValueJson")) {
			return null;
		}

		try {
			ComplexValueJsonDescriptor cvs = new ComplexValueJsonDescriptor(id,
					(Class<ComplexValueJson<?, ?>>) ExtensionUtil.loadClass(conf, "class"));
			return cvs;
		} catch (Exception e) {
			log.error("Could not load JSON converter for complex parameter type with ID " + id, e);
			return null;
		}
	}

	@Override
	protected String getIdAttributeName() {
		return "ref";
	}

	@Override
	public ComplexValueJsonDescriptor get(String id) {
		ComplexValueJsonDescriptor result = super.get(id);
		if (result == null) {
			// try to lookup alias
			ComplexValueAlias alias = ComplexValueAliasExtension.INSTANCE.get(id);
			if (alias != null) {
				result = super.get(alias.getRef());
			}
		}
		return result;
	}

}
