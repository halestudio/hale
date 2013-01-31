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

package eu.esdihumboldt.hale.common.core.io.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;

import eu.esdihumboldt.hale.common.core.io.IOProvider;
import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.Value;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;

/**
 * Abstract base class for implementing {@link IOProvider}s
 * 
 * @author Simon Templer
 * @partner 01 / Fraunhofer Institute for Computer Graphics Research
 * @since 2.2
 */
public abstract class AbstractIOProvider implements IOProvider {

	/**
	 * The configuration parameters
	 */
	private final Map<String, Value> parameters = new HashMap<String, Value>();

	/**
	 * The supported configuration parameter names
	 */
	private final Set<String> supported = new HashSet<String>();

	/**
	 * The content type
	 */
	private IContentType contentType = null;

	/**
	 * Default constructor
	 */
	protected AbstractIOProvider() {
		super();

		addSupportedParameter(PARAM_CONTENT_TYPE);
	}

	/**
	 * @see IOProvider#execute(ProgressIndicator)
	 */
	@Override
	public IOReport execute(ProgressIndicator progress) throws IOProviderConfigurationException,
			IOException {
		return execute((progress == null) ? (new LogProgressIndicator()) : (progress),
				createReporter());
	}

	/**
	 * Execute the I/O provider.
	 * 
	 * @param progress the progress indicator
	 * @param reporter the reporter to use for the execution report
	 * @return the execution report
	 * 
	 * @throws IOProviderConfigurationException if the I/O provider was not
	 *             configured properly
	 * @throws IOException if an I/O operation fails
	 */
	protected abstract IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException;

	/**
	 * Get the content type name.
	 * 
	 * @return the content type name
	 */
	protected String getTypeName() {
		IContentType ct = getContentType();
		if (ct != null) {
			return ct.getName();
		}

		return getDefaultTypeName();
	}

	/**
	 * Get the default type name if no content type is provided
	 * 
	 * @return the default content type
	 */
	protected abstract String getDefaultTypeName();

	/**
	 * @see IOProvider#validate()
	 */
	@Override
	public void validate() throws IOProviderConfigurationException {
		// TODO check parameters?
	}

	/**
	 * Uses {@link #setParameter(String, Value)} to load the configuration. For
	 * changing the behavior please override
	 * {@link #setParameter(String, Value)}
	 * 
	 * @see IOProvider#loadConfiguration(Map)
	 */
	@Override
	public final void loadConfiguration(Map<String, Value> configuration) {
		for (Entry<String, Value> entry : configuration.entrySet()) {
			setParameter(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Stores all parameters that were set using
	 * {@link #setParameter(String, Value)} in the configuration. For changing
	 * the behavior please override this method.
	 * 
	 * @see IOProvider#storeConfiguration(Map)
	 */
	@Override
	public void storeConfiguration(Map<String, Value> configuration) {
		// store content type (if set)
		if (contentType != null) {
			configuration.put(PARAM_CONTENT_TYPE, Value.of(contentType.getId()));
		}

		// store generic parameters
		configuration.putAll(parameters);
	}

	/**
	 * Fail validation or execution if the configuration is not valid
	 * 
	 * @param message the error message
	 * 
	 * @throws IOProviderConfigurationException always
	 */
	protected void fail(String message) throws IOProviderConfigurationException {
		throw new IOProviderConfigurationException(message);
	}

	/**
	 * Add a supported parameter name, should be called in the constructor
	 * 
	 * @param name the supported parameter name to add
	 */
	protected void addSupportedParameter(String name) {
		supported.add(name);
	}

	/**
	 * @see IOProvider#getParameter(String)
	 */
	@Override
	public Value getParameter(String name) {
		Value value = parameters.get(name);
		if (value == null)
			value = Value.NULL;
		return value;
	}

	/**
	 * @see IOProvider#getSupportedParameters()
	 */
	@Override
	public Set<String> getSupportedParameters() {
		return Collections.unmodifiableSet(supported);
	}

	@Override
	public void setParameter(String name, Value value) {
		if (name.equals(PARAM_CONTENT_TYPE)) {
			// configure content type
			setContentType(Platform.getContentTypeManager().getContentType(
					value.getAs(String.class)));
		}
		else {
			// load generic parameter
			parameters.put(name, value);
		}
	}

	/**
	 * @see IOProvider#getContentType()
	 */
	@Override
	public IContentType getContentType() {
		return contentType;
	}

	/**
	 * @see IOProvider#setContentType(IContentType)
	 */
	@Override
	public void setContentType(IContentType contentType) {
		this.contentType = contentType;
	}

}
