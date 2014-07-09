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

package eu.esdihumboldt.hale.common.align.extension.transformation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.jcip.annotations.Immutable;

import org.eclipse.core.runtime.IConfigurationElement;

import de.fhg.igd.eclipse.util.extension.AbstractConfigurationFactory;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectDefinition;
import de.fhg.igd.eclipse.util.extension.ExtensionObjectFactory;
import eu.esdihumboldt.hale.common.align.transformation.function.TransformationFunction;

/**
 * Base class for transformation function factories
 * 
 * @param <T> the transformation function type
 * 
 * @author Simon Templer
 */
@Immutable
public abstract class AbstractTransformationFactory<T extends TransformationFunction<?>> extends
		AbstractConfigurationFactory<T> implements TransformationFactory<T> {

	private final Map<String, String> parameters;

	/**
	 * Create a transformation function factory based on the given configuration
	 * element.
	 * 
	 * @param conf the configuration element
	 */
	protected AbstractTransformationFactory(IConfigurationElement conf) {
		super(conf, "class");

		parameters = createExecutionParameters(conf);
	}

	private static Map<String, String> createExecutionParameters(IConfigurationElement conf) {
		Map<String, String> result = new LinkedHashMap<String, String>();

		IConfigurationElement[] params = conf.getChildren("executionParameter");
		if (params != null) {
			for (IConfigurationElement param : params) {
				result.put(param.getAttribute("key"), param.getAttribute("value"));
			}
		}

		return result;
	}

	/**
	 * @see ExtensionObjectFactory#dispose(Object)
	 */
	@Override
	public void dispose(T instance) {
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
		// TODO instead return function name?
		return getIdentifier();
	}

	/**
	 * @see TransformationFactory#getEngineId()
	 */
	@Override
	public String getEngineId() {
		return conf.getAttribute("engine");
	}

	/**
	 * @see TransformationFactory#getFunctionId()
	 */
	@Override
	public String getFunctionId() {
		return conf.getAttribute("function");
	}

	/**
	 * @see TransformationFactory#getExecutionParameters()
	 */
	@Override
	public Map<String, String> getExecutionParameters() {
		return Collections.unmodifiableMap(parameters);
	}

}
