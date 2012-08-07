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

import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import de.cs3d.util.eclipse.extension.AbstractConfigurationFactory;
import de.cs3d.util.eclipse.extension.AbstractObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectDefinition;
import de.cs3d.util.eclipse.extension.ExtensionObjectFactory;
import de.cs3d.util.logging.ALogger;
import de.cs3d.util.logging.ALoggerFactory;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionParameter;
import eu.esdihumboldt.hale.common.align.extension.function.FunctionUtil;
import eu.esdihumboldt.hale.ui.common.EditorFactory;
import eu.esdihumboldt.hale.ui.function.extension.ParameterEditorFactory;

/**
 * Editor factory based on a configuration element.
 * 
 * @author Simon Templer
 */
public class ParameterEditorFactoryImpl extends AbstractConfigurationFactory<EditorFactory> implements
		ParameterEditorFactory {
	
	private static final ALogger _log = ALoggerFactory.getLogger(ParameterEditorFactoryImpl.class);
	
	private FunctionParameter associatedFunctionParameter;

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
			return - Integer.parseInt(conf.getAttribute("priority")); // negate
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
	public FunctionParameter getAssociatedParameter() {
		if (associatedFunctionParameter == null) {
			// get defined parameters
			Set<FunctionParameter> definedParameters = FunctionUtil.getFunction(getFunctionId()).getDefinedParameters();
			// search for defined parameter, add it to associated params
			// XXX throw some exception if param name is not defined?
			String name = getParameterName();
			for (FunctionParameter definedParameter : definedParameters) {
				if (definedParameter.getName().equals(name)) {
					associatedFunctionParameter = definedParameter;
					break;
				}
			}
		}

		return associatedFunctionParameter;
	}
	
}
