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

package eu.esdihumboldt.hale.ui.function.extension.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.function.extension.ParameterPageFactory;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page factory based on a configuration element.
 * 
 * @author Kai Schwierczek
 */
public class ParameterPageFactoryImpl extends AbstractConfigurationFactory<ParameterPage> implements
		ParameterPageFactory {

	private Set<FunctionParameterDefinition> associatedFunctionParameters;
	private static final ALogger _log = ALoggerFactory.getLogger(ParameterPageFactoryImpl.class);

	/**
	 * Create a parameter page factory based on the given configuration element.
	 * 
	 * @param conf the configuration element
	 */
	public ParameterPageFactoryImpl(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition#getPriority()
	 */
	@Override
	public int getPriority() {
		if (conf.getAttribute("order") == null)
			return 0;
		try {
			return Integer.parseInt(conf.getAttribute("order"));
		} catch (NumberFormatException nfe) {
			_log.warn("order not a valid integer", nfe);
			return 0;
		}
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(ParameterPage instance) {
		// do nothing
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * @see de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.extension.ParameterPageFactory#getFunctionId()
	 */
	@Override
	public String getFunctionId() {
		return conf.getAttribute("function");
	}

	/**
	 * @see eu.esdihumboldt.hale.ui.function.extension.ParameterPageFactory#getAssociatedParameters()
	 */
	@Override
	public Set<FunctionParameterDefinition> getAssociatedParameters() {
		if (associatedFunctionParameters == null) {
			Set<FunctionParameterDefinition> params = new HashSet<FunctionParameterDefinition>();
			// get defined parameters
			Collection<FunctionParameterDefinition> definedParameters = FunctionUtil.getFunction(
					getFunctionId(), HaleUI.getServiceProvider()).getDefinedParameters();
			// walk over conf parameters
			IConfigurationElement[] parameterElements = conf.getChildren("parameter");
			for (IConfigurationElement parameterElement : parameterElements) {
				// search for defined parameter, add it to associated params
				// XXX throw some exception if param name is not defined?
				String name = parameterElement.getAttribute("name");
				for (FunctionParameterDefinition definedParameter : definedParameters)
					if (definedParameter.getName().equals(name)) {
						params.add(definedParameter);
						break;
					}
			}
			associatedFunctionParameters = Collections.unmodifiableSet(params);
		}

		return associatedFunctionParameters;
	}
}
