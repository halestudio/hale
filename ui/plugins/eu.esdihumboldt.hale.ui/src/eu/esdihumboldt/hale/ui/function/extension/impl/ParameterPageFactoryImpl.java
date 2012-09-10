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

package eu.esdihumboldt.hale.ui.function.extension.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.function.extension.ParameterPageFactory;
import eu.esdihumboldt.hale.ui.function.generic.pages.ParameterPage;

/**
 * Parameter page factory based on a configuration element.
 * 
 * @author Kai Schwierczek
 */
public class ParameterPageFactoryImpl extends AbstractConfigurationFactory<ParameterPage> implements
		ParameterPageFactory {

	private Set<FunctionParameter> associatedFunctionParameters;
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
	 * @see de.cs3d.util.eclipse.extension.AbstractObjectDefinition#getPriority()
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
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectFactory#dispose(java.lang.Object)
	 */
	@Override
	public void dispose(ParameterPage instance) {
		// do nothing
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return conf.getAttribute("id");
	}

	/**
	 * @see de.cs3d.util.eclipse.extension.ExtensionObjectDefinition#getDisplayName()
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
	public Set<FunctionParameter> getAssociatedParameters() {
		if (associatedFunctionParameters == null) {
			Set<FunctionParameter> params = new HashSet<FunctionParameter>();
			// get defined parameters
			Set<FunctionParameter> definedParameters = FunctionUtil.getFunction(getFunctionId())
					.getDefinedParameters();
			// walk over conf parameters
			IConfigurationElement[] parameterElements = conf.getChildren("parameter");
			for (IConfigurationElement parameterElement : parameterElements) {
				// search for defined parameter, add it to associated params
				// XXX throw some exception if param name is not defined?
				String name = parameterElement.getAttribute("name");
				for (FunctionParameter definedParameter : definedParameters)
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
