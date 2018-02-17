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

package eu.esdihumboldt.hale.common.core.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.extension.ComplexValueExtension;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.service.ServiceProvider;

/**
 * Interface for I/O providers
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public interface IOProvider {

	/**
	 * The configuration parameter name for the content type
	 */
	public static final String PARAM_CONTENT_TYPE = "contentType";

	/**
	 * The configuration parameter name for the character set
	 */
	public static final String PARAM_CHARSET = "charset";

	/**
	 * Execute the I/O provider.
	 * 
	 * @param progress the progress indicator, may be <code>null</code>
	 * @return the execution report
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 * @throws IOException if an I/O operation fails
	 */
	public IOReport execute(ProgressIndicator progress)
			throws IOProviderConfigurationException, IOException;

	/**
	 * Set the identifier of the associated action.
	 * 
	 * @param action the action identifier
	 */
	public void setActionId(String action);

	/**
	 * Get the identifier of the associated action, if set
	 * 
	 * @return the action identifier or <code>null</code>
	 */
	@Nullable
	public String getActionId();

	/**
	 * Create a reporter configured for the execution of this I/O provider.
	 * 
	 * This method can also be used internally in the implementation of
	 * {@link #execute(ProgressIndicator)}.
	 * 
	 * @return the I/O reporter
	 */
	public IOReporter createReporter();

	/**
	 * States if the execution of the provider is cancelable
	 * 
	 * @return if the execution is cancelable
	 */
	public boolean isCancelable();

	/**
	 * Validate the I/O provider configuration
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 */
	public void validate() throws IOProviderConfigurationException;

	/**
	 * Set the content type. This may be optional if the I/O provider doesn't
	 * differentiate between content types.
	 * 
	 * @param contentType the content type
	 */
	public void setContentType(IContentType contentType);

	/**
	 * Get the content type
	 * 
	 * @return the content type, may be <code>null</code>
	 */
	public IContentType getContentType();

	/**
	 * Set the character set that should be used for encoding/decoding. There
	 * might be I/O providers thought, that don't respect this setting.
	 * 
	 * @param charset the character set
	 */
	public void setCharset(Charset charset);

	/**
	 * Get the configured character set. Implementations may fall back to a
	 * default character set if none is configured.
	 * 
	 * @return the character set or <code>null</code>
	 */
	public Charset getCharset();

	/**
	 * Set the contextual service provider for the I/O provider.
	 * 
	 * @param serviceProvider the service provider
	 */
	public void setServiceProvider(ServiceProvider serviceProvider);

	/**
	 * Get the supported configuration parameters.
	 * 
	 * @return the supported parameters
	 */
	public Set<String> getSupportedParameters();

	/**
	 * Set a parameter
	 * 
	 * @param name the parameter name
	 * @param value the parameter value, it is either a string, a DOM elements
	 *            or a complex value types defined in the
	 *            {@link ComplexValueExtension}
	 */
	public void setParameter(String name, Value value);

	/**
	 * Get the value for the given parameter name
	 * 
	 * @param name the parameter name
	 * @return the parameter value or the NULL value, the value is either a
	 *         string, a DOM elements or a complex value types defined in the
	 *         {@link ComplexValueExtension}
	 */
	public Value getParameter(String name);

	/**
	 * Load the configuration from a map of key/value pairs
	 * 
	 * @param configuration the configuration to load, values are either
	 *            strings, DOM elements or complex value types defined in the
	 *            {@link ComplexValueExtension}
	 */
	public void loadConfiguration(Map<String, Value> configuration);

	/**
	 * Store the configuration in a map of key/value pairs
	 * 
	 * @param configuration the configuration to populate, values are either
	 *            strings, DOM elements or complex value types defined in the
	 *            {@link ComplexValueExtension}
	 */
	public void storeConfiguration(Map<String, Value> configuration);

}
