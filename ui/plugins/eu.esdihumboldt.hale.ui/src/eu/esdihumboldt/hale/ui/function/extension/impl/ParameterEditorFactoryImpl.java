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

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.AbstractObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import de.fhg.igd.slf4jplus.ALogger;
import de.fhg.igd.slf4jplus.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameterDefinition;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.HaleUI;
import eu.esdihumboldt.hale.ui.common.EditorFactory;
import eu.esdihumboldt.hale.ui.function.extension.ParameterEditorFactory;

/**
 * Editor factory based on a configuration element.
 * 
 * @author Simon Templer
 */
public class ParameterEditorFactoryImpl extends AbstractConfigurationFactory<EditorFactory>
		implements ParameterEditorFactory {

	private static final ALogger _log = ALoggerFactory.getLogger(ParameterEditorFactoryImpl.class);

	private FunctionParameterDefinition associatedFunctionParameter;

	/**
	 * Create a parameter page factory based on the given configuration element.
	 * 
	 * @param conf the configuration element
	 */
	public ParameterEditorFactoryImpl(IConfigurationElement conf) {
		super(conf, "class");
	}

	/**
	 * @see AbstractObjectDefinition#getPriority()
	 */
	@Override
	public int getPriority() {
		if (conf.getAttribute("priority") == null)
			return 0;
		try {
			return -Integer.parseInt(conf.getAttribute("priority")); // negate
		} catch (NumberFormatException nfe) {
			_log.warn("priority not a valid integer", nfe);
			return 0;
		}
	}

	/**
	 * @see ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(EditorFactory instance) {
		// do nothing
	}

	/**
	 * @see ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * @see ExtensionObjectDefinition#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return getIdentifier();
	}

	/**
	 * @see ParameterEditorFactory#getFunctionId()
	 */
	@Override
	public String getFunctionId() {
		return conf.getAttribute("function");
	}

	/**
	 * @see ParameterEditorFactory#getParameterName()
	 */
	@Override
	public String getParameterName() {
		return conf.getAttribute("parameter");
	}

	/**
	 * @see ParameterEditorFactory#getAssociatedParameter()
	 */
	@Override
	public FunctionParameterDefinition getAssociatedParameter() {
		if (associatedFunctionParameter == null) {
			// get defined parameters
			Collection<FunctionParameterDefinition> definedParameters = FunctionUtil.getFunction(
					getFunctionId(), HaleUI.getServiceProvider()).getDefinedParameters();
			// search for defined parameter, add it to associated params
			// XXX throw some exception if param name is not defined?
			String name = getParameterName();
			for (FunctionParameterDefinition definedParameter : definedParameters) {
				if (definedParameter.getName().equals(name)) {
					associatedFunctionParameter = definedParameter;
					break;
				}
			}
		}

		return associatedFunctionParameter;
	}

}
